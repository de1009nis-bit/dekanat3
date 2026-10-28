package com.example.dekanat.repository;

import com.example.dekanat.model.Ticket;
import com.example.dekanat.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findAllByOrderByCreatedAtAsc();

    long countByStatus(TicketStatus status);
}