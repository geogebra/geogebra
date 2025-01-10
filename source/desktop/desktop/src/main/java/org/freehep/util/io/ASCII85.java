// Copyright 2001, FreeHEP.
package org.freehep.util.io;

/**
 * Constants for the ASCII85 encoding.
 * 
 * @author Mark Donszelmann
 * @version $Id: ASCII85.java,v 1.3 2008-05-04 12:21:41 murkle Exp $
 */
public interface ASCII85 {

	/**
	 * Maxmimum line length for ASCII85
	 */
	public final static int MAX_CHARS_PER_LINE = 80;

	/**
	 * 85^1
	 */
	public static long a85p1 = 85;

	/**
	 * 85^2
	 */
	public static long a85p2 = a85p1 * a85p1;

	/**
	 * 85^3
	 */
	public static long a85p3 = a85p2 * a85p1;

	/**
	 * 85^4
	 */
	public static long a85p4 = a85p3 * a85p1;

}
