package id.co.mii.LMS.Service;


import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import id.co.mii.LMS.Models.AppUserDetail;
import id.co.mii.LMS.Models.User;
import id.co.mii.LMS.Repository.UserRepository;

@Service
@AllArgsConstructor
public class AppUserDetailService implements UserDetailsService {

  private UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username)
    throws UsernameNotFoundException {
    User user = userRepository
      .findByUsernameOrPerson_Email(username, username)
      .orElseThrow(() ->
        new UsernameNotFoundException("Username or Email incorrect!!!")
      );

    return new AppUserDetail(user);
  }
}