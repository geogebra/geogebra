package org.geogebra.web.html5.gui.util;

import org.gwtproject.user.client.ui.UIObject;

public interface MathKeyboardListener {

	/**
	 * @param focus
	 *            set focus true or false
	 */
	void setFocus(boolean focus);

	void ensureEditing();

	UIObject asWidget();

	String getText();

	boolean needsAutofocus();

	boolean hasFocus();

	boolean acceptsCommandInserts();
}
