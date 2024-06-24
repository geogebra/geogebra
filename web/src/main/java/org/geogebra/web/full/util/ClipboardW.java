package org.geogebra.web.full.util;

import org.geogebra.common.spreadsheet.core.ClipboardInterface;

public class ClipboardW implements ClipboardInterface {

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
