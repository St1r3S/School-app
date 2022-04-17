package com.example.dao.mappers;

import com.example.model.Course;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.example.model.Course.*;

public class CourseMapper implements Mapper<Course> {
    @Override
    public Course apply(ResultSet rs) throws SQLException {
        return new Course(rs.getLong(COURSE_ID), rs.getString(COURSE_NAME), rs.getString(COURSE_DESCRIPTION));
    }
}
