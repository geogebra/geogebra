// Copyright 2002-2007, FreeHEP.
package org.freehep.xml.util;

import java.awt.Color;
import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;

import org.freehep.util.io.IndentPrintWriter;

/**
 * A class that makes it easy to write XML documents.
 *
 * @author Tony Johnson
 * @author Mark Donszelmann
 * @version $Id: XMLWriter.java,v 1.3 2008-05-04 12:20:34 murkle Exp $
 */
public class XMLWriter implements XMLTagWriter {
	public XMLWriter(Writer w, String indentString, String defaultNameSpace) {
		writer = new IndentPrintWriter(w);
		writer.setIndentString(indentString);
		this.defaultNameSpace = defaultNameSpace;
	}

	public XMLWriter(Writer w, String indentString) {
		this(w, indentString, "");
	}

	public XMLWriter(Writer w) {
		this(w, "  "); // By popular demand of Babar
	}

	/**
	 * closes the writer
	 */
	@Override
	public void close() throws IOException {
		closeDoc();
		writer.close();
	}

	/**
	 * Opens the document with an xml header
	 */
	@Override
	public void openDoc() {
		openDoc("1.0", "", false);
	}

	/**
	 * Opens the document with an xml header
	 */
	@Override
	public void openDoc(String version, String encoding, boolean standalone) {
		String indentString = writer.getIndentString();
		writer.setIndentString(indentString);

		closed = false;
		if (!XMLCharacterProperties.validVersionNum(version)) {
			throw new RuntimeException("Invalid version number: " + version);
		}
		writer.print("<?xml version=\"");
		writer.print(version);
		writer.print("\" ");
		if ((encoding != null) && (!encoding.equals(""))) {
			if (!XMLCharacterProperties.validEncName(encoding)) {
				throw new RuntimeException(
						"Invalid encoding name: " + encoding);
			}
			writer.print("encoding=\"");
			writer.print(encoding);
			writer.print("\" ");
		}
		if (standalone) {
			writer.print("standalone=\"yes\" ");
		}
		writer.println("?>");
		writer.setIndentString(indentString);
	}

	/**
	 * Writes a reference to a DTD
	 */
	@Override
	public void referToDTD(String name, String pid, String ref) {
		if (dtdName != null) {
			throw new RuntimeException("ReferToDTD cannot be called twice");
		}
		dtdName = name;
		writer.println("<!DOCTYPE " + name + " PUBLIC \"" + pid + "\" \"" + ref
				+ "\">");
	}

	/**
	 * Writes a reference to a DTD
	 */
	@Override
	public void referToDTD(String name, String system) {
		if (dtdName != null) {
			throw new RuntimeException("ReferToDTD cannot be called twice");
		}
		dtdName = name;
		writer.println("<!DOCTYPE " + name + " SYSTEM \"" + system + "\">");
	}

	/**
	 * Closes the document, and checks if you closed all the tags
	 */
	@Override
	public void closeDoc() {
		if (!closed) {
			if (!openTags.isEmpty()) {
				StringBuffer sb = new StringBuffer(
						"Not all tags were closed before closing XML document:\n");
				while (!openTags.isEmpty()) {
					sb.append("   </");
					sb.append((String) openTags.pop());
					sb.append(">\n");
				}
				throw new RuntimeException(sb.toString());
			}
			closed = true;
		}
		writer.flush();
	}

	/**
	 * Print a comment
	 */
	@Override
	public void printComment(String comment) {
		if (comment.indexOf("--") >= 0) {
			throw new RuntimeException("'--' sequence not allowed in comment");
		}
		writer.print("<!--");
		writer.print(normalizeText(comment));
		writer.println("-->");
	}

	/**
	 * Prints character data, while escaping &lt; and &gt;
	 */
	@Override
	public void print(String text) {
		writer.print(normalizeText(text));
	}

	/**
	 * Prints character data, while escaping &lt; and &gt;
	 */
	public void println(String text) {
		print(text);
		writer.println();
	}

	/**
	 * Prints a new XML tag and increases the identation level
	 */
	@Override
	public void openTag(String namespace, String name) {
		if (namespace.equals(defaultNameSpace)) {
			openTag(name);
		} else {
			openTag(namespace + ":" + name);
		}
	}

