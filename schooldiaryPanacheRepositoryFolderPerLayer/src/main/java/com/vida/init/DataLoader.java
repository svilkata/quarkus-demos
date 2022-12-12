package com.vida.init;

import com.vida.model.entity.Klas;
import com.vida.model.entity.Student;
import com.vida.model.enums.KlasLetterEnum;
import com.vida.service.KlasService;
import com.vida.service.StudentService;
import io.quarkus.runtime.Startup;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;


@ApplicationScoped
@Startup
public class DataLoader {
    @Inject
    KlasService klasService;
    @Inject
    StudentService studentService;

    @PostConstruct
    public void initSomeData() {
        Klas klas1A = new Klas().setKlasNumber(1).setKlasLetterEnum(KlasLetterEnum.A);
        Klas klas1B = new Klas().setKlasNumber(1).setKlasLetterEnum(KlasLetterEnum.B);
        Klas klas1C = new Klas().setKlasNumber(1).setKlasLetterEnum(KlasLetterEnum.C);
        klasService.addNewKlasFromEntity(klas1A);
        klasService.addNewKlasFromEntity(klas1B);
        klasService.addNewKlasFromEntity(klas1C);
        klasService.addNewKlasFromEntity(new Klas().setKlasNumber(1).setKlasLetterEnum(KlasLetterEnum.D));

        klasService.addNewKlasFromEntity(new Klas().setKlasNumber(2).setKlasLetterEnum(KlasLetterEnum.A));
        klasService.addNewKlasFromEntity(new Klas().setKlasNumber(2).setKlasLetterEnum(KlasLetterEnum.B));
        klasService.addNewKlasFromEntity(new Klas().setKlasNumber(2).setKlasLetterEnum(KlasLetterEnum.C));
        klasService.addNewKlasFromEntity(new Klas().setKlasNumber(2).setKlasLetterEnum(KlasLetterEnum.D));

        Student student1A_1 = new Student().setFirstName("Svilen").setLastName("Velikov").setEGN("6666666660").setKlas(klas1A);
        Student student1A_2 = new Student().setFirstName("Georgi").setLastName("Georgiev").setEGN("6666666661").setKlas(klas1A);

        Student student1B_1 = new Student().setFirstName("Petar").setLastName("Petrov").setEGN("1111111111").setKlas(klas1B);
        Student student1C_1 = new Student().setFirstName("Dimitar").setLastName("Dimitrov").setEGN("1111111112").setKlas(klas1C);
        this.studentService.addNewStudentFromEntity(student1A_1);
        this.studentService.addNewStudentFromEntity(student1A_2);
        this.studentService.addNewStudentFromEntity(student1B_1);
        this.studentService.addNewStudentFromEntity(student1C_1);
    }
}
