package org.geogebra.common.spreadsheet.core;

import java.util.function.Consumer;

/**
 * System clipboard interface for textual copy/paste.
 */
public interface ClipboardInterface {
	void readContent(Consumer<String> reader);

	void setContent(String content);
}
