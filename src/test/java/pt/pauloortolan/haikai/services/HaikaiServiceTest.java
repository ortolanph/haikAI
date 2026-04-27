package pt.pauloortolan.haikai.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pt.pauloortolan.haikai.pojo.ActorFilms;
import pt.pauloortolan.haikai.pojo.Haikai;
import pt.pauloortolan.haikai.pojo.HaikaiRequest;

@Slf4j
@SpringBootTest
class HaikaiServiceTest {

    @Autowired
    private HaikaiService haikaiService;

    @Test
    void generateSimpleHaikai() {
        log.info(haikaiService.generateSimpleHaikai());
    }

    @Test
    void generatePowerfulHaikai() {
        HaikaiRequest request = new HaikaiRequest("sci-fi", "mountains", "jp-JP");

        Haikai result = haikaiService.generatePowerfulHaikai(request);

        log.info(result.title());
        log.info(result.content());
        log.info(result.genre());
    }

    @Test
    void getFilmography() {
        ActorFilms actorFilms = haikaiService.getFilmography("Tom Hanks");

        log.info(actorFilms.name());
        log.info(actorFilms.films().toString());
    }

    @Test
    void getFilmographyByFilmId() {}
}