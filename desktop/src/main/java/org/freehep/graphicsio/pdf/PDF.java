// Copyright 2000-2005 FreeHEP
package org.freehep.graphicsio.pdf;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Implements the lookup tables.
 * <p>
 * 
 * @author Mark Donszelmann
 * @version $Id: PDF.java,v 1.6 2009-08-17 21:44:44 murkle Exp $
 */
public class PDF {

	private int generationNumber = 0;

	private Hashtable refsByName = new Hashtable(); // of PDFRefs stored by name

	private Vector refsByNumber = new Vector(); // of PDFRefs stored by number

	private Vector xrefsByNumber = new Vector(); // of offsets stored by
													// refnumber

	private int startXref = 0;

	protected PDFByteWriter out;

	PDF(PDFByteWriter out) {
		this.out = out;
		// add dummy element to refsByNumber and xrefsByNumber
		refsByNumber.addElement(new PDFRef("Dummy", 0, 0));
		xrefsByNumber.addElement(Integer.valueOf(999999));
	}

	public PDFName name(String name) {
		return new PDFName(name);
	}

	public PDFRef ref(String name) {
		if (name == null) {
			return null;
		}
		PDFRef ref = (PDFRef) refsByName.get(name);
		if (ref == null) {
			int refNumber = refsByNumber.size();
			ref = new PDFRef(name, refNumber, generationNumber);
			refsByName.put(name, ref);
			refsByNumber.add(ref);
			xrefsByNumber.add(null);
		}
		return ref;
	}

	public PDFRef[] ref(String[] names) {
		PDFRef[] refs = new PDFRef[names.length];
		for (int i = 0; i < names.length; i++) {
			refs[i] = ref(names[i]);
		}
		return refs;
	}

	protected void setXRef(int objectNumber, int offset) {
		xrefsByNumber.set(objectNumber, Integer.valueOf(offset));
	}

	protected void xref() throws IOException {
		DecimalFormat offsetFormat = new DecimalFormat("0000000000");
		DecimalFormat linkFormat = new DecimalFormat("00000");
		startXref = out.getCount();
		out.printPlain("xref");
		out.println();
		out.printPlain(0 + " " + xrefsByNumber.size());
		out.println();

		// the free list header
		out.printPlain(offsetFormat.format(0) + " " + linkFormat.format(65535)
				+ " f\r\n");

		// the used list
		for (int i = 1; i < xrefsByNumber.size(); i++) {
			Integer offsetObject = (Integer) xrefsByNumber.get(i);
			if (offsetObject != null) {
				int offset = offsetObject.intValue();
				out.printPlain(offsetFormat.format(offset) + " "
						+ linkFormat.format(0) + " n\r\n");
			} else {
				PDFRef ref = (PDFRef) refsByNumber.get(i);
				System.err.println("PDFWriter: PDFRef '" + ref.getName()
						+ "' is used but not defined.");
			}
		}

		out.println();
	}

	protected void trailer(String rootName, String docInfoName)
			throws IOException {
		out.println("trailer");
		PDFDictionary dictionary = new PDFDictionary(this, out);
		dictionary.entry("Size", refsByName.size());
		dictionary.entry("Root", ref(rootName));
		if (docInfoName != null) {
			dictionary.entry("Info", ref(docInfoName));
		}
		dictionary.close();

		out.println();
	}

	protected void startxref() throws IOException {
		out.println("startxref");
		out.println(startXref);

		out.println();
	}
}