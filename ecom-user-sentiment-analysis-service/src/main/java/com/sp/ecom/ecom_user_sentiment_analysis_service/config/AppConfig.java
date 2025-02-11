package com.sp.ecom.ecom_user_sentiment_analysis_service.config;

import com.sp.ecom.ecom_user_sentiment_analysis_service.dto.UserSentimentAnalysis;
import com.sp.user_comment_producer_service.dto.CustomerRatingAndFeedback;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafkaStreams
public class AppConfig {

    private final String appId;
    private final String bootstrapServers;

    public AppConfig(@Value("${kafka.application-id}") String appId, @Value("${kafka.bootstrap-servers}") String bootstrapServers) {
        this.appId = appId;
        this.bootstrapServers = bootstrapServers;
    }

    @Bean
    public ChatClient chatClient(OllamaChatModel ollamaChatModel) {
        return ChatClient.builder(ollamaChatModel).build();
    }

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kafkaStreamsConfiguration() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, appId);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, JsonSerde.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        // Only needed if you want to add a default DTO for all consumers, But it will increase the message size
        // There is another way to resolve the DTO type issue using explicitly configure the JsonSerde
        // props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, true);
        props.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG, LogAndContinueExceptionHandler.class.getName());
        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    public JsonSerde<CustomerRatingAndFeedback> customerRatingAndFeedbackJsonSerde() {
        JsonSerde<CustomerRatingAndFeedback> feedbackJsonSerde = new JsonSerde<>(CustomerRatingAndFeedback.class);
        feedbackJsonSerde.configure(Map.of(JsonDeserializer.TRUSTED_PACKAGES, "com.sp.ecom.ecom_user_sentiment_analysis_service.dto"), false);
        return feedbackJsonSerde;
    }

    @Bean
    public JsonSerde<UserSentimentAnalysis> userSentimentAnalysisJsonSerde() {
        return new JsonSerde<>(UserSentimentAnalysis.class);
    }

}
