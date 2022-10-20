package com.aubay.formations.nr.rest;

import com.aubay.formations.nr.rest.model.Feedback;
import com.aubay.formations.nr.services.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.aubay.formations.nr.mappers.FeedbackMapper.toDto;
import static com.aubay.formations.nr.mappers.FeedbackMapper.toItem;

@Service
public class FeedbacksApiDelegateImpl implements FeedbacksApiDelegate {
    @Autowired
    FeedbackService feedbackService;

    @Override
    public ResponseEntity<Feedback> createFeedback(Feedback feedback) {
        return ResponseEntity.ok()
                .body(toDto(feedbackService.createFeedback(toItem(feedback))));
    }
}
