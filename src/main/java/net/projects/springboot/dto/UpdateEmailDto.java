
package net.projects.springboot.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
public class UpdateEmailDto {

    @NotEmpty(message = "New email should not be empty")
    @Email(message = "Please provide a valid email")
    private String newEmail;
}

