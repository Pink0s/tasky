package com.tasky.api.controllers;

import com.tasky.api.dto.toDo.CreateToDoRequest;
import com.tasky.api.dto.toDo.SearchToDoResponse;
import com.tasky.api.dto.toDo.TodoDto;
import com.tasky.api.dto.toDo.UpdateTodoRequest;
import com.tasky.api.services.toDo.TodoService;
import jakarta.websocket.server.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class that handles HTTP requests related to To-Do tasks in the Tasky API.
 */
@RestController
@RequestMapping("/api/v1/toDo")
public class TodoController {
    private final Logger logger = LoggerFactory.getLogger(TodoController.class);
    private final TodoService todoService;

    /**
     * Constructor for TodoController.
     *
     * @param todoService The service responsible for handling To-Do task-related operations.
     */
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    /**
     * Handles POST requests to create a new To-Do task within a feature.
     *
     * @param authentication The authentication details of the user making the request.
     * @param featureId      The ID of the feature in which the To-Do task will be created.
     * @param request        The request body containing the information of the new To-Do task.
     */
    @PostMapping("feature/{featureId}")
    @ResponseStatus(HttpStatus.CREATED)
    void createTodo(Authentication authentication, @PathVariable Long featureId, @RequestBody CreateToDoRequest request) {
        logger.info("POST /api/v1/toDo/feature/%s".formatted(featureId));
        todoService.createTodo(authentication,featureId,request);
    }

    /**
     * Handles GET requests to retrieve a To-Do task by its ID.
     *
     * @param authentication The authentication details of the user making the request.
     * @param toDoId         The ID of the To-Do task to retrieve.
     * @return The To-Do task DTO corresponding to the given ID.
     */
    @GetMapping("{toDoId}")
    TodoDto getTodoById(Authentication authentication, @PathVariable Long toDoId) {
        logger.info("GET /api/v1/toDo/%s".formatted(toDoId));
        return todoService.findToDoById(authentication,toDoId);
    }

    /**
     * Handles GET requests to retrieve all To-Do tasks associated with the authenticated user's profile.
     *
     * @param authentication The authentication details of the user making the request.
     * @param page           The page number for pagination.
     * @param name           The search pattern for To-Do task names.
     * @return The search response containing a list of matching To-Do tasks and pagination information.
     */
    @GetMapping("profile")
    SearchToDoResponse getAllMyTodo(Authentication authentication, @PathParam("page") Integer page, @PathParam("name") String name) {
        logger.info("GET /api/v1/toDo/profile");
        return todoService.findTodosWhereUserIsAnNameContains(authentication,name,page);
    }

    /**
     * Handles GET requests to retrieve all To-Do tasks associated with a specific feature based on name pattern and page number.
     *
     * @param authentication The authentication details of the user making the request.
     * @param featureId      The ID of the feature for which to retrieve To-Do tasks.
     * @param page           The page number for pagination.
     * @param name           The search pattern for To-Do task names.
     * @return The search response containing a list of matching To-Do tasks and pagination information.
     */
    @GetMapping("feature/{featureId}")
    SearchToDoResponse getAllTodosWhereFeatureIsAndNameContains(Authentication authentication, @PathVariable Long featureId, @PathParam("page") Integer page, @PathParam("name") String name) {
        logger.info("GET /api/v1/toDo/feature/%s".formatted(featureId));
        return todoService.findTodosWhereFeatureIsAndNameContains(authentication,featureId,name,page);
    }

    /**
     * Handles PATCH requests to update a To-Do task by its ID.
     *
     * @param authentication The authentication details of the user making the request.
     * @param toDoId         The ID of the To-Do task to update.
     * @param request        The request body containing the updated To-Do task information.
     */
    @PatchMapping("{toDoId}")
    void updateTodoById(Authentication authentication, @PathVariable Long toDoId, @RequestBody UpdateTodoRequest request) {
        logger.info("PATCH /api/v1/toDo/%s".formatted(toDoId));
        todoService.updateTodo(authentication,toDoId,request);
    }

    /**
     * Handles DELETE requests to delete a To-Do task by its ID.
     *
     * @param authentication The authentication details of the user making the request.
     * @param toDoId         The ID of the To-Do task to delete.
     */
    @DeleteMapping("{toDoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteTodoById(Authentication authentication, @PathVariable Long toDoId)
    {
        logger.info("DELETE /api/v1/toDo/%s".formatted(toDoId));
        todoService.deleteToDoById(authentication,toDoId);
    }

}
