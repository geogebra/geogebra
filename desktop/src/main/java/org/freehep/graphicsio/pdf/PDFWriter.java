// Copyright 2000-2005 FreeHEP
package org.freehep.graphicsio.pdf;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This class creates a PDF file/stream. It keeps track of all logical PDF
 * objects in the PDF file, will create a cross-reference table and do some
 * error checking while writing the file.
 * <p>
 * This class takes care of wrapping both PDFStreams and PDFDictionaries into
 * PDFObjects.
 * <p>
 * 
 * @author Mark Donszelmann
 * @version $Id: PDFWriter.java,v 1.6 2009-08-17 21:44:44 murkle Exp $
 */
public class PDFWriter extends PDF implements PDFConstants {

	private String open = null;

	public PDFWriter(OutputStream out) throws IOException {
		this(out, "1.3");
	}

	public PDFWriter(OutputStream writer, String version) throws IOException {
		super(new PDFByteWriter(writer));

		// PDF version
		out.println("%PDF-" + version);

		// Make sure intelligent readers understand that binary may be included
		out.print("%");
		out.write(0xE2);
		out.write(0xE3);
		out.write(0xCF);
		out.write(0xD3);
		out.println();
		out.println();
	}

	public void close(String catalogName, String docInfoName)
			throws IOException {
		// FIXME, check for dangling references

		xref();
		trailer(catalogName, docInfoName);
		startxref();
		out.printPlain("%%EOF");
		out.println();
		out.close();
	}

	public void comment(String comment) throws IOException {
		out.println("% " + comment);
	}

	public void object(String name, Object[] objs) throws IOException {
		PDFObject object = openObject(name);
		object.entry(objs);
		close(object);
	}

	public void object(String name, int number) throws IOException {
		PDFObject object = openObject(name);
		object.entry(number);
		close(object);
	}

	// public void object(String name, String string) throws IOException {
	// PDFObject object = openObject(name);
	// object.entry(string);
	// close(object);
	// }

	public PDFObject openObject(String name) throws IOException {
		// FIXME: check if name was already written!
		if (open != null) {
			System.err
					.println("PDFWriter error: '" + open + "' was not closed");
		}
		open = "PDFObject: " + name;

		PDFRef ref = ref(name);
		int objectNumber = ref.getObjectNumber();

		setXRef(objectNumber, out.getCount());
		PDFObject obj = new PDFObject(this, out, objectNumber,
				ref.getGenerationNumber());
		return obj;
	}

	public void close(PDFObject object) throws IOException {
		object.close();
		open = null;
	}

	public PDFDictionary openDictionary(String name) throws IOException {
		PDFObject object = openObject(name);
		PDFDictionary dictionary = object.openDictionary();
		return dictionary;
	}

	public void close(PDFDictionary dictionary) throws IOException {
		dictionary.close();
		open = null;
	}

	private static final String lengthSuffix = "-length";

	public PDFStream openStream(String name) throws IOException {
		return openStream(name, null);
	}

	public PDFStream openStream(String name, String[] encode)
			throws IOException {
		PDFObject object = openObject(name);
		PDFStream stream = object.openStream(name, encode);
		stream.entry("Length", ref(name + lengthSuffix));
		return stream;
	}

	public void close(PDFStream stream) throws IOException {
		stream.close();
		open = null;
		object(stream.getName() + lengthSuffix, stream.getLength());
	}

	//
	// high level interface
	//
	private String catalogName;

	private String docInfoName;

	public void close() throws IOException {
		close(catalogName, docInfoName);
	}

	public PDFDocInfo openDocInfo(String name) throws IOException {
		docInfoName = name;
		PDFObject object = openObject(name);
		PDFDocInfo info = object.openDocInfo(this);
		return info;
	}

	public void close(PDFDocInfo info) throws IOException {
		info.close();
		open = null;
	}

	public PDFCatalog openCatalog(String name, String pageTree)
			throws IOException {
		catalogName = name;
		PDFObject object = openObject(name);
		PDFCatalog catalog = object.openCatalog(this, ref(pageTree));
		return catalog;
	}

	public void close(PDFCatalog catalog) throws IOException {
		catalog.close();
		open = null;
	}

	public PDFPageTree openPageTree(String name, String parent)
			throws IOException {
		PDFObject object = openObject(name);
		PDFPageTree tree = object.openPageTree(this, ref(parent));
		return tree;
	}

	public void close(PDFPageTree tree) throws IOException {
		tree.close();
		open = null;
	}

	public PDFPage openPage(String name, String parent) throws IOException {
		PDFObject object = openObject(name);
		PDFPage page = object.openPage(this, ref(parent));
		return page;
	}

	public void close(PDFPage page) throws IOException {
		page.close();
		open = null;
	}

	public PDFViewerPreferences openViewerPreferences(String name)
			throws IOException {
		PDFObject object = openObject(name);
		PDFViewerPreferences prefs = object.openViewerPreferences(this);
		return prefs;
	}

	public void close(PDFViewerPreferences prefs) throws IOException {
		prefs.close();
		open = null;
	}

	public PDFOutlineList openOutlineList(String name, String first,
			String next) throws IOException {
		PDFObject object = openObject(name);
		PDFOutlineList list = object.openOutlineList(this, ref(first),
				ref(next));
		return list;
	}

	public void close(PDFOutlineList list) throws IOException {
		list.close();
		open = null;
	}

	public PDFOutline openOutline(String name, String title, String parent,
			String prev, String next) throws IOException {
		PDFObject object = openObject(name);
		PDFOutline outline = object.openOutline(this, ref(parent), title,
				ref(prev), ref(next));
		return outline;
	}

	public void close(PDFOutline outline) throws IOException {
		outline.close();
		open = null;
	}

}
