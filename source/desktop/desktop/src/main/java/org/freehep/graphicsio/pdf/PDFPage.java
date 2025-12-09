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
import java.util.Date;

/**
 * Implements the Page Object (see Table 3.17). Inheritable Page Attributes are
 * in PDFPageBase.
 * <p>
 * 
 * @author Mark Donszelmann
 * @version $Id: PDFPage.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
public class PDFPage extends PDFPageBase {

	PDFPage(PDF pdf, PDFByteWriter writer, PDFObject object, PDFRef parent)
			throws IOException {
		super(pdf, writer, object, parent);
		entry("Type", pdf.name("Page"));
	}

	public void setBleedBox(double x, double y, double w, double h)
			throws IOException {
		double[] rectangle = { x, y, w, h };
		entry("BleedBox", rectangle);
	}

	public void setTrimBox(double x, double y, double w, double h)
			throws IOException {
		double[] rectangle = { x, y, w, h };
		entry("TrimBox", rectangle);
	}

	public void setArtBox(double x, double y, double w, double h)
			throws IOException {
		double[] rectangle = { x, y, w, h };
		entry("ArtBox", rectangle);
	}

	public void setContents(String content) throws IOException {
		entry("Contents", pdf.ref(content));
	}

	public void setThumb(String thumb) throws IOException {
		entry("Thumb", pdf.ref(thumb));
	}

	public void setB(String[] b) throws IOException {
		entry("B", pdf.ref(b));
	}

	public void setDur(double dur) throws IOException {
		entry("Dur", dur);
	}

	public void setTrans(String trans) throws IOException {
		entry("Trans", pdf.ref(trans));
	}

	public void setAnnots(String[] annots) throws IOException {
		entry("Annots", pdf.ref(annots));
	}

	public void setAA(String aa) throws IOException {
		entry("AA", pdf.ref(aa));
	}

	public void setPieceInfo(String pieceInfo) throws IOException {
		entry("PieceInfo", pdf.ref(pieceInfo));
	}

	public void setLastModified(Date date) throws IOException {
		entry("LastModified", date);
	}

	public void setStructParents(int structParents) throws IOException {
		entry("StructParents", structParents);
	}

	public void setID(String id) throws IOException {
		entry("ID", id);
	}

	public void setPZ(double pz) throws IOException {
		entry("PZ", pz);
	}

	public void setSeparationInfo(String separationInfo) throws IOException {
		entry("SeparationInfo", pdf.ref(separationInfo));
	}
}
