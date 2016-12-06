// Copyright 2001, FreeHEP.
package org.freehep.util.io;

import java.io.IOException;

/**
 * Exception for the TaggedOutputStream. Signals that the user tries to write a
 * tag which is not defined at this version or below.
 * 
 * @author Mark Donszelmann
 * @author Charles Loomis
 * @version $Id: UndefinedTagException.java,v 1.3 2008-05-04 12:21:33 murkle Exp
 *          $
 */
public class UndefinedTagException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7504997713135869344L;

	/**
	 * Create an Undefined Tag Exception.
	 */
	public UndefinedTagException() {
		super();
	}

	/**
	 * Create an Undefined Tag Exception.
	 * 
	 * @param msg
	 *            message
	 */
	public UndefinedTagException(String msg) {
		super(msg);
	}

	/**
	 * Create an Undefined Tag Exception.
	 * 
	 * @param code
	 *            undefined tagID
	 */
	public UndefinedTagException(int code) {
		super("Code: (" + code + ")");
	}
}
