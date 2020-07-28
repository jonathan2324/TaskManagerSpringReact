package com.jonathanhuertas.taskmanager2.services;

import com.jonathanhuertas.taskmanager2.domain.Backlog;
import com.jonathanhuertas.taskmanager2.domain.Project;
import com.jonathanhuertas.taskmanager2.domain.User;
import com.jonathanhuertas.taskmanager2.exceptions.ProjectIdException;
import com.jonathanhuertas.taskmanager2.exceptions.ProjectNotFoundException;
import com.jonathanhuertas.taskmanager2.repositories.BacklogRepository;
import com.jonathanhuertas.taskmanager2.repositories.ProjectRepository;
import com.jonathanhuertas.taskmanager2.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
Here we connect to the CRUD Repository so that we have access to query the database
we use this file for logic and query the database to return the data to the controller
In the repository, we get access to basic crud operations, we added a few methods such as findByProjectIdentifier
but we have access to save(), delete() out of the box due to JPA
 */

@Service
public class ProjectService {

    //wire up project repository-CRUD operations
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private UserRepository userRepository;


    public Project saveOrUpdateProject(Project project, String username){
        String projectIdentifierUpper = project.getProjectIdentifier().toUpperCase();

        //check to see if the project id is not null which  means its an update
        if(project.getId() != null){
            Project existingProject = projectRepository.findByProjectIdentifier(project.getProjectIdentifier());

            //if the project exists but the project leader is not the username passed in, throw exception
            if(existingProject != null && (!existingProject.getProjectLeader().equals(username))){
                throw new ProjectNotFoundException("Project not found in your account");
            } else if (existingProject == null){
                throw new ProjectNotFoundException("Project with ID: " + project.getProjectIdentifier() + " cannot be updated because it does not exist");
            }
        }

        try{


            User user = userRepository.findByUsername(username);

            project.setUser(user);//sets the OneToMany relationship
            project.setProjectLeader(user.getUsername());
            project.setProjectIdentifier(projectIdentifierUpper);

            //if the id coming in is null, this means its a new project -> must set a new backlog upon creation
            //if it contains an id, it will be an update
            if(project.getId() == null){
                Backlog backlog = new Backlog();
                project.setBacklog(backlog);
                backlog.setProject(project);
                backlog.setProjectIdentifier(projectIdentifierUpper);
            }

            //if it is an update, we will set the backlog with the existing backlog for that project with the specific id
            if(project.getId() != null){
                project.setBacklog(backlogRepository.findByProjectIdentifier(projectIdentifierUpper));
            }
            return projectRepository.save(project); //save comes with CRUD Repository JPA

        } catch(Exception err){
            //we have to throw an exception here because @Column errors are not caught by validation
            //they occur at the database level so we must throw this custom exception for projectIdentifier that is already used
            //this will occur if a user tries to save a project that already uses the specific projectIdentifier attempting to be saved
            throw new ProjectIdException("Project ID " + projectIdentifierUpper + " already exists");
        }
    }

    public Project findProjectByIdentifier(String projectId, String username){

        Project project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());
        if(project == null){
            //the ProjectExceptionResponse object is returned with the message below in JSON format to client
            throw new ProjectIdException("Project ID " + projectId + " does not exists");
        }

        //error if you are not the creator of the project
        if(!project.getProjectLeader().equals(username)){
            throw new ProjectNotFoundException("Project not found in your account");
        }

        return project;
    }

    //iterable-> something that returns a list, basic way to extract is to traverse
    //this iterable returns an array of JSON format projects
    public Iterable<Project> findAllProjects(String username){
        return projectRepository.findAllByProjectLeader(username);
    }

    public void deleteProjectByIdentifier(String projectId, String username){

        projectRepository.delete(findProjectByIdentifier(projectId, username));
    }

}
