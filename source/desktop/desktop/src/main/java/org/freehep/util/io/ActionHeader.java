// Copyright 2001, FreeHEP.
package org.freehep.util.io;

/**
 * Keeps the actionCode and Length of a specific action. To be used in the
 * TaggedInputStream to return the actionCode and Length, and in the
 * TaggedOutputStream to write them.
 * 
 * @author Mark Donszelmann
 * @author Charles Loomis
 * @version $Id: ActionHeader.java,v 1.3 2008-05-04 12:21:27 murkle Exp $
 */
public class ActionHeader {

	int actionCode;

	long length;

	/**
	 * Creates an action header
	 * 
	 * @param actionCode
	 *            code for action
	 * @param length
	 *            total length of the tag
	 */
	public ActionHeader(int actionCode, long length) {
		this.actionCode = actionCode;
		this.length = length;
	}

	/**
	 * Sets the action code
	 * 
	 * @param actionCode
	 *            new action code
	 */
	public void setAction(int actionCode) {
		this.actionCode = actionCode;
	}

	/**
	 * @return action code
	 */
	public int getAction() {
		return actionCode;
	}

	/**
	 * Sets the length of this tag
	 * 
	 * @param length
	 *            new length
	 */
	public void setLength(long length) {
		this.length = length;
	}

	/**
	 * @return length of this tag
	 */
	public long getLength() {
		return length;
	}
}
