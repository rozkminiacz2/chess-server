package net.pschab.chessserver.rest.controller;

import net.pschab.chessserver.model.Game;
import net.pschab.chessserver.rest.ApiResponse;
import net.pschab.chessserver.rest.assembler.GameModelAssembler;
import net.pschab.chessserver.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("games")
public class GameController {

    @Autowired
    GameService gameService;

    @Autowired
    GameModelAssembler gameModelAssembler;

    @GetMapping()
    public ResponseEntity<CollectionModel<EntityModel<Game>>> getAllGames(
            @RequestParam(name="hostName", required = false) String hostName,
            @RequestParam(name="guestName", required = false) String guestName) {
        if (hostName!=null && guestName!=null) {
            throw new IllegalArgumentException("Only one of parameters allowed: hostName or guestName.");
        }
        List<Game> games = Collections.emptyList();
        if (hostName==null && guestName==null) {
            games = gameService.getAllGames();
        }
        if (hostName!=null) {
            games = gameService.getAllGamesHostedByPlayer(hostName);
        }
        if (guestName!=null) {
            games = gameService.getAllGamesGuestedByPlayer(guestName);
        }
        if (games.isEmpty()) {
            throw new NoSuchElementException("There are no games in the database.");
        } else {
            return new ResponseEntity<>(gameModelAssembler.toCollectionModel(games), HttpStatus.OK);
        }
    }

//    @GetMapping()
//    public ResponseEntity<CollectionModel<EntityModel<Game>>> getAllGamesHostedByPlayer(@RequestParam String hostName) {
//        List<Game> games = gameService.getAllGamesHostedByPlayer(hostName);
//        if (games.isEmpty()) {
//            throw new NoSuchElementException(String.format("There are no games hosted by player: %s in the database.", hostName));
//        } else {
//            return new ResponseEntity<>(gameModelAssembler.toCollectionModel(games), HttpStatus.OK);
//        }
//    }
//
//    @GetMapping()
//    public ResponseEntity<CollectionModel<EntityModel<Game>>> getAllGamesGuestedByPlayer(@RequestParam String guestName) {
//        List<Game> games = gameService.getAllGamesGuestedByPlayer(guestName);
//        if (games.isEmpty()) {
//            throw new NoSuchElementException(String.format("There are no games hosted by player: %s in the database.", guestName));
//        } else {
//            return new ResponseEntity<>(gameModelAssembler.toCollectionModel(games), HttpStatus.OK);
//        }
//    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Game>> getById(@PathVariable("id") Integer id) {
        Optional<Game> gameOptional = gameService.getGameById(id);
        if (gameOptional.isPresent()) {
            return new ResponseEntity<>(gameModelAssembler.toModel(gameOptional.get()), HttpStatus.OK);
        } else {
            throw new NoSuchElementException(getNoSuchGameMessage(id));
        }
    }

    private String getNoSuchGameMessage(Integer id) {
        return "There is no game with id: ".concat(id.toString()).concat(" in the database.");
    }

    @PostMapping()
    public ResponseEntity<ApiResponse> addNewGame(@RequestBody Game game) {
        gameService.addNewGame(game);
        ApiResponse apiResponse = createApiResponse(HttpStatus.CREATED, "Game with id: %s created.", game);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    private ApiResponse createApiResponse(HttpStatus ok, String message, Game game) {
        ApiResponse apiResponse = new ApiResponse(ok);
        apiResponse.setMessage(String.format(message, game.getId()));
        return apiResponse;
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> replaceGame(@RequestBody Game game, @PathVariable Integer id) {
        if (!id.equals(game.getId())) {
            throw new IllegalArgumentException("Inconsistent game id value provided as service variable.");
        }
        Optional<Game> optional = gameService.getGameById(id);
        Game gameInDb = optional.orElseThrow(() -> new NoSuchElementException(getNoSuchGameMessage(id)));
        if (isStatusChangeRequested(game, gameInDb) || isHistoryChangeRequested(game, gameInDb)) {
            if (gameService.updateGame(game)) {
                ApiResponse apiResponse = createApiResponse(HttpStatus.OK, "Game with id: %s modified.", game);
                return new ResponseEntity<>(apiResponse, HttpStatus.OK);
            } else {
                throw new IllegalArgumentException("Unidentified service call error.");
            }
        }
        throw new IllegalArgumentException("Nothing to modify: provided game data are identical to data in the database.");
    }

    private boolean isHistoryChangeRequested(Game game, Game gameInDb) {
        return !gameInDb.getHistory().equals(game.getHistory());
    }

    private boolean isStatusChangeRequested(Game game, Game gameInDb) {
        return !gameInDb.getStatus().equals(game.getStatus());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable("id") Integer id) {
        boolean status = gameService.deleteGame(id);
        if (status) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            throw new NoSuchElementException(getNoSuchGameMessage(id));
        }
    }

}
