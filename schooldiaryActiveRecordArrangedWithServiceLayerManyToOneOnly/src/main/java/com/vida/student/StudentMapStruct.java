package com.vida.student;

import com.vida.model.Student;
import com.vida.student.dto.StudentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

//@Dependent
@Mapper(componentModel = "cdi")
public interface StudentMapStruct {

////    @Mapping(target = "klas.klasNumber", source = "klas.klasNumber")
//    @Mapping(target = "klas.klasLetter", source = "klas.klasLetterEnum")
//    @Mapping(source = "klas.id", target = "klas.id")
//    StudentDto fromStudentToStudentDto(Student student);
//
//    @Mapping(target = "klas.klasLetterEnum", source = "klas.klasLetter")
//    Student fromStudentDtoToStudent(StudentDto studentDto);
}
