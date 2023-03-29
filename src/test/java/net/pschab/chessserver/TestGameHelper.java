package net.pschab.chessserver;

import net.pschab.chessserver.model.Game;

import java.util.ArrayList;
import java.util.List;

import static net.pschab.chessserver.TestPlayerHelper.createTestPlayer;

public class TestGameHelper {

    public static List<Game> getThreeGameList() {
        List<Game> gameList = new ArrayList<>();
        gameList.add(createTestGame(1));
        gameList.add(createTestGame(2));
        gameList.add(createTestGame(3));
        return gameList;
    }

    public static Game createTestGame(Integer id) {
        Game game = new Game();
        game.setId(id);
        return game;
    }

    public static Game createTestGame(Integer id, String hostName, String guestName) {
        Game game = new Game();
        game.setId(id);
        game.setHost(createTestPlayer(hostName));
        game.setGuest(createTestPlayer(guestName));
        return game;
    }
}
