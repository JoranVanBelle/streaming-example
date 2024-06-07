package org.streaming.example.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<FeedbackEntity, String> {}

