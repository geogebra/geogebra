// Copyright 2001-2006, FreeHEP.
package org.freehep.graphicsio.emf;

import java.io.IOException;

import org.freehep.util.io.Tag;
import org.freehep.util.io.TaggedInputStream;
import org.freehep.util.io.TaggedOutputStream;

/**
 * EMF specific tag, from which all other EMF Tags inherit.
 * 
 * @author Mark Donszelmann
 * @version $Id: EMFTag.java,v 1.5 2009-08-17 21:44:45 murkle Exp $
 */
public abstract class EMFTag extends Tag {

	protected int flags = 0;

	/**
	 * Constructs a EMFTag.
	 * 
	 * @param id
	 *            id of the element
	 * @param version
	 *            emf version in which this element was first supported
	 */
	protected EMFTag(int id, int version) {
		super(id, version);
	}

	@Override
	public Tag read(int tagID, TaggedInputStream input, int len)
			throws IOException {

		EMFInputStream emf = (EMFInputStream) input;
		EMFTagHeader tagHeader = (EMFTagHeader) emf.getTagHeader();
		flags = tagHeader.getFlags();
		return read(tagID, emf, len);
	}

	public abstract EMFTag read(int tagID, EMFInputStream emf, int len)
			throws IOException;

	@Override
	public void write(int tagID, TaggedOutputStream output) throws IOException {
		write(tagID, (EMFOutputStream) output);
	}

	/**
	 * Writes the extra tag information to the outputstream in binary format.
	 * This implementation writes nothing, but concrete tags may override this
	 * method. This method is called just after the TagHeader is written.
	 * 
	 * @param tagID
	 *            id of the tag
	 * @param emf
	 *            Binary EMF output stream
	 */
	public void write(int tagID, EMFOutputStream emf) throws IOException {
		// empty
	}

	public int getFlags() {
		return flags;
	}

	/**
	 * @return a description of the tagName and tagID
	 */
	@Override
	public String toString() {
		int id = getTag();
		return "EMFTag " + getName() + " (" + id + ") (0x"
				+ Integer.toHexString(id) + ")";
	}
}
