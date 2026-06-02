package pt.pauloortolan.haikai.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.stereotype.Service;
import pt.pauloortolan.haikai.pojo.Haikai;
import pt.pauloortolan.haikai.pojo.HaikaiRequest;

import java.util.Base64;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class HaikaiService {

    private static final PromptTemplate SIMPLE_TEMPLATE = new PromptTemplate("Write a playful haiku about mountains and the joy of programming with AI following the traditional 5-7-5 syllable structure.");
    private static final PromptTemplate COMPLEX_TEMPLATE = new PromptTemplate("Write a {genre} haiku about {theme} following the traditional 5-7-5 syllable structure written {language}.");
    private static final String IMAGE_TEMPLATE = """
            Create an image with the following Haikai:
        
                {haikai_line1}
                {haikai_line2}
                {haikai_line3}
        
            Be creative and catch the essence of what has been asked.
        """;

    private final ChatClient chatClient;
    private final ImageModel imageModel;

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

        Prompt prompt = COMPLEX_TEMPLATE
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

    public byte[] generateImageHaikai(ImageHaikaiRequest imageHaikaiRequest) {
        log.info("HaikaiService:generateImageHaikai(imageHaikaiRequest={})", imageHaikaiRequest);

        String renderedPrompt = IMAGE_TEMPLATE
            .replace("{haikai_line1}", imageHaikaiRequest.line1())
            .replace("{haikai_line2}", imageHaikaiRequest.line2())
            .replace("{haikai_line3}", imageHaikaiRequest.line3());

        ImageResponse response = imageModel.call(
            new ImagePrompt(renderedPrompt,
                OpenAiImageOptions.builder()
                    .model("gpt-image-1")
                    .build())
        );

        Image image = Objects.requireNonNull(response.getResult()).getOutput();

        return Base64.getDecoder().decode(image.getB64Json());
    }

}
