INSERT INTO users (username, email, password, created_at) VALUES
('admin', 'admin@aulab.it', '25f9e794323b453885f5181f1b624d0b');



INSERT INTO roles (name) VALUES 
('ROLE_ADMIN'),
('ROLE_REVISOR'),
('ROLE_WRITER'),
('ROLE_USER');


INSERT INTO users_roles (user_id, role_id) values 
(1,1);


INSERT INTO categories (name) values 
('politica'),
('economia'),
('food&drink'),
('business'),
('sport'),
('intrattenimento'),
('tech');

