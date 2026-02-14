CREATE TABLE IF NOT EXISTS roles (
                                     id SERIAL PRIMARY KEY,
                                     role_name VARCHAR(50) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS users (
                                     id SERIAL PRIMARY KEY,
                                     username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role_id INT NOT NULL,
    FOREIGN KEY (role_id) REFERENCES roles(id)
    );

CREATE TABLE IF NOT EXISTS products (
                                        id SERIAL PRIMARY KEY,
                                        name VARCHAR(255) NOT NULL,
    category VARCHAR(255) NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    quantity INT NOT NULL DEFAULT 0
    );

CREATE TABLE IF NOT EXISTS account (
                                       id SERIAL PRIMARY KEY,
                                       balance NUMERIC(15, 2) NOT NULL DEFAULT 0.00
    );

CREATE TABLE IF NOT EXISTS orders (
                                      id SERIAL PRIMARY KEY,
                                      user_id INT NOT NULL,
                                      total_price NUMERIC(15, 2) NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
    );

INSERT INTO roles (role_name)
VALUES ('ADMIN'), ('CLIENT')
    ON CONFLICT (role_name) DO NOTHING;

INSERT INTO account (id, balance)
SELECT 1, 10000.00
    WHERE NOT EXISTS (SELECT 1 FROM account WHERE id = 1);