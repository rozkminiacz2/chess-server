package net.pschab.chessserver.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@Entity
public class Game {

    //TODO add lombok

    @Id()
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "host", referencedColumnName = "name")
    @NotNull
    private Player host;

    @ManyToOne
    @JoinColumn(name = "guest", referencedColumnName = "name")
    @NotNull
    private Player guest;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String history;

    public Game() {
        status = Status.INITIATED;
        history = "";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Player getHost() {
        return host;
    }

    public void setHost(Player host) {
        this.host = host;
    }

    public Player getGuest() {
        return guest;
    }

    public void setGuest(Player guest) {
        this.guest = guest;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return id.equals(game.id) && status == game.status && Objects.equals(history, game.history);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, history);
    }
}
