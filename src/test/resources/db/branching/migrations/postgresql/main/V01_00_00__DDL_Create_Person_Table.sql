create table PERSON (
    ID SERIAL not null primary key,
    FIRSTNAME varchar(100) not null,
    LASTNAME varchar(100) not null,
    GENDER varchar(10) not null,
    BIRTHDATE date
);