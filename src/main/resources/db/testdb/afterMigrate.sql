
DELETE FROM USERS_ROLES;
DELETE FROM USERS;

INSERT INTO USERS (user_id, username, email, cpf, password, user_type, full_name, phone_number) VALUES
    ('e53b4d24-6b49-4b7e-9f0b-69f77d4d64b8', 'admintest', 'admin@example.com', '008.655.700-94', '$2a$10$QiJx3zNwT6/1AcApSpetN.oirQP1utkiQlKk/3/SNM6.hedLfQZFW', 'ADMIN', 'Maciel Viana Admin', '+55 11 999999999'),
    ('e736ba38-1927-4b7f-ad87-74815fbeb7d7', 'vianatest', 'viana@jgmail.com', '680.965.590-52', '$2a$10$QiJx3zNwT6/1AcApSpetN.oirQP1utkiQlKk/3/SNM6.hedLfQZFW', 'STUDENT', 'Matos Viana Pereira', '0859922335544');

INSERT INTO USERS_ROLES (user_id, role_id) VALUES ('e53b4d24-6b49-4b7e-9f0b-69f77d4d64b8',  '24924ef3-15ca-44e7-b615-d199b8506f65');
INSERT INTO USERS_ROLES (user_id, role_id) VALUES ('e736ba38-1927-4b7f-ad87-74815fbeb7d7',  'd831507e-37db-48f3-aab7-1a53cca2053f');