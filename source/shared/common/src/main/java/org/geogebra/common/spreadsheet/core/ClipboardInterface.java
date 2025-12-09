/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
