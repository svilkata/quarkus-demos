# Schooldiary Project

## Description  
SchoolDiary project with ActiveRecord of Panache implemented.  
Folders organized per object, i.e. object Student has a folder student with all the layers like  StudentService, StudentRestResource, StudentMapStruct, StudentDto, etc.  
Implemented with Service layer in each folder.  
Used only ManyToOne relations in the relational database.


## Schema
Using only Many-To-One relation
![img.png](img.png)



## Testing
All the tests can be run altogether with the terminal command: mvn test
Except the tests for  StudentService.java and StudentRestResource.java - they are made only with the TestOrdererMethod and without the @BeforeEach and @AfterEach annotated methods.
You can run the tests for StudentService.java and StudentRestResource.java manually.

We should remove the @PostConstruct annotation for starting the tests


## План за endpoint-и:

В момента има едно ограничение, и то е че един ученик не може да се мести от един клас в друг клас.

I) klas

get на /klas – връща всички класове  
вход json: няма  
изход json: масив от {klasId, klasLetter, klasNumber}  

get на /klas/{id} – взема данни за съответния клас  
вход json: няма  
изход json: {klasId, klasLetter, klasNumber}  

post на /klas – добавяне нов клас  
вход json: {klasLetter, klasNumber}         
изход json: {klasId, klasLetter, klasNumber}  

get на /klas/{id}/students – взема всички студенти от даден клас  
вход json: няма    
изход json: масив от {studentId, firstName, lastName, egn, klasId, klasName}  

get на /klas/{id}/teachers – взема всички учители преподаващи на даден клас  
вход json: няма  
изход json: масив от {teacherId, firstName, lastName, egn}  

patch на /klas/{klasId}/student/{studentId} – задава на ученика в кой клас ще е  
вход json: няма  
изход json: {studentId, firstName, lastName, egn, klasId, klasName}  


II) student

get на /student – връща всички студенти  
вход json: няма  
изход json: масив от {studentId, firstName, lastName, egn, klasId, klasName}  
Когато няма зададен клас на ученика, то klasId и klasName са null и не се показват от JSON-B  

get на /student/{studentId} – връща студент по studentId  
вход json: няма  
изход json: {studentId, firstName, lastName, egn, klasId, klasName}  
Когато няма зададен клас на ученика, то klasId и klasName са null и не се показват от JSON-B  

post на /student – добавя нов ученик  
вход json: {firstName, lastName, egn}       
изход json: масив от {studentId, firstName, lastName, egn}  

patch на /student/{studentId}/klas/{klasId} – задава нов ученик в даден клас  
вход json: няма        
изход json: {studentId, firstName, lastName, egn, klasId, klasName}  


III) subject

get на /subject – връща всички учебни предмети  
вход json: няма  
изход json: масив от {subjectId, subject}  

get на /subject/{subjectId} – връща конкретен учебен предмет  
вход json: няма  
изход json: {subjectId, subject}  

post на /subject – записва нов учебен предмет  
вход json: {subject}  
изход json: масив от {subjectId, subject}  


IV) teacher

get на /teacher – връща всички учители  
вход json: няма  
изход json: масив от {teacherId, firstName, lastName, egn}  

get на /teacher/{teacherId} – връща всички учители  
вход json: няма  
изход json: {teacherId, firstName, lastName, egn}  

post на /teacher – записва нов учител  
вход json: {firstName, lastName, egn}     
изход json: масив от {teacherId, firstName, lastName, egn}  

get на /teacher/{teacherId}/klasses – връща всички класове, на които учителя преподава  
вход json: няма  
изход json: масив от {klasId, klasNumber, klasLetter}  

get на /teacher/{teacherId}/subjects – връща всички учебни предмети, по които учителя преподава  
вход json: няма       
изход json: масив от {subjectId, subject}  

get на /teacher/{teacherId}/{subjectId}/klasses – връща всички класове, на които учителя преподава по даден предмет  
вход json: няма     
изход json: масив от {klasId, klasNumber, klasLetter}  

get на /teacher/{teacherId}/{klasId}/subjects – връща всички учебни предмети, по които учителя преподава на даден клас  
вход json: няма  
изход json: масив от {subjectId, subject}  


V) assignment

post на /teacher – назначава учител да преподава на даден клас по даден предмет  
вход json: {teacherId, subjectId, klasId}  
изход json: {assignmentId, teacherId, firstName, lastName, egn}  


VI) mark

get на /mark/{studentId}/{subjectId} – връща всички оценки сортирани по дата и час на въвеждане възходящо на даден ученик по даден предмет  
вход json: няма      
изход json: масив от {markId, mark, timeAdded, teacherId, teacherNames}  

post на /mark/{studentId}/{subjectId} – прави запис за поставяне на оценка от учител на ученик по даден предмет  
вход json: {mark, teacherId}  
изход json: {markId, mark, timeAdded, teacherId, teacherFullName}  


VII) absence

get на /absence/{studentId}/{subjectId} – връща всички отсъствия сортирани по дата и час на въвеждане възходящо за даден ученик по даден предмет  
вход json: няма  
изход json: масив от {absenceId, absenceType, countHours, timeAdded, teacherId, teacherNames}  

post на /mark/{studentId}/{subjectId} – прави запис за поставяне на отсъствие - от учител на ученик по даден предмет  
вход json: {countHours, absenceType, teacherId}  
изход json: {absenceId, absenceType, countHours, timeAdded, teacherId, teacherFullName}  










## Info
This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/schooldiary-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Related Guides

- REST resources for Hibernate ORM with Panache ([guide](https://quarkus.io/guides/rest-data-panache)): Generate JAX-RS resources for your Hibernate Panache entities and repositories
- Hibernate ORM ([guide](https://quarkus.io/guides/hibernate-orm)): Define your persistent model with Hibernate ORM and JPA
- Hibernate Validator ([guide](https://quarkus.io/guides/validation)): Validate object properties (field, getter) and method parameters for your beans (REST, CDI, JPA)
- RESTEasy Classic JSON-B ([guide](https://quarkus.io/guides/rest-json)): JSON-B serialization support for RESTEasy Classic
- SmallRye OpenAPI ([guide](https://quarkus.io/guides/openapi-swaggerui)): Document your REST APIs with OpenAPI - comes with Swagger UI
- Hibernate ORM with Panache ([guide](https://quarkus.io/guides/hibernate-orm-panache)): Simplify your persistence code for Hibernate ORM via the active record or the repository pattern
- RESTEasy Classic ([guide](https://quarkus.io/guides/resteasy)): REST endpoint framework implementing JAX-RS and more

## Provided Code

### Hibernate ORM

Create your first JPA entity

[Related guide section...](https://quarkus.io/guides/hibernate-orm)

### RESTEasy JAX-RS

Easily start your RESTful Web Services

[Related guide section...](https://quarkus.io/guides/getting-started#the-jax-rs-resources)
