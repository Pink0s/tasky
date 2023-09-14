package com.tasky.api.repositories;

import com.tasky.api.models.Feature;
import com.tasky.api.models.ToDo;
import com.tasky.api.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 * Repository interface for managing ToDo entities.
 */
public interface ToDoRepository extends JpaRepository<ToDo,Long> {

    /**
     * Retrieves a page of To-Do items associated with a specific feature, where the To-Do item names contain the specified pattern.
     *
     * @param feature  The Feature associated with the To-Do items to retrieve.
     * @param name     The pattern to search for in To-Do item names.
     * @param pageable The pageable configuration for the result page.
     * @return A Page containing To-Do items associated with the specified feature and matching the name pattern.
     */
    Page<ToDo> findAllByFeatureIsAndNameContaining(Feature feature, String name, Pageable pageable);

    /**
     * Retrieves a page of To-Do items associated with a specific user, where the To-Do item names contain the specified pattern.
     *
     * @param user     The User associated with the To-Do items to retrieve.
     * @param name     The pattern to search for in To-Do item names.
     * @param pageable The pageable configuration for the result page.
     * @return A Page containing To-Do items associated with the specified user and matching the name pattern.
     */
    Page<ToDo> findAllByUserAndAndNameContaining(User user,String name, Pageable pageable);

}
