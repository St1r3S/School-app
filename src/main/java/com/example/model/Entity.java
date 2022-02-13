package com.example.model;

public interface Entity<K> {
    K getId();

    void setId(K id);
}
