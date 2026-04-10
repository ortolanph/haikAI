package pt.pauloortolan.haikai.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.openai.api.common.OpenAiApiClientErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;


@Slf4j
@Controller
public class ExceptionHandlingController {

    private static final String LLM_COMMUNICATION_ERROR =
            "Unable to communicate with the configured LLM. Please try again later.";


    @ExceptionHandler(OpenAiApiClientErrorException.class)
    ProblemDetail handle(OpenAiApiClientErrorException exception) {
        log.error("OpenAI returned an error.", exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, LLM_COMMUNICATION_ERROR);
    }
}
