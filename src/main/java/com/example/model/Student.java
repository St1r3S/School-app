package com.example.model;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@ToString
public class Student extends LongEntity {
    public static final String STUDENT_ID = "student_id";
    public static final String STUDENT_GROUP_ID = "group_id";
    public static final String STUDENT_FIRST_NAME = "first_name";
    public static final String STUDENT_LAST_NAME = "last_name";

    private String firstName;
    private String lastName;
    private Long groupId;

    public Student(Long id, Long groupId, String firstName, String lastName) {
        super(id);
        this.groupId = groupId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Student(Long groupId, String firstName, String lastName) {
        this(null, groupId, firstName, lastName);
    }
    
}
