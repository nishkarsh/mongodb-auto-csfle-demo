package com.intentfilter.mongoencryptdemo.models;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import static com.intentfilter.mongoencryptdemo.models.Person.Fields.CollectionName;

@Document(CollectionName)
public class Person {
    @Indexed(unique = true)
    @Field(Fields.Pan)
    private String pan;

    private String name;

    private int age;

    public Person(String pan, String name, int age) {
        this.pan = pan;
        this.name = name;
        this.age = age;
    }

    public String getPan() {
        return pan;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public interface Fields {
        String CollectionName = "person";
        String Pan = "pan";
    }
}
