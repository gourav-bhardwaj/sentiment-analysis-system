package com.sp.user_sentiment_analysis_listener.repository;

import com.sp.user_sentiment_analysis_listener.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {}
