// Copyright 2005-2007, FreeHEP.
package org.freehep.xml.util;

import java.awt.Color;
import java.io.IOException;

/**
 * XMLTagWriter Interface. Attributes need to be set before tags are written.
 *
 * @author Mark Donszelmann
 * @version $Id: XMLTagWriter.java,v 1.3 2008-05-04 12:20:50 murkle Exp $
 */
public interface XMLTagWriter {

	/**
	 * Write an xml open tag
	 * 
	 * @param ns
	 *            namespace
	 * @param name
	 *            tagname
	 * @throws IOException
	 *             if stream cannot be written
	 */
	public void openTag(String ns, String name) throws IOException;

	/**
	 * Write empty tag
	 * 
	 * @param ns
	 *            namespace
	 * @param name
	 *            tagname
	 * @throws IOException
	 *             if stream cannot be written
	 */
	public void printTag(String ns, String name) throws IOException;

	/**
	 * Close writer
	 * 
	 * @throws IOException
	 *             if stream cannot be written
	 */
	public void close() throws IOException;

	/**
	 * Open XML doc with standard parameters
	 * 
	 * @throws IOException
	 *             if stream cannot be written
	 */
	public void openDoc() throws IOException;

	/**
	 * Open XML doc
	 * 
	 * @param version
	 *            version string
	 * @param encoding
	 *            encoding
	 * @param standalone
	 *            if XML is standalone
	 * @throws IOException
	 *             if stream cannot be written
	 */
	public void openDoc(String version, String encoding, boolean standalone)
			throws IOException;

	/**
	 * Close XML doc
	 * 
	 * @throws IOException
	 *             if stream cannot be written
	 */
	public void closeDoc() throws IOException;

	public void referToDTD(String name, String system);

	public void referToDTD(String name, String pid, String ref);

	/**
	 * Write an xml open tag
	 * 
	 * @param name
	 *            tagname
	 * @throws IOException
	 *             if stream cannot be written
	 */
	public void openTag(String name) throws IOException;

	/**
	 * Close nearest tag
	 * 
	 * @throws IOException
	 *             if stream cannot be written
	 */
	public void closeTag() throws IOException;

	/**
	 * Write empty tag
	 * 
	 * @param name
	 *            tagname
	 * @throws IOException
	 *             if stream cannot be written
	 */
	public void printTag(String name) throws IOException;

	public void printComment(String comment) throws IOException;

	public void print(String text) throws IOException;

	/**
	 * Set String attribute
	 * 
	 * @param name
	 *            attribute name
	 * @param value
	 *            attribute value
	 */
	public void setAttribute(String name, String value);

	/**
	 * Set Color attribute
	 * 
	 * @param name
	 *            attribute name
	 * @param value
	 *            attribute value
	 */
	public void setAttribute(String name, Color value);

	/**
	 * Set byte attribute
	 * 
	 * @param name
	 *            attribute name
	 * @param value
	 *            attribute value
	 */
	public void setAttribute(String name, byte value);

	/**
	 * Set char attribute
	 * 
	 * @param name
	 *            attribute name
	 * @param value
	 *            attribute value
	 */
	public void setAttribute(String name, char value);

	/**
	 * Set long attribute
	 * 
	 * @param name
	 *            attribute name
	 * @param value
	 *            attribute value
	 */
	public void setAttribute(String name, long value);

	/**
	 * Set int attribute
	 * 
	 * @param name
	 *            attribute name
	 * @param value
	 *            attribute value
	 */
	public void setAttribute(String name, int value);

	/**
	 * Set short attribute
	 * 
	 * @param name
	 *            attribute name
	 * @param value
	 *            attribute value
	 */
	public void setAttribute(String name, short value);

	/**
	 * Set boolean attribute
	 * 
	 * @param name
	 *            attribute name
	 * @param value
	 *            attribute value
	 */
	public void setAttribute(String name, boolean value);

	/**
	 * Set float attribute
	 * 
	 * @param name
	 *            attribute name
	 * @param value
	 *            attribute value
	 */
	public void setAttribute(String name, float value);

	/**
	 * Set double attribute
	 * 
	 * @param name
	 *            attribute name
	 * @param value
	 *            attribute value
	 */
	public void setAttribute(String name, double value);

	/**
	 * Set String attribute
	 * 
	 * @param ns
	 *            namespace
	 * @param name
	 *            attribute name
	 * @param value
	 *            attribute value
	 */
	public void setAttribute(String ns, String name, String value);

	/**
	 * Set Color attribute
	 * 
	 * @param ns
	 *            namespace
	 * @param name
	 *            attribute name
	 * @param value
	 *            attribute value
	 */
	public void setAttribute(String ns, String name, Color value);

	/**
	 * Set byte attribute
	 * 
	 * @param ns
	 *            namespace
	 * @param name
	 *            attribute name
	 * @param value
	 *            attribute value
	 */
	public void setAttribute(String ns, String name, byte value);

	/**
	 * Set char attribute
	 * 
	 * @param ns
	 *            namespace
	 * @param name
	 *            attribute name
	 * @param value
	 *            attribute value
	 */
	public void setAttribute(String ns, String name, char value);

	/**
	 * Set long attribute
	 * 
	 * @param ns
	 *            namespace
	 * @param name
	 *            attribute name
	 * @param value
	 *            attribute value
	 */
	public void setAttribute(String ns, String name, long value);

	/**
	 * Set int attribute
	 * 
	 * @param ns
	 *            namespace
	 * @param name
	 *            attribute name
	 * @param value
	 *            attribute value
	 */
	public void setAttribute(String ns, String name, int value);

	/**
	 * Set short attribute
	 * 
	 * @param ns
	 *            namespace
	 * @param name
	 *            attribute name
	 * @param value
	 *            attribute value
	 */
	public void setAttribute(String ns, String name, short value);

	/**
	 * Set boolean attribute
	 * 
	 * @param ns
	 *            namespace
	 * @param name
	 *            attribute name
	 * @param value
	 *            attribute value
	 */
	public void setAttribute(String ns, String name, boolean value);

	/**
	 * Set float attribute
	 * 
	 * @param ns
	 *            namespace
	 * @param name
	 *            attribute name
	 * @param value
	 *            attribute value
	 */
	public void setAttribute(String ns, String name, float value);

	/**
	 * Set double attribute
	 * 
	 * @param ns
	 *            namespace
	 * @param name
	 *            attribute name
	 * @param value
	 *            attribute value
	 */
	public void setAttribute(String ns, String name, double value);

}
