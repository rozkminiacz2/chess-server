package net.pschab.chessserver.rest.assembler;

import net.pschab.chessserver.model.Info;
import net.pschab.chessserver.rest.controller.GameController;
import net.pschab.chessserver.rest.controller.PlayerController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class InfoModelAssembler implements RepresentationModelAssembler<Info, EntityModel<Info>> {

    @Override
    public EntityModel<Info> toModel(Info info) {
        return EntityModel.of(info,
                linkTo(methodOn(PlayerController.class).getAllPlayers())
                        .withRel("players")
                        .withType(HttpMethod.GET.toString()),
                linkTo(methodOn(GameController.class).getAllGames("hostName","guestName"))
                        .withRel("games")
                        .withType(HttpMethod.GET.toString()));
    }

    @Override
    public CollectionModel<EntityModel<Info>> toCollectionModel(Iterable<? extends Info> infos) {
        return RepresentationModelAssembler.super.toCollectionModel(infos);
    }
}
