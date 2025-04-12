ALTER TABLE products_features
DROP
CONSTRAINT fk_proprofea_on_product_feature;

ALTER TABLE products_features
    ADD features_id BIGINT;

ALTER TABLE products_features
    ADD CONSTRAINT fk_profea_on_product_feature FOREIGN KEY (features_id) REFERENCES product_features (id);

ALTER TABLE products_features
DROP
COLUMN product_features_id;

ALTER TABLE products_features
    ADD CONSTRAINT pk_products_features PRIMARY KEY (features_id, product_id);