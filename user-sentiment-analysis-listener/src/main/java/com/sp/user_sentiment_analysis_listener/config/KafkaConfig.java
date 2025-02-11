package com.sp.user_sentiment_analysis_listener.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaConfig {

    private final String groupId;
    private final String bootstrapServers;
    private final CustomMetrics customMetrics;

    public KafkaConfig(@Value("${kafka.topic.groupId}") String groupId, @Value("${kafka.bootstrap-servers}") String bootstrapServers, CustomMetrics customMetrics) {
        this.groupId = groupId;
        this.bootstrapServers = bootstrapServers;
        this.customMetrics = customMetrics;
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10);
        configProps.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 60000);
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "com.sp.ecom.ecom_user_sentiment_analysis_service.dto");
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> concurrentKafkaListenerContainerFactory(ConsumerFactory<String, Object> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, Object> containerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        containerFactory.setConsumerFactory(consumerFactory);
        containerFactory.setCommonErrorHandler(customErrorHandler());
        containerFactory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        containerFactory.getContainerProperties().setObservationEnabled(true);
        return containerFactory;
    }

    public DefaultErrorHandler customErrorHandler() {
        return new DefaultErrorHandler(((consumerRecord, exception) -> {
            customMetrics.errorSentimentListenerCount();
            log.error("Unable to process the record due to kafka listener error --> {} :: {}", consumerRecord, exception.getMessage());
        }));
    }

}
