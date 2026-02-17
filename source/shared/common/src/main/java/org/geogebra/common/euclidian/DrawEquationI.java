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

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;

import com.himamis.retex.renderer.share.platform.graphics.Image;

/**
 * Class for drawing LaTeX equations.
 */
public interface DrawEquationI {
	/**
	 * @param app
	 *            application
	 * @param geo
	 *            geo
	 * @param g2
	 *            graphics
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param text
	 *            text
	 * @param font
	 *            font
	 * @param serif
	 *            true for serif
	 * @param fgColor
	 *            foreground color
	 * @param bgColor
	 *            background color
	 * @param useCache
	 *            true to cache
	 * @param updateAgain
	 *            TODO always false
	 * @param callback
	 *            callback for complete render (needed if font loading is async)
	 * @return dimensions of result
	 */
	GDimension drawEquation(App app, GeoElementND geo, GGraphics2D g2, int x, int y,
			String text, GFont font, boolean serif, GColor fgColor, GColor bgColor,
			boolean useCache, boolean updateAgain, Runnable callback);

	/**
	 * @param text
	 *            LaTeX
	 * @param geo
	 *            element
	 * @param fgColor
	 *            text color
	 * @param font
	 *            font
	 * @param style
	 *            combines TeXFormula.BOLD, TeXFormula.ITALIC,
	 *            TeXFormula.SANSSERIF
	 * @param ret
	 *            dimension return array
	 * @return cached image
	 */
	Image getCachedDimensions(String text, GeoElementND geo, GColor fgColor,
			GFont font, int style, int[] ret);

	/**
	 * Initialize commands if this is the first run
	 *
	 */
	void checkFirstCall();

	/**
	 * @param app
	 *            application
	 * @param text
	 *            LaTeX
	 * @param font
	 *            font
	 * @param serif
	 *            whether to use serif font
	 * @return equation size
	 */
	GDimension measureEquation(App app, String text, GFont font,
			boolean serif);
}
