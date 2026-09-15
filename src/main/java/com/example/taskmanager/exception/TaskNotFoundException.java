package com.example.taskmanager.exception;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(Long id) {
        super("Task with id " + id + " was not found");
    }

    public TaskNotFoundException(String message) {
        super(message);
    }
}
