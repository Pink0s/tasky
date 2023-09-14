package com.tasky.api.dao.toDo;

import com.tasky.api.models.Feature;
import com.tasky.api.models.ToDo;
import com.tasky.api.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * The ToDoDao interface defines methods to interact with ToDo data.
 */
public interface ToDoDao {

    /**
     * Creates a new ToDo.
     *
     * @param toDo The ToDo object to be created.
     */
    void createTodo(ToDo toDo);

    /**
     * Retrieves a ToDo by its unique identifier (ID).
     *
     * @param id The unique identifier (ID) of the ToDo to retrieve.
     * @return An Optional containing the retrieved ToDo object, or an empty Optional if not found.
     */
    Optional<ToDo> selectToDoById(Long id);

    /**
     * Retrieves a page of ToDos associated with a specific Feature based on the Feature's unique identifier,
     * where the ToDo's name contains the specified pattern.
     *
     * @param feature  The Feature associated with the ToDos to retrieve.
     * @param name     The pattern to search for in ToDo names.
     * @param pageable The pageable configuration for the result page.
     * @return A Page containing ToDos associated with the specified Feature that match the name pattern.
     */
    Page<ToDo> selectAllToDoWhereFeatureIdIsAndNameContaining(Feature feature, String name, Pageable pageable);

    /**
     * Retrieves a page of ToDos associated with a specific User,
     * where the ToDo's name contains the specified pattern.
     *
     * @param user     The User associated with the ToDos to retrieve.
     * @param name     The pattern to search for in ToDo names.
     * @param pageable The pageable configuration for the result page.
     * @return A Page containing ToDos associated with the specified User that match the name pattern.
     */
    Page<ToDo> selectToDosWhereUserIsAndNameContaining(User user, String name, Pageable pageable);
    /**
     * Updates an existing ToDo.
     *
     * @param toDo The ToDo object containing updated information to be saved.
     */
    void updateTodo(ToDo toDo);

    /**
     * Deletes a ToDo by its unique identifier (ID).
     *
     * @param toDo The unique identifier (ID) of the ToDo to be deleted.
     */
    void deleteTodoById(Long toDo);

}
