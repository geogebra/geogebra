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
 * Implements the Outline Dictionary (see Table 7.3).
 * <p>
 * 
 * @author Mark Donszelmann
 * @version $Id: PDFOutlineList.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
public class PDFOutlineList extends PDFDictionary {

	PDFOutlineList(PDF pdf, PDFByteWriter writer, PDFObject object,
			PDFRef first, PDFRef last) throws IOException {
		super(pdf, writer, object);
		entry("Type", pdf.name("Outlines"));
		entry("First", first);
		entry("Last", last);
	}

	public void setCount(int count) throws IOException {
		entry("Count", count);
	}
}
