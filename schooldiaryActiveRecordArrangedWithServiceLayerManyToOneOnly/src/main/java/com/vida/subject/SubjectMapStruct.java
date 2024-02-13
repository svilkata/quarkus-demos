package com.vida.subject;

import com.vida.model.Subject;
import com.vida.subject.dto.SubjectDto;
import org.mapstruct.Mapper;

//@Dependent
@Mapper(componentModel = "cdi")
public interface SubjectMapStruct {

    SubjectDto fromSubjectToSubjectDto(Subject subject);

    Subject fromSubjectDtoToSubject(SubjectDto subjectDto);
}
