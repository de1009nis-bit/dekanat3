package com.example.dekanat.service;

import com.example.dekanat.dto.TicketForm;
import com.example.dekanat.model.Ticket;
import com.example.dekanat.model.TicketStatus;
import com.example.dekanat.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Transactional
    public Ticket createTicket(TicketForm form) {
        Ticket ticket = new Ticket();

        ticket.setStudentName(form.getStudentName().trim());
        ticket.setCategory(form.getCategory());
        ticket.setUrgent(form.isUrgent());
        ticket.setStatus(TicketStatus.WAITING);
        ticket.setCreatedAt(LocalDateTime.now());

        // Сначала сохраняем запись, чтобы получить ID.
        ticket = ticketRepository.saveAndFlush(ticket);

        // Формируем уникальный номер на основе ID.
        String number = String.format(
                Locale.ROOT,
                "SQ-%03d",
                ticket.getId()
        );

        ticket.setTicketNumber(number);

        return ticketRepository.save(ticket);
    }

    @Transactional(readOnly = true)
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAllByOrderByCreatedAtAsc();
    }

    @Transactional(readOnly = true)
    public long getWaitingCount() {
        return ticketRepository.countByStatus(TicketStatus.WAITING);
    }
}
