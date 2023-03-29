package net.pschab.chessserver.rest.controller;

import net.pschab.chessserver.model.Info;
import net.pschab.chessserver.rest.assembler.InfoModelAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
public class ServerController {

    @Autowired
    InfoModelAssembler infoModelAssembler;

    //TODO add new Player link in get all Players

    @GetMapping()
    public ResponseEntity<EntityModel<Info>> getServerInfo() {
        Info info = new Info("Games and Players controller. Available actions:");
        return new ResponseEntity<>(infoModelAssembler.toModel(info), HttpStatus.OK);
    }
}
