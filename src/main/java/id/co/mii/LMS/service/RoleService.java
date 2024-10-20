package id.co.mii.LMS.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import id.co.mii.LMS.Models.Role;
import id.co.mii.LMS.Repository.RoleRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RoleService {
  private RoleRepository roleRepository;

  public List<Role> getAll() {
    return roleRepository.findAll();
  }

  public Role getById(Integer id) {
    return roleRepository
        .findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found!!!"));
  }

  public Role create(Role role) {
    return roleRepository.save(role);
  }

  public Role update(Integer id, Role role) {
    getById(id);
    role.setId(id);
    return roleRepository.save(role);
  }

  public Role delete(Integer id) {
    Role role = getById(id);
    roleRepository.delete(role);
    return role;
  }

  public List<Role> getByIds(List<Integer> ids) {
    List<Role> roles = new ArrayList<>();
    for (Integer id : ids) {
      Role role = getById(id);
      if (role != null) {
        roles.add(role);
      }
    }
    return roles;
  }
}
