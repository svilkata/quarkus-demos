package com.vida.assignment;

import com.vida.model.Assignment;
import com.vida.model.Klas;
import com.vida.model.Subject;
import com.vida.model.Teacher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@RequestScoped
public class AssignmentService {
    Logger logger = LoggerFactory.getLogger(AssignmentService.class);

    //Only for initial seeding of data
    @Transactional
    public void addNewAssignmentInitial(Assignment assignment) {
        Assignment.persist(assignment);
        logger.info(String.format("New subject assignment %s saved in the database", assignment));
    }

    public Optional<Assignment> assignATeacherASubjectAndAKlas(Teacher teacher, Klas klas, Subject subject) {
        Assignment assignmentAvailable = Assignment.<Assignment>find("teacher = ?1 AND klas = ?2 AND subject = ?3",
                teacher, klas, subject).firstResult();

        if (assignmentAvailable != null) {
            logger.info(String.format("Assignment with teacherId %d, klasId %d and subjectId %d already exists in the database",
                    assignmentAvailable.getTeacher().getId(), assignmentAvailable.getKlas().getId(),
                    assignmentAvailable.getSubject().getId()));
            return Optional.empty(); //conflict 409 - klas exists
        } else {
            Assignment newAssignment = new Assignment(subject, klas, teacher);
            Assignment.persist(newAssignment);
            logger.info(String.format("New assignment %s saved in the database", newAssignment));

            return Optional.of(newAssignment);
        }
    }

    public List<Subject> getSubjectsForTeacher(Teacher teacherToCheck, int page, int size) {
        return Assignment.getAllSubjectsAssignedToATeacher(teacherToCheck, page - 1, size);
    }

    public List<Klas> getKlassesForTeacher(Teacher teacherToCheck, int page, int size) {
        return Assignment.getAllKlassesAssignedToATeacher(teacherToCheck, page - 1, size);
    }

    public List<Subject> getSubjectsForTeacherAndKlas(Teacher teacherToCheck, Klas klasToCheck, int page, int size) {
        return Assignment.getAllSubjectPerTeacherPerKlas(teacherToCheck, klasToCheck, page - 1, size);
    }

    public List<Klas> getKlassesForTeacherAndSubject(Teacher teacherToCheck, Subject subjectToCheck, int page, int size) {
        return Assignment.getAllKlassesPerTeacherPerSubject(teacherToCheck, subjectToCheck, page - 1, size);
    }

    //Methods that may be used in the future
//    public List<Assignment> getAssignmentsForTeacherAndKlas(Teacher teacher, Klas klas) {
//        return Assignment.<Assignment>list("teacher= ?1 and klas= ?2", teacher, klas);
//    }
//
//    public List<Assignment> getAssignmentsForTeacherAndSubject(Teacher teacher, Subject subject) {
//        return Assignment.<Assignment>list("teacher= ?1 and subject= ?2", teacher, subject);
//    }
//
//    public List<Assignment> getAssignmentsForTeacher(Teacher teacher) {
//        return Assignment.<Assignment>list("teacher= ?1", teacher);
//    }

    public List<Teacher> getAllTeachersByKlas(Klas klas, int page, int size) {
        return Assignment.getAllTeachersPerKlas(klas, page - 1, size);
    }


    public List<Assignment> getAssignmentsForSubjectAndKlasOfStudent(Subject subject, Klas klas) {
        return Assignment.<Assignment>list("subject= ?1 and klas= ?2", subject, klas);
    }

    public Optional<Assignment> getAssignmentForSubjectTeacherAndKlasOfStudent(Subject subject, Klas klas, Teacher teacher) {
        return Assignment.<Assignment>find("subject= ?1 AND klas= ?2 AND teacher= ?3", subject, klas, teacher).firstResultOptional();
    }
}
