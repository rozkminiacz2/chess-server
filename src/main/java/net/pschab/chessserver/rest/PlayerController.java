package net.pschab.chessserver.rest;

import net.pschab.chessserver.entity.Player;
import net.pschab.chessserver.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

@RestController
@RequestMapping("tttserver/players")
public class PlayerController {

    @Autowired
    PlayerService playerService;

    //TODO add security
    //TODO establish maturity level 4

    @GetMapping("/get-all")
    public List<Player> getAllPlayers() {
        return playerService.getAllPlayers();
    }

    //TODO change to Optional return value
    //TODO adjust message status and response message
    @GetMapping("/get-by-id")
    public Player getById(@PathParam("name") String name) {
        return playerService.getPlayerById(name).orElse(null);
    }

    @PostMapping("/add")
    public boolean addNewPlayer(@RequestBody Player player) {
        return playerService.addNewPlayer(player);
    }

    @PostMapping("/change-password")
    public boolean changePassword(@RequestBody Player player) {
        return playerService.changePassword(player);
    }

    @PostMapping("/change-role")
    public boolean changeRole(@RequestBody Player player) {
        return playerService.changeRole(player);
    }

    @GetMapping("/delete")
    public boolean deletePlayer(@PathParam("name") String name) {
        return playerService.deletePlayer(name);
    }
}
