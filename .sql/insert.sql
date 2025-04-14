INSERT INTO users (firstname, lastname,username, email, password, created_at) VALUES
('Admin','Admin','admin', 'admin@aulab.it', '$2y$10$pyVFAecNHZcK/3Yy7IBOAuhCT7vG21Y8JESp.zNdvGb0u9lTqHJ4W', "20240101");



INSERT INTO roles (name) VALUES 
('ROLE_ADMIN'),
('ROLE_REVISOR'),
('ROLE_WRITER'),
('ROLE_USER');


INSERT INTO users_roles (user_id, role_id) values 
(1,1);


INSERT INTO categories (name) values 
('Politica'),
('Economia'),
('Food&Drink'),
('Business'),
('Sport'),
('Intrattenimento'),
('Tech');

