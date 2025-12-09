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

package org.geogebra.desktop.gui.util;

public class JSVGConstants {
	static final String NO_URI = "file:nouri";
	static final String BLANK_SVG
			= "data:image/svg+xml,<svg xmlns=\"http://www.w3.org/2000/svg\"/>";
	static final String HEADER = "<?xml version=\"1.0\" standalone=\"no\"?>\n"
			+ "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \n"
			+ "\"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n";

	public static final String UNSUPPORTED_SVG = HEADER
			+ "<svg xmlns=\"http://www.w3.org/2000/svg\" enable-background=\"new 0 0 24 24\""
					+ " height=\"24px\" viewBox=\"0 0 24 24\" width=\"24px\" fill=\"#000000\">"
					+ "<g><rect fill=\"none\" height=\"24\" width=\"24\"/></g><g>"
					+ "<path d=\"M11.07,12.85c0.77-1.39,2.25-2.21,3.11-3.44c0.91-1.29,0.4-3.7"
					+ "-2.18-3.7c-1.69,0-2.52,1.28-2.87,2.34L6.54,6.96 C7.25,4.83,9.18,3,11.99,"
					+ "3c2.35,0,3.96,1.07,4.78,2.41c0.7,1.15,1.11,3.3,0.03,4.9c-1.2,1.77-2.35,"
					+ "2.31-2.97,3.45 c-0.25,0.46-0.35,0.76-0.35,2.24h-2.89C10.58,15.22,10.46,"
					+ "13.95,11.07,12.85z M14,20c0,1.1-0.9,2-2,2s-2-0.9-2-2c0-1.1,0.9-2,2-2 S14,"
					+ "18.9,14,20z\"/></g></svg>";
}
