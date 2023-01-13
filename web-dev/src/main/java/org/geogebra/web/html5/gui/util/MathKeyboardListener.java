package org.geogebra.web.html5.gui.util;

import com.google.gwt.user.client.ui.Widget;

public interface MathKeyboardListener {

	/**
	 * @param focus
	 *            set focus true or false
	 */
	void setFocus(boolean focus);

	void ensureEditing();

	Widget asWidget();

	String getText();

	boolean needsAutofocus();

	boolean hasFocus();

	boolean acceptsCommandInserts();
}
