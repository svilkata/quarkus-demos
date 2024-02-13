package com.vida.init;

import com.vida.absence.AbsenceService;
import com.vida.assignment.AssignmentService;
import com.vida.klas.KlasService;
import com.vida.mark.MarksService;
import com.vida.model.Absence;
import com.vida.model.Assignment;
import com.vida.model.Klas;
import com.vida.model.Mark;
import com.vida.model.Student;
import com.vida.model.Subject;
import com.vida.model.Teacher;
import com.vida.model.enums.AbsenceTypeEnum;
import com.vida.model.enums.KlasLetterEnum;
import com.vida.student.StudentService;
import com.vida.subject.SubjectService;
import com.vida.teacher.TeacherService;
import io.quarkus.runtime.Startup;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;


@ApplicationScoped
@Startup
public class DataLoader {
    @Inject
    StudentService studentService;
    @Inject
    KlasService klasService;
    @Inject
    TeacherService teacherService;
    @Inject
    SubjectService subjectService;
    @Inject
    AssignmentService assignmentService;
    @Inject
    MarksService marksService;
    @Inject
    AbsenceService absenceService;

    @PostConstruct
    @Transactional
    public void initSomeData() {
        //Create and save klasses 1A, 1B, 1C and 1D in the database
        Klas klas1A = new Klas(1, KlasLetterEnum.A);
        Klas klas1B = new Klas(1, KlasLetterEnum.B);
        Klas klas1C = new Klas(1, KlasLetterEnum.C);
        Klas klas1D = new Klas(1, KlasLetterEnum.D);
        klasService.addNewKlasInitial(klas1A);
        klasService.addNewKlasInitial(klas1B);
        klasService.addNewKlasInitial(klas1C);
        klasService.addNewKlasInitial(klas1D);

        //Create and save the students in the db
        Student student1A_1 = new Student("Svilen", "Velikov", "6666666660").setKlas(klas1A);
        Student student1A_2 = new Student("Georgi", "Georgiev", "6666666661").setKlas(klas1A);
        Student student1A_3 = new Student("Vassil", "Levski", "vvvvvvvvvv").setKlas(klas1A);
        Student student1A_4 = new Student("Boiko", "Borisov", "bbbbbbbbbb").setKlas(klas1A);
        Student student1A_5 = new Student("Zahari", "Stoyanov", "zzzzzzzzzz").setKlas(klas1A);
        Student student1A_6 = new Student("Kiril", "Petkov", "kkkkkkkkkk").setKlas(klas1A);
        Student student1A_7 = new Student("Gospodin", "Gospodinov", "gggggggggg").setKlas(klas1A);
        Student student1B_1 = new Student("Petar", "Petrov", "pppppppppp").setKlas(klas1B);
        Student student1C_1 = new Student("Dimitar", "Dimitrov", "1111111112").setKlas(klas1C);
        this.studentService.addNewStudentInitial(student1A_1);
        this.studentService.addNewStudentInitial(student1A_2);
        this.studentService.addNewStudentInitial(student1A_3);
        this.studentService.addNewStudentInitial(student1A_4);
        this.studentService.addNewStudentInitial(student1A_5);
        this.studentService.addNewStudentInitial(student1A_6);
        this.studentService.addNewStudentInitial(student1A_7);
        this.studentService.addNewStudentInitial(student1B_1);
        this.studentService.addNewStudentInitial(student1C_1);

        //Create and save more klasses in the db
        klasService.addNewKlasInitial(new Klas(2, KlasLetterEnum.A));
        klasService.addNewKlasInitial(new Klas(2, KlasLetterEnum.B));
        klasService.addNewKlasInitial(new Klas(2, KlasLetterEnum.C));
        klasService.addNewKlasInitial(new Klas(2, KlasLetterEnum.D));

        //Create and save teachers in the db
        Teacher teacher1 = new Teacher("Gabriela", "Gabrielova", "9205386678");
        Teacher teacher2 = new Teacher("Dobromir", "Dobromirov", "9200000000");
        Teacher teacher3 = new Teacher("Todor", "Todorov", "9292888888");
        Teacher teacher4 = new Teacher("Mariela", "Marielova", "9200920092");
        Teacher teacher5 = new Teacher("Vlad", "Vladimirovski", "vladvlad11");
        teacherService.addNewTeacherInitial(teacher1);
        teacherService.addNewTeacherInitial(teacher2);
        teacherService.addNewTeacherInitial(teacher3);
        teacherService.addNewTeacherInitial(teacher4);
        teacherService.addNewTeacherInitial(teacher5);


        //Create and save subjects in the db
        Subject math = new Subject("Math");
        Subject literature = new Subject("Literature");
        Subject biology = new Subject("Biology");
        Subject geography = new Subject("Geography");
        Subject history = new Subject("History");
        Subject sport = new Subject("Sport");
        subjectService.addNewSubjectInitial(math);
        subjectService.addNewSubjectInitial(literature);
        subjectService.addNewSubjectInitial(biology);
        subjectService.addNewSubjectInitial(geography);
        subjectService.addNewSubjectInitial(history);
        subjectService.addNewSubjectInitial(sport);

        //Create and save several subject assignments in the db
        Assignment asgnmnt1_1A_math = new Assignment(math, klas1A, teacher1);
        Assignment asgnmnt2_1B_math = new Assignment(math, klas1B, teacher1);
        Assignment asgnmnt3_1C_math = new Assignment(math, klas1C, teacher2);
        Assignment asgnmnt4_1D_math = new Assignment(math, klas1D, teacher2);
        Assignment asgnmnt5_1A_biology = new Assignment(biology, klas1A, teacher3);
        Assignment asgnmnt6_1B_biology = new Assignment(biology, klas1B, teacher3);
        Assignment asgnmnt7_1A_geography = new Assignment(geography, klas1A, teacher4);
        Assignment asgnmnt8_1B_geography = new Assignment(geography, klas1B, teacher4);
        Assignment asgnmnt9_1A_literature = new Assignment(literature, klas1A, teacher1);
        Assignment asgnmnt10_1A_sport = new Assignment(sport, klas1A, teacher5);
        Assignment asgnmnt11_1A_math = new Assignment(math, klas1A, teacher2); //another teacher math for klas 1A
        assignmentService.addNewAssignmentInitial(asgnmnt1_1A_math);
        assignmentService.addNewAssignmentInitial(asgnmnt2_1B_math);
        assignmentService.addNewAssignmentInitial(asgnmnt3_1C_math);
        assignmentService.addNewAssignmentInitial(asgnmnt4_1D_math);
        assignmentService.addNewAssignmentInitial(asgnmnt5_1A_biology);
        assignmentService.addNewAssignmentInitial(asgnmnt6_1B_biology);
        assignmentService.addNewAssignmentInitial(asgnmnt7_1A_geography);
        assignmentService.addNewAssignmentInitial(asgnmnt8_1B_geography);
        assignmentService.addNewAssignmentInitial(asgnmnt9_1A_literature);
        assignmentService.addNewAssignmentInitial(asgnmnt10_1A_sport);
        assignmentService.addNewAssignmentInitial(asgnmnt11_1A_math);


        //Create and save several marks for student 1 in the db
        Mark student1_klas1A_subjMath_mark1 = new Mark(student1A_1, asgnmnt1_1A_math, 6);
        Mark student1_klas1A_subjMath_mark2 = new Mark(student1A_1, asgnmnt1_1A_math, 6);
        Mark student1_klas1A_subjMath_mark3 = new Mark(student1A_1, asgnmnt1_1A_math, 6);
        Mark student1_klas1A_subjMath_mark4 = new Mark(student1A_1, asgnmnt1_1A_math, 6);
        Mark student1_klas1A_subjMath_mark5 = new Mark(student1A_1, asgnmnt11_1A_math, 2);
        marksService.addMarksDataInitial(student1_klas1A_subjMath_mark1);
        marksService.addMarksDataInitial(student1_klas1A_subjMath_mark2);
        marksService.addMarksDataInitial(student1_klas1A_subjMath_mark3);
        marksService.addMarksDataInitial(student1_klas1A_subjMath_mark4);
        marksService.addMarksDataInitial(student1_klas1A_subjMath_mark5);

        Mark student1_klas1A_subjBiol_mark1 = new Mark(student1A_1, asgnmnt5_1A_biology, 3);
        Mark student1_klas1A_subjBiol_mark2 = new Mark(student1A_1, asgnmnt5_1A_biology, 4);
        Mark student1_klas1A_subjBiol_mark3 = new Mark(student1A_1, asgnmnt5_1A_biology, 5);
        marksService.addMarksDataInitial(student1_klas1A_subjBiol_mark1);
        marksService.addMarksDataInitial(student1_klas1A_subjBiol_mark2);
        marksService.addMarksDataInitial(student1_klas1A_subjBiol_mark3);

        Mark student1_klas1A_subjGeogr_mark1 = new Mark(student1A_1, asgnmnt7_1A_geography, 6);
        Mark student1_klas1A_subjGeogr_mark2 = new Mark(student1A_1, asgnmnt7_1A_geography, 6);
        Mark student1_klas1A_subjGeogr_mark3 = new Mark(student1A_1, asgnmnt7_1A_geography, 6);
        marksService.addMarksDataInitial(student1_klas1A_subjGeogr_mark1);
        marksService.addMarksDataInitial(student1_klas1A_subjGeogr_mark2);
        marksService.addMarksDataInitial(student1_klas1A_subjGeogr_mark3);

        Mark student1_klas1A_subjLitera_mark1 = new Mark(student1A_1, asgnmnt9_1A_literature, 6);
        Mark student1_klas1A_subjLitera_mark2 = new Mark(student1A_1, asgnmnt9_1A_literature, 5);
        Mark student1_klas1A_subjLitera_mark3 = new Mark(student1A_1, asgnmnt9_1A_literature, 5);
        marksService.addMarksDataInitial(student1_klas1A_subjLitera_mark1);
        marksService.addMarksDataInitial(student1_klas1A_subjLitera_mark2);
        marksService.addMarksDataInitial(student1_klas1A_subjLitera_mark3);

        Mark student1_klas1A_subjSport_mark1 = new Mark(student1A_1, asgnmnt10_1A_sport, 4);
        Mark student1_klas1A_subjSport_mark2 = new Mark(student1A_1, asgnmnt10_1A_sport, 4);
        Mark student1_klas1A_subjSport_mark3 = new Mark(student1A_1, asgnmnt10_1A_sport, 4);
        marksService.addMarksDataInitial(student1_klas1A_subjSport_mark1);
        marksService.addMarksDataInitial(student1_klas1A_subjSport_mark2);
        marksService.addMarksDataInitial(student1_klas1A_subjSport_mark3);


        //Create and save several absences for student 1 in the db
        Absence student1_klas1A_subjMath_absence1 = new Absence(student1A_1, asgnmnt1_1A_math, 2, AbsenceTypeEnum.OLYMPIAD);
        Absence student1_klas1A_subjMath_absence2 = new Absence(student1A_1, asgnmnt1_1A_math, 2, AbsenceTypeEnum.OLYMPIAD);
        Absence student1_klas1A_subjBiol_absence3 = new Absence(student1A_1, asgnmnt5_1A_biology, 1, AbsenceTypeEnum.STUDENTGUILTY);
        Absence student1_klas1A_subjMath_absence4 = new Absence(student1A_1, asgnmnt1_1A_math, 1, AbsenceTypeEnum.HOSPITAL);
        Absence student1_klas1A_subjMath_absence5 = new Absence(student1A_1, asgnmnt1_1A_math, 2, AbsenceTypeEnum.OLYMPIAD);
        Absence student1_klas1A_subjMath_absence6 = new Absence(student1A_1, asgnmnt1_1A_math, 2, AbsenceTypeEnum.STUDENTGUILTY);
        absenceService.addAbsencesDataInitial(student1_klas1A_subjMath_absence1);
        absenceService.addAbsencesDataInitial(student1_klas1A_subjMath_absence2);
        absenceService.addAbsencesDataInitial(student1_klas1A_subjBiol_absence3);
        absenceService.addAbsencesDataInitial(student1_klas1A_subjMath_absence4);
        absenceService.addAbsencesDataInitial(student1_klas1A_subjMath_absence5);
        absenceService.addAbsencesDataInitial(student1_klas1A_subjMath_absence6);
    }
}

