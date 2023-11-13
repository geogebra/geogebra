package org.geogebra.common.spreadsheet.core;

public class TestClipboard
		implements ClipboardInterface {

	private String content;

	@Override
	public String getContent() {
		return content;
	}

	@Override
	public void setContent(String content) {
		this.content = content;
	}
}
