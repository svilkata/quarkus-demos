package com.vida.klas;

import com.vida.klas.dto.KlasDto;
import com.vida.model.Klas;
import com.vida.model.Student;
import com.vida.student.dto.StudentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Optional;

//@Dependent
@Mapper(componentModel = "cdi")
public interface KlasMapStruct {

    @Mapping(target = "klasLetter", source = "klasLetterEnum")
//    @Mapping(ignore = true, target = "id", source = "version")
    KlasDto fromKlasToKlasDto(Klas klas);

    @Mapping(target = "klasLetterEnum", source = "klasLetter")
    Klas fromKlasDtoToKlas(KlasDto klasDto);

}
