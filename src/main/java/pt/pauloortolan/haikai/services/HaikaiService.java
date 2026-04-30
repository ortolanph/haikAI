package pt.pauloortolan.haikai.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;
import pt.pauloortolan.haikai.pojo.ActorFilms;
import pt.pauloortolan.haikai.pojo.Haikai;
import pt.pauloortolan.haikai.pojo.HaikaiRequest;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class HaikaiService {

    private static final PromptTemplate SIMPLE_TEMPLATE = new PromptTemplate("Write a playful haiku about mountains and the joy of programming with AI following the traditional 5-7-5 syllable structure.");
    private static final PromptTemplate TEMPLATE = new PromptTemplate("Write a {genre} haiku about {theme} following the traditional 5-7-5 syllable structure written {language}.");
    private static final PromptTemplate ACTOR_TEMPLATE = new PromptTemplate("Generate the filmography for a {name} with the name of played character");

    private final ChatClient chatClient;

    private final UserService userService;

    public String generateSimpleHaikai() {
        log.info("HaikaiService:generateSimpleHaikai())");

        return chatClient
                .prompt(SIMPLE_TEMPLATE.create())
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, userService.getCustomerId()))
                .call()
                .content();
    }

    public Haikai generatePowerfulHaikai(HaikaiRequest request) {
        log.info("HaikaiService:generatePowerfulHaikai(request={})", request);

        Prompt prompt = TEMPLATE
                .create(
                        Map.of(
                                "genre", request.genre(),
                                "theme", request.theme(),
                                "language", request.language()));

        return chatClient
                .prompt(prompt)
                .advisors(new SimpleLoggerAdvisor())
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, userService.getCustomerId()))
                .call().entity(Haikai.class);
    }

    public ActorFilms getFilmography(String name) {
        log.info("HaikaiService:getFilmography(name={})", name);

        return chatClient
                .prompt(ACTOR_TEMPLATE.create(Map.of("name", name)))
                .advisors(new SimpleLoggerAdvisor())
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, userService.getCustomerId()))
                .call()
                .entity(ActorFilms.class);
    }

}
