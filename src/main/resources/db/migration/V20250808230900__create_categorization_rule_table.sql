CREATE TABLE categorization_rules (
    id BIGSERIAL PRIMARY KEY,
    keyword VARCHAR(255) NOT NULL,
    category_id BIGINT NOT NULL,
    user_id UUID NOT NULL,
    priority INTEGER NOT NULL,
    CONSTRAINT fk_categorization_rules_category FOREIGN KEY (category_id)
        REFERENCES categories (id),
    CONSTRAINT fk_categorization_rules_user FOREIGN KEY (user_id)
        REFERENCES users (id)
);
