package org.amadeus.grpc.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "PRODUCTS")
public class ProductEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue
    public String id;
    public String name;
    public Double price;
    public String description;
    public Integer quantity;
    @Column(name = "created_at")
    public LocalDateTime creationDate;
    public String tags;
    public boolean active;
}
