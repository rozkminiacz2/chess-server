package net.pschab.chessserver.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Game {

    @Id
    private int number;

    private String hostPlayerName;
    private String guestPlayerName;

    //private List<Move> moves;

    private Status status;

}
