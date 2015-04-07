/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

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
	 * @throws Exception
	 *             if invalid
	 */
	public void startElement(String tag, LinkedHashMap<String, String> h)
			throws Exception;

	/**
	 * @param tag
	 *            element name
	 * @throws Exception
	 *             if invalid
	 */
	public void endElement(String tag) throws Exception;

	/**
	 * @throws Exception
	 *             if invalid
	 */
	public void startDocument() throws Exception;

	/**
	 * @throws Exception
	 *             if invalid
	 */
	public void endDocument() throws Exception;

	/**
	 * @param str
	 *            string between start and end tags
	 * @throws Exception
	 *             if invalid
	 */
	public void text(String str) throws Exception;

	/**
	 * For navigation bar
	 * 
	 * @return current construction step
	 */
	public int getConsStep();
}
