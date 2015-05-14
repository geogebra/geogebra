package org.geogebra.web.html5.gui.view.algebra;

import com.google.gwt.user.client.ui.UIObject;

public interface MathKeyboardListener {
	public void setFocus(boolean focus);

	public void ensureEditing();

	public UIObject asWidget();

}
