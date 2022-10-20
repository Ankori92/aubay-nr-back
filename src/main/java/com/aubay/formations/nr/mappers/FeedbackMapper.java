package com.aubay.formations.nr.mappers;

import com.aubay.formations.nr.entities.FeedbackItem;
import com.aubay.formations.nr.rest.model.Feedback;

public class FeedbackMapper {
	public static FeedbackItem toItem(final Feedback feedback) {
		return new FeedbackItem(feedback.getUsername(), feedback.getContent());
	}

	public static Feedback toDto(final FeedbackItem item) {
		final var feedback = new Feedback();
		feedback.setUsername(item.getUsername());
		feedback.setContent(item.getContent());
		return feedback;
	}
}
