ALTER TABLE products_reservations
DROP
CONSTRAINT fk_prores_on_product;

ALTER TABLE products_reservations
DROP
CONSTRAINT fk_prores_on_reservation;

ALTER TABLE reservations
    ADD amount DOUBLE PRECISION;

ALTER TABLE reservations
    ADD cancelled BOOLEAN;

ALTER TABLE reservations
    ADD product_id BIGINT;

ALTER TABLE reservations
    ALTER COLUMN amount SET NOT NULL;

ALTER TABLE reservations
    ALTER COLUMN cancelled SET NOT NULL;

ALTER TABLE reservations
    ALTER COLUMN product_id SET NOT NULL;

ALTER TABLE reservations
    ADD CONSTRAINT FK_RESERVATIONS_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);

DROP TABLE products_reservations CASCADE;

ALTER TABLE reservations
DROP
COLUMN payment_type;
