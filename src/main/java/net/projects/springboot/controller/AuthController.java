package net.projects.springboot.controller;
import net.projects.springboot.dto.RegistrationDto;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.validation.Valid;
import net.projects.springboot.entity.User;
import net.projects.springboot.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class AuthController {

    private UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // handler method to handle login page request
    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }
    // handler method to handle user registration request
    @GetMapping("/register")
    public String showRegistrationForm(Model model){
        // this object holds form data
        RegistrationDto user = new RegistrationDto();
        model.addAttribute("user", user);
        return "register";
    }

    // handler method to handle user registration form submit request
    @PostMapping("/register/save")
    public String register(@Valid @ModelAttribute("user") RegistrationDto user,
                           BindingResult result,
                           Model model){
        User existingUser = userService.findByEmail(user.getEmail());
        if(existingUser != null && existingUser.getEmail() !=null && !existingUser.getEmail().isEmpty()){
            result.rejectValue("email", null, "There is already a user with same email id");
        }

        if(result.hasErrors()){
            model.addAttribute("user", user);
            return "register";
        }
        userService.saveUser(user);
        return "redirect:/register?success";
    }




    // handler method to show the update email form
    @GetMapping("/update-email")
    public String showUpdateEmailForm(Principal principal, Model model){
        User user = userService.findByEmail(principal.getName());

        if(user == null){
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
}
