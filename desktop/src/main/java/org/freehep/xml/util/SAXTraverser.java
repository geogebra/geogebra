/*
 * SAXTraverser.java
 *
 * Created on February 9, 2001, 3:02 PM
 */

package org.freehep.xml.util;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A utility for working with SAX parsers. A heirarchy of SAXTraversers can be
 * used to maintain state while parsing an XML file.
 * 
 * @author tonyj
 * @version $Id: SAXTraverser.java,v 1.4 2009-06-22 02:18:23 hohenwarter Exp $
 */
public class SAXTraverser extends DefaultHandler {
	/**
	 * Attach an XMLReader to this traverser. Registers this SAXTraverser as the
	 * content handler for the reader.
	 */
	public void setReader(XMLReader reader) {
		this.parser = reader;
		reader.setContentHandler(this);
	}

	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes attrs) throws SAXException {
		qName = qName.intern();
		SAXTraverser newHandler = handleElement(qName, attrs);
		if (newHandler != this) {
			newHandler.handleElementAttributes(attrs);
			newHandler.previous = this;
			newHandler.parser = parser;
			parser.setContentHandler(newHandler);
		} else {
			stack++;
		}

	}

	@Override
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		if (stack == 0) {
			handleEndElement(localName);
			previous.handleSubElement(this);
			parser.setContentHandler(previous);
		} else {
			stack--;
		}
	}

	/**
	 * Override to handle node attributes
	 */
	protected void handleElementAttributes(Attributes atts)
			throws SAXException {
	}

	/**
	 * Override this to handle the end of an element
	 */
	protected void handleEndElement(String name) throws SAXException {
	}

	/**
	 * Override this to be notified of sub nodes of the current node
	 */
	protected void handleSubElement(SAXTraverser sub) throws SAXException {
	}

	/**
	 * Override this to handle the start of new sub elements Return the
	 * SAXTraverser to be used for the sub node, or <code>this</code> to
	 * continue using the current traverser.
	 * 
	 * @return The SAXTraverser to be used as the content handler for the sub
	 *         node
	 */
	protected SAXTraverser handleElement(String name, Attributes attrs)
			throws SAXException {
		throw new BadXMLException("Unhandled element " + name);
	}

	/**
	 * Utility routine to convert a String to a boolean
	 */
	public boolean toBoolean(String value) throws BadXMLException {
		if (value.equalsIgnoreCase("true")) {
			return true;
		} else if (value.equalsIgnoreCase("false")) {
			return false;
		} else {
			throw new BadXMLException("Bad boolean value " + value);
		}
	}

	/**
	 * Utility routine to convert a String to a double
	 */
	public double toDouble(String value) throws SAXException {
		try {
			return new Double(value).doubleValue();
		} catch (Throwable x) {
			throw new BadXMLException("Bad double value " + value);
		}
	}

	/**
	 * Utility routine to convert a String to a float
	 */
	public float toFloat(String value) throws SAXException {
		try {
			return Float.parseFloat(value);
		} catch (Throwable x) {
			throw new BadXMLException("Bad float value " + value);
		}
	}

	/**
	 * Utility routine to convert a String to an int
	 */
	public int toInt(String value) throws SAXException {
		try {
			return Integer.parseInt(value);
		} catch (Throwable x) {
			throw new BadXMLException("Bad integer value " + value);
		}
	}

	/**
	 * Utility routine to convert a String to a short
	 */
	public short toShort(String value) throws SAXException {
		try {
			return Short.parseShort(value);
		} catch (Throwable x) {
			throw new BadXMLException("Bad short value " + value);
		}
	}

	/**
	 * Utility routine to convert a String to a long
	 */
	public long toLong(String value) throws SAXException {
		try {
			return Long.parseLong(value);
		} catch (Throwable x) {
			throw new BadXMLException("Bad long value " + value);
		}
	}

	/**
	 * Utility routine to convert a String to a byte
	 */
	public byte toByte(String value) throws SAXException {
		try {
			return Byte.parseByte(value);
		} catch (Throwable x) {
			throw new BadXMLException("Bad byte value " + value);
		}
	}

	private SAXTraverser previous;
	private XMLReader parser;
	private int stack = 0;
}
