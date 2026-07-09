//package net.projects.springboot.dto;
//
//import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.NotEmpty;
//import lombok.Getter;
//import lombok.Setter;
//
//@Getter
//@Setter
//public class UpdatePasswordDto {
//
//    @NotEmpty(message = "New pass should not be empty")
//    @NotEmpty  (message = "Please provide a valid pass")
//    private String newPassword;
//}
package net.projects.springboot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePasswordDto {
    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String newPassword;

    @NotBlank(message = "Confirm password is required")
    private String confirmNewPassword;
}