package com.jonathanhuertas.taskmanager2.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/*
We are using a backlog as an architectural design because this will allow the front end to obtain the project information
without having to get all the tasks for the project every time we only need the project information
this saves the amount of information returned to the client making the application more efficient
 */

@Entity
public class Backlog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //set to zero because every project will have its own sequence for projectTasks
    private Integer PTSequence = 0;

    private String projectIdentifier;

    //OneToOne with project
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id", nullable = false)//this means we will have a column named project_id which will reference the project this backlog is assigned to
    @JsonIgnore //need this to avoid infinite recursion
    private Project project; //-> this project id will be stored as project_id

    //OneToMany Backlog will be owning side
    //deleting backlog, delete project tasks
    //orphanRemoval -> when child entity is no longer referenced from the parent, then the child will go away as well
    @OneToMany(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER, mappedBy = "backlog", orphanRemoval = true)
    private List<ProjectTask> projectTasks = new ArrayList<>();

    public Backlog() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPTSequence() {
        return PTSequence;
    }

    public void setPTSequence(Integer PTSequence) {
        this.PTSequence = PTSequence;
    }

    public String getProjectIdentifier() {
        return projectIdentifier;
    }

    public void setProjectIdentifier(String projectIdentifier) {
        this.projectIdentifier = projectIdentifier;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<ProjectTask> getProjectTasks() {
        return projectTasks;
    }

    public void setProjectTasks(List<ProjectTask> projectTasks) {
        this.projectTasks = projectTasks;
    }
}
