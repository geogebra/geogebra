package org.geogebra.common.spreadsheet.core;

import java.util.function.Consumer;

public interface ClipboardInterface {
	void readContent(Consumer<String> reader);

	void setContent(String content);
}
