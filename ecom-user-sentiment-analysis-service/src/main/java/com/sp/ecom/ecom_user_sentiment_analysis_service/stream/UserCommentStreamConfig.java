package com.sp.ecom.ecom_user_sentiment_analysis_service.stream;

import com.sp.ecom.ecom_user_sentiment_analysis_service.config.TopicsConfiguration;
import com.sp.user_comment_producer_service.dto.CustomerRatingAndFeedback;
import com.sp.ecom.ecom_user_sentiment_analysis_service.dto.Sentiment;
import com.sp.ecom.ecom_user_sentiment_analysis_service.dto.SentimentAnalysisResponse;
import com.sp.ecom.ecom_user_sentiment_analysis_service.dto.UserSentimentAnalysis;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.KafkaStreamBrancher;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Slf4j
@Component
public class UserCommentStreamConfig {

    private final TopicsConfiguration topicsConfig;
    private final ChatClient chatClient;
    private final JsonSerde<CustomerRatingAndFeedback> customerRatingAndFeedbackJsonSerde;
    private final JsonSerde<UserSentimentAnalysis> userSentimentAnalysisJsonSerde;
    private static final String COMMENT_SENTIMENT_ANALYSIS_PROMPT = "Kindly provide sentiment from the given values (NEGATIVE, POSITIVE, MIXED, NEUTRAL) based on the input comment: {comment} and rating: {ratings}";

    public UserCommentStreamConfig(TopicsConfiguration topicsConfig,
                                   ChatClient chatClient,
                                   JsonSerde<CustomerRatingAndFeedback> customerRatingAndFeedbackJsonSerde,
                                   JsonSerde<UserSentimentAnalysis> userSentimentAnalysisJsonSerde) {
        this.topicsConfig = topicsConfig;
        this.chatClient = chatClient;
        this.customerRatingAndFeedbackJsonSerde = customerRatingAndFeedbackJsonSerde;
        this.userSentimentAnalysisJsonSerde = userSentimentAnalysisJsonSerde;
    }

    @Autowired
    public void  userCommentStreamProcessing(StreamsBuilder streamsBuilder) {
        KStream<String, CustomerRatingAndFeedback> stream = streamsBuilder.stream(topicsConfig.getInput(), Consumed.with(Serdes.String(), customerRatingAndFeedbackJsonSerde));
        KStream<String, UserSentimentAnalysis> transformedStream = stream.mapValues(ratingAndFeedback -> {
            SentimentAnalysisResponse sentimentAnalysisResponse = sentimentAnalysis(ratingAndFeedback);
            return new UserSentimentAnalysis(ratingAndFeedback.customerId(), ratingAndFeedback.feedbackComment(), ratingAndFeedback.ratings(),
                    sentimentAnalysisResponse.sentiment());
        });
        new KafkaStreamBrancher<String, UserSentimentAnalysis>()
                .branch((key,value) -> Sentiment.POSITIVE.equals(value.sentiment()),
                        ks -> ks.to(topicsConfig.getPositiveFeedback(), Produced.with(Serdes.String(), userSentimentAnalysisJsonSerde)))
                .branch((key,value) -> Sentiment.NEGATIVE.equals(value.sentiment()),
                        ks -> ks.to(topicsConfig.getNegativeFeedback(), Produced.with(Serdes.String(), userSentimentAnalysisJsonSerde)))
                .branch((key,value) -> Arrays.asList(Sentiment.NEUTRAL, Sentiment.MIXED).contains(value.sentiment()),
                        ks -> ks.to(topicsConfig.getImprovementFeedback(), Produced.with(Serdes.String(), userSentimentAnalysisJsonSerde)))
                .defaultBranch(ks -> ks.to(topicsConfig.getDefaultOutput()))
                .onTopOf(transformedStream);
    }

    public SentimentAnalysisResponse sentimentAnalysis(CustomerRatingAndFeedback ratingAndFeedback) {
        PromptTemplate promptTemplate = new PromptTemplate(COMMENT_SENTIMENT_ANALYSIS_PROMPT);
        promptTemplate.add("comment", ratingAndFeedback.feedbackComment());
        promptTemplate.add("ratings", ratingAndFeedback.ratings());
        log.info("Actual request for sentiment detection : {}", promptTemplate.render());
        return chatClient.prompt(promptTemplate.create()).call().entity(SentimentAnalysisResponse.class);
    }

}
