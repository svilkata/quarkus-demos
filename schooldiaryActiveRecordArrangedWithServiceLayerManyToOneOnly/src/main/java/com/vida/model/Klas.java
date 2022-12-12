package com.vida.model;

import com.vida.model.enums.KlasLetterEnum;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Entity
@Table(name = "klasses")
@NamedQuery(name = Klas.GET_KLAS_BY_NUMBER_AND_LETTER, query = "SELECT k FROM Klas k WHERE k.klasNumber = :param1 AND k.klasLetterEnum = :param2")
public class Klas extends AbstractEntity {
    public static final String GET_KLAS_BY_NUMBER_AND_LETTER = "getKlasByNumberAndLetter";

    @Column(name = "klas_number")
    private Integer klasNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "klas_letter")
    private KlasLetterEnum klasLetterEnum;

    public Klas() {
    }

    public Klas(Integer klasNumber, KlasLetterEnum klasLetterEnum) {
        this.klasNumber = klasNumber;
        this.klasLetterEnum = klasLetterEnum;
    }


    public Integer getKlasNumber() {
        return klasNumber;
    }

    public Klas setKlasNumber(Integer klasNumber) {
        this.klasNumber = klasNumber;
        return this;
    }

    public KlasLetterEnum getKlasLetterEnum() {
        return klasLetterEnum;
    }

    public Klas setKlasLetterEnum(KlasLetterEnum klasLetterEnum) {
        this.klasLetterEnum = klasLetterEnum;
        return this;
    }

    public static Optional<Klas> getKlasByNumberAndLetter(Integer klasNumber, KlasLetterEnum klasLetterEnum){
        Map<String, Object> paramsSearchKlas = new LinkedHashMap<>();
        paramsSearchKlas.put("param1", klasNumber);
        paramsSearchKlas.put("param2", klasLetterEnum);

        return find("#" + Klas.GET_KLAS_BY_NUMBER_AND_LETTER, paramsSearchKlas).firstResultOptional();
    }

    @Override
    public String toString() {
        return "{" +
                "id:" + id + "," +
                "klasNumber:" + klasNumber + "," +
                "klasLetterEnum:" + klasLetterEnum +
                '}';
    }
}
