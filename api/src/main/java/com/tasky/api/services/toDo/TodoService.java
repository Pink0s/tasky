package com.tasky.api.services.toDo;

import com.tasky.api.dto.toDo.CreateToDoRequest;
import com.tasky.api.dto.toDo.SearchToDoResponse;
import com.tasky.api.dto.toDo.TodoDto;
import com.tasky.api.dto.toDo.UpdateTodoRequest;
import com.tasky.api.models.Feature;
import com.tasky.api.models.ToDo;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

/**
 * The TodoService interface defines methods for managing ToDo tasks.
 */
public interface TodoService {
    /**
     * Create a new ToDo task associated with a feature.
     *
     * @param authentication The authentication object for the user creating the ToDo task.
     * @param featureId      The unique identifier (ID) of the feature associated with the ToDo task.
     * @param request        The request object containing ToDo task details.
     */
    void createTodo(Authentication authentication, Long featureId, CreateToDoRequest request);

    /**
     * Find a ToDo task by its unique identifier (ID).
     *
     * @param authentication The authentication object for the user making the request.
     * @param toDoId         The unique identifier (ID) of the ToDo task to retrieve.
     * @return The TodoDto containing information about the retrieved ToDo task.
     */
    TodoDto findToDoById(Authentication authentication, Long toDoId);

    /**
     * Find ToDo tasks associated with a feature and matching a name pattern.
     *
     * @param authentication The authentication object for the user making the request.
     * @param featureId      The unique identifier (ID) of the feature associated with the ToDo tasks.
     * @param name           The pattern to search for in ToDo task names.
     * @param page           The page number for pagination.
     * @return A SearchToDoResponse containing a list of matching ToDo tasks and pagination details.
     */
    SearchToDoResponse findTodosWhereFeatureIsAndNameContains(Authentication authentication, Long featureId, String name, Integer page);

    /**
     * Find ToDo tasks associated with a user and matching a name pattern.
     *
     * @param authentication The authentication object for the user making the request.
     * @param name           The pattern to search for in ToDo task names.
     * @param page           The page number for pagination.
     * @return A SearchToDoResponse containing a list of matching ToDo tasks and pagination details.
     */
    SearchToDoResponse findTodosWhereUserIsAnNameContains(Authentication authentication, String name, Integer page);

    /**
     * Update an existing ToDo task.
     *
     * @param authentication The authentication object for the user making the request.
     * @param toDoId         The unique identifier (ID) of the ToDo task to update.
     * @param request        The request object containing updated ToDo task details.
     */
    void updateTodo(Authentication authentication, Long toDoId, UpdateTodoRequest request);

    /**
     * Delete a ToDo task by its unique identifier (ID).
     *
     * @param authentication The authentication object for the user making the request.
     * @param toDoId         The unique identifier (ID) of the ToDo task to delete.
     */
    void deleteToDoById(Authentication authentication, Long toDoId);

}
