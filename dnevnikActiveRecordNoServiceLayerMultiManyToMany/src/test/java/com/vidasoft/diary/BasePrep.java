package com.vidasoft.diary;

import com.vidasoft.diary.model.*;

import javax.transaction.Transactional;
import java.util.Locale;
import java.util.Set;

@Transactional
public abstract class BasePrep {
    public static final String TEST_BASE_URLPATH = "http://localhost:8083";

    protected static final String EGNSTUDENT1A_1 = "1111111111";
    protected static final String EGNSTUDENT1A_2 = "1111111112";
    protected static final String EGNSTUDENT1A_3 = "1111111113";
    protected static final String EGNSTUDENT1A_4 = "1111111114";
    protected static final String EGNSTUDENT1A_5 = "1111111115";
    protected static final String EGNSTUDENT1A_6 = "1111111116";
    protected static final String EGNSTUDENT1A_7 = "1111111117";
    protected static final String EGNSTUDENT1B_8 = "1111111118";
    protected static final String EGNSTUDENT1C_9 = "1111111119";

    protected static final String EGNTEACHER_1 = "9292929201";
    protected static final String EGNTEACHER_2 = "9292929202";
    protected static final String EGNTEACHER_3 = "9292929203";
    protected static final String EGNTEACHER_4 = "9292929204";
    protected static final String EGNTEACHER_5 = "9292929205";

    protected static final String SUBJECTMATH_1 = "Math";
    protected static final String SUBJECTLITERA_2 = "Literature";
    protected static final String SUBJECTBIOL_3 = "Biology";
    protected static final String SUBJECTGEOGRA_4 = "Geography";
    protected static final String SUBJECTHISTORY_5 = "History";
    protected static final String SUBJECTSPORT_6 = "Sport";
    protected static final String SUBJECTPAINTING_7 = "Painting";

