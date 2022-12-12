package com.vida.klas;

import com.vida.model.Klas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@RequestScoped
public class KlasService {
    Logger logger = LoggerFactory.getLogger(KlasService.class);

    //Only for initial seeding of data
    @Transactional
    public void addNewKlasInitial(Klas klas) {
        klas.persist(klas);
        logger.info(String.format("New klas %s saved in the database", klas));
    }

    public Optional<Klas> addNewKlas(Klas klas) {
        //Check for existing klas
        Optional<Klas> klasOpt = Klas.find("klasNumber = ?1 and klasLetterEnum = ?2", klas.getKlasNumber(),
                        klas.getKlasLetterEnum())
                .firstResultOptional();

        if (klasOpt.isPresent()) {
            logger.info(String.format("Klas with id %s already exists in the database", klasOpt.get().getId()));
            return Optional.empty(); //conflict 409 - klas exists
        } else {
            klas.persist();
            logger.info(String.format("New klas %s saved in the database", klas));

            return Optional.of(klas);
        }
    }

    public List<Klas> getAllKlasses(int page, int size) {
        return Klas.<Klas>findAll().page(page - 1, size).list();
    }

    public Optional<Klas> getKlasById(Long klasId) {
        return Klas.findByIdOptional(klasId);
    }
}
