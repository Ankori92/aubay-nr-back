package com.aubay.formations.nr.entities;

import java.util.UUID;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

@Document("feedbacks")
public class FeedbackItem {
	@Id
	private final String id = UUID.randomUUID().toString();
	private String username;
	private String content;

	public FeedbackItem(final String username, final String content) {
		this.username = username;
		this.content = content;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getContent() {
		return content;
	}

	public void setContent(final String content) {
		this.content = content;
	}

	public String getId() {
		return id;
	}

}
