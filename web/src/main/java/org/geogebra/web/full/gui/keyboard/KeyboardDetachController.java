package org.geogebra.web.full.gui.keyboard;

import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.util.VirtualKeyboardGUI;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.RootPanel;

class KeyboardDetachController {

	private final boolean enabled;
	private final boolean hasCustomParent;
	private final String keyboardParentSelector;
	private final AppW app;
	private RootPanel keyboardRoot = null;

	public KeyboardDetachController(AppW app, boolean hasRootAsParent) {
		this.app = app;
		String detachKeyboardParent = app.getAppletParameters().getDetachKeyboardParent();
		this.hasCustomParent = !detachKeyboardParent.trim().isEmpty();
		this.keyboardParentSelector = hasCustomParent ? "#" + detachKeyboardParent : "";
		this.enabled = hasCustomParent || hasRootAsParent;
	}

	void addAsDetached(VirtualKeyboardGUI keyboard) {
		if (!enabled || hasKeyboardRoot()) {
			return;
		}

		createKeyboardRoot();
		keyboardRoot.add(keyboard);

		if (hasCustomParent()) {
			addRootToCustomParent();
		}
	}

	private void addRootToCustomParent() {
		Element parent = Dom.querySelector(
				keyboardParentSelector);
		if (parent != null) {
			parent.appendChild(keyboardRoot.getElement());
		} else {
			Log.error("No such keyboard parent in HTML: #" + keyboardParentSelector);
		}
	}

	private void createKeyboardRoot() {
		Element detachedKeyboardParent = DOM.createDiv();
		detachedKeyboardParent.setClassName("GeoGebraFrame");
		Element container = getAppletContainer();
		container.appendChild(detachedKeyboardParent);
		String keyboardParentId = app.getAppletId() + "keyboard";
		detachedKeyboardParent.setId(keyboardParentId);
		keyboardRoot = RootPanel.get(keyboardParentId);
	}

	private Element getAppletContainer() {
		Element scaler = app.getGeoGebraElement().getParentElement();
		Element container = scaler == null ? null : scaler.getParentElement();
		if (container == null) {
			return RootPanel.getBodyElement();
		}
		return container;
	}

	void removeFromDom() {
		if (keyboardRoot != null) {
			// both clear and remove to save memory
			keyboardRoot.removeFromParent();
			keyboardRoot.clear();
		}
	}

	boolean hasKeyboardRoot() {
		return keyboardRoot != null;
	}

	boolean hasCustomParent() {
		return hasCustomParent;
	}

	public boolean isEnabled() {
		return enabled;
	}
}