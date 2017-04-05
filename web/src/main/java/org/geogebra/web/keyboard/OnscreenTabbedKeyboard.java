package org.geogebra.web.keyboard;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.keyboard.web.ButtonHandler;
import org.geogebra.keyboard.web.HasKeyboard;
import org.geogebra.keyboard.web.KeyBoardButtonBase;
import org.geogebra.keyboard.web.KeyBoardButtonFunctionalBase;
import org.geogebra.keyboard.web.KeyboardListener;
import org.geogebra.keyboard.web.KeyboardListener.ArrowType;
import org.geogebra.keyboard.web.TabbedKeyboard;
import org.geogebra.keyboard.web.UpdateKeyBoardListener;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.web.gui.util.VirtualKeyboardGUI;

import com.google.gwt.core.client.Scheduler;

public class OnscreenTabbedKeyboard extends TabbedKeyboard
		implements VirtualKeyboardGUI, ButtonHandler {

	private KeyboardListener processField;
	private UpdateKeyBoardListener updateKeyBoardListener;

	public OnscreenTabbedKeyboard(HasKeyboard app) {
		buildGUI(this, app.getLocalization());
	}

	public void show() {
		setVisible(true);

	}

	public void resetKeyboardState() {
		// TODO Auto-generated method stub

	}

	public boolean shouldBeShown() {
		// TODO Auto-generated method stub
		return true;
	}

	public void showOnFocus() {
		// TODO Auto-generated method stub

	}

	public void updateSize() {
		// TODO Auto-generated method stub

	}

	public void setStyleName() {
		// TODO Auto-generated method stub

	}

	public void endEditing() {
		// TODO Auto-generated method stub

	}

	public void setProcessing(KeyboardListener field) {
		this.processField = field;

	}

	public void setListener(UpdateKeyBoardListener listener) {
		this.updateKeyBoardListener = listener;

	}

	@Override
	public void onClick(KeyBoardButtonBase btn, PointerEventType type) {
		ToolTipManagerW.hideAllToolTips();
		if (processField == null) {
			return;
		}
		if (btn instanceof KeyBoardButtonFunctionalBase) {
			KeyBoardButtonFunctionalBase button = (KeyBoardButtonFunctionalBase) btn;

			switch (button.getAction()) {
			case SHIFT:
				// removeAccents();
				// processShift();
				break;
			case BACKSPACE:
				processField.onBackSpace();
				break;
			case ENTER:
				// make sure enter is processed correctly
				processField.onEnter();
				if (processField.resetAfterEnter()) {
					updateKeyBoardListener.keyBoardNeeded(false, null);
				}
				break;
			case ARROW_LEFT:
				processField.onArrow(ArrowType.left);
				break;
			case ARROW_RIGHT:
				processField.onArrow(ArrowType.right);
				break;
			case SWITCH_KEYBOARD:
				// String caption = button.getCaption();
				// if (caption.equals(GREEK)) {
				// setToGreekLetters();
				// } else if (caption.equals(NUMBER)) {
				// setKeyboardMode(KeyboardMode.NUMBER);
				// } else if (caption.equals(TEXT)) {
				// if (greekActive) {
				// greekActive = false;
				// switchABCGreek.setCaption(GREEK);
				// updateKeys("lowerCase", this.keyboardLocale);
				// setStyleName();
				// }
				// if (shiftIsDown) {
				// processShift();
				// }
				// if (accentDown) {
				// removeAccents();
				// }
				// setKeyboardMode(KeyboardMode.TEXT);
				// } else if (caption.equals(SPECIAL_CHARS)) {
				// setKeyboardMode(KeyboardMode.SPECIAL_CHARS);
				// } else if (caption.equals(PAGE_ONE_OF_TWO)) {
				// showSecondPage();
				// } else if (caption.equals(PAGE_TWO_OF_TWO)) {
				// showFirstPage();
				// }
			}
		} else {

			String text = btn.getFeedback();
			processField.insertString(text); // TODO
			// if (isAccent(text)) {
			// processAccent(text, btn);
			// } else {
			// processField.insertString(text);
			// if (accentDown) {
			// removeAccents();
			// }
			// }
			//
			// if (shiftIsDown && !isAccent(text)) {
			// processShift();
			// }

			processField.setFocus(true);
		}

		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				Scheduler.get()
						.scheduleDeferred(new Scheduler.ScheduledCommand() {
							@Override
							public void execute() {
								processField.scrollCursorIntoView();
							}
						});
			}
		});
	}

}
