ALTER TABLE `user`
ALTER COLUMN CreateOn SET DEFAULT "2000-01-02";

SELECT CURRENT_TIMESTAMP;

insert into user (id,name,email,role) values (1,"test","test@gmail.com","student");
insert into user (id,name,email,role) values (2,"สหทัศน์","sahatat@gmail.com","student");

update user set name = "tests" where id = 1; 

select * from user;

drop table user;

CREATE TABLE `user` (
  `id` int NOT NULL,
  `name` varchar(100) NOT NULL,
  `email` varchar(50) NOT NULL,
  `role` varchar(10) NOT NULL,
  `createdOn` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateOn` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `_UNIQUE` (`name`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


