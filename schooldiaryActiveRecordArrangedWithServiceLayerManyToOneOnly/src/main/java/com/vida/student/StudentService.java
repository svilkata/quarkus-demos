package com.vida.student;

import com.vida.model.Klas;
import com.vida.model.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@RequestScoped
public class StudentService {
    Logger logger = LoggerFactory.getLogger(StudentService.class);

    //Only for initial seeding of data - we assume that in the initial seeding data already existing classes and students will not be mentioned
    @Transactional
    public void addNewStudentInitial(Student student) {
        //We persist here also the new klas
        student.persist();
        logger.info(String.format("New student %s saved in the database", student));
    }

    public Optional<Student> addNewStudent(Student student) {
        student.persist();
        logger.info(String.format("New student %s saved in the database", student));

        return Optional.of(student);
    }

    public Student assignStudentToKlas(Student student, Klas klas) {
        //No need here to persist the student again - it is in Hibernate session in the upper scope (the rest endpoint scope) and autosaves, but HTTP method should be PATCH
        student.setKlas(klas);

//        Student.persist(student);
//        Student.getEntityManager().merge(student);

        return student;
    }


    public List<Student> getAllStudents(int page, int size) {
//        // Using projection
//        TypedQuery<OneStudentProjectionDto> namedQuery = Student.getEntityManager()
//                .createNamedQuery(Student.GET_ALL_STUDENTS_WITHOUT_KLAS_INFO, OneStudentProjectionDto.class);
//        List<OneStudentProjectionDto> resultList = namedQuery.getResultList();

        return Student.<Student>findAll().page(page - 1, size).list();
    }


    public Optional<Student> getStudentById(Long stId) {
        return Student.findByIdOptional(stId);
    }

    public List<Student> getAllStudentsByKlas(Klas klas, int page, int size) {
        return Student.<Student>find("klas", klas).page(page - 1, size).list();
    }

    public Optional<Student> getStudentByEgn(String egn) {
        return Student.<Student>find("egn", egn).firstResultOptional();
    }
}
