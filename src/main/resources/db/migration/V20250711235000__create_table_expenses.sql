CREATE TABLE expenses (
    id SERIAL PRIMARY KEY, -- Identificador único da despesa
    user_id UUID NOT NULL, -- Relaciona a despesa ao usuário
    category_id INT NOT NULL, -- Relaciona a despesa à categoria
    amount DECIMAL(10, 2) NOT NULL, -- Valor da despesa
    description TEXT, -- Descrição da despesa
    date DATE NOT NULL, -- Data da despesa
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Data de criação
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Data de atualização (será atualizado no backend)
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE, -- Chave estrangeira para usuários
    FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE CASCADE -- Chave estrangeira para categorias
);