# rest-service

This technical test repository contains a small Quarkus-based microservice showcasing a hybrid
gRPC + REST architecture for a simple Product domain catalogue.

---

## Architecture overview

- Runtime: Quarkus (Jakarta / Mutiny / Hibernate ORM + Panache)
- API surfaces:
  - gRPC service (primary business logic) — implemented under `org.amadeus.grpc.service`.
  - REST facade — thin HTTP layer that forwards requests to the gRPC client and
	adapts responses for HTTP clients. Implemented under `org.amadeus.rest.resource`.
- Persistence: H2 (JDBC) + Hibernate ORM Panache (`org.amadeus.grpc.entity`, repository under `org.amadeus.grpc.repository`).
- Validation: Jakarta Bean Validation (hibernate-validator) for REST DTOs.
- Error mapping: gRPC errors (`StatusRuntimeException`) are translated to HTTP
  responses by a JAX-RS `ExceptionMapper` that returns a consistent
  `APIResponseDTO` envelope.

Diagram (logical):

REST Client -> REST Resource -> gRPC Client -> gRPC Service -> Repository -> H2 DB

---

## Build and run

Prerequisites: Java 21 (or configured JDK), Maven.

From project root (Windows PowerShell examples):

- Build and run tests:

```powershell
mvn clean test
```

- Run the application in dev mode (live reload):

```powershell
mvn quarkus:dev
```

- Package the application:

```powershell
mvn package
```

- Run the packaged application:

```powershell
java -jar target/quarkus-app/quarkus-run.jar
```

Notes:
- The project includes a Maven wrapper (`mvnw`), you can use `./mvnw`/`mvnw.cmd` instead of system `mvn`.
- When running tests in CI or locally make sure the test resources are present. Integration tests expect the application root path `/amadeus/api/v1` configured in `src/main/resources/application.yaml`.

---

## REST endpoints

The service exposes a REST API under the base path `/amadeus/api/v1/products`.

For concrete request examples, refer to the Postman collection file in the
repository root: `requests.postman_collection.json`.

Responses use a consistent envelope `APIResponseDTO` containing `grpcCode`, `message` and `data`.

---

## gRPC contract summary

The gRPC contract is defined in `src/main/proto/product.proto` and generates the
`org.amadeus.*` classes used by the service and client layers.

### RPCs

- `CreateProduct(CreateProductRequest) -> ProductResponse`
- `GetProduct(ProductId) -> ProductResponse`
- `GetAllProducts(EmptyRequest) -> ProductListResponse`
- `UpdateProduct(UpdateProductRequest) -> ProductResponse`
- `DeleteProduct(ProductId) -> EmptyResponse`

### Messages

- `ProductId`: product identifier wrapper.
- `EmptyRequest` / `EmptyResponse`: empty wrappers for operations without payload.
- `CreateProductRequest`: input for product creation.
- `UpdateProductRequest`: input for full product updates.
- `ProductResponse`: product representation returned by the service.
- `ProductListResponse`: wrapper containing `repeated ProductResponse products`.

### Consumer notes

- `creationDate` is exposed as a string and mapped from the server-side `LocalDateTime`.

---

## Testing

- Unit tests (Mockito) are under `src/test/java` for service-layer logic.
- Integration tests use `@QuarkusTest` and RestAssured (`ProductResourceTest`).

Run all tests with:

```powershell
mvn test
```

If integration tests expect a stable product id, add a test seed file at `src/test/resources/import.sql` with an insert for a known id. Example:

```sql
INSERT INTO "PRODUCTS" (id, active, created_at, description, name, price, quantity, tags)
VALUES ('11111111-1111-1111-1111-111111111111', TRUE, CURRENT_TIMESTAMP(), 'Sample product', 'Demo Product', 19.99, 5, 'demo');
```

otherwise tests will rely on the existing `src/main/resources/import.sql` data.

---

## Design decisions and trade-offs

- gRPC-first business core
  - All the business logic is implemented in a gRPC service, which is in charge of handling transactional behaviours and execute all the CRUD operationss.
  - Trade-off: requires generated stubs and an extra call hop for REST clients.

- REST facade
  - Keeps HTTP mapping, validation and error translation separate from core logic.
  - Trade-off: additional mapping code and potential duplication of DTO definitions.

- Error handling
  - gRPC errors (`StatusRuntimeException`) are mapped to HTTP codes using a
	JAX-RS `ExceptionMapper` that returns `APIResponseDTO` for consistency.

- Transactions and reactive types
  - Methods annotated `@Transactional` return Mutiny `Uni`. Response objects
	are constructed eagerly inside the transaction to avoid detached/lazy loading issues.

- Validation
  - REST DTOs are validated with Jakarta Bean Validation; invalid requests are
	translated to `INVALID_ARGUMENT` gRPC code mapped to HTTP 400.