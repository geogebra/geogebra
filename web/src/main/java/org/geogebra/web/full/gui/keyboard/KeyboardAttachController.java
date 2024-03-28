package org.geogebra.web.full.gui.keyboard;

import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.util.VirtualKeyboardGUI;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.Panel;
import org.gwtproject.user.client.ui.RequiresResize;
import org.gwtproject.user.client.ui.RootPanel;

class KeyboardAttachController {

	private final boolean hasCustomParent;
	private final boolean detach;
	private final String keyboardParentSelector;
	private final AppW app;
	private RootPanel keyboardRoot = null;

	private enum AttachMode {
		FRAME,
		ROOT,
		CUSTOM_PARENT;

	}

	private final AttachMode attachMode;

	public KeyboardAttachController(AppW app, boolean detach) {
		this.app = app;
		String detachKeyboardParent = app.getAppletParameters().getDetachKeyboardParent();
		this.hasCustomParent = !detachKeyboardParent.trim().isEmpty();
		this.keyboardParentSelector = hasCustomParent ? "#" + detachKeyboardParent : "";
		this.detach = detach;
		attachMode = calculateAttachMode();
	}

	private AttachMode calculateAttachMode() {
		if (hasCustomParent) {
			return AttachMode.CUSTOM_PARENT;
		}
		if (!detach) {
			return AttachMode.FRAME;
		}
		return AttachMode.ROOT;
	}

	void attach(Panel appFrame, VirtualKeyboardGUI keyboard, RequiresResize requiresResize) {
		switch (attachMode) {
		case FRAME:
			appFrame.add(keyboard);
			break;
		case ROOT:
			createKeyboardRoot(requiresResize);
			keyboardRoot.add(keyboard);
			break;
		case CUSTOM_PARENT:
			createKeyboardRoot(requiresResize);
			attachKeyboardToDetachedParent(keyboard);
			break;
		}
	}

	private void attachKeyboardToDetachedParent(VirtualKeyboardGUI keyboard) {
		Element parent = Dom.querySelector(
				keyboardParentSelector);
		if (parent != null) {
			keyboardRoot.add(keyboard);
			parent.appendChild(keyboardRoot.getElement());
		} else {
			Log.error("No such keyboard parent in HTML: #" + keyboardParentSelector);
		}
	}

	private void createKeyboardRoot(RequiresResize requiresResize) {
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
		return attachMode == AttachMode.CUSTOM_PARENT;
	}

	boolean isInFrame() {
		return attachMode == AttachMode.FRAME;
	}
}
