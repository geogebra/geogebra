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

package org.geogebra.desktop.awt;

import java.awt.Graphics2D;
import java.awt.Image;

import org.geogebra.common.awt.MyImage;

public interface ImageD extends MyImage {

	/**
	 * Render in given position.
	 */
	void render(Graphics2D impl, int x, int y);

	/**
	 * @return wrapped image
	 */
	Image getImage();

	/**
	 * Render sub-image at given position.
	 */
	void render(Graphics2D impl, int sx, int sy, int sw, int sh, int dx, int dy, int dw, int dh);
}
