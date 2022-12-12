package com.vidasoft.diary.absence;

import com.vidasoft.diary.model.Absence;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

public class AbsenceDTO {
    public Long id;

    @NotNull
    @Positive
    public Integer absenceHours;

    public LocalDateTime timeAdded;

    public String teacherFullName;

    public AbsenceDTO() {
    }

    public AbsenceDTO(Absence absence){
        this.id = absence.id;
        this.absenceHours = absence.absenceHours;
        this.timeAdded = LocalDateTime.now();
        this.teacherFullName = absence.teacher.firstName + " " + absence.teacher.lastName;
    }
}
