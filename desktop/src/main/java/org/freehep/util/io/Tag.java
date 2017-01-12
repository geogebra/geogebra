// Copyright 2001, FreeHEP.
package org.freehep.util.io;

import java.io.IOException;

/**
 * Generic Tag to be used by TaggedIn/OutputStreams. The tag contains an ID,
 * name and a version. Concrete subclasses should implement the IO Read and
 * Write methods.
 * 
 * @author Mark Donszelmann
 * @author Charles Loomis
 * @version $Id: Tag.java,v 1.3 2008-05-04 12:21:24 murkle Exp $
 */
public abstract class Tag {

	private int tagID;

	private String name;

	private int version;

	/**
	 * This is the tagID for the default tag handler.
	 */
	final public static int DEFAULT_TAG = -1;

	protected Tag(int tagID, int version) {
		this.tagID = tagID;
		this.version = version;
		this.name = null;
	}

	/**
	 * Get the tag number.
	 * 
	 * @return tagID
	 */
	public int getTag() {
		return tagID;
	}

	/**
	 * Get the version number.
	 * 
	 * @return version number
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * Get the tag name.
	 * 
	 * @return tag name
	 */
	public String getName() {
		if (name == null) {
			name = getClass().getName();
			int dot = name.lastIndexOf(".");
			name = (dot >= 0) ? name.substring(dot + 1) : name;
		}
		return name;
	}

	/**
	 * This returns the type of block
	 * 
	 * @return tag type
	 */
	public int getTagType() {
		return 0;
	}

	/**
	 * This reads the information from the given input and returns a new Tag
	 * 
	 * @param tagID
	 *            id of the tag to read
	 * @param input
	 *            stream to read from
	 * @param len
	 *            length to read
	 * @return read Tag
	 * @throws IOException
	 *             if read fails
	 */
	public abstract Tag read(int tagID, TaggedInputStream input, int len)
			throws IOException;

	/**
	 * This writes the information to the given output
	 * 
	 * @param tagID
	 *            id of tag to write
	 * @param output
	 *            stream to write to
	 * @throws IOException
	 *             if write fails
	 */
	public abstract void write(int tagID, TaggedOutputStream output)
			throws IOException;

	@Override
	public abstract String toString();
}
