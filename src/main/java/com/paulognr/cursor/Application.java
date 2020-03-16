package com.paulognr.cursor;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.paulognr.cursor.repository.SimpleCursorRepository;
import com.paulognr.cursor.repository.Person;
import com.paulognr.cursor.repository.PersonRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = SimpleCursorRepository.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        app.run(args);
    }

    @Bean
    public CommandLineRunner run(PersonRepository personRepository){
        return (String[] args) -> {
            personRepository.saveAll(
                    Stream.of(
                            new Person("Person 1"),
                            new Person("Person 2"),
                            new Person("Person 3"),
                            new Person("Person 4"),
                            new Person("Person 5"),
                            new Person("Person 6"),
                            new Person("Person 7"))
                    .collect(Collectors.toList()));
        };
    }
}
