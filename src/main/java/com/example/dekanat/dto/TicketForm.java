package com.example.dekanat.dto;

import com.example.dekanat.model.TicketCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TicketForm {

    @NotBlank(message = "Введите имя студента")
    @Size(max = 100, message = "Имя должно содержать не более 100 символов")
    private String studentName;

    @NotNull(message = "Выберите категорию обращения")
    private TicketCategory category;

    private boolean urgent;

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public TicketCategory getCategory() {
        return category;
    }

    public void setCategory(TicketCategory category) {
        this.category = category;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
    }
}