CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,
    color VARCHAR(20),
    user_id UUID,
    is_default BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL -- Se o usuário for deletado, a categoria personalizada fica sem dono
);