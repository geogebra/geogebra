/* DefaultTeXFontParser.java
 * =========================================================================
 * This file is originally part of the JMathTeX Library - http://jmathtex.sourceforge.net
 *
 * Copyright (C) 2004-2007 Universiteit Gent
 * Copyright (C) 2009 DENIZET Calixte
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License can be found in the file
 * LICENSE.txt provided with the source distribution of this program (see
 * the META-INF directory in the source jar). This license can also be
 * found on the GNU website at http://www.gnu.org/licenses/gpl.html.
 *
 * If you did not receive a copy of the GNU General Public License along
 * with this program, contact the lead developer, or write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * Linking this library statically or dynamically with other modules 
 * is making a combined work based on this library. Thus, the terms 
 * and conditions of the GNU General Public License cover the whole 
 * combination.
 * 
 * As a special exception, the copyright holders of this library give you 
 * permission to link this library with independent modules to produce 
 * an executable, regardless of the license terms of these independent 
 * modules, and to copy and distribute the resulting executable under terms 
 * of your choice, provided that you also meet, for each linked independent 
 * module, the terms and conditions of the license of that module. 
 * An independent module is a module which is not derived from or based 
 * on this library. If you modify this library, you may extend this exception 
 * to your version of the library, but you are not obliged to do so. 
 * If you do not wish to do so, delete this exception statement from your 
 * version.
 * 
 */

/* Modified by Calixte Denizet */

package com.himamis.retex.renderer.share;

import java.util.HashMap;
import java.util.Map;

import com.himamis.retex.renderer.share.exception.ResourceParseException;
import com.himamis.retex.renderer.share.exception.XMLResourceParseException;
import com.himamis.retex.renderer.share.platform.FontAdapter;
import com.himamis.retex.renderer.share.platform.ParserAdapter;
import com.himamis.retex.renderer.share.platform.Resource;
import com.himamis.retex.renderer.share.platform.font.Font;
import com.himamis.retex.renderer.share.platform.parser.Element;
import com.himamis.retex.renderer.share.platform.parser.Node;
import com.himamis.retex.renderer.share.platform.parser.NodeList;

/**
 * Parses the font information from an XML-file.
 */
public class DefaultTeXFontParser {

	// /**
	// * if the register font cannot be found, we display an error message but
	// we do it only once
	// */
	// private static boolean registerFontExceptionDisplayed = false;
	// private static boolean shouldRegisterFonts = true;

	private static interface CharChildParser { // NOPMD
		public void parse(Element el, char ch, FontInfo info)
				throws XMLResourceParseException;
	}

	private static class ExtensionParser implements CharChildParser {

		ExtensionParser() {
			// avoid generation of access class
		}

		@Override
		public void parse(Element el, char ch, FontInfo info)
				throws ResourceParseException {
			int[] extensionChars = new int[4];
			// get required integer attributes
			extensionChars[TeXFont.REP] = DefaultTeXFontParser
					.getIntAndCheck("rep", el);
			// get optional integer attributes
			extensionChars[TeXFont.TOP] = DefaultTeXFontParser
					.getOptionalInt("top", el, TeXFont.NONE);
			extensionChars[TeXFont.MID] = DefaultTeXFontParser
					.getOptionalInt("mid", el, TeXFont.NONE);
			extensionChars[TeXFont.BOT] = DefaultTeXFontParser
					.getOptionalInt("bot", el, TeXFont.NONE);

			// parsing OK, add extension info
			info.setExtension(ch, extensionChars);
		}
	}

	private static class KernParser implements CharChildParser {

		KernParser() {
			// avoid generation of access class
		}

		@Override
		public void parse(Element el, char ch, FontInfo info)
				throws ResourceParseException {
			// get required integer attribute
			int code = DefaultTeXFontParser.getIntAndCheck("code", el);
			// get required double attribute
			double kernAmount = DefaultTeXFontParser.getFloatAndCheck("val",
					el);

			// parsing OK, add kern info
			info.addKern(ch, (char) code, kernAmount);
		}
	}

	private static class LigParser implements CharChildParser {

		LigParser() {
			// avoid generation of access class
		}

		@Override
		public void parse(Element el, char ch, FontInfo info)
				throws ResourceParseException {
			// get required integer attributes
			int code = DefaultTeXFontParser.getIntAndCheck("code", el);
			int ligCode = DefaultTeXFontParser.getIntAndCheck("ligCode", el);

			// parsing OK, add ligature info
			info.addLigature(ch, (char) code, (char) ligCode);
		}
	}

	private static class NextLargerParser implements CharChildParser {

		NextLargerParser() {
			// avoid generation of access class
		}

		@Override
		public void parse(Element el, char ch, FontInfo info)
				throws ResourceParseException {
			// get required integer attributes
			String fontId = DefaultTeXFontParser
					.getAttrValueAndCheckIfNotNull("fontId", el);
			int code = DefaultTeXFontParser.getIntAndCheck("code", el);

			// parsing OK, add "next larger" info
			info.setNextLarger(ch, (char) code, Font_ID.fromString(fontId));
		}
	}

	public static final String RESOURCE_NAME = "DefaultTeXFont.xml";

	private static Map<String, CharChildParser> charChildParsers = new HashMap<>();

	static {
		// parsers for the child elements of a "Char"-element
		setCharChildParsers();
	}

