// Copyright 2001-2006, FreeHEP.
package org.freehep.util.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * Class to read Tagged blocks from a Stream. The tagged blocks (Tags) contain a
 * tagID and a Length, so that known and unknown tags can be read and written
 * (using the TaggedOutputStream). The stream also allows to read Actions, which
 * again come with a actionCode and a length.
 * 
 * A set of recognized Tags and Actions can be added to this stream. A concrete
 * implementation of this stream should decode/read the TagHeader. All Concrete
 * tags should be inherited from the Tag class and implement their read methods.
 * 
 * @author Mark Donszelmann
 * @author Charles Loomis
 * @version $Id: TaggedInputStream.java,v 1.3 2008-05-04 12:22:14 murkle Exp $
 */
public abstract class TaggedInputStream extends ByteCountInputStream {

	/**
	 * Set of tags that can be used by this Stream
	 */
	protected TagSet tagSet;

	/**
	 * Set of actions that can be used by this Stream
	 */
	protected ActionSet actionSet;

	/**
	 * Currently read tagHeader, valid during readTag call.
	 */
	private TagHeader tagHeader;

	/**
	 * Creates a Tagged Input Stream
	 * 
	 * @param in
	 *            stream to read from
	 * @param tagSet
	 *            available tag set
	 * @param actionSet
	 *            available action set
	 */
	public TaggedInputStream(InputStream in, TagSet tagSet,
			ActionSet actionSet) {
		this(in, tagSet, actionSet, false);
	}

	/**
	 * Creates a Tagged Input Stream
	 * 
	 * @param in
	 *            stream to read from
	 * @param tagSet
	 *            available tag set
	 * @param actionSet
	 *            available action set
	 * @param littleEndian
	 *            true if stream is little endian
	 */
	public TaggedInputStream(InputStream in, TagSet tagSet, ActionSet actionSet,
			boolean littleEndian) {
		super(in, littleEndian, 8);

		this.tagSet = tagSet;
		this.actionSet = actionSet;
	}

	/**
	 * Add tag to tagset
	 * 
	 * @param tag
	 *            new tag
	 */
	public void addTag(Tag tag) {
		tagSet.addTag(tag);
	}

	/**
	 * Decodes and returns the TagHeader, which includes a TagID and a length.
	 * 
	 * @return Decoded TagHeader
	 * @throws IOException
	 *             if read fails
	 */
	protected abstract TagHeader readTagHeader() throws IOException;

	/**
	 * Read a tag.
	 * 
	 * @return read tag
	 * @throws IOException
	 *             if read fails
	 */
	public Tag readTag() throws IOException {

		tagHeader = readTagHeader();
		if (tagHeader == null) {
			return null;
		}

		int size = (int) tagHeader.getLength();

		// Look up the proper tag.
		Tag tag = tagSet.get(tagHeader.getTag());

		// set max tag length and read tag
		pushBuffer(size);
		tag = tag.read(tagHeader.getTag(), this, size);
		byte[] rest = popBuffer();

		// read non-read part of tag
		if (rest != null) {
			throw new IncompleteTagException(tag, rest);
		}
		return tag;
	}

	/**
	 * Returns the currently valid TagHeader. Can be called durring the
	 * tag.read() method.
	 */
	public TagHeader getTagHeader() {
		return tagHeader;
	}

	/**
	 * Add action to action set.
	 * 
	 * @param action
	 *            new action
	 */
	public void addAction(Action action) {
		actionSet.addAction(action);
	}

	/**
	 * Decodes and returns the ActionHeader, which includes an actionCode and a
	 * length.
	 * 
	 * @return decoded ActionHeader
	 * @throws IOException
	 *             if read fails
	 */
	protected abstract ActionHeader readActionHeader() throws IOException;

	/**
	 * Reads action.
	 * 
	 * @return read action
	 * @throws IOException
	 *             if read fails
	 */
	public Action readAction() throws IOException {

		ActionHeader header = readActionHeader();
		if (header == null) {
			return null;
		}

		int size = (int) header.getLength();

		// Look up the proper action.
		Action action = actionSet.get(header.getAction());

		pushBuffer(size);
		action = action.read(header.getAction(), this, size);
		byte[] rest = popBuffer();

		if (rest != null) {
			throw new IncompleteActionException(action, rest);
		}
		return action;
	}

}
