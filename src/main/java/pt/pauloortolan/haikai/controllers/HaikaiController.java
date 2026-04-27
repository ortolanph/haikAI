package pt.pauloortolan.haikai.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.pauloortolan.haikai.pojo.ActorFilms;
import pt.pauloortolan.haikai.pojo.Haikai;
import pt.pauloortolan.haikai.pojo.HaikaiRequest;
import pt.pauloortolan.haikai.services.HaikaiService;

@Slf4j
@RestController
@RequestMapping("/haikais")
@RequiredArgsConstructor
public class HaikaiController {

    private final HaikaiService haikaiService;

    @GetMapping("/simple")
    public String simple() {
        log.info("HaikaiController::simple");
        return haikaiService.generateSimpleHaikai();
    }

    @PostMapping("/complex")
    public Haikai complexTitle(@RequestBody HaikaiRequest haikaiRequest) {
        log.info("HaikaiController::complexTitle - request: {}", haikaiRequest);
        return haikaiService.generatePowerfulHaikai(haikaiRequest);
    }

    @GetMapping("/filmography/{actor}")
    public ActorFilms filmography(@PathVariable String actor) {
        log.info("HaikaiController::filmography - actor: {}", actor);
        return haikaiService.getFilmography(actor);
    }

}
