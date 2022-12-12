package com.vida.repository;

import com.vida.model.bind.StudentBindingModel;
import com.vida.model.entity.Student;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import javax.enterprise.context.ApplicationScoped;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
public class StudentRepository implements PanacheRepositoryBase<Student, Long> {

    public Optional<Student> findByFirstName(String firstName) {
        return find("firstName", firstName).firstResultOptional();
    }

    public Optional<Student> getStudentByEGNAndKlas(StudentBindingModel studentBindingModel) {
        return null;
//                find("EGN", egn).firstResultOptional();
    }

    public Set<Student> getAllStudentsByKlasId(Long klasId) {
//        String query = "SELECT st FROM Student st WHERE st.klas.id= ?1";
        List<Student> list = list("#getAllStudentsByKlasId", klasId);
        Set<Student> studentsSet = new LinkedHashSet<>();

        list.forEach(l -> {
            Student st = new Student()
                    .setId(l.getId())
                    .setFirstName(l.getFirstName())
                    .setLastName(l.getLastName())
                    .setEGN(l.getEGN());

            studentsSet.add(st);
        });

        return studentsSet;
    }
}