package org.geogebra.web.full.gui.keyboard;

import org.geogebra.common.util.debug.Log;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.full.gui.util.VirtualKeyboardGUI;
import org.geogebra.web.html5.gui.util.Dom;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.RootPanel;

final class KeyboardDetachController {

	private final boolean enabled;
	private final boolean hasCustomParent;
	private final String keyboardParentSelector;
	private final Element scaler;
	private RootPanel keyboardRoot = null;
	private final String keyboardRootId;
	private Element customParent;

	KeyboardDetachController(String appletId, String keyboardParentSelector,
			Element scaler, boolean hasRootAsParent) {
		keyboardRootId = appletId + "keyboard";
		this.hasCustomParent = !"".equals(keyboardParentSelector);
		this.keyboardParentSelector = keyboardParentSelector;
		this.enabled = hasCustomParent || hasRootAsParent;
		this.scaler = scaler;
	}

	void addAsDetached(VirtualKeyboardGUI keyboard) {
		if (!enabled || isKeyboardRootExists()) {
			return;
		}

		if (hasCustomParent) {
			createCustomKeyboardRoot();
		} else {
			createAppletKeyboardRoot();
		}
		keyboardRoot.add(keyboard);
	}

	private void createCustomKeyboardRoot() {
		customParent = Dom.querySelector(
				keyboardParentSelector);
		Element detachedKeyboardParent = DOM.createDiv();
		detachedKeyboardParent.setClassName("GeoGebraFrame");
		if (customParent != null) {
			detachedKeyboardParent.setId(keyboardRootId);
			customParent.appendChild(detachedKeyboardParent);
			keyboardRoot = RootPanel.get(keyboardRootId);
		} else {
			Log.error("No such keyboard parent in HTML: #" + keyboardParentSelector);
		}
	}

	private void createAppletKeyboardRoot() {
		Element detachedKeyboardParent = DOM.createDiv();
		detachedKeyboardParent.setClassName("GeoGebraFrame");
		Element container = getAppletContainer();
		container.appendChild(detachedKeyboardParent);
		detachedKeyboardParent.setId(keyboardRootId);
		keyboardRoot = RootPanel.get(keyboardRootId);
	}

	private Element getAppletContainer() {
		Element container = scaler == null ? null : scaler.getParentElement();
		if (container == null) {
			return RootPanel.getBodyElement();
		}
		return container;
	}

	void removeKeyboardRootFromDom() {
		if (keyboardRoot != null) {
			// both clear and remove to save memory
			keyboardRoot.removeFromParent();
			keyboardRoot.clear();
		}
	}

	boolean isKeyboardRootExists() {
		return keyboardRoot != null;
	}

	boolean hasCustomParent() {
		return customParent != null;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public int getParentWidth() {
		return hasCustomParent()
				? customParent.getClientWidth()
				: NavigatorUtil.getWindowWidth();
	}
}