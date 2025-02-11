package com.sp.ecom.ecom_user_sentiment_analysis_service.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"comment", "sentiment"})
public record SentimentAnalysisResponse(String comment, Sentiment sentiment) {}