package com.example.dekanat.model;

public enum TicketStatus {

    WAITING("Ожидает"),
    CALLED("Вызван"),
    IN_SERVICE("Обслуживается"),
    COMPLETED("Завершён"),
    CANCELLED("Отменён");

    private final String label;

    TicketStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}