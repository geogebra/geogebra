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

package org.geogebra.web.editor;

import org.geogebra.keyboard.web.KeyboardCloseListener;
import org.geogebra.keyboard.web.KeyboardListener;
import org.geogebra.keyboard.web.TabbedKeyboard;
import org.geogebra.web.html5.bridge.AttributeProvider;
import org.geogebra.web.resources.StyleInjector;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.RootPanel;

import elemental2.dom.DomGlobal;

public class EditorKeyboard {

	private TabbedKeyboard tabbedKeyboard;

	void create(AttributeProvider element) {
		if (tabbedKeyboard != null) {
			return;
		}

		EditorKeyboardContext editorKeyboardContext = new EditorKeyboardContext(element);
		tabbedKeyboard = new TabbedKeyboard(editorKeyboardContext, false);
		tabbedKeyboard.addStyleName("detached");
		FlowPanel keyboardWrapper = new FlowPanel();
		keyboardWrapper.setStyleName("GeoGebraFrame");
		RootPanel.get().add(keyboardWrapper);
		tabbedKeyboard.clearAndUpdate();
		DomGlobal.window.addEventListener("resize", evt -> tabbedKeyboard.onResize());
		StyleInjector.onStylesLoaded(() -> {
			keyboardWrapper.add(tabbedKeyboard);
			tabbedKeyboard.show();
		});
	}

	/**
	 * Sets keyboard processing component.
	 * @param processingComponent keyboard processing component
	 */
	public void setProcessing(KeyboardListener processingComponent) {
		tabbedKeyboard.setProcessing(processingComponent);
	}

	public TabbedKeyboard getTabbedKeyboard() {
		return tabbedKeyboard;
	}

	/**
	 * @param listener keyboard close listener
	 */
	public void setListener(KeyboardCloseListener listener) {
		tabbedKeyboard.setListener(listener);
	}
}
