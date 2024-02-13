package com.vida.klas;

import com.vida.klas.dto.KlasDto;
import com.vida.model.Klas;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

//@Dependent
@Mapper(componentModel = "cdi")
public interface KlasMapStruct {

    @Mapping(target = "klasLetter", source = "klasLetterEnum")
//    @Mapping(ignore = true, target = "id", source = "version")
    KlasDto fromKlasToKlasDto(Klas klas);

    @Mapping(target = "klasLetterEnum", source = "klasLetter")
    Klas fromKlasDtoToKlas(KlasDto klasDto);

}
