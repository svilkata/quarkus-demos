package com.vida.service;

import com.vida.model.bind.StudentBindingModel;
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
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class StudentService {
    @Inject
    StudentRepository studentRepository;
    @Inject
    KlasRepository klasRepository;

    Logger logger = LoggerFactory.getLogger(StudentService.class);

    //Only for initial seeding of data - we assume that in the initial seeding data already existing classes and students will not be mentioned
    @Transactional
    public Student addNewStudentFromEntity(Student student) {
        //We persist here also the new class
        this.studentRepository.persist(student);
        logger.info(String.format("New student %s saved in the database", student));

        return student;
    }

    @Transactional
    public Student addNewStudentFromBindingModel(StudentBindingModel studentBindingModel) {
        //check for existing klas
        Optional<Klas> klasByNumberAndLetterOpt = this.klasRepository.findKlasByNumberAndLetter(
                studentBindingModel.getKlas().getKlasNumber(), KlasLetterEnum.valueOf(studentBindingModel.getKlas().getKlasLetter()));

        Klas klas = null;
        if (klasByNumberAndLetterOpt.isPresent()) {
            klas = klasByNumberAndLetterOpt.get();
            logger.info(String.format("Klas %s already exists in the database", klas.toString()));
        } else {
            klas = new Klas()
                    .setKlasNumber(studentBindingModel.getKlas().getKlasNumber())
                    .setKlasLetterEnum(KlasLetterEnum.valueOf(studentBindingModel.getKlas().getKlasLetter()));
            logger.info(String.format("New klas %s saved in the database", klas.toString()));
        }

        //check for existing student is in the correct existing klas
        Student studentToAdd = null;
        Optional<Student> studentOpt = this.studentRepository.getStudentByEGNAndKlas(studentBindingModel);
        if (studentOpt.isPresent()) {
            studentToAdd = studentOpt.get();
            logger.info(String.format("Student %s already exists in the database in klas %s", studentToAdd, klas));
        } else {
            studentToAdd = new Student()
                    .setFirstName(studentBindingModel.getFirstName())
                    .setLastName(studentBindingModel.getLastName())
                    .setEGN(studentBindingModel.getEGN())
                    .setKlas(klas);

            //We persist here also the new class
            this.studentRepository.persist(studentToAdd);
            logger.info(String.format("New student %s saved in the database", studentToAdd.toString()));
        }

        return studentToAdd;
    }

    @Transactional
    public Set<Student> getAllStudents() {
        PanacheQuery<Student> all = this.studentRepository.findAll();
//        StringBuilder sb = new StringBuilder();
//        sb.append("All students so far:").append(System.lineSeparator());
//        all.stream().forEach(st -> sb.append(st).append(System.lineSeparator()));

        return all.stream().collect(Collectors.toSet());
    }

    @Transactional
    public Optional<Student> getStudentById(Long stId) {
        return this.studentRepository.findByIdOptional(stId);
    }
}
