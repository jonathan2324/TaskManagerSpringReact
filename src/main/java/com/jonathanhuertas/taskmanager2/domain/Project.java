package com.jonathanhuertas.taskmanager2.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
public class Project {


    @Id//this will be stored as project_id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Project name is required")
    private String projectName;

    @NotBlank(message = "Project identifier is required")
    @Size(min = 4, max = 5, message = "Please use 4 to 5 characters")
    @Column(updatable = false, unique = true) //@Column sets validation at database level-> happens after the validation service
    private String projectIdentifier;

    @NotBlank(message = "Project description is required")
    private String description;

    @JsonFormat(pattern  = "yyyy-MM-dd")
    private Date start_date;
    @JsonFormat(pattern  = "yyyy-MM-dd")
    private Date end_date;
    @JsonFormat(pattern  = "yyyy-MM-dd")
    @Column(updatable = false)
    private Date created_At;
    @JsonFormat(pattern  = "yyyy-MM-dd")
    private Date updated_At;

    //Fetch-> Backlog data readily available, cascade all means project is the owning side of the relationship
    //FetechType.EAGER-> we will receive access to the full list of project tasks immediately with the rest of the fields for this project object
    //CacaseType.ALL -> if we delete the project, it will delete the backlog and the project tasks
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "project") //mapped by the field name "project" located on backlog for this project
    @JsonIgnore//use this to break infinite recursion
    private Backlog backlog;

    public Project() {
    }

    @ManyToOne(fetch = FetchType.LAZY)//
    @JsonIgnore//avoid infinite recursion
    private User user;

    private String projectLeader;



    //every time we create a new object we will store the current date -> should only happen once
    @PrePersist
    protected void onCreate(){
        this.created_At = new Date();
    }

    //every time the object is updated, we will store the current date
    @PreUpdate
    protected void onUpdate(){
        this.updated_At = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectIdentifier() {
        return projectIdentifier;
    }

    public void setProjectIdentifier(String projectIdentifier) {
        this.projectIdentifier = projectIdentifier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public Date getCreated_At() {
        return created_At;
    }

    public void setCreated_At(Date created_At) {
        this.created_At = created_At;
    }

    public Date getUpdated_At() {
        return updated_At;
    }

    public void setUpdated_At(Date updated_At) {
        this.updated_At = updated_At;
    }

    public String getProjectLeader() {
        return projectLeader;
    }

    public void setProjectLeader(String projectLeader) {
        this.projectLeader = projectLeader;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Backlog getBacklog() {
        return backlog;
    }

    public void setBacklog(Backlog backlog) {
        this.backlog = backlog;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", projectName='" + projectName + '\'' +
                ", projectIdentifier='" + projectIdentifier + '\'' +
                ", description='" + description + '\'' +
                ", start_date=" + start_date +
                ", end_date=" + end_date +
                ", created_At=" + created_At +
                ", updated_At=" + updated_At +
                ", backlog=" + backlog +
                '}';
    }
}
