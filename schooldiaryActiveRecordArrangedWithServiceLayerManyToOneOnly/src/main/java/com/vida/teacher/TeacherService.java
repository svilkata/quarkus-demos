package com.vida.teacher;

import com.vida.model.Teacher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@RequestScoped
public class TeacherService {
    Logger logger = LoggerFactory.getLogger(TeacherService.class);

    //Only for initial seeding of data
    @Transactional
    public void addNewTeacherInitial(Teacher teacher) {
        Teacher.persist(teacher);
        logger.info(String.format("New teacher %s saved in the database", teacher));
    }

    public List<Teacher> getAllTeachers(int page, int size) {
        return Teacher.<Teacher>findAll().page(page - 1, size).stream().toList();
    }

    public Optional<Teacher> getTeacherById(Long tId) {
        return Teacher.<Teacher>find("id", tId).firstResultOptional();
    }

    public Optional<Teacher> addNewTeacher(Teacher teacher) {
        //Check if teacher already exists
        Optional<Teacher> teacherOpt = Teacher.<Teacher>find("egn", teacher.getEgn()).firstResultOptional();

        if (teacherOpt.isPresent()) {
            logger.info(String.format("Teacher with egn %s already exists in the database", teacherOpt.get().getEgn()));
            return Optional.empty(); //conflict 409 - klas exists
        } else {
            teacher.persist();
            logger.info(String.format("New teacher %s saved in the database", teacher));
            return Optional.of(teacher);
        }
    }

    public Optional<Teacher> getTeacherByEgn(String egnteacher) {
        return Teacher.<Teacher>find("egn", egnteacher).firstResultOptional();
    }
}
