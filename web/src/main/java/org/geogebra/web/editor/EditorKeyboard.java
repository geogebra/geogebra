package org.geogebra.web.editor;

import org.geogebra.keyboard.web.KeyboardListener;
import org.geogebra.keyboard.web.TabbedKeyboard;
import org.geogebra.keyboard.web.UpdateKeyBoardListener;
import org.geogebra.web.resources.StyleInjector;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.RootPanel;

import elemental2.dom.DomGlobal;

public class EditorKeyboard {

	private TabbedKeyboard tabbedKeyboard;

	void create(Element element) {
		if (tabbedKeyboard != null) {
			return;
		}

		EditorKeyboardContext editorKeyboardContext = new EditorKeyboardContext(element);
		tabbedKeyboard = new TabbedKeyboard(editorKeyboardContext, false);
		tabbedKeyboard.addStyleName("detached");
		FlowPanel keyboardWrapper = new FlowPanel();
		keyboardWrapper.setStyleName("GeoGebraFrame");
		keyboardWrapper.add(tabbedKeyboard);
		RootPanel.get().add(keyboardWrapper);
		tabbedKeyboard.clearAndUpdate();
		DomGlobal.window.addEventListener("resize", evt -> onResize());
		StyleInjector.onStylesLoaded(tabbedKeyboard::show);
	}

	private void onResize() {
		tabbedKeyboard.onResize();
	}

	public void setProcessing(KeyboardListener listener) {
		tabbedKeyboard.setProcessing(listener);
	}

	public TabbedKeyboard getTabbedKeyboard() {
		return tabbedKeyboard;
	}

	public void setListener(UpdateKeyBoardListener listener) {
		tabbedKeyboard.setListener(listener);
	}
}
