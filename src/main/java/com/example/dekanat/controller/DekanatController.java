package com.example.dekanat.controller;

import com.example.dekanat.model.TicketStatus;
import com.example.dekanat.service.TicketService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DekanatController {

    private final TicketService ticketService;

    public DekanatController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/dekanat")
    public String dekanatPage(Model model) {

        model.addAttribute("tickets", ticketService.getAllTicketsSorted());

        model.addAttribute("waitingCount",
                ticketService.getWaitingCount());

        model.addAttribute("inServiceCount",
                ticketService.getCountByStatus(TicketStatus.CALLED)
                        + ticketService.getCountByStatus(TicketStatus.IN_SERVICE));

        model.addAttribute("completedCount",
                ticketService.getCountByStatus(TicketStatus.COMPLETED));

        return "dekanat";
    }

    @PostMapping("/dekanat/tickets/{id}/status")
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam TicketStatus status,
            RedirectAttributes redirectAttributes
    ) {

        try {
            ticketService.updateStatus(id, status);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Статус талона успешно изменён!"
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        return "redirect:/dekanat";
    }

    @PostMapping("/dekanat/tickets/{id}/delete")
    public String deleteTicket(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            ticketService.deleteTicket(id);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Талон успешно удалён."
            );
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        return "redirect:/dekanat";
    }
}
