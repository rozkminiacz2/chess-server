package net.pschab.chessserver.rest.controller;

import net.pschab.chessserver.model.Player;
import net.pschab.chessserver.rest.ApiResponse;
import net.pschab.chessserver.rest.assembler.PlayerModelAssembler;
import net.pschab.chessserver.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static net.pschab.chessserver.util.HashEncoder.matches;

@RestController
@RequestMapping("players")
public class PlayerController {

    @Autowired
    PlayerService playerService;

    @Autowired
    PlayerModelAssembler playerModelAssembler;

    //TODO add security

    @GetMapping()
    public ResponseEntity<CollectionModel<EntityModel<Player>>> getAllPlayers() {
        List<Player> players = playerService.getAllPlayers();
        if (players.isEmpty()) {
            throw new NoSuchElementException("There are no players in the database.");
        } else {
            return new ResponseEntity<>(playerModelAssembler.toCollectionModel(players), HttpStatus.OK);
        }
    }

    @GetMapping("/{name}")
    public ResponseEntity<EntityModel<Player>> getByName(@PathVariable("name") String name) {
        Optional<Player> playerOptional = playerService.getPlayerByName(name);
        if (playerOptional.isPresent()) {
            return new ResponseEntity<>(playerModelAssembler.toModel(playerOptional.get()), HttpStatus.OK);
        } else {
            throw new NoSuchElementException(getNoSuchPlayerMessage(name));
        }
    }

    private String getNoSuchPlayerMessage(String name) {
        return "There is no player with name: ".concat(name).concat(" in the database.");
    }

    @PostMapping()
    public ResponseEntity<ApiResponse> addNewPlayer(@RequestBody Player player) {
        playerService.addNewPlayer(player);
        ApiResponse apiResponse = createApiResponse(HttpStatus.CREATED, "Player with name: %s created.", player);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    private ApiResponse createApiResponse(HttpStatus ok, String format, Player player) {
        ApiResponse apiResponse = new ApiResponse(ok);
        apiResponse.setMessage(String.format(format, player.getName()));
        return apiResponse;
    }

    @PutMapping("/{name}")
    public ResponseEntity<ApiResponse> replacePlayer(@RequestBody Player player, @PathVariable String name) {
        if (!name.equals(player.getName())) {
            throw new IllegalArgumentException("Inconsistent player name value provided as service variable.");
        }
        Optional<Player> optional = playerService.getPlayerByName(name);
        Player playerInDb = optional.orElseThrow(() -> new NoSuchElementException(getNoSuchPlayerMessage(name)));
        if (isPasswordChangeRequested(player, playerInDb) || isRoleChangeRequested(player, playerInDb)) {
            if (playerService.updatePlayer(player)) {
                ApiResponse apiResponse = createApiResponse(HttpStatus.OK, "Player with name: %s modified.", player);
                return new ResponseEntity<>(apiResponse, HttpStatus.OK);
            } else {
                throw new IllegalArgumentException("Unidentified service call error.");
            }
        }
        throw new IllegalArgumentException("Nothing to modify: provided player data are identical to data in the database.");
    }

    private boolean isRoleChangeRequested(Player player, Player playerInDb) {
        return !playerInDb.getRole().equals(player.getRole());
    }

    private boolean isPasswordChangeRequested(Player player, Player playerInDb) {
        return !matches(player.getPassword(), playerInDb.getPassword());
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deletePlayer(@PathVariable("name") String name) {
        boolean status = playerService.deletePlayer(name);
        if (status) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            throw new NoSuchElementException(getNoSuchPlayerMessage(name));
        }
    }
}
