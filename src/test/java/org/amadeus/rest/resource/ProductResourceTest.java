package org.amadeus.rest.resource;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestHTTPEndpoint(ProductResource.class)
@DisplayName("ProductResource REST API Tests")
class ProductResourceTest {

    @Test
    @DisplayName("GET /products - should return list of all products")
    void testGetAllProducts() {
        given()
                .when()
                .get()
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("grpcCode", equalTo("OK"))
                .body("data", notNullValue())
                .body("data", instanceOf(java.util.List.class));
    }

    @Test
    @DisplayName("GET /products/{id} - should return product when found")
    void testGetProductById_Success() {
        given()
                .when()
                .get("/11111111-1111-1111-1111-111111111111")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("grpcCode", equalTo("OK"))
                .body("data.id", equalTo("11111111-1111-1111-1111-111111111111"))
                .body("data.name", notNullValue())
                .body("data.price", notNullValue());
    }

    @Test
    @DisplayName("GET /products/{id} - should return 404 when product not found")
    void testGetProductById_NotFound() {
        given()
                .when()
                .get("/123")
                .then()
                .statusCode(404)
                .contentType(ContentType.JSON)
                .body("grpcCode", equalTo("NOT_FOUND"))
                .body("message", notNullValue())
                .body("data", nullValue());
    }

    @Test
    @DisplayName("POST /products - should create product with valid data")
    void testCreateProduct_Success() {
        String requestBody = """
                {
                    "name": "New Test Product",
                    "price": 29.99,
                    "description": "A brand new test product",
                    "quantity": 50,
                    "tags": "test,new"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post()
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("grpcCode", equalTo("OK"))
                .body("data.id", notNullValue())
                .body("data.name", equalTo("New Test Product"))
                .body("data.price", equalTo(29.99f))
                .body("data.active", equalTo(true));
    }

    @Test
    @DisplayName("POST /products - should return 400 when name is missing")
    void testCreateProduct_MissingName() {
        String requestBody = """
                {
                    "price": 29.99,
                    "description": "Missing name",
                    "quantity": "100",
                    "tags": "test"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post()
                .then()
                .statusCode(400)
                .body("grpcCode", equalTo("INVALID_ARGUMENT"))
                .body("message", equalTo("product name is required"));
    }

    @Test
    @DisplayName("POST /products - should return 400 when price is negative")
    void testCreateProduct_NegativePrice() {
        String requestBody = """
                {
                    "name": "Test Product",
                    "price": -5.00,
                    "description": "Negative price",
                    "quantity": "100",
                    "tags": "test"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post()
                .then()
                .statusCode(400)
                .body("grpcCode", equalTo("INVALID_ARGUMENT"))
                .body("message", equalTo("product price must be greater than 0"));
    }

    @Test
    @DisplayName("PUT /products/{id} - should update product with valid data")
    void testUpdateProduct_Success() {
        String requestBody = """
                {
                    "name": "Updated Product Name",
                    "price": 39.99,
                    "description": "Updated description",
                    "quantity": 100,
                    "tags": "updated",
                    "active": true
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/11111111-1111-1111-1111-111111111111")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("grpcCode", equalTo("OK"))
                .body("data.id", equalTo("11111111-1111-1111-1111-111111111111"))
                .body("data.name", equalTo("Updated Product Name"))
                .body("data.price", equalTo(39.99f));
    }

    @Test
    @DisplayName("PUT /products/{id} - should return 404 when product not found")
    void testUpdateProduct_NotFound() {
        String requestBody = """
                {
                    "name": "Updated Product Name",
                    "price": 39.99,
                    "description": "Updated description",
                    "quantity": 100,
                    "tags": "updated",
                    "active": true
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/123")
                .then()
                .statusCode(404)
                .contentType(ContentType.JSON)
                .body("grpcCode", equalTo("NOT_FOUND"));
    }

    @Test
    @DisplayName("PUT /products/{id} - should return 400 when data is invalid")
    void testUpdateProduct_InvalidData() {
        String requestBody = """
                {
                    "name": "Product test update",
                    "price": "-10",
                    "description": "Product test update description",
                    "quantity": "10",
                    "tags": "test",
                    "active": false
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/1")
                .then()
                .statusCode(400)
                .body("grpcCode", equalTo("INVALID_ARGUMENT"))
                .body("message", equalTo("product price must be greater than 0"));
    }

    @Test
    @TestTransaction
    @DisplayName("DELETE /products/{id} - should delete product successfully")
    void testDeleteProduct_Success() {
        given()
                .when()
                .delete("/11111111-1111-1111-1111-111111111111")
                .then()
                .statusCode(200)
                .body("grpcCode", equalTo("OK"));

        given()
                .when()
                .get("/11111111-1111-1111-1111-111111111111")
                .then()
                .statusCode(404)
                .body("grpcCode", equalTo("NOT_FOUND"));
    }

    @Test
    @DisplayName("DELETE /products/{id} - should return 404 when product not found")
    void testDeleteProduct_NotFound() {
        given()
                .when()
                .delete("/123")
                .then()
                .statusCode(404)
                .contentType(ContentType.JSON)
                .body("grpcCode", equalTo("NOT_FOUND"));
    }

}

