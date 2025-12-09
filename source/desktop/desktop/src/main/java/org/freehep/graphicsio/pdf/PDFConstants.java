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

package org.freehep.graphicsio.pdf;

/**
 * Specifies constants for use with the PDFWriter, PDFStream and PDFUtil.
 * <p>
 * 
 * @author Mark Donszelmann
 * @version $Id: PDFConstants.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
public interface PDFConstants {

	public final static String EOL = System.getProperty("line.separator");

	//
	// Constants for PDFStream
	//

	// Line Cap Styles (see Table 4.4)
	public static final int CAP_BUTT = 0;

	public static final int CAP_ROUND = 1;

	public static final int CAP_SQUARE = 2;

	// Line Join Styles (see Table 4.5)
	public static final int JOIN_MITTER = 0;

	public static final int JOIN_ROUND = 1;

	public static final int JOIN_BEVEL = 2;

	// Rendering Modes (see Table 5.3)
	public static final int MODE_FILL = 0;

	public static final int MODE_STROKE = 1;

	public static final int MODE_FILL_STROKE = 2;

	public static final int MODE_INVISIBLE = 3;

	public static final int MODE_FILL_CLIP = 4;

	public static final int MODE_STROKE_CLIP = 5;

	public static final int MODE_FILL_STROKE_CLIP = 6;

	public static final int MODE_CLIP = 7;

}
