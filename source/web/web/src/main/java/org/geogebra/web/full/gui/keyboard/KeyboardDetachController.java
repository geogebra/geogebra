/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
		if (!enabled || keyboardRoot != null) {
			return;
		}

		if (hasCustomParent) {
			updateCustomParent();
		}
		createAppletKeyboardRoot(customParent == null ? getAppletContainer() : customParent);
		keyboardRoot.add(keyboard);
	}

	private void updateCustomParent() {
		customParent = Dom.querySelector(keyboardParentSelector);
		if (customParent == null) {
			Log.error("No such keyboard parent in HTML: " + keyboardParentSelector);
		}
	}

	private void createAppletKeyboardRoot(Element container) {
		Element detachedKeyboardParent = DOM.createDiv();
		detachedKeyboardParent.setClassName("GeoGebraFrame");
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

	boolean removeKeyboardRootFromDom() {
		if (keyboardRoot != null) {
			// both clear and remove to save memory
			keyboardRoot.removeFromParent();
			keyboardRoot.clear();
			return true;
		}
		return false;
	}

	boolean hasCustomParent() {
		return hasCustomParent;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public int getParentWidth() {
		return customParent != null
				? customParent.getClientWidth()
				: NavigatorUtil.getWindowWidth();
	}
}