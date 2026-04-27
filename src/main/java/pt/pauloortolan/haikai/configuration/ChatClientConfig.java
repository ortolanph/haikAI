package pt.pauloortolan.haikai.configuration;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.message.Message;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.mongo.MongoChatMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ChatClientConfig {

    private final ChatClient.Builder chatClientBuilder;

    private final MongoChatMemoryRepository mongoChatMemoryRepository;

    @Bean
    public ChatClient chatClient() {
        return chatClientBuilder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(mongoChatMemory()).build())
                .build();
    }

    public ChatMemory mongoChatMemory() {
        return MessageWindowChatMemory
                .builder()
                .chatMemoryRepository(mongoChatMemoryRepository)
                .maxMessages(10)
                .build();
    }

}
