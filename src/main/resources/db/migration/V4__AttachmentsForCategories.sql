ALTER TABLE product_categories
DROP
CONSTRAINT fk9vx5742j662qgfjy53vaw1csd;

ALTER TABLE attachments
    ADD product_category_id BIGINT;

ALTER TABLE attachments
    ADD CONSTRAINT uc_attachments_productcategory UNIQUE (product_category_id);

ALTER TABLE attachments
    ADD CONSTRAINT FK_ATTACHMENTS_ON_PRODUCTCATEGORY FOREIGN KEY (product_category_id) REFERENCES product_categories (id);

ALTER TABLE product_categories
DROP
COLUMN attachments_id;

ALTER TABLE product_categories
ALTER
COLUMN description TYPE VARCHAR(300) USING (description::VARCHAR(300));

ALTER TABLE product_categories
ALTER
COLUMN name TYPE VARCHAR(30) USING (name::VARCHAR(30));