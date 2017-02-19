package hello.dao;

import org.springframework.data.repository.CrudRepository;

import hello.entities.Greeting;

public interface GreetingRepository extends CrudRepository<Greeting, Long> {

}
