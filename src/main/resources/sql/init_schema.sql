drop table if exists students_courses;
drop table if exists students;
drop table if exists groups;
drop table if exists courses;

create table groups
(
    group_id   serial PRIMARY KEY,
    group_name text unique
);
create table students
(
    student_id serial PRIMARY KEY,
    group_id   int references groups (group_id),
    first_name text,
    last_name  text

);
create table courses
(
    course_id          serial PRIMARY KEY,
    course_name        text unique,
    course_description text unique
);
CREATE TABLE students_courses
(
    student_id INT NOT NULL,
    course_id  INT NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students (student_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses (course_id) ON DELETE CASCADE
);