package id.co.mii.LMS.Service;


import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import id.co.mii.LMS.Models.Person;
import id.co.mii.LMS.Models.Role;
import id.co.mii.LMS.Models.User;
import id.co.mii.LMS.Models.dto.UserRequest;
import id.co.mii.LMS.Repository.UserRepository;

@Service
@AllArgsConstructor
public class UserService {

  private UserRepository userRepository;
  private ModelMapper modelMapper;
  private RoleService roleService;

  public List<User> getAll() {
    return userRepository.findAll();
  }

  public User getById(Integer id) {
    return userRepository
      .findById(id)
      .orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!!!")
      );
  }

  public User create(UserRequest userRequest) {
    Person person = modelMapper.map(userRequest, Person.class);
    User user = modelMapper.map(userRequest, User.class);

    person.setUser(user);
    user.setPerson(person);

    // set default role
    List<Role> roles = new ArrayList<>();
    roles.add(roleService.getById(1));
    user.setRoles(roles);

    return userRepository.save(user);
  }

  public User update(Integer id, User user) {
    getById(id);
    user.setId(id);
    return userRepository.save(user);
  }

  public User delete(Integer id) {
    User user = getById(id);
    userRepository.delete(user);
    return user;
  }
}
