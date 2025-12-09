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

package org.freehep.graphicsio;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;

import org.freehep.graphics2d.TagString;

public interface MultiPageDocument {

	public void setMultiPage(boolean isMultiPage);

	public boolean isMultiPage();

	/** Set the headline of all pages. */
	public void setHeader(Font font, TagString left, TagString center,
			TagString right, int underlineThickness);

	/** Set the footline of all pages. */
	public void setFooter(Font font, TagString left, TagString center,
			TagString right, int underlineThickness);

	/** Start the next page */
	public void openPage(Component component) throws IOException;

	public void openPage(Dimension size, String title) throws IOException;

	/** End the current page. */
	public void closePage() throws IOException;

}