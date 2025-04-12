ALTER TABLE product_category
    ADD attachment_id BIGINT;

ALTER TABLE product_category
    ADD CONSTRAINT FK_PRODUCT_CATEGORY_ON_ATTACHMENT FOREIGN KEY (attachment_id) REFERENCES attachments (id);