	private final Resource resource;
	private final ParserAdapter parserAdapter;

	public DefaultTeXFontParser() throws ResourceParseException {
		resource = new Resource();
		parserAdapter = new ParserAdapter();
	}

	private static void setCharChildParsers() {
		charChildParsers.put("Kern", new KernParser());
		charChildParsers.put("Lig", new LigParser());
		charChildParsers.put("NextLarger", new NextLargerParser());
		charChildParsers.put("Extension", new ExtensionParser());
	}

	public Map<Font_ID, FontInfo> parseFontDescriptions(
			Map<Font_ID, FontInfo> res, Font_ID fontID)
			throws ResourceParseException {
		Element font;
		try {
			font = parserAdapter.createParserAndParseFile(
					resource.loadResource(fontID.path + ".xml"));
		} catch (Exception e) {
			throw new XMLResourceParseException(
					"Cannot find the file " + fontID + "!" + e.toString());
		}

		// create FontInfo-object
		FontInfo info = new FontInfo(fontID);

		// process all "Char"-elements
		NodeList listF = font.getElementsByTagName("Char");

		for (int j = 0; j < listF.getLength(); j++) {
			processCharElement(listF.item(j).castToElement(), info);
		}

		// parsing OK, add to table
		res.put(fontID, info);

		return res;
	}

	public Map<Font_ID, FontInfo> parseFontDescriptions(
			Map<Font_ID, FontInfo> fi) throws ResourceParseException {
		for (Font_ID fontID : Font_ID.values()) {
			fi = parseFontDescriptions(fi, fontID);
		}

		return fi;
	}

	private static void processCharElement(Element charElement, FontInfo info)
			throws ResourceParseException {
		// retrieve required integer attribute
		char ch = (char) getIntAndCheck("code", charElement);
		// retrieve optional double attributes
		double[] metrics = new double[4];
		metrics[TeXFont.WIDTH] = getOptionalFloat("width", charElement, 0);
		metrics[TeXFont.HEIGHT] = getOptionalFloat("height", charElement, 0);
		metrics[TeXFont.DEPTH] = getOptionalFloat("depth", charElement, 0);
		metrics[TeXFont.IT] = getOptionalFloat("italic", charElement, 0);
		// set metrics
		info.setMetrics(ch, metrics);

		// process children
		NodeList list = charElement.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (node.getNodeType() != Node.TEXT_NODE) {
				Element el = node.castToElement();
				Object parser = charChildParsers.get(el.getTagName());
				if (parser == null) {
					throw new XMLResourceParseException(RESOURCE_NAME
							+ ": a <Char>-element has an unknown child element '"
							+ el.getTagName() + "'!");
				}
				// process the child element
				((CharChildParser) parser).parse(el, ch, info);
			}
		}
	}

	public static Font createFont(String name) throws ResourceParseException {
		FontAdapter fontAdapter = new FontAdapter();
		return fontAdapter.loadFont(name);
	}

	private static String getAttrValueAndCheckIfNotNull(String attrName,
			Element element) throws ResourceParseException {
		String attrValue = element.getAttribute(attrName);
		if ("".equals(attrValue)) {
			throw new XMLResourceParseException(RESOURCE_NAME,
					element.getTagName(), attrName, null);
		}
		return attrValue;
	}

	public static double getFloatAndCheck(String attrName, Element element)
			throws ResourceParseException {
		String attrValue = getAttrValueAndCheckIfNotNull(attrName, element);

		// try parsing string to double value
		double res = 0;
		try {
			res = Double.parseDouble(attrValue);
		} catch (NumberFormatException e) {
			throw new XMLResourceParseException(RESOURCE_NAME,
					element.getTagName(), attrName,
					"has an invalid real value!");
		}
		// parsing OK
		return res;
	}

	public static int getIntAndCheck(String attrName, Element element)
			throws ResourceParseException {
		String attrValue = getAttrValueAndCheckIfNotNull(attrName, element);

		// try parsing string to integer value
		int res = 0;
		try {
			res = Integer.parseInt(attrValue);
		} catch (NumberFormatException e) {
			throw new XMLResourceParseException(RESOURCE_NAME,
					element.getTagName(), attrName,
					"has an invalid integer value!");
		}
		// parsing OK
		return res;
	}

	public static int getOptionalInt(String attrName, Element element,
			int defaultValue) throws ResourceParseException {
		String attrValue = element.getAttribute(attrName);
		if ("".equals(attrValue)) {
			return defaultValue;
		}
		// try parsing string to integer value
		int res = 0;
		try {
			res = Integer.parseInt(attrValue);
		} catch (NumberFormatException e) {
			throw new XMLResourceParseException(RESOURCE_NAME,
					element.getTagName(), attrName,
					"has an invalid integer value!");
		}
		// parsing OK
		return res;
	}

	public static double getOptionalFloat(String attrName, Element element,
			double defaultValue) throws ResourceParseException {
		String attrValue = element.getAttribute(attrName);
		if ("".equals(attrValue)) {
			return defaultValue;
		}
		// try parsing string to double value
		double res = 0;
		try {
			res = Double.parseDouble(attrValue);
		} catch (NumberFormatException e) {
			throw new XMLResourceParseException(RESOURCE_NAME,
					element.getTagName(), attrName,
					"has an invalid double value!");
		}
		// parsing OK
		return res;
	}
}
