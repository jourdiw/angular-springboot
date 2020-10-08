# Building an application with Angular 10 and Spring Boot

My goal for this project is to learn more about Angular, Spring Boot and creating a RESTful application. And to slowly but surely integrate other tools into the project.

I started with an Angular Tutorial [Tour of Heroes](https://angular.io/tutorial) and a [Spring Boot Tutorial](https://spring.io/guides/gs/spring-boot/). 

The following versions are used:
- Angular 10
- Java 8

# How to launch the application

Prerequisites: 
- Java 8
- Maven 3.2+
- [Set up](https://angular.io/guide/setup-local) a local environment for Angular

## Launching the application from the Command Line

Clone the repository:
```
git clone git@github.com:jourdiw/angular-springboot.git
```

To launch the _back-end_:
```
cd spring-boot/
./mvnw spring-boot:run
```
The back-end will be exposed at [localhost:8080](http://localhost:8080).

After launching the back-end, you can access the _SwaggerUI_ at [localhost:8080/swagger-ui](http://localhost:8080/swagger-ui/)

In another terminal window, navigate to the root of the project.
To launch the _front-end_:
```
cd angular-tour-of-heroes/
ng serve --open
```
A window or a tab in an existing window of your navigator will open to [localhost:4200](http://localhost:4200)

# What's next?
- Create unit tests for the back and front
- Add Jenkins to the project
- Dockerize the application
