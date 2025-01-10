// Copyright 2001, FreeHEP.
package org.freehep.util.io;

import java.io.IOException;

/**
 * Encoding Exception for any of the encoding streams.
 * 
 * @author Mark Donszelmann
 * @version $Id: EncodingException.java,v 1.3 2008-05-04 12:22:07 murkle Exp $
 */
public class EncodingException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8496816190751796701L;

	/**
	 * Creates an Encoding Exception
	 * 
	 * @param msg
	 *            message
	 */
	public EncodingException(String msg) {
		super(msg);
	}
}
