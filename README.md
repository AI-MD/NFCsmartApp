Smart Attendance Book  Database
=============================================

ETC
MySQL 콘솔창에서 한글이 깨져 보일경우
---------------------------------------------
set names euckr;


1. MySQL에 존재하는 데이터베이스 확인
---------------------------------------------
show databases;


2. 데이터베이스 만들기
---------------------------------------------
create database smart_attbook DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;


3. 사용할 데이터베이스 선택
---------------------------------------------
use smart_attbook;



4. students 테이블 생성
---------------------------------------------
create table students (
	STUDENT_NUMBER INTEGER(9) not null primary key,
	NAME varchar(40),
	PHONE_NUMBER varchar(20),
	ADDRESS varchar(100),
	PICTURE varchar(100)
);

create table professor (
	PROFESSOR_ID INTEGER(10) not null primary key,
	NAME varchar(40),
	PHONE_NUMBER varchar(20),
	ADDRESS varchar(100),
	PICTURE varchar(100)
);

create table lecture (
	LECTURE_ID INTEGER(10) not null primary key,
	LECTURE_CODE INTEGER(10),
	NAME varchar(40),
	PROFESSOR_ID INTEGER(10),
	WEEK varchar(10),
	START_TIME varchar(10),
	END_TIME varchar(10),
	PLACE varchar(20)
);

create table taking_lectures (
	TAKING_LECTURES_ID INTEGER(10) not null primary key,
	STUDENT_NUMBER INTEGER(9),
	LECTURE_ID INTEGER(10)
);

create table attendance (
	ATTENDANCE_ID INTEGER(10) not null primary key,
	LECTURE_ID INTEGER(10),
	JOIN_STUDENTS varchar(1000)
);


5-1. 파일로 데이터 입력할 경우 UTF-8로 입력 설정
---------------------------------------------
set names utf8;

5-2. SQL File 실행
---------------------------------------------
\. d:\students.sql [Enter] (저장시 utf-8로 저장해야만 합니다.)

insert into students values (201240229, "강아지", "010-6404-0000", "서울시 양천구", "");
insert into students values (201340222, "홍길동", "010-6289-0000", "서울시 금천구", "");



attendance 테이블의 JOIN_STUDENTS 필드 구조
구분기호 ','


201240113(박정환 결석), 201240229(강아지 출석), 201340222(홍길동 출석)

처음은 공백

태훈이 출석함
201240229

태훈이, 병남이 출석함
201240229_0, 201340222
