package net.pschab.chessserver;

import net.pschab.chessserver.model.Player;
import net.pschab.chessserver.model.Role;

import java.util.ArrayList;
import java.util.List;

import static net.pschab.chessserver.util.HashEncoder.encode;

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

    public static Player createTestPlayerWithNullPassword(String name) {
        return new Player(name, null);
    }

    public static Player createTestPlayer(String name, Role role) {
        return new Player(name, PLAYER_PASSWORD, role);
    }

    public static Player createDbPlayer(String name) {
        return new Player(name, encode(PLAYER_PASSWORD));
    }
}