	/**
	 * Prints a new XML tag and increases the identation level
	 */
	@Override
	public void openTag(String name) {
		checkNameValid(name);
		if (openTags.isEmpty() && dtdName != null && !dtdName.equals(name)) {
			throw new RuntimeException("First tag: '" + name
					+ "' not equal to DTD id: '" + dtdName + "'");
		}
		writer.print("<" + name);
		printAttributes(name.length());
		writer.println(">");
		writer.indent();
		openTags.push(name);
	}

	/**
	 * Closes the current XML tag and decreases the indentation level
	 */
	@Override
	public void closeTag() {
		if (openTags.isEmpty()) {
			writer.close();
			throw new RuntimeException("No open tags");
		}
		Object name = openTags.pop();
		writer.outdent();
		writer.print("</");
		writer.print(name);
		writer.println(">");
	}

	/**
	 * Prints an empty XML tag.
	 */
	@Override
	public void printTag(String namespace, String name) {
		if (namespace.equals(defaultNameSpace)) {
			printTag(name);
		} else {
			printTag(namespace + ":" + name);
		}
	}

	/**
	 * Prints an empty XML tag.
	 */
	@Override
	public void printTag(String name) {
		checkNameValid(name);
		writer.print("<" + name);
		printAttributes(name.length());
		writer.println("/>");
	}

	/**
	 * Sets an attribute which will be included in the next tag printed by
	 * openTag or printTag
	 */
	@Override
	public void setAttribute(String name, String value) {
		if ((name != null) && (value != null)) {
			attributes.put(name, value);
		}
	}

	@Override
	public void setAttribute(String namespace, String name, String value) {
		if ((namespace != null) && (name != null)) {
			attributes.put(namespace + ":" + name, value);
		}
	}

	@Override
	public void setAttribute(String name, byte value) {
		setAttribute(name, String.valueOf(value));
	}

	@Override
	public void setAttribute(String name, char value) {
		setAttribute(name, String.valueOf(value));
	}

	@Override
	public void setAttribute(String name, long value) {
		setAttribute(name, String.valueOf(value));
	}

	@Override
	public void setAttribute(String name, int value) {
		setAttribute(name, String.valueOf(value));
	}

	@Override
	public void setAttribute(String name, short value) {
		setAttribute(name, String.valueOf(value));
	}

	@Override
	public void setAttribute(String name, boolean value) {
		setAttribute(name, String.valueOf(value));
	}

	@Override
	public void setAttribute(String name, float value) {
		setAttribute(name, String.valueOf(value));
	}

	@Override
	public void setAttribute(String name, double value) {
		setAttribute(name, String.valueOf(value));
	}

	@Override
	public void setAttribute(String name, Color value) {
		setAttribute(name, String.valueOf(value));
	}

	@Override
	public void setAttribute(String ns, String name, byte value) {
		setAttribute(ns + ":" + name, String.valueOf(value));
	}

	@Override
	public void setAttribute(String ns, String name, char value) {
		setAttribute(ns + ":" + name, String.valueOf(value));
	}

	@Override
	public void setAttribute(String ns, String name, long value) {
		setAttribute(ns + ":" + name, String.valueOf(value));
	}

	@Override
	public void setAttribute(String ns, String name, int value) {
		setAttribute(ns + ":" + name, String.valueOf(value));
	}

	@Override
	public void setAttribute(String ns, String name, short value) {
		setAttribute(ns + ":" + name, String.valueOf(value));
	}

	@Override
	public void setAttribute(String ns, String name, boolean value) {
		setAttribute(ns + ":" + name, String.valueOf(value));
	}

	@Override
	public void setAttribute(String ns, String name, float value) {
		setAttribute(ns + ":" + name, String.valueOf(value));
	}

	@Override
	public void setAttribute(String ns, String name, double value) {
		setAttribute(ns + ":" + name, String.valueOf(value));
	}

	@Override
	public void setAttribute(String ns, String name, Color value) {
		setAttribute(ns + ":" + name, String.valueOf(value));
	}

	protected void printAttributes(int tagLength) {
		int width = tagLength + 1;
		boolean extraIndent = false;
		Enumeration e = attributes.keys();
		while (e.hasMoreElements()) {
			String key = e.nextElement().toString();
			checkNameValid(key);
			String value = normalize(attributes.get(key).toString());
			int length = key.length() + value.length() + 3;
			if (width > 0 && width + length + 2 * writer.getIndent() > 60) {
				width = 0;
				writer.println();
				if (!extraIndent) {
					writer.indent();
					extraIndent = true;
				}
			} else {
				width += length;
				writer.print(' ');
			}
			writer.print(key);
			writer.print("=\"");
			writer.print(value);
			writer.print("\"");
		}
		attributes.clear();
		if (extraIndent) {
			writer.outdent();
		}
	}

