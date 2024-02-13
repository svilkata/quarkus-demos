package com.vida.absence;

import com.vida.model.Absence;
import com.vida.model.Assignment;
import com.vida.model.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import java.util.List;

@RequestScoped
public class AbsenceService {
    Logger logger = LoggerFactory.getLogger(AbsenceService.class);

    @Transactional
    public void addAbsencesDataInitial(Absence absence) {
        absence.persist();
        logger.info(String.format("New absence %s saved in the database", absence));

    }

    public List<Absence> getAllAbsencesFromAStudentAndASubject(Student student, List<Assignment> assignmentsForSubjectAndKlasOfStudent,
                                                            int page, int size) {

        return Absence.<Absence>find("student= ?1 AND assignment IN (?2)",
                student, assignmentsForSubjectAndKlasOfStudent).page(page - 1, size).list();

    }


    public Absence writeAbsencePerStudentPerSubject(Absence absencePrep) {
        absencePrep.persist();
        return absencePrep;
    }
}
