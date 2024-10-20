package id.co.mii.LMS.Models.dto;

import java.util.List;

import lombok.Data;

@Data
public class UserRequest {
    private String name;
    private String email;
    private String phone;
    private String address;
    private String username;
    private String password;
    private List<Integer> roleIds;
}
