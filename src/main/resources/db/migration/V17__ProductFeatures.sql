CREATE TABLE products_features
(
    feature_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    CONSTRAINT pk_products_features PRIMARY KEY (feature_id, product_id)
);

ALTER TABLE products_features
    ADD CONSTRAINT fk_profea_on_category_feature FOREIGN KEY (feature_id) REFERENCES category_features (id);

ALTER TABLE products_features
    ADD CONSTRAINT fk_profea_on_product FOREIGN KEY (product_id) REFERENCES products (id);
