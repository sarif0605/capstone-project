package id.co.mii.LMS.Service;


import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import id.co.mii.LMS.Models.Person;
import id.co.mii.LMS.Repository.PersonRepository;

@Service
@AllArgsConstructor
public class PersonService {

  private PersonRepository personRepository;

  public List<Person> getAll() {
    return personRepository.findAll();
  }

  public Person getById(Integer id) {
    return personRepository
      .findById(id)
      .orElseThrow(() ->
        new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          "Person not found!!!"
        )
      );
  }

  public Person create(Person person) {
    return personRepository.save(person);
  }

  public Person update(Integer id, Person person) {
    getById(id);
    person.setId(id);
    return personRepository.save(person);
  }

  public Person delete(Integer id) {
    Person person = getById(id);
    personRepository.delete(person);
    return person;
  }
}
