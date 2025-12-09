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

package org.geogebra.common.euclidian;

import org.geogebra.common.annotation.MissingDoc;

/**
 * Text rendering settings for input boxes.
 */
public interface TextRendererSettings {
	@MissingDoc
	int getFixMargin();

	@MissingDoc
	int getMinHeight();

	@MissingDoc
	int getRightMargin();

	@MissingDoc
	int getBottomOffset();

	@MissingDoc
	int getRendererFontSize();

	@MissingDoc
	int getEditorFontSize();
}
