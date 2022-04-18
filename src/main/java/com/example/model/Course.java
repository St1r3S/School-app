package com.example.model;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@ToString
public class Course extends LongEntity {
    public static final String COURSE_ID = "course_id";
    public static final String COURSE_NAME = "course_name";
    public static final String COURSE_DESCRIPTION = "course_description";

    private String name;
    private String description;

    public Course(Long id, String name, String description) {
        super(id);
        this.name = name;
        this.description = description;
    }

    public Course(String name, String description) {
        this(null, name, description);
    }
}