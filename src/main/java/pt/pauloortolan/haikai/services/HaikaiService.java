package pt.pauloortolan.haikai.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;
import pt.pauloortolan.haikai.pojo.Haikai;
import pt.pauloortolan.haikai.pojo.HaikaiRequest;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class HaikaiService {

    private static final PromptTemplate SIMPLE_TEMPLATE = new PromptTemplate("Write a playful haiku about mountains and the joy of programming with AI following the traditional 5-7-5 syllable structure.");
    private static final PromptTemplate TEMPLATE = new PromptTemplate("Write a {genre} haiku about {theme} following the traditional 5-7-5 syllable structure written {language}.");
    private final ChatClient chatClient;

    public String generateSimpleHaikai() {
        log.info("HaikaiService:generateSimpleHaikai");
        return chatClient
                .prompt(SIMPLE_TEMPLATE.create())
                .call()
                .content();
    }

    public Haikai generatePowerfulHaikai(HaikaiRequest request) {
        log.info("HaikaiService:generatePowerfulHaikai");
        Prompt prompt = TEMPLATE
                .create(
                        Map.of(
                                "genre", request.genre(),
                                "theme", request.theme(),
                                "language", request.language()));

        return chatClient.prompt(prompt).call().entity(Haikai.class);
    }

}
