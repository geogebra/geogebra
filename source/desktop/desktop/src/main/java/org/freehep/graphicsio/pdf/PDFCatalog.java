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
 * Implements the Catalog Dictionary (see Table 3.15).
 * <p>
 * 
 * @author Mark Donszelmann
 * @version $Id: PDFCatalog.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
public class PDFCatalog extends PDFDictionary {

	PDFCatalog(PDF pdf, PDFByteWriter writer, PDFObject parent, PDFRef pageTree)
			throws IOException {
		super(pdf, writer, parent);
		entry("Type", pdf.name("Catalog"));
		entry("Pages", pageTree);
	}

	/*
	 * public void setPageLabels(PDFNumberTree pageLabels) { entry("PageLabels",
	 * pageLabels); }
	 */

	public void setNames(String names) throws IOException {
		entry("Names", pdf.ref(names));
	}

	public void setDests(String dests) throws IOException {
		entry("Dests", pdf.ref(dests));
	}

	public void setViewerPreferences(String viewerPreferences)
			throws IOException {
		entry("ViewerPreferences", pdf.ref(viewerPreferences));
	}

	public void setPageLayout(String pageLayout) throws IOException {
		entry("PageLayout", pdf.name(pageLayout));
	}

	public void setPageMode(String pageMode) throws IOException {
		entry("PageMode", pdf.name(pageMode));
	}

	public void setOutlines(String outlines) throws IOException {
		entry("Outlines", pdf.ref(outlines));
	}

	public void setThreads(String threads) throws IOException {
		entry("Threads", pdf.ref(threads));
	}

	public void setOpenAction(Object[] openAction) throws IOException {
		entry("OpenAction", openAction);
	}

	public void setURI(String uri) throws IOException {
		entry("URI", pdf.ref(uri));
	}

	public void setAcroForm(String acroForm) throws IOException {
		entry("AcroForm", pdf.ref(acroForm));
	}

	public void setStructTreeRoot(String structTreeRoot) throws IOException {
		entry("StructTreeRoot", pdf.ref(structTreeRoot));
	}

	public void setSpiderInfo(String spiderInfo) throws IOException {
		entry("SpiderInfo", pdf.ref(spiderInfo));
	}
}
