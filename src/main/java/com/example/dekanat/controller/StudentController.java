package com.example.dekanat.controller;

import com.example.dekanat.model.AppUser;
import com.example.dekanat.model.Ticket;
import com.example.dekanat.model.TicketStatus;
import com.example.dekanat.repository.UserRepository;
import com.example.dekanat.service.TicketService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StudentController {

    private final TicketService ticketService;
    private final UserRepository userRepository;

    public StudentController(
            TicketService ticketService,
            UserRepository userRepository) {
        this.ticketService = ticketService;
        this.userRepository = userRepository;
    }

    @GetMapping("/student")
    public String studentPage(
            Authentication authentication,
            Model model) {

        AppUser user = userRepository
                .findByUsername(authentication.getName())
                .orElseThrow(() ->
                        new IllegalArgumentException("Пользователь не найден"));

        Ticket currentTicket = ticketService.getCurrentTicket(user);

        model.addAttribute("username", user.getUsername());
        model.addAttribute("tickets",
                ticketService.getStudentTickets(user));
        model.addAttribute("currentTicket", currentTicket);

        boolean isCalled = currentTicket != null
                && currentTicket.getStatus() == TicketStatus.CALLED;
        model.addAttribute("isCalled", isCalled);

        if (currentTicket != null
                && currentTicket.getStatus() == TicketStatus.WAITING) {
            model.addAttribute("waitingTime",
                    ticketService.getWaitingTime(
                            currentTicket.getTicketNumber()));
        }

        return "student";
    }
}
