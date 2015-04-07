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
 * 
 * Some optimizations by Markus Hohenwarter, 19.11.2004
 */

package org.geogebra.common.io;

import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.Stack;

import org.geogebra.common.util.StringUtil;

/**
 * Quick and Dirty xml parser. This parser is, like the SAX parser, an event
 * based parser, but with much less functionality.
 */
public class QDParser {

	private LinkedHashMap<String, String> attrs;
	private Stack<Integer> stack;
	private StringBuilder sb, etag;

	/**
	 * Creates new parser
	 */
	public QDParser() {
		attrs = new LinkedHashMap<String, String>();
		stack = new Stack<Integer>();
		sb = new StringBuilder();
		etag = new StringBuilder();
	}

	/**
	 * Resets the parser
	 */
	public void reset() {
		attrs.clear();
		stack.clear();
		sb = new StringBuilder();
		etag = new StringBuilder();
	}

	private static int popMode(Stack<Integer> st) {
		if (!st.empty()) {
			return st.pop().intValue();
		}
		return PRE;
	}

	private final static int TEXT = 1, ENTITY = 2, OPEN_TAG = 3, CLOSE_TAG = 4,
			START_TAG = 5, ATTRIBUTE_LVALUE = 6, ATTRIBUTE_EQUAL = 9,
			ATTRIBUTE_RVALUE = 10, QUOTE = 7, IN_TAG = 8, SINGLE_TAG = 12,
			COMMENT = 13, DONE = 11, DOCTYPE = 14, PRE = 15, CDATA = 16;

