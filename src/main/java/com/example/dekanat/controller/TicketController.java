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

@Controller
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("ticketForm", new TicketForm());
        model.addAttribute("categories", TicketCategory.values());
        model.addAttribute("tickets", ticketService.getAllTickets());
        model.addAttribute("waitingCount", ticketService.getWaitingCount());

        return "index";
    }

    @PostMapping("/tickets")
    public String createTicket(
            @Valid @ModelAttribute("ticketForm") TicketForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", TicketCategory.values());
            model.addAttribute("tickets", ticketService.getAllTickets());
            model.addAttribute("waitingCount", ticketService.getWaitingCount());

            return "index";
        }

        var ticket = ticketService.createTicket(form);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Талон " + ticket.getTicketNumber() + " успешно создан!"
        );

        return "redirect:/";
    }
}