// Copyright 2002, FreeHEP.
package org.freehep.util.io;

import java.io.IOException;

/**
 * Listener to inform that a specific route of the RoutedInputStream has been
 * found.
 * 
 * @author Mark Donszelmann
 * @version $Id: RouteListener.java,v 1.3 2008-05-04 12:22:01 murkle Exp $
 */
public interface RouteListener {

	/**
	 * Route was found, input is supplied. If you close the Route, all remaining
	 * bytes will be read/discarded up to and including the end marker. If the
	 * end marker is null, all bytes from the underling stream will be read. If
	 * you just return, the underlying main stream will still return every byte
	 * in this route. This way you can just be informed of the start of a route.
	 * 
	 * @param input
	 *            stream to read
	 * @throws IOException
	 *             if read fails
	 */
	public void routeFound(RoutedInputStream.Route input) throws IOException;
}
