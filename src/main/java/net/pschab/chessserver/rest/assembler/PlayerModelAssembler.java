package net.pschab.chessserver.rest.assembler;

import net.pschab.chessserver.model.Player;
import net.pschab.chessserver.rest.controller.PlayerController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class PlayerModelAssembler implements RepresentationModelAssembler<Player, EntityModel<Player>> {

    @Override
    public EntityModel<Player> toModel(Player player) {
        return EntityModel.of(player,
                linkTo(methodOn(PlayerController.class).getByName(player.getName()))
                        .withSelfRel()
                        .withType(HttpMethod.GET.toString()),
                linkTo(methodOn(PlayerController.class).deletePlayer(player.getName()))
                        .withRel("delete")
                        .withType(HttpMethod.DELETE.toString()),
                linkTo(methodOn(PlayerController.class).getAllPlayers())
                        .withRel("players")
                        .withType(HttpMethod.GET.toString()));
    }

    @Override
    public CollectionModel<EntityModel<Player>> toCollectionModel(Iterable<? extends Player> players) {
        return RepresentationModelAssembler.super.toCollectionModel(players);
    }
}
