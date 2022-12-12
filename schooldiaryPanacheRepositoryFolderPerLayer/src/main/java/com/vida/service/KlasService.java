package com.vida.service;

import com.vida.model.bind.KlasBindingModel;
import com.vida.model.entity.Klas;
import com.vida.model.entity.Student;
import com.vida.model.enums.KlasLetterEnum;
import com.vida.repository.KlasRepository;
import com.vida.repository.StudentRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.*;

@ApplicationScoped
public class KlasService {
    @Inject
    KlasRepository klasRepository;
    @Inject
    StudentRepository studentRepository;

    Logger logger = LoggerFactory.getLogger(KlasService.class);

    //Only for initial seeding of data
    @Transactional
    public Klas addNewKlasFromEntity(Klas klas) {
        this.klasRepository.persist(klas);
        logger.info(String.format("New klas %s saved in the database", klas));

        return klas;
    }

    @Transactional
    public Map<Integer, Klas> addNewKlasFromBindingModel(KlasBindingModel klasBindingModel) {
        //Check for existing klas
        Optional<Klas> klasOpt = this.klasRepository.findKlasByNumberAndLetter(
                klasBindingModel.getKlasNumber(), KlasLetterEnum.valueOf(klasBindingModel.getKlasLetter()));
        Klas klasToAdd = null;
        Map<Integer, Klas> map = new HashMap<>();

        if (klasOpt.isPresent()) {
            klasToAdd = klasOpt.get();
            logger.info(String.format("Klas %s already exists in the database", klasToAdd.toString()));
            map.put(0, klasToAdd); //conflict 409
        } else {
            try {
                klasToAdd = new Klas()
                        .setKlasNumber(klasBindingModel.getKlasNumber())
                        .setKlasLetterEnum(KlasLetterEnum.valueOf(klasBindingModel.getKlasLetter()));

                this.klasRepository.persist(klasToAdd);
                logger.info(String.format("New klas %s saved in the database", klasToAdd));
                map.put(1, klasToAdd); //successfull 200
            } catch (Exception e) {
                map.put(2, null); //conflict 409
            }
        }

        return map;
    }

    @Transactional
    public List<Klas> getAllKlassesLazy() {
        PanacheQuery<Klas> all = this.klasRepository.findAll();
        return all.stream()
                .map(kl -> new Klas().setId(kl.getId()).setKlasNumber(kl.getKlasNumber()).setKlasLetterEnum(kl.getKlasLetterEnum()))
                .toList();
    }

    @Transactional
    public Optional<Klas> getKlasByKlasIdLazy(Long klasId) {
        Optional<Klas> klasOpt = this.klasRepository.findByIdOptional(klasId);
        return klasOpt;
    }

    @Transactional
    public Optional<Klas> getKlasByKlasIdEager(Long klasId) {
        TypedQuery<Klas> eagerQuery = this.klasRepository.getEntityManager()
                .createNamedQuery("getKlasWithAllItsStudents", Klas.class);
        eagerQuery.setParameter(1, klasId);

        List<Klas> resultList = eagerQuery.getResultList();
        if (resultList.isEmpty()) {
            return Optional.of(resultList.get(0));
        }

        Klas kl = resultList.get(0);
        Klas result = new Klas()
                .setId(kl.getId())
                .setKlasNumber(kl.getKlasNumber())
                .setKlasLetterEnum(kl.getKlasLetterEnum());


        Set<Student> allStudentsByKlasId = this.studentRepository.getAllStudentsByKlasId(klasId);
        result.setStudents(allStudentsByKlasId);

        return Optional.of(result);
    }
}
