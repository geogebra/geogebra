package org.geogebra.web.html5.gui.util;

import org.gwtproject.user.client.ui.Widget;

public interface MathKeyboardListener {

	/**
	 * @param focus
	 *            set focus true or false
	 */
	void setFocus(boolean focus);

	void ensureEditing();

	Widget asWidget();

	boolean needsAutofocus();

	boolean hasFocus();

	boolean acceptsCommandInserts();
}
