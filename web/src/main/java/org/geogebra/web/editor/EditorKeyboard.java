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

	public void setProcessing(KeyboardListener listener) {
		tabbedKeyboard.setProcessing(listener);
	}

	public TabbedKeyboard getTabbedKeyboard() {
		return tabbedKeyboard;
	}

	public void setListener(KeyboardCloseListener listener) {
		tabbedKeyboard.setListener(listener);
	}
}
