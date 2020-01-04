CREATE DATABASE `sage` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER `sage`@localhost IDENTIFIED BY '1234';
GRANT ALL ON `sage`.* TO `sage`@localhost;