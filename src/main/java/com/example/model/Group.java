package com.example.model;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@ToString
public class Group extends LongEntity {
    public static final String GROUP_ID = "group_id";
    public static final String GROUP_NAME = "group_name";

    private String name;

    public Group(Long id, String name) {
        super(id);
        this.name = name;
    }

    public Group(String name) {
        this(null, name);
    }

}
