package id.co.mii.LMS.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import id.co.mii.LMS.Models.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsernameOrPerson_Email(String username, String email);
}
