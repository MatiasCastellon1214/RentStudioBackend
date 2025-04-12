ALTER TABLE users
    ADD phone VARCHAR(255);

ALTER TABLE users
    ADD CONSTRAINT uc_users_phone UNIQUE (phone);

ALTER TABLE categories_features
    ADD CONSTRAINT fk_catfea_on_product_category FOREIGN KEY (category_id) REFERENCES categories (id);

ALTER TABLE categories_policies
    ADD CONSTRAINT fk_catpol_on_category_policy FOREIGN KEY (policy_id) REFERENCES category_policies (id);

ALTER TABLE categories_policies
    ADD CONSTRAINT fk_catpol_on_product_category FOREIGN KEY (category_id) REFERENCES categories (id);
