package com.aubay.formations.nr.services;

import com.aubay.formations.nr.entities.FeedbackItem;
import com.aubay.formations.nr.repositories.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeedbackService {
    @Autowired
    FeedbackRepository feedbackRepository;
    public FeedbackItem createFeedback(FeedbackItem feedbackItem) {
        return feedbackRepository.save(feedbackItem);
    }
}
