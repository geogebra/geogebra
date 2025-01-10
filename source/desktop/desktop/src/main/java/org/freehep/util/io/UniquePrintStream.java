// Copyright 2001, FreeHEP.
package org.freehep.util.io;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * The UniquePrintStream keeps Strings buffered in sorted order, but any
 * duplicates are removed. This stream can be used to print error messages
 * exactly once. When finish is called all messages are printed.
 * 
 * It only acts on the println(String) method, any other method will print
 * directly.
 * 
 * @author Mark Donszelmann
 * @version $Id: UniquePrintStream.java,v 1.3 2008-05-04 12:21:08 murkle Exp $
 */
public class UniquePrintStream extends PrintStream
		implements FinishableOutputStream {

	private SortedSet msg = new TreeSet();

	/**
	 * Create a Unique Print Stream.
	 * 
	 * @param out
	 *            stream to write
	 */
	public UniquePrintStream(OutputStream out) {
		super(out);
	}

	@Override
	public void println(String s) {
		synchronized (this) {
			msg.add(s);
		}
	}

	@Override
	public void finish() {
		for (Iterator i = msg.iterator(); i.hasNext();) {
			String s = (String) i.next();
			super.println(s);
		}
		msg = new TreeSet();
	}
}
