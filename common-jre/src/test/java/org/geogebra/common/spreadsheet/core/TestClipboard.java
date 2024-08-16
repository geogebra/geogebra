package org.geogebra.common.spreadsheet.core;

import java.util.function.Consumer;

public class TestClipboard
		implements ClipboardInterface {

	private String content;

	@Override
	public void readContent(Consumer<String> callback) {
		callback.accept(content);
	}

	@Override
	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}
}
