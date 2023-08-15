package com.tasky.api.dto.user;

import com.tasky.api.dto.PageableDto;
import java.util.List;

public record SearchUsersResponse(List<UserDto> users, PageableDto pageableDto){}

