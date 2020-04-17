package com.intentfilter.mongoencryptdemo.repositories;

import com.intentfilter.mongoencryptdemo.models.Person;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends MongoRepository<Person, String> {
    Person findByPan(String pan);
}
