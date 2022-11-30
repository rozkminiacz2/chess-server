package net.pschab.chessserver;

import net.pschab.chessserver.entity.Player;
import net.pschab.chessserver.entity.Role;

import java.util.ArrayList;
import java.util.List;

public class TestPlayerHelper {

    public static final String PLAYER_NAME = "TestPlayer";
    public static final String PLAYER_PASSWORD = "password1234";

    public static List<Player> getThreePlayerList() {
        List<Player> playerList = new ArrayList<>();
        playerList.add(createTestPlayer(PLAYER_NAME.concat("1")));
        playerList.add(createTestPlayer(PLAYER_NAME.concat("2")));
        playerList.add(createTestPlayer(PLAYER_NAME.concat("3")));
        return playerList;
    }

    public static Player createTestPlayer(String name) {
        return new Player(name, PLAYER_PASSWORD);
    }

    public static Player createTestPlayer(String name, String password) {
        return new Player(name, password);
    }
}
