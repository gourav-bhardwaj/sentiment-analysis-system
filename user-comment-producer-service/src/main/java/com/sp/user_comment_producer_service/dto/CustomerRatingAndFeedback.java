package com.sp.user_comment_producer_service.dto;

public record CustomerRatingAndFeedback(String customerId, String productId, Integer ratings, String feedbackComment) {}
