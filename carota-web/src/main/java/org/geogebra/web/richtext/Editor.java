package org.geogebra.web.richtext;

import com.google.gwt.user.client.ui.Widget;

/** The interface to the Carota editor */
public interface Editor {

	/**
	 * Return the GWT widget that represents the editor.
	 *
	 * @return a GWT widget
	 */
	Widget getWidget();

	/**
	 * Focuses the editor.
	 */
	void focus();
}
