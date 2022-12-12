##  School Dnevnik 


##  Description  
Dnevnik project with ActiveRecord of Panache implemented.  
Folders organized per object, i.e. object Student has a folder student with all the layers like  StudentResource, StudentDTO, etc.  
Implemented without Service layer!!!  
Used ManyToMany bidirectional relations implemented with Set<...>  in the relational database.



## Schema
![img.png](img.png)


## Plan for endpoints:  
В момента има следните 2 ограничения на проекта:  
** Един ученик не може да се мести от един клас в друг клас.  
  
** Възможност да се получат нежелани назначения
* t1 и t2 преподават по biol, music и history.  
* t1 и t2 преподават на 6А клас.  
* На 6A се преподава  biol, music и history.  
* В случая t1 може да преподава biol и music на 6A, a t2 да преподава history на 6A. А може и да е обратното  
* Проблемът е, че изрично не сме дефинирали, че учителите имат право да си разменят предметите - и когато правим проверка за писане на оценки например, ще е възможно учител t1 да пише оценки и по два предмета, а да има реално право да пише оценки само по един от трите предмета  
* Затова можем да използваме още една допълнителна таблица Assignments с три ключа (ManyToOne релации към Clazz, Subject и Teacher) - както е направено в предходния проект 




* I) clazz

method createClazz  
post на /clazz – добавяне нов клас  
вход json: {clazzNumber, subclazzInitial}  
изход json: няма, връща статус 201 Created  

method getAllClazzes  
get на /clazz – връща всички класове  
вход json: няма  
изход json: масив от {clazzId, clazzNumber, subclazzInitial}  

method getClazzByName  
get на /clazz/name/{clazzName} – взема по име на клас данни за класа  
вход json: няма  
изход json: {clazzId, clazzNumber, subclazzInitial}  

method addStudentToClazz  
patch на /clazz/{clazzId}/student?studentId={studentId} – добавя ученик към клас  
вход json: няма  
изход json: няма, връща статус 204 No content  

method getClazzesByTeacher  
get на /clazz/teacher/{teacherId} – взема всички класове, на които даден учител преподава  
вход json: няма  
изход json: масив от {clazzId, clazzNumber, subclazzInitial}  

method assignSubjectToClazzAllowed  
patch на /clazz/{clazzId}/allowedsubjects?subjectId={subjectId} – определя/задава какъв предмет може(е позволено) да се преподва на даден клас  
вход json: няма  
изход json: няма, връща статус 204 No content  

method assignTeacherToClazzAndSubject  
patch на /clazz/{clazzId}/assign?teacherId={teacherId}&subjectId={subjectId} – назначава на даден клас даден учител по даден предмет, ако учебния предмет е одобрен за преподаване за този клас  
вход json: няма  
изход json: няма, връща статус 204 No content  


* II) student

method createStudent  
post на /student – добавя нов ученик  
вход json: {firstName, lastName, identity}  
изход json: няма, връща статус 201 Created  

method getAllStudents  
get на /student – връща всички студенти  
вход json: няма  
изход json: масив от {studentId, firstName, lastName, identity, clazz}  
Когато няма зададен клас на ученика, то clazz e null и не се показва от JSON-B  

method getStudentByIdentity  
get на /student/identity/{identity} – връща студент по identity  
вход json: няма  
изход json: {studentId, firstName, lastName, identity, clazz}  
Когато няма зададен клас на ученика, то clazz e null и не се показва от JSON-B  


* III) teacher 

method createTeacher  
post на /teacher – записва нов учител  
вход json: {firstName, lastName, identity}  
изход json: няма, връща статус 201 Created  

method getAllTeachers  
get на /teacher – връща всички учители  
вход json: няма  
изход json: масив от {teacherId, firstName, lastName, identity}  

method getTeacherByIdentity  
get на /teacher/identity/{identity} – връща учител по identity  
вход json: няма  
изход json: {teacherId, firstName, lastName, identity}  

method getClazzesOfATeacher  
get на /teacher/{teacherId}/klazzes – връща всички класове, на които учителя преподава  
вход json: няма  
изход json: масив от {clazzId, clazzNumber, subclazzInitial}  

method getSubjectsOfATeacher  
get на /teacher/{teacherId}/subjects – връща всички учебни предмети, по които учителя преподава  
вход json: няма  
изход json: масив от {subjectId, name}  

method getClazzesByTeacherAndSubject  
get на /teacher/{teacherId}/clazzes/{subjectId} – връща всички класове, на които учителя преподава по даден предмет  
вход json: няма  
изход json: масив от {klasId, klasNumber, klasLetter}  

method getSubjectsByTeacherAndClazz  
get на /teacher/{teacherId}/subjects/{clazzId} – връща всички учебни предмети, по които учителя преподава на даден клас  
вход json: няма  
изход json: масив от {subjectId, name}  


* IV) subject

method createSubject  
post на /subject – записва нов учебен предмет  
вход json: {name}  
изход json: няма, връща статус 201 Created  

method getAllSubjects  
get на /subject – връща всички учебни предмети  
вход json: няма  
изход json: масив от {subjectId, name}  

method getSubjectByName  
get на /subject/name/{name} – търси по име и връща конкретен учебен предмет  
вход json: няма  
изход json: {subjectId, name}  


* V) grade

method teacherWritesGradePerStudentPerSubject
post на /grade?studentId={studentId}&subjectId={subjectId}&teacherId={teacherId} – прави запис за поставяне на оценка от учител на ученик по даден предмет  
вход json: {grade}  
изход json: {gradeId, grade, timeAdded, teacherFullName} и също така връща 201 Created и URL

method getAllGradesPerStudentPerSubject
get на /grade?studentId={studentId}&subjectId={subjectId} – връща всички оценки сортирани по дата и час на въвеждане възходящо на даден ученик по даден предмет (може и от различни учители да са оценките ако има няколко учителя назначени да преподват в даден клас по един и същи предмет)  
вход json: няма  
изход json: масив от {gradeId, grade, timeAdded, teacherFullName}  


* VI) absence

method teacherWritesAbsencePerStudentPerSubject
post на /absence?studentId={studentId}&subjectId={subjectId}&teacherId={teacherId} – прави запис за поставяне на отсъствие от учител на ученик по даден предмет  
вход json: {absenceHours}  
изход json: {absenceId, absenceHours, timeAdded, teacherFullName} и също така връща 201 Created и URL

method getAllAbsenceRecordsPerStudentPerSubject
get на /absence?studentId={studentId}&subjectId={subjectId} – връща всички записи за отсъствия на даден ученик по даден предмет (може и от различни учители да са отсъствията ако има няколко учителя назначени да преподват в даден клас по един и същи предмет)  
вход json: няма  
изход json: масив от {absenceId, absenceHours, timeAdded, teacherFullName}  

## RestAssured integration testing:  

We should remove the (@Observes StartupEvent ev) from the initSomeData method in the DataLoader class, before starting the tests
