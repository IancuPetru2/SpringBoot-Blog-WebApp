package net.projects.springboot.controller;
import lombok.RequiredArgsConstructor;
import net.projects.springboot.dto.RegistrationDto;
import net.projects.springboot.dto.UpdatePasswordDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import net.projects.springboot.entity.User;
import net.projects.springboot.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import java.security.Principal;
//import javax.validation.Valid

@Controller
public class AuthController {

    private UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // handler method to handle login page request
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // handler method to handle user registration request
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // this object holds form data
        RegistrationDto user = new RegistrationDto();
        model.addAttribute("user", user);
        return "register";
    }

    // handler method to handle user registration form submit request
    @PostMapping("/register/save")
    public String register(@Valid @ModelAttribute("user") RegistrationDto user,
                           BindingResult result,
                           Model model) {
        User existingUser = userService.findByEmail(user.getEmail());
        if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
            result.rejectValue("email", null, "There is already a user with same email id");
        }

        if (result.hasErrors()) {
            user.setPassword(null);
            model.addAttribute("user", user);
            return "register";
        }
        userService.saveUser(user);
        return "redirect:/register?success";

    }


    // handler method to show the update email form
    @GetMapping("/update-email")
    public String showUpdateEmailForm(Principal principal, Model model) {
        User user = userService.findByEmail(principal.getName());

        if (user == null) {
            return "redirect:/login?error";
        }
        model.addAttribute("user", user);
        return "admin/update-email";
    }

    @PostMapping("/update-email")
    public String updateEmail(@RequestParam("newEmail") String newEmail,
                              Principal principal,
                              Model model) {

        User currentUser = userService.findByEmail(principal.getName());

        User existingUser = userService.findByEmail(newEmail);
        if (existingUser != null) {
            model.addAttribute("emailError", "There is already a user with that email address");
            model.addAttribute("user", currentUser);
            return "admin/update-email";
        }

        userService.updateEmail(currentUser.getId(), newEmail);
        return "redirect:/update-email?success";
    }



@GetMapping("/update-password")
public String showUpdatePasswordForm(Principal principal, Model model) {
    User user = userService.findByEmail(principal.getName());

    if (user == null) {
        return "redirect:/login?error";
    }

    model.addAttribute("updatePasswordDto", new UpdatePasswordDto()); // matches th:object
    model.addAttribute("user", user);
    return "admin/update-password";
}

    @PostMapping("/update-password")
    public String updatePassword(@Valid @ModelAttribute("updatePasswordDto") UpdatePasswordDto dto,
                                 BindingResult result,
                                 Principal principal,
                                 Model model) {

        if (result.hasErrors()) {
            model.addAttribute("updatePasswordDto", new UpdatePasswordDto());
            return "admin/update-password";
        }

        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            model.addAttribute("updatePasswordDto", new UpdatePasswordDto()); // ✅ reset
            model.addAttribute("error", "New passwords do not match");
            return "admin/update-password";
        }

        try {
            User currentUser = userService.findByEmail(principal.getName());
            userService.updatePassword(
                    String.valueOf(currentUser.getId()),
                    dto.getCurrentPassword(),
                    dto.getNewPassword()
            );
            model.addAttribute("updatePasswordDto", new UpdatePasswordDto()); // ✅ reset
            model.addAttribute("message", "Password updated successfully");

        } catch (RuntimeException e) {
            model.addAttribute("updatePasswordDto", new UpdatePasswordDto()); // ✅ reset
            model.addAttribute("error", e.getMessage());
        }

        return "admin/update-password";
    }
    @GetMapping("/settings")
    public String settings() {
        return "admin/settings";
    }
}