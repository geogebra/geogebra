package org.geogebra.web.keyboard;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.keyboard.web.ButtonHandler;
import org.geogebra.keyboard.web.HasKeyboard;
import org.geogebra.keyboard.web.KeyBoardButtonBase;
import org.geogebra.keyboard.web.KeyBoardButtonFunctionalBase;
import org.geogebra.keyboard.web.KeyboardListener;
import org.geogebra.keyboard.web.KeyboardListener.ArrowType;
import org.geogebra.keyboard.web.TabbedKeyboard;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.web.gui.util.VirtualKeyboardGUI;

import com.google.gwt.core.client.Scheduler;

public class OnscreenTabbedKeyboard extends TabbedKeyboard
		implements VirtualKeyboardGUI, ButtonHandler {

	private KeyboardListener processField;


	public OnscreenTabbedKeyboard(HasKeyboard app) {
		buildGUI(this, app);
		ClickStartHandler.init(this, new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				// just stop propagation

			}
		});
	}

	public void show() {
		checkLanguage();
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



	public void setStyleName() {
		// TODO Auto-generated method stub

	}

	public void endEditing() {
		// TODO Auto-generated method stub

	}

	public void setProcessing(KeyboardListener field) {
		this.processField = field;

	}



	@Override
	public void onClick(KeyBoardButtonBase btn, PointerEventType type) {
		ToolTipManagerW.hideAllToolTips();
		if (processField == null) {
			return;
		}
		if (btn instanceof KeyBoardButtonFunctionalBase
				&& ((KeyBoardButtonFunctionalBase) btn).getAction() != null) {
			KeyBoardButtonFunctionalBase button = (KeyBoardButtonFunctionalBase) btn;

			switch (button.getAction()) {
			case CAPS_LOCK:
				// removeAccents();
				processShift();
				break;
			case BACKSPACE_DELETE:
				processField.onBackSpace();
				break;
			case RETURN_ENTER:
				// make sure enter is processed correctly
				processField.onEnter();
				if (processField.resetAfterEnter()) {
					getUpdateKeyBoardListener().keyBoardNeeded(false, null);
				}
				break;
			case LEFT_CURSOR:
				processField.onArrow(ArrowType.left);
				break;
			case RIGHT_CURSOR:
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
			if (isAccent(text)) {
				processAccent(text);
			} else {
				processField.insertString(text); // TODO
				processAccent(null);
				disableCapsLock();
			}
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

	public void afterShown(final Runnable runnable) {
		runOnAnimation(runnable, getElement());
	}

	private native void runOnAnimation(Runnable runnable,
			com.google.gwt.dom.client.Element root) /*-{
		var callback = function() {
			root.className = root.className.replace(/animating/, "");
			runnable.@java.lang.Runnable::run()();
		};
		if ((root.style.animation || root.style.animation === "")
				&& root.className.match(/animating/)) {

			root.addEventListener("animationend", callback);
			return;
		}
		window.setTimeout(callback, 0);

	}-*/;

	public void prepareShow(boolean animated) {
		if (animated) {
			addStyleName("animating");
		}
		show();

	}

	public void remove(Runnable runnable) {
		app.updateCenterPanelAndViews();
		this.addStyleName("animatingOut");
		runOnAnimation(runnable, getElement());

	}

	public boolean hasTouchFeedback() {
		return true;
	}

}
