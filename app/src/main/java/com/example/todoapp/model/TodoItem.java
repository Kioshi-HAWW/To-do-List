package com.example.todoapp.model;

import java.io.Serializable;

public class TodoItem implements Serializable {
    private int id;
    private String title;
    private String description;
    private boolean isCompleted;
    private long dueDate; // Timestamp
    private boolean hasReminder;

    public TodoItem() {
    }

    public TodoItem(String title, String description, boolean isCompleted, long dueDate, boolean hasReminder) {
        this.title = title;
        this.description = description;
        this.isCompleted = isCompleted;
        this.dueDate = dueDate;
        this.hasReminder = hasReminder;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public boolean hasReminder() {
        return hasReminder;
    }

    public void setHasReminder(boolean hasReminder) {
        this.hasReminder = hasReminder;
    }
}
