package com.tasky.api.dao.toDo;

import com.tasky.api.models.Feature;
import com.tasky.api.models.ToDo;
import com.tasky.api.models.User;
import com.tasky.api.repositories.ToDoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("TO_DO_JPA")
public class ToDoDaoImpl implements ToDoDao {

    private final ToDoRepository toDoRepository;

    public ToDoDaoImpl(ToDoRepository toDoRepository) {
        this.toDoRepository = toDoRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createTodo(ToDo toDo) {
        toDoRepository.save(toDo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<ToDo> selectToDoById(Long id) {
        return toDoRepository.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<ToDo> selectAllToDoWhereFeatureIdIsAndNameContaining(Feature feature, String name, Pageable pageable) {
        return toDoRepository.findAllByFeatureIsAndNameContaining(feature,name,pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<ToDo> selectToDosWhereUserIsAndNameContaining(User user, String name, Pageable pageable) {
       return toDoRepository.findAllByUserAndAndNameContaining(user,name,pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateTodo(ToDo toDo) {
        toDoRepository.save(toDo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteTodoById(Long toDo) {
        toDoRepository.deleteById(toDo);
    }
}
