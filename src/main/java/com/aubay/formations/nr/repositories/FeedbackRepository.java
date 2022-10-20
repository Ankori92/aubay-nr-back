package com.aubay.formations.nr.repositories;

import com.aubay.formations.nr.entities.FeedbackItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface FeedbackRepository extends MongoRepository<FeedbackItem, UUID> {
}
