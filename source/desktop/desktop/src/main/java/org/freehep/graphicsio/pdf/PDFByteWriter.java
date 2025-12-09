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
import java.io.OutputStream;

import org.freehep.util.io.CountedByteOutputStream;

/**
 * Implements the real writer for the PDFWriter. This class does byte-counting
 * to eventually build the cross-reference table, block length counting for the
 * length of streams, and platform dependent end-of-line characters.
 * <p>
 * 
 * @author Mark Donszelmann
 * @version $Id: PDFByteWriter.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
public class PDFByteWriter extends CountedByteOutputStream
		implements PDFConstants {

	private int indent;

	private String indentString = "   ";

	PDFByteWriter(OutputStream out) {
		super(out);
		indent = 0;
	}

	public void write(String s) throws IOException {
		write(s.getBytes("ISO-8859-1"));
	}

	@Override
	public void close() throws IOException {
		out.close();
		super.close();
	}

	public void print(String string) throws IOException {
		for (int i = 0; i < indent; i++) {
			write(indentString);
		}
		printPlain(string);
	}

	public void printPlain(String string) throws IOException {
		write(string);
	}

	public void println() throws IOException {
		write(EOL);
	}

	public void indent() {
		indent++;
	}

	public void outdent() {
		if (indent > 0) {
			indent--;
		}
	}

	// Convenience methods
	public void println(String string) throws IOException {
		print(string);
		println();
	}

	public void print(int number) throws IOException {
		print(Integer.toString(number));
	}

	public void println(int number) throws IOException {
		print(number);
		println();
	}

	public void printPlain(int number) throws IOException {
		printPlain(Integer.toString(number));
	}

	public void print(double number) throws IOException {
		print(Double.toString(number));
	}

	public void println(double number) throws IOException {
		print(number);
		println();
	}

	public void printPlain(double number) throws IOException {
		printPlain(Double.toString(number));
	}

	public void print(Object object) throws IOException {
		print(object.toString());
	}

	public void println(Object object) throws IOException {
		print(object);
		println();
	}

	public void printPlain(Object object) throws IOException {
		printPlain(object.toString());
	}

}