	/**
	 * @param doc
	 *            handler that receives document events
	 * @param r
	 *            source of XML data
	 * @throws Exception
	 *             if XML is not valid
	 */
	final public void parse(DocHandler doc, Reader r) throws Exception {
		// Stack stack = new Stack();
		stack.clear();

		int depth = 0;
		int mode = PRE;
		int c = 0;
		int quotec = '"';
		depth = 0;
		// StringBuilder sb = new StringBuilder();
		// StringBuilder etag = new StringBuilder();
		sb.setLength(0);
		etag.setLength(0);
		String tagName = null;
		String lvalue = null;
		String rvalue = null;

		// attrs = new LinkedHashMap();
		attrs.clear();

		doc.startDocument();
		int line = 1, col = 0;
		boolean eol = false;
		while ((c = r.read()) != -1) {

			// We need to map \r, \r\n, and \n to \n
			// See XML spec section 2.11
			if (c == '\n' && eol) {
				eol = false;
				continue;
			} else if (eol) {
				eol = false;
			} else if (c == '\n') {
				line++;
				col = 0;
			} else if (c == '\r') {
				eol = true;
				c = '\n';
				line++;
				col = 0;
			} else {
				col++;
			}

			switch (mode) {
			case DONE:
				doc.endDocument();
				return;

				// We are between tags collecting text.
			case TEXT:
				switch (c) {
				case '<':
					stack.push(new Integer(mode));
					mode = START_TAG;
					if (sb.length() > 0) {
						doc.text(sb.toString());
						sb.setLength(0);
					}
					break;
				case '&':
					stack.push(new Integer(mode));
					mode = ENTITY;
					etag.setLength(0);
					break;
				default:
					sb.append((char) c);
				}
				break;

			// we are processing a closing tag: e.g. </foo>
			case CLOSE_TAG:
				switch (c) {
				case '>':
					mode = popMode(stack);
					tagName = sb.toString();
					sb.setLength(0);
					depth--;
					if (depth == 0)
						mode = DONE;
					doc.endElement(tagName);
					break;
				default:
					sb.append((char) c);
				}
				break;

			// we are processing CDATA
			case CDATA:
				if (c == '>' && sb.toString().endsWith("]]")) {
					sb.setLength(sb.length() - 2);
					doc.text(sb.toString());
					sb.setLength(0);
					mode = popMode(stack);
				} else
					sb.append((char) c);
				break;

			// we are processing a comment. We are inside
			// the <!-- .... --> looking for the -->.
			case COMMENT:
				if (c == '>' && sb.toString().endsWith("--")) {
					sb.setLength(0);
					mode = popMode(stack);
				} else
					sb.append((char) c);
				break;

			// We are outside the root tag element
			case PRE:
				if (c == '<') {
					mode = TEXT;
					stack.push(new Integer(mode));
					mode = START_TAG;
				}
				break;

			// We are inside one of these <? ... ?>
			// or one of these <!DOCTYPE ... >
			case DOCTYPE:
				if (c == '>') {
					mode = popMode(stack);
					if (mode == TEXT)
						mode = PRE;
				}
				break;

			// we have just seen a < and
			// are wondering what we are looking at
			// <foo>, </foo>, <!-- ... --->, etc.
			case START_TAG:
				mode = popMode(stack);
				switch (c) {
				case '/':
					stack.push(new Integer(mode));
					mode = CLOSE_TAG;
					break;
				case '?':
					mode = DOCTYPE;
					break;
				default:
					stack.push(new Integer(mode));
					mode = OPEN_TAG;
					tagName = null;
					// attrs = new LinkedHashMap();
					sb.append((char) c);
				}
				break;

			// we are processing an entity, e.g. &lt;, &#187;, etc.
			case ENTITY:
				if (c == ';') {
					mode = popMode(stack);
					String cent = etag.toString();
					etag.setLength(0);
					if (cent.equals("lt"))
						sb.append('<');
					else if (cent.equals("gt"))
						sb.append('>');
					else if (cent.equals("amp"))
						sb.append('&');
					else if (cent.equals("quot"))
						sb.append('"');
					else if (cent.equals("apos"))
						sb.append('\'');
					// Could parse hex entities if we wanted to
					else if (cent.startsWith("#x"))
						sb.append((char) Integer.parseInt(cent.substring(2), 16));
					else if (cent.charAt(0) == '#')
						sb.append((char) Integer.parseInt(cent.substring(1)));
					// Insert custom entity definitions here
					else
						exc("Unknown entity: &" + cent + ";", line, col);
				} else {
					etag.append((char) c);
				}
				break;

			// we have just seen something like this:
			// <foo a="b"/
			// and are looking for the final >.
			case SINGLE_TAG:
				if (tagName == null)
					tagName = sb.toString();
				if (c != '>')
					exc("Expected > for tag: <" + tagName + "/>", line, col);
				doc.startElement(tagName, attrs);
				doc.endElement(tagName);
				if (depth == 0) {
					doc.endDocument();
					return;
				}
				sb.setLength(0);
				// attrs = new LinkedHashMap();
				attrs.clear();
				tagName = null;
				mode = popMode(stack);
				break;

			// we are processing something
			// like this <foo ... >. It could
			// still be a <!-- ... --> or something.
			case OPEN_TAG:
				switch (c) {
				case '>':
					if (tagName == null)
						tagName = sb.toString();
					sb.setLength(0);
					depth++;
					doc.startElement(tagName, attrs);
					tagName = null;
					// attrs = new LinkedHashMap();
					attrs.clear();
					mode = popMode(stack);
					break;

				case '/':
					mode = SINGLE_TAG;
					break;

				case '-':
					if (sb.toString().equals("!-")) {
						mode = COMMENT;
					} else {
						sb.append((char) c);
					}
					break;

				case '[':
					if (sb.toString().equals("![CDATA")) {
						mode = CDATA;
						sb.setLength(0);
					}
					break;

				case 'E':
					if (sb.toString().equals("!DOCTYP")) {
						sb.setLength(0);
						mode = DOCTYPE;
					}
					break;

				default:
					if (StringUtil.isWhitespace((char) c)) {
						tagName = sb.toString();
						sb.setLength(0);
						mode = IN_TAG;
					} else {
						sb.append((char) c);
					}
				}
				break;

			// We are processing the quoted right-hand side
			// of an element's attribute.
			case QUOTE:
				if (c == quotec) {
					rvalue = sb.toString();
					sb.setLength(0);
					attrs.put(lvalue, rvalue);
					mode = IN_TAG;
					// See section the XML spec, section 3.3.3
					// on normalization processing.
				}

				// Markus Hohenwarter, begin
				// I need to get all characters within quotes
				// including newlines
				// else if (" \r\n\u0009".indexOf(c) >= 0) {
				// sb.append(' ');
				// }
				// Markus Hohenwarter, end

				else if (c == '&') {
					stack.push(new Integer(mode));
					mode = ENTITY;
					etag.setLength(0);
				} else {
					sb.append((char) c);
				}
				break;

			case ATTRIBUTE_RVALUE:
				if (c == '"' || c == '\'') {
					quotec = c;
					mode = QUOTE;
				} else if (!StringUtil.isWhitespace((char) c)) {
					exc("Error in attribute processing", line, col);
				}
				break;

			case ATTRIBUTE_LVALUE:
				if (StringUtil.isWhitespace((char) c)) {
					lvalue = sb.toString();
					sb.setLength(0);
					mode = ATTRIBUTE_EQUAL;
				} else if (c == '=') {
					lvalue = sb.toString();
					sb.setLength(0);
					mode = ATTRIBUTE_RVALUE;
				} else {
					sb.append((char) c);
				}
				break;

			case ATTRIBUTE_EQUAL:
				if (c == '=') {
					mode = ATTRIBUTE_RVALUE;
				} else if (!StringUtil.isWhitespace((char) c)) {
					exc("Error in attribute processing.", line, col);
				}
				break;

			case IN_TAG:
				switch (c) {
				case '>':
					mode = popMode(stack);
					doc.startElement(tagName, attrs);
					depth++;
					tagName = null;
					// attrs = new LinkedHashMap();
					attrs.clear();
					break;

				case '/':
					mode = SINGLE_TAG;
					break;

				default:
					if (!StringUtil.isWhitespace((char) c)) {
						mode = ATTRIBUTE_LVALUE;
						sb.append((char) c);
					}
				}
				break;
			}
		}

		if (mode == DONE)
			doc.endDocument();
		else
			exc("missing end tag", line, col);

	}

	private static void exc(String s, int line, int col) throws Exception {
		throw new Exception(s + " near line " + line + ", column " + col);
	}
}
