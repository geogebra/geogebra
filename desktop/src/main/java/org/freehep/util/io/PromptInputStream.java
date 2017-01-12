// Copyright 2002, FreeHEP.
package org.freehep.util.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * The PromptInputStream reads from an inputstream until it reads any prompt for
 * which a listener is added. The listener is informed that the prompt is found.
 * The route which contains the prompt is supplied as a parameter to the
 * listener. Returning from the prompt listener without reading the route to its
 * end will allow the main stream to read it.
 * 
 * The implementation of this class is based on the RoutedInputStream.
 * 
 * @author Mark Donszelmann
 * @version $Id: PromptInputStream.java,v 1.3 2008-05-04 12:21:47 murkle Exp $
 */
public class PromptInputStream extends RoutedInputStream {

	/**
	 * Craete a Prompt input stream.
	 * 
	 * @param input
	 *            stream to read
	 */
	public PromptInputStream(InputStream input) {
		super(input);
	}

	/**
	 * Add a prompt listener for given prompt.
	 * 
	 * @param prompt
	 *            prompt to listen for
	 * @param listener
	 *            listener to be informed
	 */
	public void addPromptListener(String prompt, PromptListener listener) {
		final PromptListener promptListener = listener;
		addRoute(prompt, prompt, new RouteListener() {
			@Override
			public void routeFound(RoutedInputStream.Route input)
					throws IOException {
				promptListener.promptFound(input);
			}
		});
	}
}
