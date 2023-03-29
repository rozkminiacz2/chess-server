package net.pschab.chessserver.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Objects;

@Entity
public class Player {
    @Id
    private String name;

    @NotNull
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "host")
    private List<Game> hostedGames;

    @OneToMany(mappedBy = "guest")
    private List<Game> guestedGames;

    public Player() {
    }

    public Player(String name) {
        this.name = name;
        this.role = Role.USER;
    }

    public Player(String name, String password) {
        this(name);
        this.password = password;
    }

    public Player(String name, String password, Role role) {
        this.name = name;
        this.password = password;
        this.role = role == null ? Role.USER : role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return name.equals(player.name) && password.equals(player.password) && role == player.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, password, role);
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                '}';
    }
}
