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

package org.geogebra.common.kernel.geos;

/**
 * Element with text properties
 */
public interface TextProperties extends TextStyle, HasCorners {

	/**
	 * 
	 * @param size
	 *            font size (relative)
	 */
	public void setFontSizeMultiplier(double size);

	/**
	 * 
	 * @param fontStyle
	 *            font style
	 */
	public void setFontStyle(int fontStyle);

	/**
	 * 
	 * @return print decimals (-1 for kernel default)
	 */
	public int getPrintDecimals();

	/**
	 * 
	 * @return print figures (-1 for kernel default)
	 */
	public int getPrintFigures();

	/**
	 * 
	 * @param printDecimals
	 *            print decimals
	 * @param update
	 *            true to update the text
	 */
	public void setPrintDecimals(int printDecimals, boolean update);

	/**
	 * 
	 * @param printFigures
	 *            print figures
	 * @param update
	 *            true to update the text
	 */
	public void setPrintFigures(int printFigures, boolean update);

	/**
	 * 
	 * @return true iff using serif font
	 */
	public boolean isSerifFont();

	/**
	 * 
	 * @param serifFont
	 *            true for serif font
	 */
	public void setSerifFont(boolean serifFont);

	/**
	 * 
	 * @return true iff significant figures should be used
	 */
	public boolean useSignificantFigures();

	/**
	 * @return whether this was created using a command with LaTeX output
	 */
	public boolean isLaTeXTextCommand();

}
