package net.pschab.chessserver.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Info {

    @Id
    String message;

    public Info(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
