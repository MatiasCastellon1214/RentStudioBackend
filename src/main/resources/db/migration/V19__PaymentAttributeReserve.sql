ALTER TABLE reservations
    ADD payment VARCHAR(255);

ALTER TABLE reservations
    ALTER COLUMN payment SET NOT NULL;
