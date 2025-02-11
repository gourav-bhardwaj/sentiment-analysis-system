package com.sp.ecom.ecom_user_sentiment_analysis_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum Sentiment {
        NEGATIVE, POSITIVE, NEUTRAL, MIXED;
}