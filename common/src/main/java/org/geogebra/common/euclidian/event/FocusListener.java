package org.geogebra.common.euclidian.event;

import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.main.App;

public class FocusListener {

	private Object listenerClass;

	protected void wrapFocusGained(GFocusEvent event) {
		if (listenerClass instanceof DrawInputBox.InputFieldListener) {
			((DrawInputBox.InputFieldListener) listenerClass)
					.focusGained(event);
		} else {
			App.debug("other type");
		}
	}

	protected void wrapFocusGained() {
		wrapFocusGained(null);
	}

	protected void wrapFocusLost(GFocusEvent event) {
		if (listenerClass instanceof DrawInputBox.InputFieldListener) {
			((DrawInputBox.InputFieldListener) listenerClass).focusLost(event);
		} else {
			App.debug("other type");
		}
	}

	protected void wrapFocusLost() {
		wrapFocusLost(null);
	}

	public Object getListenerClass() {
		return listenerClass;
	}

	public void setListenerClass(Object listenerClass) {
		this.listenerClass = listenerClass;
	}
}
