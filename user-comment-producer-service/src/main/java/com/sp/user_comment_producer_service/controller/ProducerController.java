package com.sp.user_comment_producer_service.controller;

import com.sp.user_comment_producer_service.dto.CustomerRatingAndFeedback;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("kafka")
public class ProducerController {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ProducerController(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    private static final String TOPIC = "input-topic";

    @PostMapping("/publish")
    public String post(@RequestBody CustomerRatingAndFeedback ratingAndFeedback) {
        kafkaTemplate.send(TOPIC, ratingAndFeedback);
        return "Published successfully";
    }

}
