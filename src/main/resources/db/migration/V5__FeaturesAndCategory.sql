ALTER TABLE attachments
DROP
CONSTRAINT fk_attachments_on_productcategory;

ALTER TABLE product_features
DROP
CONSTRAINT fkd2x1ygsefpn2v5gjett1skhsr;

ALTER TABLE product_categories
    RENAME TO product_category;

CREATE TABLE products_product_features
(
    product_features_id BIGINT NOT NULL,
    product_id          BIGINT NOT NULL,
    CONSTRAINT pk_products_productfeatures PRIMARY KEY (product_features_id, product_id)
);

ALTER TABLE products_product_features
    ADD CONSTRAINT fk_proprofea_on_product FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE products_product_features
    ADD CONSTRAINT fk_proprofea_on_product_feature FOREIGN KEY (product_features_id) REFERENCES product_features (id);

ALTER TABLE attachments
DROP
COLUMN product_category_id;

ALTER TABLE product_features
DROP
COLUMN product_id;

ALTER TABLE products
    ALTER COLUMN category_id SET NOT NULL;

ALTER TABLE product_features
ALTER
COLUMN description TYPE VARCHAR(300) USING (description::VARCHAR(300));

ALTER TABLE product_features
ALTER
COLUMN name TYPE VARCHAR(30) USING (name::VARCHAR(30));