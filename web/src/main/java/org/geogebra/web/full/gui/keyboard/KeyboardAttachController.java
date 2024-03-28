package org.geogebra.web.full.gui.keyboard;

import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.util.VirtualKeyboardGUI;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.RequiresResize;
import org.gwtproject.user.client.ui.RootPanel;

class KeyboardAttachController {

	private final boolean hasCustomParent;
	private final boolean detach;
	private final String keyboardParentSelector;
	private final AppW app;
	private RootPanel keyboardRoot = null;

	private enum DetachMode {
		NO_DETACH,
		ROOT,
		CUSTOM_PARENT;

	}

	private final DetachMode detachMode;

	public KeyboardAttachController(AppW app, boolean detach) {
		this.app = app;
		String detachKeyboardParent = app.getAppletParameters().getDetachKeyboardParent();
		this.hasCustomParent = !detachKeyboardParent.trim().isEmpty();
		this.keyboardParentSelector = hasCustomParent ? "#" + detachKeyboardParent : "";
		this.detach = detach;
		detachMode = calculateDetachMode();
	}

	private DetachMode calculateDetachMode() {
		if (hasCustomParent) {
			return DetachMode.CUSTOM_PARENT;
		}
		if (!detach) {
			return DetachMode.NO_DETACH;
		}
		return DetachMode.ROOT;
	}

	void addAsDetached(VirtualKeyboardGUI keyboard, RequiresResize requiresResize) {
		if (!isInFrame()) {
			createKeyboardRoot(keyboard, requiresResize);
			if (hasCustomParent()) {
				addToParent();
			}
		}
	}

	private void addToParent() {
		Element parent = Dom.querySelector(
				keyboardParentSelector);
		if (parent != null) {
			parent.appendChild(keyboardRoot.getElement());
		} else {
			Log.error("No such keyboard parent in HTML: #" + keyboardParentSelector);
		}
	}

	private void createKeyboardRoot(VirtualKeyboardGUI keyboard,
			RequiresResize requiresResize) {
		if (keyboardRoot != null) {
			return;
		}
		Element detachedKeyboardParent = DOM.createDiv();
		detachedKeyboardParent.setClassName("GeoGebraFrame");
		Element container = getAppletContainer();
		container.appendChild(detachedKeyboardParent);
		String keyboardParentId = app.getAppletId() + "keyboard";
		detachedKeyboardParent.setId(keyboardParentId);
		app.addWindowResizeListener(requiresResize);
		keyboardRoot = RootPanel.get(keyboardParentId);
		keyboardRoot.add(keyboard);
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
		return detachMode == DetachMode.CUSTOM_PARENT;
	}

	boolean isInFrame() {
		return detachMode == DetachMode.NO_DETACH;
	}
}
