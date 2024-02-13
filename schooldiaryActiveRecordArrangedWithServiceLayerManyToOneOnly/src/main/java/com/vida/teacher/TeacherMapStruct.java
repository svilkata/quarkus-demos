package com.vida.teacher;

import com.vida.model.Teacher;
import com.vida.teacher.dto.TeacherDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface TeacherMapStruct {

    TeacherDto fromTeacherToTeacherDto(Teacher teacher);

    Teacher fromTeacherDtoToTeacher(TeacherDto teacherDto);
}
