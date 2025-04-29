package org.geogebra.common.spreadsheet.core;

import java.util.function.Consumer;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * Clipboard (platform) abstraction.
 */
public interface ClipboardInterface {

	/**
	 * Read content from the clipboard.
	 * @param reader A reader.
	 */
	void readContent(@Nonnull Consumer<String> reader);

	/**
	 * Set the clipboard's content.
	 * @param content Clipboard content.
	 */
	void setContent(@CheckForNull String content);
}
