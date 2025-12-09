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
import java.util.Vector;

/**
 * Implements the Page Tree Node (see Table 3.16).
 * <p>
 * 
 * @author Mark Donszelmann
 * @version $Id: PDFPageTree.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
public class PDFPageTree extends PDFPageBase {

	Vector pages = new Vector();

	PDFPageTree(PDF pdf, PDFByteWriter writer, PDFObject object, PDFRef parent)
			throws IOException {
		super(pdf, writer, object, parent);
		entry("Type", pdf.name("Pages"));
	}

	public void addPage(String name) {
		pages.add(pdf.ref(name));
	}

	@Override
	void close() throws IOException {
		Object[] kids = new Object[pages.size()];
		pages.copyInto(kids);
		entry("Kids", kids);
		entry("Count", kids.length);
		super.close();
	}
}
