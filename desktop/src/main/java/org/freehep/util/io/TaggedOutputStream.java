// Copyright 2001, FreeHEP.
package org.freehep.util.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Class to write Tagged blocks to a Stream. The tagged blocks (Tags) contain a
 * tagID and a Length, so that known and unknown tags (read with the
 * TaggedInputStream) can again be written. The stream also allows to write
 * Actions, which again come with a actionCode and a length.
 * 
 * A concrete implementation of this stream should encode/write the TagHeader.
 * All Concrete tags should be inherited from the Tag class and implement their
 * write methods.
 * 
 * @author Mark Donszelmann
 * @author Charles Loomis
 * @version $Id: TaggedOutputStream.java,v 1.3 2008-05-04 12:20:54 murkle Exp $
 */
public abstract class TaggedOutputStream extends ByteCountOutputStream
		implements TaggedOutput {
	/**
	 * Set of tags that can be used by this Stream
	 */
	protected TagSet tagSet;

	/**
	 * Set of actions that can be used by this Stream
	 */
	protected ActionSet actionSet;

	/**
	 * Create a Tagged Output stream.
	 * 
	 * @param out
	 *            stream to write
	 * @param tagSet
	 *            allowable tag set
	 * @param actionSet
	 *            allowable action set
	 */
	public TaggedOutputStream(OutputStream out, TagSet tagSet,
			ActionSet actionSet) {
		this(out, tagSet, actionSet, false);
	}

	/**
	 * Create a Tagged Output stream.
	 * 
	 * @param out
	 *            stream to write
	 * @param tagSet
	 *            allowable tag set
	 * @param actionSet
	 *            allowable action set
	 * @param littleEndian
	 *            true if stream is little endian
	 */
	public TaggedOutputStream(OutputStream out, TagSet tagSet,
			ActionSet actionSet, boolean littleEndian) {
		super(out, littleEndian);

		this.tagSet = tagSet;
		this.actionSet = actionSet;
	}

	/**
	 * Writes the TagHeader, which includes a TagID and a length.
	 * 
	 * @param header
	 *            TagHeader to write
	 * @throws IOException
	 *             if write fails
	 */
	protected abstract void writeTagHeader(TagHeader header) throws IOException;

	/**
	 * Specifies tag alignment: 1 byte, 2 short, 4 int and 8 long.
	 * 
	 * @return tag alignment
	 */
	protected int getTagAlignment() {
		return 1;
	}

	/*
	 * Write a tag.
	 */
	@Override
	public void writeTag(Tag tag) throws IOException {

		int tagID = tag.getTag();

		if (!tagSet.exists(tagID)) {
			throw new UndefinedTagException(tagID);
		}

		pushBuffer();
		tag.write(tagID, this);
		int align = getTagAlignment();
		int pad = (align - (getBufferLength() % align)) % align;
		for (int i = 0; i < pad; i++) {
			write(0);
		}
		int len = popBuffer();
		TagHeader header = createTagHeader(tag, len);
		writeTagHeader(header);
		append();
	}

	/**
	 * Returns newly created TagHeader. The default implementation creates a
	 * tagHeader from tagID and length. This method is called "after" the tag
	 * information is written, but the tag header is inserted before the tag
	 * info into the stream. Its called after since it needs the length of the
	 * tag info.
	 */
	protected TagHeader createTagHeader(Tag tag, long len) {
		return new TagHeader(tag.getTag(), len);
	}

	/**
	 * Writes the ActionHeader, which includes an actionCode and a length.
	 * 
	 * @param header
	 *            ActionHeader to write
	 * @throws IOException
	 *             if write fails
	 */
	protected abstract void writeActionHeader(ActionHeader header)
			throws IOException;

	/**
	 * Write action.
	 * 
	 * @param action
	 *            action to write
	 * @throws IOException
	 *             if write fails
	 */
	public void writeAction(Action action) throws IOException {
		// handle end of action stream
		if (action == null) {
			writeByte(0);
			return;
		}

		int actionCode = action.getCode();

		if (!actionSet.exists(actionCode)) {
			throw new UndefinedTagException(actionCode);
		}

		pushBuffer();
		action.write(actionCode, this);
		int len = popBuffer();
		ActionHeader header = new ActionHeader(actionCode, len);
		writeActionHeader(header);
		append();
	}

}
