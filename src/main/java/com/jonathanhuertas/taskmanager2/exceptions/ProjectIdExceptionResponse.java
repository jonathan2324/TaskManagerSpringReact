package com.jonathanhuertas.taskmanager2.exceptions;

/*
This is how we handle the exception Response
 */
public class ProjectIdExceptionResponse {

    private String projectIdentifier;

    public ProjectIdExceptionResponse(String projectIdentifier) {
        this.projectIdentifier = projectIdentifier;
    }

    public String getProjectIdentifier() {
        return projectIdentifier;
    }

    public void setProjectIdentifier(String projectIdentifier) {
        this.projectIdentifier = projectIdentifier;
    }

    @Override
    public String toString() {
        return "ProjectIdExceptionResponse{" +
                "projectIdentifier='" + projectIdentifier + '\'' +
                '}';
    }
}
