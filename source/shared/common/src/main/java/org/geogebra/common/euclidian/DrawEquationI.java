package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;

import com.himamis.retex.renderer.share.platform.graphics.Color;
import com.himamis.retex.renderer.share.platform.graphics.Image;

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
	public abstract GDimension drawEquation(App app, GeoElementND geo, GGraphics2D g2, int x, int y,
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
	Image getCachedDimensions(String text, GeoElementND geo, Color fgColor,
			GFont font, int style, int[] ret);

	/**
	 * Initialize commands if this is the first run
	 *
	 */
	void checkFirstCall();

	/**
	 * @param color
	 *            GeoGebra color
	 * @return LaTeX color
	 */
	public abstract Color convertColor(GColor color);

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
	public abstract GDimension measureEquation(App app, String text, GFont font,
			boolean serif);
}