    protected void setUp() {
        //Create and save klazzes 6A, 6B, 6C and 6D in the database
        Clazz clazz6A = new Clazz("6", "A");
        Clazz clazz6B = new Clazz("6", "B");
        Clazz clazz6C = new Clazz("6", "C");
        Clazz clazz6D = new Clazz("6", "D");
        clazz6A.persist();
        clazz6B.persist();
        clazz6C.persist();
        clazz6D.persist();

        //Create and save the students in the db
        Student student6A_1 = new Student("Svilen", "Velikov", EGNSTUDENT1A_1).setClazz(clazz6A);
        Student student6A_2 = new Student("Georgi", "Georgiev", EGNSTUDENT1A_2).setClazz(clazz6A);
        Student student6A_3 = new Student("Vassil", "Levski", EGNSTUDENT1A_3).setClazz(clazz6A);
        Student student6A_4 = new Student("Boiko", "Borisov", EGNSTUDENT1A_4).setClazz(clazz6A);
        Student student6A_5 = new Student("Zahari", "Stoyanov", EGNSTUDENT1A_5).setClazz(clazz6A);
        Student student6A_6 = new Student("Kiril", "Petkov", EGNSTUDENT1A_6).setClazz(clazz6A);
        //student 7th does not have a klas yet
        Student student___7 = new Student("Gospodin", "Gospodinov", EGNSTUDENT1A_7);
        Student student6B_8 = new Student("Petar", "Petrov", EGNSTUDENT1B_8).setClazz(clazz6B);
        Student student6C_9 = new Student("Dimitar", "Dimitrov", EGNSTUDENT1C_9).setClazz(clazz6C);
        student6A_1.persist();
        student6A_2.persist();
        student6A_3.persist();
        student6A_4.persist();
        student6A_5.persist();
        student6A_6.persist();
        student___7.persist();
        student6B_8.persist();
        student6C_9.persist();

        //Create and save more clazzes in the db
        new Clazz("1", "A").persist();
        new Clazz("1", "B").persist();
        new Clazz("1", "C").persist();
        new Clazz("1", "D").persist();
        new Clazz("2", "A").persist();
        new Clazz("2", "B").persist();
        new Clazz("2", "C").persist();
        new Clazz("2", "D").persist();
        new Clazz("3", "A").persist();
        new Clazz("3", "B").persist();
        new Clazz("3", "C").persist();
        new Clazz("3", "D").persist();
        new Clazz("4", "A").persist();
        new Clazz("4", "B").persist();
        new Clazz("4", "C").persist();
        new Clazz("4", "D").persist();
        new Clazz("5", "A").persist();
        new Clazz("5", "B").persist();
        new Clazz("5", "C").persist();
        new Clazz("5", "D").persist();

        //Create and save teachers in the db
        Teacher teacher1 = new Teacher("Gabriela", "Gabrielova", EGNTEACHER_1);
        Teacher teacher2 = new Teacher("Dobromir", "Dobromirov", EGNTEACHER_2);
        Teacher teacher3 = new Teacher("Todor", "Todorov", EGNTEACHER_3);
        Teacher teacher4 = new Teacher("Mariela", "Marielova", EGNTEACHER_4);
        Teacher teacher5 = new Teacher("Vlad", "Vladimirovski", EGNTEACHER_5);
        teacher1.persist();
        teacher2.persist();
        teacher3.persist();
        teacher4.persist();
        teacher5.persist();

        //FRONT_SIDE of the bidirectional relation clazzes <-> teachers
        //Assign a clazz which teachers
        clazz6A.teachers.addAll(Set.of(teacher1, teacher2, teacher3, teacher4));
        clazz6B.teachers.addAll(Set.of(teacher3, teacher4));
        clazz6C.teachers.addAll(Set.of(teacher1, teacher2));
        clazz6D.teachers.add(teacher2);


        //Create and save subjects in the db
        Subject math = new Subject(SUBJECTMATH_1.toLowerCase(Locale.ROOT));
        Subject literature = new Subject(SUBJECTLITERA_2.toLowerCase(Locale.ROOT));
        Subject biology = new Subject(SUBJECTBIOL_3.toLowerCase(Locale.ROOT));
        Subject geography = new Subject(SUBJECTGEOGRA_4.toLowerCase(Locale.ROOT));
        Subject history = new Subject(SUBJECTHISTORY_5.toLowerCase(Locale.ROOT));
        Subject sport = new Subject(SUBJECTSPORT_6.toLowerCase(Locale.ROOT));
        Subject painting = new Subject(SUBJECTPAINTING_7.toLowerCase(Locale.ROOT));
        math.persist();
        literature.persist();
        biology.persist();
        geography.persist();
        history.persist();
        sport.persist();
        painting.persist();

        //FRONT_SIDE of the bidirectional relation subjects <-> teachers
        //Assign a subject is taught by which teachers
        math.teachers.addAll(Set.of(teacher1, teacher2));
        literature.teachers.add(teacher1);
        biology.teachers.add(teacher3);
        geography.teachers.add(teacher4);
        sport.teachers.addAll(Set.of(teacher1, teacher4));

        //FRONT_SIDE of the bidirectional relation subjects <-> clazzes
        //Assign a subject is taught in which clazzes
        math.clazzes.addAll(Set.of(clazz6A, clazz6B, clazz6C, clazz6D));
        literature.clazzes.add(clazz6A);
        biology.clazzes.addAll(Set.of(clazz6A, clazz6B));
        sport.clazzes.addAll(Set.of(clazz6A, clazz6C));


        // Create and save grades in the db
        Grade student6A_1_subjMath_grade1 = new Grade(6, student6A_1, clazz6A, math, teacher1);
        Grade student6A_1_subjMath_grade2 = new Grade(6, student6A_1, clazz6A, math, teacher1);
        Grade student6A_1_subjMath_grade3 = new Grade(6, student6A_1, clazz6A, math, teacher1);
        Grade student6A_1_subjMath_grade4 = new Grade(6, student6A_1, clazz6A, math, teacher1);
        Grade student6A_1_subjMath_grade5 = new Grade(6, student6A_1, clazz6A, math, teacher2);
        student6A_1_subjMath_grade1.persist();
        student6A_1_subjMath_grade2.persist();
        student6A_1_subjMath_grade3.persist();
        student6A_1_subjMath_grade4.persist();
        student6A_1_subjMath_grade5.persist();
        Grade student6A_1_subjBiol_grade6 = new Grade(3, student6A_1, clazz6A, biology, teacher3);
        Grade student6A_1_subjBiol_grade7 = new Grade(4, student6A_1, clazz6A, biology, teacher3);
        Grade student6A_1_subjBiol_grade8 = new Grade(5, student6A_1, clazz6A, biology, teacher3);
        student6A_1_subjBiol_grade6.persist();
        student6A_1_subjBiol_grade7.persist();
        student6A_1_subjBiol_grade8.persist();
        Grade student6A_1_subjGeogr_grade9 = new Grade(6, student6A_1, clazz6A, geography, teacher4);
        Grade student6A_1_subjGeogr_grade10 = new Grade(6, student6A_1, clazz6A, geography, teacher4);
        Grade student6A_1_subjGeogr_grade11 = new Grade(6, student6A_1, clazz6A, geography, teacher4);
        student6A_1_subjGeogr_grade9.persist();
        student6A_1_subjGeogr_grade10.persist();
        student6A_1_subjGeogr_grade11.persist();
        Grade student6A_1_subjLiter_grade9 = new Grade(6, student6A_1, clazz6A, literature, teacher1);
        Grade student6A_1_subjLiter_grade10 = new Grade(5, student6A_1, clazz6A, literature, teacher1);
        Grade student6A_1_subjLiter_grade11 = new Grade(5, student6A_1, clazz6A, literature, teacher1);
        student6A_1_subjLiter_grade9.persist();
        student6A_1_subjLiter_grade10.persist();
        student6A_1_subjLiter_grade11.persist();
        Grade student6A_1_subjSport_grade12 = new Grade(4, student6A_1, clazz6A, sport, teacher5);
        Grade student6A_1_subjSport_grade13 = new Grade(4, student6A_1, clazz6A, sport, teacher5);
        Grade student6A_1_subjSport_grade14 = new Grade(4, student6A_1, clazz6A, sport, teacher5);
        student6A_1_subjSport_grade12.persist();
        student6A_1_subjSport_grade13.persist();
        student6A_1_subjSport_grade14.persist();

        // Create and save absences in the db
        Absence student6A_1_subjMath_absence1 = new Absence(2, student6A_1, clazz6A, math, teacher1);
        Absence student6A_1_subjMath_absence2 = new Absence(2, student6A_1, clazz6A, math, teacher1);
        Absence student6A_1_subjBiol_absence3 = new Absence(1, student6A_1, clazz6A, biology, teacher3);
        Absence student6A_1_subjMath_absence4 = new Absence(1, student6A_1, clazz6A, math, teacher2);
        Absence student6A_1_subjMath_absence5 = new Absence(1, student6A_1, clazz6A, math, teacher2);
        Absence student6A_1_subjMath_absence6 = new Absence(1, student6A_1, clazz6A, math, teacher1);
        student6A_1_subjMath_absence1.persist();
        student6A_1_subjMath_absence2.persist();
        student6A_1_subjBiol_absence3.persist();
        student6A_1_subjMath_absence4.persist();
        student6A_1_subjMath_absence5.persist();
        student6A_1_subjMath_absence6.persist();
    }

    protected void tearDown() {
        //Deleting firstly records from tables which are not a foreign key to other tables
        Absence.deleteAll();
        Grade.deleteAll();

        Student.deleteAll();

        //ManyToMany  се трие лесно
        Clazz.deleteAll();
        Teacher.deleteAll();
        Subject.deleteAll();
    }
}
