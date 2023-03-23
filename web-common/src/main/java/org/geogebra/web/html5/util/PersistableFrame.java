package org.geogebra.web.html5.util;

import org.gwtproject.user.client.ui.Frame;

/**
 * Frame that should stay when keyboard is opened/closed
 * or floating panels come up.
 *
 */
public class PersistableFrame extends Frame
		implements Persistable {

	/**
	 * @param src
	 *            the source URL of the frame.
	 */
	public PersistableFrame(String src) {
		super(src);
	}
}