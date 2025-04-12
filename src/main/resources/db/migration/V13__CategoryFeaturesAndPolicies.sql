ALTER TABLE products_features
    RENAME TO categories_features;

ALTER TABLE product_features
    RENAME TO categories_policies;

ALTER TABLE products_policies
    RENAME TO category_features;
