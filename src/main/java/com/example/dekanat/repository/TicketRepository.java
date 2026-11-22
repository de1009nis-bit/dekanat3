package com.example.dekanat.repository;

import com.example.dekanat.model.Ticket;
import com.example.dekanat.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.dekanat.model.AppUser;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findAllByOrderByCreatedAtAsc();

    List<Ticket> findAllByStatusOrderByCreatedAtAsc(TicketStatus status);

    List<Ticket> findAllByUserOrderByCreatedAtDesc(AppUser user);

    long countByStatus(TicketStatus status);
}