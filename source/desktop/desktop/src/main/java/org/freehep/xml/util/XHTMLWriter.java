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

package org.freehep.xml.util;

import java.io.Writer;

import org.geogebra.common.util.debug.Log;

/**
 * A class that makes it easy to write XHTML documents.
 *
 * @author Mark Donszelmann
 * @version $Id: XHTMLWriter.java,v 1.5 2008-10-23 19:04:05 hohenwarter Exp $
 */
public class XHTMLWriter extends XMLWriter {
	/**
	 * @param type
	 *            [strict, transitional, frameset]
	 */
	public XHTMLWriter(Writer w, String indentString, String type) {
		super(w, indentString, "xhtml");
		openDoc("1.0", "UTF-8", false);
		if (type.equals("strict")) {
			referToDTD("html", "-//W3C//DTD XHTML 1.0 Strict//EN",
					"http://wwww.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd");
		} else if (type.equals("transitional")) {
			referToDTD("html", "-//W3C//DTD XHTML 1.0 Transitional//EN",
					"http://wwww.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd");
		} else if (type.equals("frameset")) {
			referToDTD("html", "-//W3C//DTD XHTML 1.0 Frameset//EN",
					"http://wwww.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd");
		} else {
			Log.debug("XHTMLWriter: unknown type: " + type
					+ ", allowed are: strict, transitional, frameset");
		}
		setAttribute("xmlns", "http://www.w3.org/1999/xhtml");
		if (!type.equals("strict")) {
			setAttribute("xml", "lang", "en");
		}
		setAttribute("lang", "en");
		openTag("html");
	}

	public XHTMLWriter(Writer w) {
		this(w, "  ", "strict");
	}

	@Override
	public void closeDoc() {
		if (!closed) {
			closeTag();
		}
		super.closeDoc();
	}

	@Override
	public void printTag(String name) {
		checkNameValid(name);
		writer.print("<" + name);
		printAttributes(name.length());
		writer.println(" />");
	}

	@Override
	public void setAttribute(String name, boolean value) {
		if (value) {
			setAttribute(name, name);
		}
	}

	/**
	 * Prints text as is (no escaping of anything)
	 */
	public void printPlain(String text) {
		writer.print(text);
	}
}
