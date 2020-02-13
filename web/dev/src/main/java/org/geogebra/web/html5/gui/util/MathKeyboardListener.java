package org.geogebra.web.html5.gui.util;

import com.google.gwt.user.client.ui.UIObject;

public interface MathKeyboardListener {

	void setFocus(boolean focus);

	void ensureEditing();

	UIObject asWidget();

	String getText();

	boolean needsAutofocus();

	boolean hasFocus();
}
