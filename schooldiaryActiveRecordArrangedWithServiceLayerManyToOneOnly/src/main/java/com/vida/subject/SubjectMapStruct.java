package com.vida.subject;

import com.vida.model.Student;
import com.vida.model.Subject;
import com.vida.student.dto.StudentDto;
import com.vida.subject.dto.SubjectDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

//@Dependent
@Mapper(componentModel = "cdi")
public interface SubjectMapStruct {

    SubjectDto fromSubjectToSubjectDto(Subject subject);

    Subject fromSubjectDtoToSubject(SubjectDto subjectDto);
}
