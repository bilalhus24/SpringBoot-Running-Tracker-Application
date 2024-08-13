package com.example.demo.mapper;

import com.example.demo.entity.Run.Run;
import com.example.demo.entity.User;

public class RunMapper {
    public static Run mapToRun(RunDto runDto) {
        Run run = new Run(
                runDto.getId(),
                runDto.getUserId(),
                runDto.getMiles(),
                runDto.getLocation(),
                runDto.getStart(),
                runDto.getEnd(),
                runDto.getDuration(),
                runDto.getDate(),
                runDto.getAveragePace()
        );
        return run;
    }

    public static RunDto mapToRunDto(Run run) {
        RunDto runDto = new RunDto(
                run.getId(),
                run.getUserId(),
                run.getMiles(),
                run.getLocation(),
                run.getStart(),
                run.getEnd(),
                run.getDuration(),
                run.getDate(),
                run.getAveragePace()
        );
        return runDto;
    }

}
