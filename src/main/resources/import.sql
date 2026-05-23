CREATE SCHEMA IF NOT EXISTS public;

CREATE TABLE IF NOT EXISTS "PRODUCTS" (id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                             name VARCHAR(255),
                             price FLOAT(53),
                             description VARCHAR(255),
                             quantity INTEGER,
                             created_at TIMESTAMP(6),
                             tags VARCHAR(255),
                             active BOOLEAN NOT NULL);

INSERT INTO "PRODUCTS" (id, active, created_at, description, name, price, quantity, tags)
    VALUES ('11111111-1111-1111-1111-111111111111', true, current_timestamp(), 'Sample product', 'Product #1', 19.99, 5, 'demo');
