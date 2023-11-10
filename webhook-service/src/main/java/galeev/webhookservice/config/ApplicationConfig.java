package galeev.webhookservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.JacksonUtils;

@Configuration
@Slf4j
public class ApplicationConfig {
    private final String topicName;

    public ApplicationConfig(@Value("${application.kafka.topic}") String topicName) {
        this.topicName = topicName;
    }

    @Bean
    public NewTopic newTopic() {
        return TopicBuilder.name(topicName)
                .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return JacksonUtils.enhancedObjectMapper();
    }
}
