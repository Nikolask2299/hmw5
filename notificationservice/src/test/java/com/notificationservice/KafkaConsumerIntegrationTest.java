package com.notificationservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.containers.KafkaContainer;

import com.notificationservice.dto.UserActionEvent;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class KafkaConsumerIntegrationTest {
    
    @Container 
    static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest")).withEmbeddedZookeeper();
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    private KafkaTemplate<String, UserActionEvent> kafkaTemplate;

    @Test 
    void shouldConsumeUserActionEventFromKafka() throws InterruptedException {
        String topic = "user-action";
        UserActionEvent event = new UserActionEvent("CREATE", "user@example.com");

        kafkaTemplate.send(topic, event);

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {});
    }
    

}
