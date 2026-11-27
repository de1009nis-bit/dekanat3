package com.example.dekanat.controller;

import com.example.dekanat.dto.TicketForm;
import com.example.dekanat.model.TicketCategory;
import com.example.dekanat.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.dekanat.repository.UserRepository;
import org.springframework.security.core.Authentication;

@Controller
public class TicketController {

    private final TicketService ticketService;
    private final UserRepository userRepository;

    public TicketController(TicketService ticketService, UserRepository userRepository) {
        this.ticketService = ticketService;
        this.userRepository = userRepository;
    }

    @ModelAttribute
    public void addCommonAttributes(Model model) {
        if (!model.containsAttribute("ticketForm")) {
            model.addAttribute("ticketForm", new TicketForm());
        }

        model.addAttribute("categories", TicketCategory.values());
    }

    @GetMapping("/")
    public String index(Model model) {
        var activeTickets = ticketService.getActiveTickets();

        model.addAttribute("tickets", activeTickets);
        model.addAttribute("waitingCount", ticketService.getWaitingCount());
        model.addAttribute("waitingTimes", ticketService.getWaitingTimes(activeTickets));

        return "index";
    }

    @PostMapping("/tickets")
    public String createTicket(
            @Valid @ModelAttribute("ticketForm") TicketForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes,
            Authentication authentication
    ) {
        boolean isStudent = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));

        if (!isStudent) {
            redirectAttributes.addFlashAttribute(
                    "accessDeniedMessage",
                    "Сотрудник не может получить талон студента"
            );
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("tickets", ticketService.getAllTickets());
            model.addAttribute("waitingCount", ticketService.getWaitingCount());

            return "index";
        }

        var user = userRepository.findByUsername(authentication.getName()).orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        var ticket = ticketService.createTicket(form, user);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Талон " + ticket.getTicketNumber() + " успешно создан!"
        );

        return "redirect:/";
    }
}