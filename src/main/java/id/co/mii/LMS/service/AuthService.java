package id.co.mii.LMS.Service;

import id.co.mii.LMS.Models.Person;
import id.co.mii.LMS.Models.Role;
import id.co.mii.LMS.Models.User;
import id.co.mii.LMS.Models.dto.UserRequest;
import id.co.mii.LMS.Models.dto.request.LoginRequest;
import id.co.mii.LMS.Models.dto.response.LoginResponse;
import id.co.mii.LMS.Repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private UserRepository userRepository;
  private ModelMapper modelMapper;
  private RoleService roleService;
  private PasswordEncoder passwordEncoder;
  private AuthenticationManager authenticationManager;
  private AppUserDetailService appUserDetailService;

  public User register(UserRequest userRequest) {
    Person person = modelMapper.map(userRequest, Person.class);
    User user = modelMapper.map(userRequest, User.class);

    person.setUser(user);
    user.setPerson(person);

    // set default role
   List<Role> roles = roleService.getByIds(userRequest.getRoleIds());
        user.setRoles(roles);

    // set password
    user.setPassword(passwordEncoder.encode(userRequest.getPassword()));

    return userRepository.save(user);
  }

  
  public LoginResponse login(LoginRequest loginRequest) {
    // authentication => login request = username & password
    UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(
      loginRequest.getUsername(),
      loginRequest.getPassword()
    );

    // set principle
    Authentication auth = authenticationManager.authenticate(authReq);
    SecurityContextHolder.getContext().setAuthentication(auth);

    User user = userRepository
      .findByUsernameOrPerson_Email(
        loginRequest.getUsername(),
        loginRequest.getUsername()
      )
      .get();

    UserDetails userDetails = appUserDetailService.loadUserByUsername(
      loginRequest.getUsername()
    );

    List<String> authorities = userDetails
      .getAuthorities()
      .stream()
      .map(authority -> authority.getAuthority())
      .collect(Collectors.toList());

    // response => user detail = username, email, List<GrantedAuthority>
    return new LoginResponse(
      user.getUsername(),
      user.getPerson().getEmail(),
      authorities
    );
  }
}