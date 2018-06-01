package com.etwge.databindingdemo.data;

public class Poem {
	private final String mTitle;
	private final String mContent;

	public Poem(String title, String content) {
		mTitle = title;
		mContent = content;
	}

	public String getContent() {
		return mContent;
	}

	public String getTitle() {
		return mTitle;
	}
}
