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
// This code has been written initially for Scilab (http://www.scilab.org/).

package org.geogebra.desktop.gui.editor;

import java.awt.Color;
import java.awt.Font;

import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

/**
 * 
 * @author Calixte DENIZET
 *
 */
public abstract class ViewContext implements ViewFactory {

	/**
	 * Contains the colors of the different tokens
	 */
	public Color[] tokenColors;

	/**
	 * The font to use
	 */
	public Font tokenFont;

	/**
	 * @param font
	 *            Font to be set
	 */
	public void setTokenFont(Font font) {
		tokenFont = font;
	}

	/**
	 * Contains the attributes (underline or stroke) of the different tokens
	 */
	public int[] tokenAttrib;

	/**
	 * @return the view to use to render the document
	 */
	public abstract View getCurrentView();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract View create(Element elem);
}
