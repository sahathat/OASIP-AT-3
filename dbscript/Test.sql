-- category
insert into Category values (1,'Project Management Clinic','ตารางนัดหมายนี้ใช้สำหรับนัดหมาย project management clinic ในวิชา INT221 integrated project I ให้นักศึกษาเตรียมเอกสารที่เกี่ยวข้องเพื่อแสดงระหว่างขอคำปรึกษา',30);
insert into Category values (2,'DevOps/Infra Clinic','Use this event category for DevOps/Infra clinic.',20);
insert into Category values (3,'Database Clinic','ตารางนัดหมายนี้ใช้สำหรับนัดหมาย database clinic ในวิชา INT221 integrated project I',15);
insert into Category values (4,'Client-side Clinic','ตารางนัดหมายนี้ใช้สำหรับนัดหมาย client-side clinic ในวิชา INT221 integrated project I',30);
insert into Category values (5,'Server-side Clinic',null,30);

-- bookingTimeuser
insert into Event values (1,'Somchai Jaidee (OR-7)','somchai.jai@mail.kmutt.ac.th',2,'2022-05-23 13:30:00',30,null);
insert into Event values (2,'Somsri Rakdee (SJ-3)','somsri.rak@mail.kmutt.ac.th',1,'2022-04-27 9:30:00',30,'ขอปรึกษาปัญหาเพื่อนไม่ช่วยงาน');
insert into Event values (3,'สมเกียรติ ขยันเรียน กลุ่ม TT-4','somkiat.kay@kmutt.ac.th',3,'2022-05-23 16:30:00',15,null);

-- lecturerMapping
insert into Lecturer_Mapping values(1,'olarn.roj@kmutt.ac.th',1);
insert into Lecturer_Mapping values(2,'olarn.roj@kmutt.ac.th',2);
insert into Lecturer_Mapping values(3,'olarn.roj@kmutt.ac.th',5);
insert into Lecturer_Mapping values(4,'siam.yam@kmutt.ac.th',2);

-- user
-- need add email olarn.roj@kmutt.ac.th and siam.yam@kmutt.ac.th in lecturer role before he use owner

-- user confirm
-- add email for confirmation