// vendored
/*
 * Quick and dirty XML parser. Java Tip 128
 * http://www.javaworld.com/javaworld/javatips/jw-javatip128.html
 */

package org.geogebra.common.io;

import java.util.LinkedHashMap;

/**
 * Event listener for MyXMLio
 */
public interface DocHandler {
	/**
	 * @param tag
	 *            element name
	 * @param h
	 *            attributes
	 * @throws XMLParseException
	 *             if invalid
	 */
	void startElement(String tag, LinkedHashMap<String, String> h)
			throws XMLParseException;

	/**
	 * @param tag
	 *            element name
	 * @throws XMLParseException
	 *             if invalid
	 */
	void endElement(String tag) throws XMLParseException;

	/**
	 * @throws XMLParseException
	 *             if invalid
	 */
	void startDocument() throws XMLParseException;

	/**
	 * @throws XMLParseException
	 *             if invalid
	 */
	void endDocument() throws XMLParseException;

	/**
	 * @param str
	 *            string between start and end tags
	 * @throws XMLParseException
	 *             if invalid
	 */
	void text(String str) throws XMLParseException;

}
