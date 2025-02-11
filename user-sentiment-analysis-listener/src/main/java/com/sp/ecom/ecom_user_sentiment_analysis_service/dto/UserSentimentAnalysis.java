package com.sp.ecom.ecom_user_sentiment_analysis_service.dto;

public record UserSentimentAnalysis(String customerId, String feedbackComment, Integer ratings, Sentiment sentiment) {}
