package com.sp.ecom.ecom_user_sentiment_analysis_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "topics")
public class TopicsConfiguration {
    private String input;
    private String positiveFeedback;
    private String negativeFeedback;
    private String improvementFeedback;
    private String defaultOutput;
}
