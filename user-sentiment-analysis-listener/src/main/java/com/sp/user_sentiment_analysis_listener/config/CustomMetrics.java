package com.sp.user_sentiment_analysis_listener.config;

import io.micrometer.core.annotation.Counted;
import org.springframework.stereotype.Component;

@Component
public class CustomMetrics {

    @Counted(
            value = "positive-sentiment-listener-count",
            description = "Count only the positive sentiments",
            extraTags = {"positive-sentiment", "sentiment"})
    public void positiveSentimentListenerCount() {}

    @Counted(
            value = "negative-sentiment-listener-count",
            description = "Count only the negative sentiments",
            extraTags = {"negative-sentiment", "sentiment"})
    public void negativeSentimentListenerCount() {}

    @Counted(
            value = "improvement-sentiment-listener-count",
            description = "Count only the improvement sentiments",
            extraTags = {"improvement-sentiment", "sentiment"})
    public void improvementSentimentListenerCount() {}

    @Counted(
            value = "unpredictable-sentiment-listener-count",
            description = "Count only the unpredictable sentiments",
            extraTags = {"unpredictable-sentiment", "sentiment"})
    public void unpredictableSentimentListenerCount() {}

    @Counted(
            value = "error-sentiment-listener-count",
            description = "Count only the errors in sentiment listener",
            extraTags = {"error-in-sentiment-listener", "error"})
    public void errorSentimentListenerCount() {}

}
