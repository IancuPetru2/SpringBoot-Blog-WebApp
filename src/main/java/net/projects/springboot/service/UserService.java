package net.projects.springboot.service;

import net.projects.springboot.dto.RegistrationDto;
import net.projects.springboot.entity.User;

public interface UserService {
    void saveUser(RegistrationDto registrationDto);
    User findById(Long id);
    User findByEmail(String email);
    void updateEmail(Long id, String newEmail);
}

//   User findById(Long id);
//
//    void updateUser(Long id, RegistrationDto user);
//}
