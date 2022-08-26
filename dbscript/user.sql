ALTER TABLE `user`
ALTER COLUMN CreateOn SET DEFAULT "2000-01-02";

SELECT CURRENT_TIMESTAMP;

insert into user (id,name,email,role,createdOn,updatedOn) values (3,"test1","test1@gmail.com","student",0,0);
insert into user (id,name,email,role) values (2,"สหทัศน์","sahatat@gmail.com","student");

update user set name = "tests" where id = 1; 

select * from user;

drop table user;

CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `email` varchar(50) NOT NULL,
  `role` varchar(10) NOT NULL,
  `createdOn` datetime not NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedOn` datetime not NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNIQUE` (`name`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

select now();
set global time_zone = "+7:00";
