package com.tasky.api.services.toDo;

import com.tasky.api.configurations.errors.BadRequestException;
import com.tasky.api.configurations.errors.NotFoundException;
import com.tasky.api.configurations.errors.UnauthorizedException;
import com.tasky.api.dao.feature.FeatureDao;
import com.tasky.api.dao.toDo.ToDoDao;
import com.tasky.api.dao.user.UserDao;
import com.tasky.api.dto.PageableDto;
import com.tasky.api.dto.toDo.CreateToDoRequest;
import com.tasky.api.dto.toDo.SearchToDoResponse;
import com.tasky.api.dto.toDo.TodoDto;
import com.tasky.api.dto.toDo.UpdateTodoRequest;
import com.tasky.api.mappers.ToDoDtoMapper;
import com.tasky.api.models.Feature;
import com.tasky.api.models.ToDo;
import com.tasky.api.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class TodoServiceImpl implements TodoService {
    private final Logger logger = LoggerFactory.getLogger(TodoService.class);
    private final FeatureDao featureDao;
    private final ToDoDao toDoDao;
    private final UserDao userDao;
    private final ToDoDtoMapper toDoDtoMapper;


    public TodoServiceImpl(@Qualifier("FEATURE_JPA") FeatureDao featureDao, @Qualifier("TO_DO_JPA") ToDoDao toDoDao, ToDoDtoMapper toDoDtoMapper, @Qualifier("JPA") UserDao userDao) {
        this.featureDao = featureDao;
        this.toDoDao = toDoDao;
        this.toDoDtoMapper = toDoDtoMapper;
        this.userDao = userDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createTodo(Authentication authentication, Long featureId, CreateToDoRequest request) {
        Feature feature = retrieveFeatureById(featureId);
        User user = retrieveUserAuthenticated(authentication);
        checkAccessToFeature(feature,user);

        List<String> stackTrace = new ArrayList<>();
        boolean isBadRequest = false;

        String description = "";
        String name = "";
        String type = "";

        if(request.description() == null) {
            String message = "Description is required";
            stackTrace.add(message);
            logger.error(message);
            isBadRequest = true;
        } else {
            description = request.description();
        }

        if(request.name() == null) {
            String message = "name is required";
            stackTrace.add(message);
            logger.error(message);
            isBadRequest = true;
        } else {
            name = request.name();
        }

        if(request.type() == null) {
            String message = "type is required";
            stackTrace.add(message);
            logger.error(message);
            isBadRequest = true;
        } else  {
            switch (request.type()) {
                case "task", "bug" -> type = request.type();
                default -> {
                    isBadRequest = true;
                    String message = "Invalid type";
                    stackTrace.add(message);
                    logger.error(message);
                }
            }
        }

        if(isBadRequest) {
            throw new BadRequestException(stackTrace.toString());
        }

        ToDo toDo = new ToDo(name,type,description,feature,user);

        toDoDao.createTodo(toDo);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TodoDto findToDoById(Authentication authentication, Long toDoId) {
        ToDo toDo = retriveTodo(toDoId);
        User user = retrieveUserAuthenticated(authentication);
        checkAccessToToDo(user,toDo);
        return toDoDtoMapper.apply(toDo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchToDoResponse findTodosWhereFeatureIsAndNameContains(Authentication authentication, Long featureId, String name, Integer page) {
        Feature feature = retrieveFeatureById(featureId);
        User user = retrieveUserAuthenticated(authentication);
        checkAccessToFeature(feature,user);

        String pattern = "";
        int currentPage = 0;

        if(page != null) {
            currentPage = page;
        }

        if(name != null) {
            pattern = name;
        }

        Pageable pageable = PageRequest.of(currentPage,6);

        Page<ToDo> resultPage = toDoDao.selectAllToDoWhereFeatureIdIsAndNameContaining(feature,pattern,pageable);

        if(currentPage != 0 && (resultPage.getTotalPages()-1) < page) {
            throw new BadRequestException("Page requested does not exists");
        }

        PageableDto pageableDto = new PageableDto(
                page,
                resultPage.getTotalPages()-1 > -1 ? resultPage.getTotalPages()-1 : 0,
                resultPage.getTotalPages(),
                resultPage.getNumberOfElements()
        );

        List<TodoDto> toDos = resultPage.getContent().stream().map(toDoDtoMapper).toList();

        return new SearchToDoResponse(toDos,pageableDto);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchToDoResponse findTodosWhereUserIsAnNameContains(Authentication authentication, String name, Integer page) {
        User user = retrieveUserAuthenticated(authentication);
        String pattern = "";
        int currentPage = 0;

        if(page != null) {
            currentPage = page;
        }

        if(name != null) {
            pattern = name;
        }

        Pageable pageable = PageRequest.of(currentPage,6);
        Page<ToDo> resultPage = toDoDao.selectToDosWhereUserIsAndNameContaining(user,pattern,pageable);

        if(currentPage != 0 && (resultPage.getTotalPages()-1) < page) {
            throw new BadRequestException("Page requested does not exists");
        }

        PageableDto pageableDto = new PageableDto(
                page,
                resultPage.getTotalPages()-1 > -1 ? resultPage.getTotalPages()-1 : 0,
                resultPage.getTotalPages(),
                resultPage.getNumberOfElements()
        );

        List<TodoDto> toDos = resultPage.getContent().stream().map(toDoDtoMapper).toList();

        return new SearchToDoResponse(toDos,pageableDto);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateTodo(Authentication authentication, Long toDoId, UpdateTodoRequest request) {
        ToDo toDo = retriveTodo(toDoId);
        User user = retrieveUserAuthenticated(authentication);
        checkAccessToToDo(user,toDo);
        boolean changes = false;

        if(request.description() != null && !Objects.equals(toDo.getDescription(), request.description())) {
            toDo.setDescription(request.description());
            changes = true;
        }

        if(request.userId() != null) {
            User assignedUser = userDao.selectUserById(request.userId()).orElseThrow(() -> new NotFoundException("User with id %s does not exists".formatted(request.userId())));
            if(toDo.getUser() != assignedUser) {
                toDo.setUser(assignedUser);
                changes = true;
            }
        }

        if(request.name() != null && !request.name().equals(toDo.getName())) {
            toDo.setName(request.name());
            changes = true;
        }

        if(request.type() != null) {
            switch (request.type()) {
                case "task", "bug" -> {
                    if(!request.type().equals(toDo.getType())) {
                        toDo.setType(request.type());
                        changes = true;
                    }
                }
                default -> {
                    String message = "Invalid type";
                    logger.error(message);
                    throw new BadRequestException(message);
                }
            }
        }

        if(request.status() != null) {
            switch (request.status()) {
                case "New", "In progress", "Completed" -> {
                    if (!request.status().equals(toDo.getStatus())) {
                        toDo.setStatus(request.status());
                        changes = true;
                    }
                }
                default -> throw new BadRequestException("Status not supported");
            }
        }

        if(!changes) {
            throw new BadRequestException("No changes found");
        }

        toDoDao.updateTodo(toDo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteToDoById(Authentication authentication, Long toDoId) {
        ToDo toDo = retriveTodo(toDoId);
        User user = retrieveUserAuthenticated(authentication);
        checkAccessToToDo(user,toDo);
        toDoDao.deleteTodoById(toDoId);
    }

    private Feature retrieveFeatureById(Long featureId) {
        return featureDao
                .findFeatureById(featureId)
                .orElseThrow(
                        ()-> new NotFoundException("Feature id %s does not exists".formatted(featureId)
                        )
                );
    }

    private User retrieveUserAuthenticated(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }

    private void checkAccessToFeature(Feature feature, User user) {
        boolean fullAccess = user.getRole().equals("PROJECT_MANAGER");

        if(!fullAccess) {
            feature.getProject()
                    .getUsers()
                    .stream()
                    .filter(
                            user1 -> user1
                                    .getEmail()
                                    .equals(
                                            user.getEmail()
                                    )
                    )
                    .findFirst()
                    .orElseThrow(
                            () -> new UnauthorizedException("You can't access to this resource.")
                    );
        }
    }

    private void checkAccessToToDo(User user, ToDo toDo) {
        boolean fullAccess = user.getRole().equals("PROJECT_MANAGER");

        if(!fullAccess) {
            toDo.getFeature()
                    .getProject()
                    .getUsers()
                    .stream()
                    .filter(
                            user1 -> user1
                                    .getEmail()
                                    .equals(
                                            user.getEmail()
                                    )
                    )
                    .findFirst()
                    .orElseThrow(
                            () -> new UnauthorizedException("You can't access to this resource")
                    );
        }
    }

    private ToDo retriveTodo(Long toDoId) {
        return toDoDao.selectToDoById(toDoId)
                .orElseThrow(
                        () -> new NotFoundException("Todo with id %s does not exists".formatted(toDoId)
                        )
                );
    }
}
