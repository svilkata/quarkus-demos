package com.vida.assignment;

import com.vida.assignment.dto.AssignTeacherSubjectAndKlasDto;
import com.vida.model.Assignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

//@Dependent
@Mapper(componentModel = "cdi")
public interface AssignmentMapStruct {
    @Mapping(target = "subjectId", source = "subject.id")
    @Mapping(target = "klasId", source = "klas.id")
    @Mapping(target = "teacherId", source = "teacher.id")
    AssignTeacherSubjectAndKlasDto fromAssignmentToAssignmentDto(Assignment asgn);
}
