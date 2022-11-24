package net.pschab.chessserver;

import net.pschab.chessserver.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TestPlayerHelper {

    public static final String TEST_PLAYER_NAME = "PlayerRestTester";
    public static final String TEST_PLAYER_PASSWORD = "password1234";

    public static List<Player> getAllPlayerList() {
        List<Player> playerList = new ArrayList<>();
        playerList.add(createPlayer(TEST_PLAYER_NAME.concat("1")));
        playerList.add(createPlayer(TEST_PLAYER_NAME.concat("2")));
        playerList.add(createPlayer(TEST_PLAYER_NAME.concat("3")));
        return playerList;
    }

    public static Player createPlayer(String name) {
        Player playerToGet = new Player();
        playerToGet.setName(name);
        playerToGet.setPassword(TEST_PLAYER_PASSWORD);
        return playerToGet;
    }

    public static Player createPlayer(String name, String password) {
        Player playerToGet = new Player();
        playerToGet.setName(name);
        playerToGet.setPassword(password);
        return playerToGet;
    }
}
