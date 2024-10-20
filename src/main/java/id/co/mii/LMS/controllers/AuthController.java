package id.co.mii.LMS.Controller;


import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import id.co.mii.LMS.Models.User;
import id.co.mii.LMS.Models.dto.UserRequest;
import id.co.mii.LMS.Models.dto.request.LoginRequest;
import id.co.mii.LMS.Models.dto.response.LoginResponse;
import id.co.mii.LMS.Service.AuthService;

@RestController
@AllArgsConstructor
@RequestMapping
public class AuthController {

  private AuthService authService;

  @PostMapping("/register")
  public User registrasi(@RequestBody UserRequest userRequest) {
    return authService.register(userRequest);
  }

  @PostMapping("/login")
  public LoginResponse login(@RequestBody LoginRequest loginRequest) {
    return authService.login(loginRequest);
  }
}
