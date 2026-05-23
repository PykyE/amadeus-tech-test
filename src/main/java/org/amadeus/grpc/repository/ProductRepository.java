package org.amadeus.grpc.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.amadeus.grpc.entity.ProductEntity;

@ApplicationScoped
public class ProductRepository implements PanacheRepositoryBase<ProductEntity, String> {
}
