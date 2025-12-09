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

import java.io.IOException;

/**
 * Implements the Viewer Preferences (see Table 7.1).
 * <p>
 * 
 * @author Mark Donszelmann
 * @version $Id: PDFViewerPreferences.java,v 1.4 2009-08-17 21:44:44 murkle Exp
 *          $
 */
public class PDFViewerPreferences extends PDFDictionary {

	PDFViewerPreferences(PDF pdf, PDFByteWriter writer, PDFObject object)
			throws IOException {
		super(pdf, writer, object);
	}

	public void setHideToolbar(boolean hide) throws IOException {
		entry("HideToolbar", hide);
	}

	public void setHideMenubar(boolean hide) throws IOException {
		entry("HideMenubar", hide);
	}

	public void setHideWindowUI(boolean hide) throws IOException {
		entry("HideWindowUI", hide);
	}

	public void setFitWindow(boolean fit) throws IOException {
		entry("FitWindow", fit);
	}

	public void setCenterWindow(boolean center) throws IOException {
		entry("CenterWindow", center);
	}

	public void setNonFullScreenPageMode(String mode) throws IOException {
		entry("NonFullScreenPageMode", pdf.name(mode));
	}

	public void setDirection(String direction) throws IOException {
		entry("Direction", pdf.name(direction));
	}
}
