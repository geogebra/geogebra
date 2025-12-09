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
 * Implements the Outline Item Dictionary (see Table 7.4).
 * <p>
 * 
 * @author Mark Donszelmann
 * @version $Id: PDFOutline.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
public class PDFOutline extends PDFDictionary {

	PDFOutline(PDF pdf, PDFByteWriter writer, PDFObject object, PDFRef parent,
			String title, PDFRef prev, PDFRef next) throws IOException {
		super(pdf, writer, object);
		entry("Parent", parent);
		entry("Title", title);
		entry("Prev", prev);
		entry("Next", next);
	}

	public void setFirst(String first) throws IOException {
		entry("First", pdf.ref(first));
	}

	public void setLast(String last) throws IOException {
		entry("Last", pdf.ref(last));
	}

	public void setCount(int count) throws IOException {
		entry("Count", count);
	}

	public void setDest(PDFName dest) throws IOException {
		entry("Dest", dest);
	}

	public void setDest(String dest) throws IOException {
		entry("Dest", dest);
	}

	public void setDest(Object[] dest) throws IOException {
		entry("Dest", dest);
	}

	public void setA(String a) throws IOException {
		entry("A", pdf.ref(a));
	}

	public void setSE(String se) throws IOException {
		entry("SE", pdf.ref(se));
	}
}
