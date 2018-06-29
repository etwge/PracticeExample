package com.etwge.databindingdemo.data;

public class Poem {
	private final String title;
	private final String content;

	public Poem(String title, String content) {
		this.title = title;
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public String getTitle() {
		return title;
	}
}
