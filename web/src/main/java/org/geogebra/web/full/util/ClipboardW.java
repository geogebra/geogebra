package org.geogebra.web.full.util;

import java.util.function.Consumer;

import org.geogebra.common.spreadsheet.core.ClipboardInterface;
import org.geogebra.web.html5.util.CopyPasteW;

public class ClipboardW implements ClipboardInterface {

	@Override
	public void readContent(Consumer<String> reader) {
		CopyPasteW.pasteNative(reader, ignore -> {});
	}

	@Override
	public void setContent(String content) {
		CopyPasteW.writeToExternalClipboardWithFallback(content);
	}
}
