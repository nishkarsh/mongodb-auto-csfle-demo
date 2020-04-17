package com.intentfilter.mongoencryptdemo;

import com.intentfilter.mongoencryptdemo.models.Person;
import com.intentfilter.mongoencryptdemo.repositories.PersonRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EncryptionDemo {
    private PersonRepository personRepository;

    public EncryptionDemo(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void insertPersonAndQuery() {
        personRepository.insert(new Person("FC3546601D", "Mr. Foo", 28));
        final Person foundPerson = personRepository.findByPan("FC3546601D");

        System.out.printf("%s %s %d%n", foundPerson.getName(), foundPerson.getPan(), foundPerson.getAge());
    }
}
