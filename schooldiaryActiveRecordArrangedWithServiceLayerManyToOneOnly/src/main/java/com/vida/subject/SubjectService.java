package com.vida.subject;

import com.vida.model.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@RequestScoped
public class SubjectService {
    Logger logger = LoggerFactory.getLogger(SubjectService.class);

    //Only for initial seeding of data
    @Transactional
    public void addNewSubjectInitial(Subject subject) {
        Subject.persist(subject);
        logger.info(String.format("New subject %s saved in the database", subject));
    }

    public Optional<Subject> getSubjectById(Long subjId) {
        return Subject.<Subject>findByIdOptional(subjId);
    }

    public List<Subject> getAllSubjects(int page, int size) {
        return Subject.<Subject>findAll().page(page - 1, size).list();
    }

    public Subject addNewSubject(Subject subject) {
        subject.persist();
        logger.info(String.format("New subject %s saved in the database", subject));
        return subject;
    }

    public Optional<Subject> getSubjectBySubjectString(String subject) {
        return Subject.<Subject>find("subject", subject).firstResultOptional();
    }
}
