package pt.pauloortolan.haikai.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pt.pauloortolan.haikai.pojo.ActorFilms;

@Slf4j
@SpringBootTest
class ActorServiceTest {

    @Autowired
    private ActorsService service;

    @Test
    void getFilmography() {
        ActorFilms actorFilms = service.getFilmography("Tom Hanks");

        log.info(actorFilms.name());
        log.info(actorFilms.films().toString());
    }

}
