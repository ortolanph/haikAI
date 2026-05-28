package pt.pauloortolan.haikai.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;
import pt.pauloortolan.haikai.pojo.ActorFilms;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActorsService {

    private static final PromptTemplate ACTOR_TEMPLATE = new PromptTemplate(
            """
                   Generate the filmography for a {name} with the name of played character.
                   I want an object with the top 20 movies.
                   I want a list with the movie title, the movie year, the movie director, the actor role, and the tmdbId.
            """);

    private final ChatClient chatClient;

    private final UserService userService;

    public ActorFilms getFilmography(String name) {
        log.info("ActorsService:getFilmography(name={})", name);

        return chatClient
                .prompt(ACTOR_TEMPLATE.create(Map.of("name", name)))
                .advisors(new SimpleLoggerAdvisor())
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, userService.getCustomerId()))
                .call()
                .entity(ActorFilms.class);
    }

}
