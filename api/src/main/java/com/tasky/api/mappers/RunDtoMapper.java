package com.tasky.api.mappers;


import com.tasky.api.dto.run.RunDto;
import com.tasky.api.models.Run;
import org.springframework.stereotype.Service;

import java.util.function.Function;

/**
 * The RunDtoMapper class converts Feature objects to RunDto objects.
 */
@Service
public class RunDtoMapper implements Function<Run, RunDto> {
    /**
     * @param run the function argument
     * @return RunDto
     */
    @Override
    public RunDto apply(Run run) {
        return new RunDto(
                run.getId(),
                run.getName(),
                run.getDescription(),
                run.getStatus(),
                run.getStartDate(),
                run.getEndDate()
        );
    }
}
