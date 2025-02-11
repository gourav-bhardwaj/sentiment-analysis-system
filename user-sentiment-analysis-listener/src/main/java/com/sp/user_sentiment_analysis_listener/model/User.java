package com.sp.user_sentiment_analysis_listener.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    private UUID id;
    private String name;
    private String email;
    private String phoneNumber;
}