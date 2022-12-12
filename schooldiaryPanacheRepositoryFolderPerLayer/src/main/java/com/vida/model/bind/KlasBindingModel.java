package com.vida.model.bind;

import com.vida.model.enums.KlasLetterEnum;

import javax.validation.constraints.*;

public class KlasBindingModel {
    @NotNull
    @Positive
    @Max(12)
    private Integer klasNumber;
    @NotNull
    @Size(min = 1, max = 1)
    private String klasLetter;

    public KlasBindingModel() {
    }

    public Integer getKlasNumber() {
        return klasNumber;
    }

    public KlasBindingModel setKlasNumber(Integer klasNumber) {
        this.klasNumber = klasNumber;
        return this;
    }

    public String getKlasLetter() {
        return klasLetter;
    }

    public KlasBindingModel setKlasLetter(String klasLetter) {
        this.klasLetter = klasLetter;
        return this;
    }

    @Override
    public String toString() {
        return "{" +
                "klasNumber:" + klasNumber + "," +
                "klasLetter:" + klasLetter +
                '}';
    }
}
