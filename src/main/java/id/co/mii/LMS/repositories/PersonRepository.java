package id.co.mii.LMS.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import id.co.mii.LMS.Models.Person;

public interface PersonRepository extends JpaRepository<Person, Integer> {
    @Query("SELECT p.id FROM Person p INNER JOIN p.user u WHERE u.username = :username")
    Integer findIdByUsername(@Param("username") String username);

    @Query("SELECT p FROM Person p INNER JOIN p.user u WHERE u.username = :username")
    Person findByUsername(@Param("username") String username);
}
