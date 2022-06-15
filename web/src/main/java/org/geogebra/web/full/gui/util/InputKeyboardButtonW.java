package org.geogebra.web.full.gui.util;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.inputfield.AutoCompleteTextField;
import org.geogebra.common.main.InputKeyboardButton;
import org.geogebra.keyboard.web.KeyboardResources;
import org.geogebra.keyboard.web.UpdateKeyBoardListener;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.BrowserStorage;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.util.Dom;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class InputKeyboardButtonW implements InputKeyboardButton, IsWidget {
	private final ToggleButton button;
	private AutoCompleteTextFieldW textField;
	private boolean enabled = true;

	/**
	 *
	 * @param app {@link AppWFull}
	 */
	public InputKeyboardButtonW(AppWFull app) {
		button = new ToggleButton(KeyboardResources.INSTANCE.keyboard_show_material());
		button.setStyleName("matKeyboardOpenBtn");
		UpdateKeyBoardListener listener = app.getAppletFrame();
		ClickStartHandler.init(button,
				new ClickStartHandler(true, true) {
					@Override
					public void onClickStart(int x, int y, PointerEventType type) {
						showKeyboard(listener);
					}
				});
	}

	private void showKeyboard(UpdateKeyBoardListener listener) {
		BrowserStorage.LOCAL.setItem(BrowserStorage.KEYBOARD_WANTED, "true");
		listener.doShowKeyBoard(true, textField);
		Scheduler.get().scheduleDeferred(textField::requestFocus);
	}

	@Override
	public void show() {
		if (!enabled) {
			return;
		}

		Dom.toggleClass(textField, "kbdInput", true);
	}

	@Override
	public void hide() {
		Dom.toggleClass(textField, "kbdInput", false);
	}

	@Override
	public void setTextField(AutoCompleteTextField autoCompleteTextField) {
		this.textField = (AutoCompleteTextFieldW) autoCompleteTextField;
		textField.addFocusHandler(event -> show());
		textField.addBlurHandler(event -> hide());
		textField.addContent(this);
	}

	@Override
	public void detach() {
		hide();
		textField.removeContent(button);
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if (!enabled) {
			hide();
		}
	}

	@Override
	public Widget asWidget() {
		return button;
	}
}
