package com.example.dekanat.service;

import com.example.dekanat.dto.TicketForm;
import com.example.dekanat.model.Ticket;
import com.example.dekanat.model.TicketStatus;
import com.example.dekanat.repository.TicketRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.example.dekanat.model.AppUser;
import com.example.dekanat.model.TicketCategory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Comparator;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public Ticket createTicket(TicketForm form, AppUser user) {

        Ticket ticket = new Ticket();

        ticket.setTicketNumber(
                "MN-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase()
        );

        ticket.setStudentName(form.getStudentName().trim());
        ticket.setCategory(form.getCategory());
        ticket.setUrgent(form.isUrgent());
        ticket.setStatus(TicketStatus.WAITING);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUser(user);

        return ticketRepository.save(ticket);
    }

//    public List<Ticket> getAllTickets() {
//        return ticketRepository.findAllByOrderByCreatedAtAsc();
//    }
public List<Ticket> getAllTicketsSorted() {
    List<TicketStatus> order = List.of(
            TicketStatus.IN_SERVICE,
            TicketStatus.CALLED,
            TicketStatus.WAITING,
            TicketStatus.COMPLETED,
            TicketStatus.CANCELLED
    );

    return ticketRepository.findAll().stream()
            .sorted(Comparator
                    .comparingInt((Ticket t) -> order.indexOf(t.getStatus()))
                    .thenComparing(Ticket::getCreatedAt))
            .toList();
}

    public List<Ticket> getStudentTickets(AppUser user) {
        return ticketRepository.findAllByUserOrderByCreatedAtDesc(user);
    }

    public Ticket getCurrentTicket(AppUser user) {
        return getStudentTickets(user).stream()
                .filter(ticket ->
                        ticket.getStatus() == TicketStatus.WAITING
                                || ticket.getStatus() == TicketStatus.CALLED
                                || ticket.getStatus() == TicketStatus.IN_SERVICE
                )
                .findFirst()
                .orElse(null);
    }

    public long getWaitingCount() {
        return ticketRepository.countByStatus(TicketStatus.WAITING);
    }

    public long getCountByStatus(TicketStatus status) {
        return ticketRepository.countByStatus(status);
    }

    public long getWaitingTime(String ticketNumber) {
        List<Ticket> waitingTickets =
                ticketRepository.findAllByStatusOrderByCreatedAtAsc(
                        TicketStatus.WAITING
                );

        for (int i = 0; i < waitingTickets.size(); i++) {
            if (waitingTickets.get(i).getTicketNumber().equals(ticketNumber)) {
                return i * 5L;
            }
        }

        return 0;
    }

    public java.util.Map<String, Long> getWaitingTimes(List<Ticket> tickets) {
        List<Ticket> waitingTickets =
                ticketRepository.findAllByStatusOrderByCreatedAtAsc(
                        TicketStatus.WAITING
                );

        java.util.Map<String, Long> times = new java.util.HashMap<>();

        for (int i = 0; i < waitingTickets.size(); i++) {
            times.put(waitingTickets.get(i).getTicketNumber(), i * 5L);
        }
        return times;
    }

    public List<Ticket> getActiveTickets() {
        return ticketRepository.findByStatusInOrderByCreatedAtAsc(
                List.of(TicketStatus.WAITING, TicketStatus.CALLED, TicketStatus.IN_SERVICE)
        );
    }

    @Transactional
    public void updateStatus(Long ticketId, TicketStatus newStatus) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Талон не найден: " + ticketId
                ));
        TicketStatus currentStatus = ticket.getStatus();

        boolean allowed = switch (currentStatus) {
            case WAITING ->
                    newStatus == TicketStatus.CALLED ||
                            newStatus == TicketStatus.CANCELLED;
            case CALLED ->
                    newStatus == TicketStatus.IN_SERVICE ||
                            newStatus == TicketStatus.CANCELLED;
            case IN_SERVICE ->
                    newStatus == TicketStatus.COMPLETED ||
                            newStatus == TicketStatus.CANCELLED;
            case COMPLETED, CANCELLED -> false;
        };

        if (!allowed) {
            throw new IllegalArgumentException(
                    "Недопустимое изменение статуса"
            );
        }

        ticket.setStatus(newStatus);
        ticketRepository.save(ticket);
    }

    public void deleteTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Талон не найден"));

        if (ticket.getStatus() != TicketStatus.COMPLETED
                && ticket.getStatus() != TicketStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "Нельзя удалить активный талон. Сначала завершите или отмените его.");
        }

        ticketRepository.delete(ticket);
    }
}