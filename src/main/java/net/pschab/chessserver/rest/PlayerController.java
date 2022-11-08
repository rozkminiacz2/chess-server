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

    @GetMapping("/getAll")
    public List<Player> getAllPlayers() {
        return playerService.getAllPlayers();
    }

    @PostMapping("/add")
    public boolean addNewPlayer(@RequestBody Player player) {
        return playerService.addNewPlayer(player);
    }

    @GetMapping("/delete")
    public boolean deletePlayer(@PathParam("name") String name) {
        return playerService.deletePlayer(name);
    }
}
