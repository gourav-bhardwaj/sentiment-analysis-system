package com.sp.ecom.ecom_user_sentiment_analysis_service.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum Sentiment {

        NEGATIVE("We’re Here to Help", "negative-sentiment-template"),
        POSITIVE("Thank You for Your Feedback!", "positive-sentiment-template"),
        NEUTRAL("Improvement Needed - We Value Your Feedback", "improvement-sentiment-template"),
        MIXED("Improvement Needed - We Value Your Feedback", "improvement-sentiment-template");

        private final String subject;
        private final String templateName;

        Sentiment(String subject, String templateName) {
                this.subject = subject;
                this.templateName = templateName;
        }

}