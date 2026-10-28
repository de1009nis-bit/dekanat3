package com.example.dekanat.model;

public enum TicketCategory {

    CERTIFICATE("Получение справки"),
    DOCUMENTS("Учебные документы"),
    SCHOLARSHIP("Вопрос по стипендии"),
    OTHER("Другой вопрос");

    private final String label;

    TicketCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}