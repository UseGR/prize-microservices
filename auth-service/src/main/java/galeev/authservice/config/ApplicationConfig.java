package galeev.authservice.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin.NewTopics;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
public class ApplicationConfig {
    @Value("${application.kafka.topics:}")
    private String[] topics;

    @Bean
    public NewTopics newTopic() {
        List<NewTopic> topicsList = new ArrayList<>();
        for (String topic : this.topics) {
            topicsList.add(TopicBuilder.name(topic).build());
        }

        return new NewTopics(topicsList.toArray(new NewTopic[]{}));
    }
}
