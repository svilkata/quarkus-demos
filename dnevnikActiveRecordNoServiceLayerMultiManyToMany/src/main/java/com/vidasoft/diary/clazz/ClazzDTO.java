package com.vidasoft.diary.clazz;

import com.vidasoft.diary.model.Clazz;
import com.vidasoft.diary.validation.UniqueClazzName;

import javax.json.bind.annotation.JsonbTransient;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@UniqueClazzName(firstField = "clazzNumber", secondField = "subclazzInitial")
public class ClazzDTO {
    public Long id;

    @NotBlank
    @Min(1)
    @Max(12)
//    @Pattern(regexp = "^[1-9]{1}|((10)|(11)|(12))$", message = "The clazzNumber should be only 1-12 numbers")
    public String clazzNumber;

    @NotBlank
    @Pattern(regexp = "^[A-D]{1}$", message = "The subclazzInitial should be only 1 letter - A, B, C or D")
    public String subclazzInitial;

    public ClazzDTO() {
    }

    public ClazzDTO(Clazz clazz) {
        id = clazz.id;
        clazzNumber = clazz.clazzNumber;
        subclazzInitial = clazz.subclazzInitial;
    }

//    @JsonbTransient
    public Clazz toClazz() {
        return new Clazz(clazzNumber, subclazzInitial);
    }
}
