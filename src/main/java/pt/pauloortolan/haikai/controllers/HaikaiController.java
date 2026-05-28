package pt.pauloortolan.haikai.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.pauloortolan.haikai.pojo.Haikai;
import pt.pauloortolan.haikai.pojo.HaikaiRequest;
import pt.pauloortolan.haikai.services.HaikaiService;

import java.io.IOException;
import java.util.UUID;

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
    public Haikai complex(@RequestBody HaikaiRequest haikaiRequest) {
        log.info("HaikaiController::complexTitle - request: {}", haikaiRequest);
        return haikaiService.generatePowerfulHaikai(haikaiRequest);
    }

    @GetMapping("/image/{haikai}")
    public ResponseEntity<byte[]> image(@PathVariable String haikai) throws IOException {
        log.info("HaikaiController::image - haikai: {}", haikai);

        String fileName = String.format("haikai_%s.png", UUID.randomUUID());
        byte[] imageContent = haikaiService.generateImageHaikai(haikai);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(imageContent);
    }

}
