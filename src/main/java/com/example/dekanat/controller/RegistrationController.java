
package com.example.dekanat.controller;

import com.example.dekanat.dto.RegistrationForm;
import com.example.dekanat.model.AppUser;
import com.example.dekanat.model.UserRole;
import com.example.dekanat.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistrationController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm());
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("registrationForm") RegistrationForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (!form.getPassword().equals(form.getPasswordConfirmation())) {
            bindingResult.rejectValue(
                    "passwordConfirmation",
                    "error.passwordConfirmation",
                    "Пароли не совпадают"
            );
        }

        if (userRepository.existsByUsername(form.getUsername())) {
            bindingResult.rejectValue(
                    "username",
                    "error.username",
                    "Такой логин уже занят"
            );
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        AppUser student = new AppUser();
        student.setUsername(form.getUsername().trim());
        student.setPassword(passwordEncoder.encode(form.getPassword()));
        student.setRole(UserRole.STUDENT);

        userRepository.save(student);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Аккаунт успешно создан! Теперь войдите."
        );

        return "redirect:/login";
    }
}