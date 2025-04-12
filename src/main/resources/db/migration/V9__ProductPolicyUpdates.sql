ALTER TABLE products
DROP
CONSTRAINT fk_products_on_policy;

CREATE TABLE products_policies
(
    policy_id  BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    CONSTRAINT pk_products_policies PRIMARY KEY (policy_id, product_id)
);

ALTER TABLE products_policies
    ADD CONSTRAINT fk_propol_on_product FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE products_policies
    ADD CONSTRAINT fk_propol_on_product_policy FOREIGN KEY (policy_id) REFERENCES product_policies (id);

ALTER TABLE products
DROP
COLUMN policy_id;