	// /**
	// * Prints a DOM node, recursively.
	// * No support for a document node
	// */
	// public void print(Node node)
	// {
	// if ( node == null ) return;
	//
	// int type = node.getNodeType();
	// switch ( type ) {
	// // print document
	// case Node.DOCUMENT_NODE:
	// throw new RuntimeException("No support for printing nodes of type
	// Document");
	//
	// // print element with attributes
	// case Node.ELEMENT_NODE:
	// NamedNodeMap attributes = node.getAttributes();
	// for ( int i = 0; i < attributes.getLength(); i++ ) {
	// Node attr = attributes.item(i);
	// setAttribute(attr.getNodeName(), attr.getNodeValue());
	// }
	// NodeList children = node.getChildNodes();
	// if ( children == null ) {
	// printTag(node.getNodeName());
	// } else {
	// openTag(node.getNodeName());
	// int len = children.getLength();
	// for ( int i = 0; i < len; i++ ) {
	// print(children.item(i));
	// }
	// closeTag();
	// }
	// break;
	//
	// // handle entity reference nodes
	// case Node.ENTITY_REFERENCE_NODE:
	// writer.print('&');
	// writer.print(node.getNodeName());
	// writer.print(';');
	// break;
	//
	// // print cdata sections
	// case Node.CDATA_SECTION_NODE:
	// writer.print("<![CDATA[");
	// writer.print(node.getNodeValue());
	// writer.print("]]>");
	// break;
	//
	// // print text
	// case Node.TEXT_NODE:
	// print(node.getNodeValue());
	// break;
	//
	// // print processing instruction
	// case Node.PROCESSING_INSTRUCTION_NODE:
	// writer.print("<?");
	// writer.print(node.getNodeName());
	// String data = node.getNodeValue();
	// if ( data != null && data.length() > 0 ) {
	// writer.print(' ');
	// writer.print(data);
	// }
	// writer.print("?>");
	// break;
	// }
	// } // print(Node)

	/** Normalizes the given string for an Attribute value */
	public static String normalize(String s) {
		StringBuffer str = new StringBuffer();

		int len = (s != null) ? s.length() : 0;
		for (int i = 0; i < len; i++) {
			char ch = s.charAt(i);
			switch (ch) {
			case '<': {
				str.append("&lt;");
				break;
			}
			case '>': {
				str.append("&gt;");
				break;
			}
			case '&': {
				str.append("&amp;");
				break;
			}
			case '"': {
				str.append("&quot;");
				break;
			}
			case '\r':
			case '\n': {
				str.append("&#");
				str.append(Integer.toString(ch));
				str.append(';');
				break;
			}
			default: {
				if (ch > 0x00FF) {
					String hex = "0000" + Integer.toHexString(ch);
					str.append("&#x");
					str.append(hex.substring(hex.length() - 4));
					str.append(';');
				} else {
					str.append(ch);
				}
			}
			}
		}

		return str.toString();

	} // normalize(String):String

	/** Normalizes the given string for Text */
	public static String normalizeText(String s) {
		StringBuffer str = new StringBuffer();

		int len = (s != null) ? s.length() : 0;
		for (int i = 0; i < len; i++) {
			char ch = s.charAt(i);
			switch (ch) {
			case '<': {
				str.append("&lt;");
				break;
			}
			case '>': {
				str.append("&gt;");
				break;
			}
			case '&': {
				str.append("&amp;");
				break;
			}
			default: {
				if (ch > 0x007f) {
					String hex = "0000" + Integer.toHexString(ch);
					str.append("&#x");
					str.append(hex.substring(hex.length() - 4));
					str.append(';');
				} else {
					str.append(ch);
				}
			}
			}
		}
		return str.toString();
	}

	protected void checkNameValid(String s) {
		if (!XMLCharacterProperties.validName(s)) {
			throw new RuntimeException("Invalid name: " + s);
		}
	}

	protected boolean closed = true;
	private String dtdName = null;
	private Hashtable attributes = new Hashtable();
	private Stack openTags = new Stack();
	protected IndentPrintWriter writer;
	protected String defaultNameSpace;
}
