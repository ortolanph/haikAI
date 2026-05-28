package pt.pauloortolan.haikai.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.pauloortolan.haikai.pojo.ActorFilms;
import pt.pauloortolan.haikai.services.ActorsService;

@Slf4j
@RestController
@RequestMapping("/actors")
@RequiredArgsConstructor
public class ActorsController {

    private final ActorsService service;

    @GetMapping("/filmography/{actor}")
    public ActorFilms filmography(@PathVariable String actor) {
        log.info("HaikaiController::filmography - actor: {}", actor);
        return service.getFilmography(actor);
    }
}
