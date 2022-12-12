package com.vida.mark;

import com.vida.model.Assignment;
import com.vida.model.Mark;
import com.vida.model.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import java.util.List;

@RequestScoped
public class MarksService {
    Logger logger = LoggerFactory.getLogger(MarksService.class);

    //Only for initial seeding of data
    @Transactional
    public void addMarksDataInitial(Mark mark) {
        mark.persist();
        logger.info(String.format("New marks for student %s added for assignment %s in the database",
                mark.getStudent(), mark.getAssignment()));
    }

    public List<Mark> getAllMarksFromAStudentAndASubject(Student student, List<Assignment> assignmentsForSubjectAndKlasOfStudent, int page, int size) {
        return Mark.<Mark>find("student= ?1 AND assignment IN (?2)",
                student, assignmentsForSubjectAndKlasOfStudent).page(page - 1, size).list();
    }

    public Mark writeMarkPerStudentPerSubject(Student student, Assignment assignmentForSubjectTeacherAndKlas, Integer mark) {
        Mark markEntity = new Mark(student, assignmentForSubjectTeacherAndKlas, mark);
        Mark.persist(markEntity);
        return markEntity;
    }
}
