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

package org.geogebra.common.io;

import org.geogebra.common.util.debug.Log;

public class XMLStringBuilder {

	private final StringBuilder sb;
	boolean currentTagEmpty = false;
	String currentTag = null;

	public XMLStringBuilder() {
		this.sb = new StringBuilder();
	}

	public XMLStringBuilder(StringBuilder sb) {
		this.sb = sb;
	}

	/**
	 * Append a XML fragment to this document.
	 * @param val string to append
	 * @return this
	 */
	public XMLStringBuilder append(XMLStringBuilder val) {
		sb.append(val);
		return this;
	}

	@Override
	public String toString() {
		return sb.toString();
	}

	/**
	 * @return whether the XML string is empty
	 */
	public boolean isEmpty() {
		return sb.length() == 0;
	}

	/**
	 * @param name attribute name
	 * @param value attribute value
	 * @return this
	 */
	public XMLStringBuilder attr(String name, double value) {
		sb.append(' ').append(name).append("=\"").append(value).append('"');
		return this;
	}

	/**
	 * @param name attribute name
	 * @param value attribute value
	 * @return this
	 */
	public XMLStringBuilder attr(String name, boolean value) {
		sb.append(' ').append(name).append("=\"").append(value).append('"');
		return this;
	}

	/**
	 * @param name attribute name
	 * @param value attribute value
	 * @return this
	 */
	public XMLStringBuilder attr(String name, int value) {
		sb.append(' ').append(name).append("=\"").append(value).append('"');
		return this;
	}

	/**
	 * @param name attribute name
	 * @param value attribute value
	 * @return this
	 */
	public XMLStringBuilder attr(String name, Enum<?> value) {
		sb.append(' ').append(name).append("=\"").append(value).append('"');
		return this;
	}

	/**
	 * @param name attribute name
	 * @param value attribute value
	 * @return this
	 */
	public XMLStringBuilder attrRaw(String name, CharSequence value) {
		sb.append(' ').append(name).append("=\"").append(value).append('"');
		return this;
	}

	/**
	 * @param name attribute name
	 * @param value attribute value
	 * @return this
	 */
	public XMLStringBuilder attr(String name, StringBuilder value) {
		return attr(name, value.toString());
	}

	/**
	 * @param name attribute name
	 * @param value attribute value
	 * @return this
	 */
	public XMLStringBuilder attr(String name, String value) {
		sb.append(' ').append(name).append("=\"");
		appendEncoded(value);
		sb.append('"');
		return this;
	}

	/**
	 * Start empty tag.
	 * @param name tag name
	 * @return this
	 */
	public XMLStringBuilder startTag(String name) {
		return startTag(name, 1);
	}

	/**
	 * Start empty tag with specific indent.
	 * @param name tag name
	 * @param indent indent
	 * @return this
	 */
	public XMLStringBuilder startTag(String name, int indent) {
		sb.append("\t".repeat(indent)).append('<').append(name);
		currentTagEmpty = true;
		assert currentTag == null : currentTag;
		currentTag = name;
		return this;
	}

	/**
	 * Start opening tag with specific indent.
	 * @param name tag name
	 * @param indent indent
	 * @return this
	 */
	public XMLStringBuilder startOpeningTag(String name, int indent) {
		startTag(name, indent);
		currentTagEmpty = false;
		return this;
	}

	/**
	 * End current tag.
	 */
	public void endTag() {
		sb.append(currentTagEmpty ? "/>\n" : ">\n");
		assert currentTag != null;
		currentTag = null;
	}

	/**
	 * Add a closing tag.
	 * @param name tag name
	 */
	public void closeTag(String name) {
		sb.append("</").append(name).append(">\n");
	}

	/**
	 * Append header for XML document.
	 */
	public void appendXMLHeader() {
		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
	}

	private void appendEncoded(String str) {
		if (str == null) {
			return;
		}

		// convert every single character and append it to sb
		int len = str.length();

		// support for high Unicode characters
		// https://stackoverflow.com/questions/24501020/how-can-i-convert-a-java-string-to-xml-entities-for-versions-of-unicode-beyond-3
		for (int i = 0; i < len; i = str.offsetByCodePoints(i, 1)) {
			int c = str.codePointAt(i);

			if (c <= '\u001f' || c >= 0x10000) {
				// #2399 all apart from U+0009, U+000A, U+000D are invalid in
				// XML
				// none should appear anyway, but encode to be safe

				// eg &#x0A;
				sb.append("&#x");
				sb.append(Integer.toHexString(c));
				sb.append(';');

				if (c <= '\u001f' && c != '\n' && c != '\r') {
					Log.warn("Control character being written to XML: " + c);
				}

			} else {

				switch (c) {
				case '>':
					sb.append("&gt;");
					break;
				case '<':
					sb.append("&lt;");
					break;
				case '"':
					sb.append("&quot;");
					break;
				case '\'':
					sb.append("&apos;");
					break;
				case '&':
					sb.append("&amp;");
					break;

				default:
					sb.append((char) c);
				}
			}
		}
	}
}
