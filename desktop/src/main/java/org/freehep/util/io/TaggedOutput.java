// Copyright 2001, FreeHEP.
package org.freehep.util.io;

import java.io.IOException;

/**
 * @author Mark Donszelmann
 * @version $Id: TaggedOutput.java,v 1.3 2008-05-04 12:22:17 murkle Exp $
 */
public interface TaggedOutput {

	/**
	 * Write a tag.
	 * 
	 * @param tag
	 *            tag to write
	 * @throws IOException
	 *             if write fails
	 */
	public void writeTag(Tag tag) throws IOException;

	/**
	 * Close the stream
	 * 
	 * @throws IOException
	 *             if close fails
	 */
	public void close() throws IOException;
}
