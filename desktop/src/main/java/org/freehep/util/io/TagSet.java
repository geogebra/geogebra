// Copyright 2001, FreeHEP.
package org.freehep.util.io;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to keep registered Tags, which should be used by the
 * TaggedIn/OutputStream.
 * 
 * A set of recognized Tags can be added to this class. A concrete
 * implementation of this stream should install all allowed tags.
 * 
 * @author Mark Donszelmann
 * @author Charles Loomis
 * @version $Id: TagSet.java,v 1.3 2008-05-04 12:21:54 murkle Exp $
 */
public class TagSet {

	/**
	 * This holds the individual tags.
	 */
	protected Map tags;

	/**
	 * The default tag handler.
	 */
	protected Tag defaultTag;

	/**
	 * Creates a Tag Set.
	 */
	public TagSet() {
		// Initialize the tag classes.
		defaultTag = new UndefinedTag();
		tags = new HashMap();
	}

	/**
	 * Add a new tag to this set. If the tagID returned is the DEFAULT_TAG, then
	 * the default handler is set to the given handler.
	 * 
	 * @param tag
	 *            tag to be added to set
	 */
	public void addTag(Tag tag) {
		int id = tag.getTag();
		if (id != Tag.DEFAULT_TAG) {
			tags.put(Integer.valueOf(id), tag);
		} else {
			defaultTag = tag;
		}
	}

	/**
	 * Find tag for tagID.
	 * 
	 * @param tagID
	 *            tagID to find
	 * @return correspoding tag or UndefinedTag if tagID is not found.
	 */
	public Tag get(int tagID) {
		Tag tag = (Tag) tags.get(Integer.valueOf(tagID));
		if (tag == null) {
			tag = defaultTag;
		}
		return tag;
	}

	/**
	 * Finds out if Tag for TagID exists.
	 * 
	 * @param tagID
	 *            tagID to find
	 * @return true if corresponding Tag for TagID exists
	 */
	public boolean exists(int tagID) {
		return (tags.get(Integer.valueOf(tagID)) != null);
	}
}
