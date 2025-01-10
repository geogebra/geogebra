package org.freehep.graphicsio.pdf;

import java.io.IOException;
import java.util.Date;

/**
 * Implements the Document Information Dictionary (see Table 8.2).
 * <p>
 * 
 * @author Mark Donszelmann
 * @version $Id: PDFDocInfo.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
public class PDFDocInfo extends PDFDictionary {

	PDFDocInfo(PDF pdf, PDFByteWriter writer, PDFObject parent)
			throws IOException {
		super(pdf, writer, parent);
	}

	public void setTitle(String title) throws IOException {
		entry("Title", title);
	}

	public void setAuthor(String author) throws IOException {
		entry("Author", author);
	}

	public void setSubject(String subject) throws IOException {
		entry("Subject", subject);
	}

	public void setKeywords(String keywords) throws IOException {
		entry("Keywords", keywords);
	}

	public void setCreator(String creator) throws IOException {
		entry("Creator", creator);
	}

	public void setProducer(String producer) throws IOException {
		entry("Producer", producer);
	}

	public void setCreationDate(Date now) throws IOException {
		entry("CreationDate", now);
	}

	public void setModificationDate(Date now) throws IOException {
		entry("ModDate", now);
	}

	public void setTrapped(String name) throws IOException {
		entry("Trapped", pdf.name(name));
	}
}
