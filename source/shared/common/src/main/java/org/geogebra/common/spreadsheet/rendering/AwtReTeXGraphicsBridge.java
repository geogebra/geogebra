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

package org.geogebra.common.spreadsheet.rendering;

import org.geogebra.common.awt.GGraphics2D;

import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;

/** Converts AWT graphics object to ReTeX graphics object. */
public interface AwtReTeXGraphicsBridge {

	/**
	 * Convert AWT to ReTeX graphics object.
	 * @param gGraphics2D AWT graphics object
	 * @return ReTeX graphics object
	 */
	Graphics2DInterface convert(GGraphics2D gGraphics2D);
}
