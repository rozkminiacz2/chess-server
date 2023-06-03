package net.pschab.chessserver.rest.assembler;

import net.pschab.chessserver.model.Game;
import net.pschab.chessserver.rest.controller.GameController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class GameModelAssembler implements RepresentationModelAssembler<Game, EntityModel<Game>> {

    @Override
    public EntityModel<Game> toModel(Game game) {
        return EntityModel.of(game,
                linkTo(methodOn(GameController.class).getById(game.getId()))
                        .withSelfRel()
                        .withType(HttpMethod.GET.toString()),
                linkTo(methodOn(GameController.class).deleteGame(game.getId()))
                        .withRel("delete")
                        .withType(HttpMethod.DELETE.toString()),
                linkTo(methodOn(GameController.class).getAllGames("hostName","guestName"))
                        .withRel("players")
                        .withType(HttpMethod.GET.toString()));
    }

    @Override
    public CollectionModel<EntityModel<Game>> toCollectionModel(Iterable<? extends Game> players) {
        return RepresentationModelAssembler.super.toCollectionModel(players);
    }
}
