package id.co.mii.LMS.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import id.co.mii.LMS.Models.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    
}
