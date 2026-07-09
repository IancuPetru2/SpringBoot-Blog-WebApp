package net.projects.springboot.service;

import net.projects.springboot.dto.RegistrationDto;
import net.projects.springboot.entity.User;


public interface UserService<UpdatePasswordRequest> {
    void saveUser(RegistrationDto registrationDto);
    User findById(Long id);
    User findByEmail(String email);
    void updateEmail(Long id, String newEmail);
    void updatePassword (String id, String currentPassword, String newPassword);
}


