package com.vida.repository;


import com.vida.model.entity.Klas;
import com.vida.model.enums.KlasLetterEnum;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class KlasRepository implements PanacheRepositoryBase<Klas, Long> {

    //    default PanacheQuery<Entity> find(String query, Object... params)
    public Optional<Klas> findKlasByNumberAndLetter(Integer klasNumber, KlasLetterEnum klasLetterEnum) {
        String query = "klasNumber = :param1 and klasLetterEnum = :param2";
        Optional<Klas> klasOpt = find(query,
                Parameters.with("param1", klasNumber).and("param2", klasLetterEnum))
                .firstResultOptional();

        return klasOpt;
    }


    public Optional<Klas> findKlasByIdEagerWithStudents(Long klasId) {
//        String query = "SELECT k FROM Klas k WHERE k.id = ?1 JOIN FETCH k.students";
        String query = "SELECT k FROM Klas k WHERE k.id = ?1";
        Optional<Klas> klasOpt = find(query, klasId).firstResultOptional();
        PanacheQuery<Klas> klasPanacheQuery = find(query, klasId);


//        Optional<Klas> klasOpt = find("#getKlasWithAllItsStudents", klasId).firstResultOptional();

        return klasOpt;
    }
}
