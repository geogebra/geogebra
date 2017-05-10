package org.geogebra.web.keyboard;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.keyboard.web.ButtonHandler;
import org.geogebra.keyboard.web.HasKeyboard;
import org.geogebra.keyboard.web.KeyBoardButtonBase;
import org.geogebra.keyboard.web.KeyBoardButtonFunctionalBase;
import org.geogebra.keyboard.web.KeyboardListener;
import org.geogebra.keyboard.web.KeyboardListener.ArrowType;
import org.geogebra.keyboard.web.TabbedKeyboard;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.inputbar.InputBarHelpPanelW;
import org.geogebra.web.web.gui.inputbar.InputBarHelpPopup;
import org.geogebra.web.web.gui.util.VirtualKeyboardGUI;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;

/**
 * Web implementation of onscreen keyboard
 * 
 * @author Zbynek, based on Balaz's cross-platform model
 */
public class OnscreenTabbedKeyboard extends TabbedKeyboard
		implements VirtualKeyboardGUI, ButtonHandler {

	private KeyboardListener processField;
	private InputBarHelpPopup helpPopup=null;


	/**
	 * @param app
	 *            keyboard context
	 */
	public OnscreenTabbedKeyboard(HasKeyboard app) {
		buildGUI(this, app);
		ClickStartHandler.init(this, new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				// just stop propagation

			}
		});
	}
	
	private void createHelpPopup() {
		if (helpPopup != null) {
			return;
		}
		helpPopup = new InputBarHelpPopup((AppW)app, null,
				"helpPopupAV");
		helpPopup.addAutoHidePartner(this.getElement());
		helpPopup.addCloseHandler(new CloseHandler<GPopupPanel>() {

			@Override
			public void onClose(CloseEvent<GPopupPanel> event) {
				// TODO handle closing?
			}

		});
	}
	
	public void show() {
		this.keyboardWanted = true;
		checkLanguage();
		setVisible(true);
	}

	public void resetKeyboardState() {
		// TODO Auto-generated method stub

	}

	public void setStyleName() {
		// TODO Auto-generated method stub

	}

	public void endEditing() {
		if (processField != null) {
			processField.endEditing();
		}
	}

	public void setProcessing(KeyboardListener field) {
		if (processField != null && processField.getField() != null) {
			if (field == null || processField.getField() != field.getField()) {
				endEditing();
			}
		}
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
			case SWITCH_TO_SPECIAL_SYMBOLS:
				selectSpecial();
				break;
			case SWITCH_TO_ABC:
				selectAbc();
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
				processField
						.insertString(app.getLocalization().getCommand(text)); // TODO
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
								scrollCursorIntoView();
							}
						});
			}
		});
	}

	/**
	 * Scroll cursor of selected textfield into view
	 */
	protected void scrollCursorIntoView() {
		processField.scrollCursorIntoView();
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

	public void remove(final Runnable runnable) {
		app.updateCenterPanelAndViews();
		this.addStyleName("animatingOut");
		runOnAnimation(new Runnable() {
			public void run() {
				removeFromParent();
				runnable.run();
			}
		}, getElement());

	}

	public boolean hasTouchFeedback() {
		return true;
	}

	@Override
	protected void showHelp(int x, int y) {
		boolean show = helpPopup != null && helpPopup.isShowing();
		if (!show) {
			createHelpPopup();
			GuiManagerInterfaceW gm = ((AppW)app).getGuiManager();
			InputBarHelpPanelW helpPanel = (InputBarHelpPanelW)(gm.getInputHelpPanel());
			updateHelpPosition(helpPanel, x, y);
			
		} else if (helpPopup != null) {
			helpPopup.hide();
		}
	}
	
	private void updateHelpPosition(final InputBarHelpPanelW helpPanel,
			final int x, final int y) {
		helpPopup.setPopupPositionAndShow(new GPopupPanel.PositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				doUpdateHelpPosition(helpPanel, x, y, offsetWidth,
						offsetHeight);
			}
		});

	}

	/**
	 * @param helpPanel
	 *            help panel
	 * @param x
	 *            popup x-coord
	 * @param y
	 *            popup y-coord
	 * @param offsetWidth
	 *            panel width
	 * @param offsetHeight
	 *            panel height
	 */
	protected void doUpdateHelpPosition(final InputBarHelpPanelW helpPanel,
			final int x, final int y, int offsetWidth, int offsetHeight) {
		AppW appw = (AppW) app;
		double scale = appw.getArticleElement().getScaleX();
		double renderScale = appw.getArticleElement().getDataParamApp() ? scale
				: 1;
		double left = x - appw.getAbsLeft()
				- helpPanel.getPreferredWidth(scale);

		helpPopup.getElement().getStyle().setProperty("left",
				left * renderScale + "px");
		int maxOffsetHeight;
		int totalHeight = (int) appw.getHeight();
		int toggleButtonTop = (int) ((y - (int) appw.getAbsTop()) / scale);
		if (toggleButtonTop < totalHeight / 2) {
			int top = (toggleButtonTop);
			maxOffsetHeight = totalHeight - top;
			helpPopup.getElement().getStyle().setProperty("top",
					top * renderScale + "px");
			helpPopup.getElement().getStyle().setProperty("bottom", "auto");
			helpPopup.removeStyleName("helpPopupAVBottom");
			helpPopup.addStyleName("helpPopupAV");
		} else {
			int minBottom = appw.isApplet() ? 0 : 10;
			int bottom = (totalHeight - toggleButtonTop);
			maxOffsetHeight = bottom > 0 ? totalHeight - bottom
					: totalHeight - minBottom;
			helpPopup.getElement().getStyle().setProperty("bottom",
					(bottom > 0 ? bottom : minBottom) * renderScale + "px");
			helpPopup.getElement().getStyle().setProperty("top", "auto");
			helpPopup.removeStyleName("helpPopupAV");
			helpPopup.addStyleName("helpPopupAVBottom");
		}
		helpPanel.updateGUI(maxOffsetHeight, 1);
		helpPopup.show();

	}
}
