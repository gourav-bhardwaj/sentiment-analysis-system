package com.sp.user_sentiment_analysis_listener.listener;

import com.sp.ecom.ecom_user_sentiment_analysis_service.dto.UserSentimentAnalysis;
import com.sp.user_sentiment_analysis_listener.config.CustomMetrics;
import com.sp.user_sentiment_analysis_listener.dto.UserDto;
import com.sp.user_sentiment_analysis_listener.model.User;
import com.sp.user_sentiment_analysis_listener.repository.UserRepository;
import com.sp.user_sentiment_analysis_listener.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class SentimentNotificationListener {

    private final EmailService emailService;
    private final CustomMetrics customMetrics;
    private final UserRepository userRepository;

    public SentimentNotificationListener(EmailService emailService, CustomMetrics customMetrics, UserRepository userRepository) {
        this.emailService = emailService;
        this.customMetrics = customMetrics;
        this.userRepository = userRepository;
    }

    @KafkaListener(topics = "#{'${kafka.topic.name}'.split(',')}",
        groupId = "${kafka.topic.groupId}",
        idIsGroup = true,
        containerFactory = "concurrentKafkaListenerContainerFactory")
    public void sentimentNotification(ConsumerRecord<String, Object> consumerRecord, Acknowledgment acknowledgment) {
        UserSentimentAnalysis sentimentAnalysis = (UserSentimentAnalysis) consumerRecord.value();
        log.info("Sentiment Analysis : {}", sentimentAnalysis);
        notifyUserBasedOnSentiment(sentimentAnalysis);
        calculateMetrics(sentimentAnalysis);
        acknowledgment.acknowledge();
    }

    private void calculateMetrics(UserSentimentAnalysis sentimentAnalysis) {
        switch (sentimentAnalysis.sentiment()) {
            case POSITIVE -> customMetrics.positiveSentimentListenerCount();
            case NEGATIVE -> customMetrics.negativeSentimentListenerCount();
            case NEUTRAL, MIXED -> customMetrics.improvementSentimentListenerCount();
            default -> customMetrics.unpredictableSentimentListenerCount();
        }
    }

    private void notifyUserBasedOnSentiment(UserSentimentAnalysis sentimentAnalysis) {
        String subject = sentimentAnalysis.sentiment().getSubject();
        Optional<User> userOpt = userRepository.findById(UUID.fromString(sentimentAnalysis.customerId()));
        if (userOpt.isEmpty()) {
            log.error("User not found for the given customer id : {} - Email cannot send", sentimentAnalysis.customerId());
            return;
        }
        User userObj = userOpt.get();
        UserDto userData = new UserDto(userObj.getName(), userObj.getEmail(),
                sentimentAnalysis.feedbackComment(), sentimentAnalysis.ratings());
        String templateName = sentimentAnalysis.sentiment().getTemplateName();
        emailService.sendEmail(subject, templateName, userData);
    }

}
