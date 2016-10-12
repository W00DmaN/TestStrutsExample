CREATE TABLE IF NOT EXISTS role (
id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
name VARCHAR(100) NOT NULL);

CREATE TABLE IF NOT EXISTS users (
id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
username VARCHAR(100) NOT NULL UNIQUE,
password VARCHAR(100) NOT NULL,
firstName VARCHAR(100) NOT NULL,
lastName VARCHAR(100) NOT NULL,
role INT NOT NULL,
FOREIGN KEY (role) REFERENCES role(id) ON DELETE CASCADE ON UPDATE CASCADE);