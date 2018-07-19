package org.geogebra.web.full.gui.util;

import org.geogebra.web.html5.gui.Persistable;

import com.google.gwt.user.client.ui.Frame;

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