package org.geogebra.keyboard.web;

import java.util.ArrayList;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.util.lang.Language;
import org.geogebra.common.util.lang.Unicode;
import org.geogebra.keyboard.base.Action;
import org.geogebra.keyboard.base.ButtonConstants;
import org.geogebra.keyboard.base.Keyboard;
import org.geogebra.keyboard.base.KeyboardFactory;
import org.geogebra.keyboard.base.Resource;
import org.geogebra.keyboard.base.listener.KeyboardObserver;
import org.geogebra.keyboard.base.model.Row;
import org.geogebra.keyboard.base.model.WeightedButton;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.KeyboardLocale;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class TabbedKeyboard extends FlowPanel {

	private KeyboardLocale locale;
	private boolean isSmallKeyboard;
	protected HasKeyboard app;
	private ArrayList<Keyboard> layouts = new ArrayList<Keyboard>(4);
	private Object keyboardLocale;
	private ButtonHandler bh;
	private UpdateKeyBoardListener updateKeyBoardListener;

	public TabbedKeyboard() {

	}

	public UpdateKeyBoardListener getUpdateKeyBoardListener() {
		return updateKeyBoardListener;
	}

	public void setListener(UpdateKeyBoardListener listener) {
		this.updateKeyBoardListener = listener;
	}

	public void buildGUI(ButtonHandler bh, HasKeyboard app) {
		KeyboardFactory kbf = new KeyboardFactory();
		FlowPanel tabs = new FlowPanel();
		FlowPanel switcher = new FlowPanel();
		switcher.addStyleName("KeyboardSwitcher");
		this.app = app;
		this.bh = bh;
		this.locale = app.getLocalization();
		this.keyboardLocale = locale.getLocaleStr();
		KeyPanelBase keyboard = buildPanel(kbf.createMathKeyboard(), bh);
		tabs.add(keyboard);
		switcher.add(makeSwitcherButton(keyboard, "123"));

		keyboard = buildPanel(kbf.createFunctionsKeyboard(), bh);
		tabs.add(keyboard);
		keyboard.setVisible(false);
		switcher.add(makeSwitcherButton(keyboard, "fx"));

		keyboard = buildPanel(kbf.createGreekKeyboard(), bh);
		tabs.add(keyboard);
		keyboard.setVisible(false);
		switcher.add(makeSwitcherButton(keyboard, Unicode.alphaBetaGamma));



		keyboard = buildPanel(
				kbf.createLettersKeyboard(filter(locale.getKeyboardRow(1).replace("'", "")),
						filter(locale.getKeyboardRow(2)),
						filter(locale.getKeyboardRow(3))),
				bh);
		tabs.add(keyboard);
		keyboard.setVisible(false);
		switcher.add(makeSwitcherButton(keyboard, "ABC"));

		switcher.add(makeCloseButton());
		add(switcher);
		add(tabs);
		addStyleName("KeyBoard");
		addStyleName("TabbedKeyBoard");
		addStyleName("gwt-PopupPanel");
	}

	private Widget makeCloseButton() {
		Button closeButton = new Button("x");
		closeButton.addStyleName("closeTabbedKeyboardButton");
		ClickStartHandler.init(closeButton, new ClickStartHandler() {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				updateKeyBoardListener.keyBoardNeeded(false, null);

			}
		});
		return closeButton;
	}

	private String filter(String keys) {
		StringBuilder sb = new StringBuilder(11);
		for (int i = 0; i < keys.length(); i += 2) {
			sb.append(keys.charAt(i));
		}
		// TODO remove the replace once ggbtrans is fixed
		return sb.toString().replace("'", "");
	}

	private Widget makeSwitcherButton(final KeyPanelBase keyboard,
			String string) {
		Button ret = new Button(string);
		ClickStartHandler.init(ret, new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				for (int i = 0; i < ((FlowPanel) keyboard.getParent())
						.getWidgetCount(); i++) {
					((FlowPanel) keyboard.getParent()).getWidget(i)
							.setVisible(false);
				}
				keyboard.setVisible(true);

			}
		});
		return ret;
	}

	private KeyPanelBase buildPanel(Keyboard layout, final ButtonHandler bh) {
		final KeyPanelBase keyboard = new KeyPanelBase();
		layouts.add(layout);
		keyboard.addStyleName("KeyPanel");
		keyboard.addStyleName("normal");
		updatePanel(keyboard, layout, bh);
		layout.registerKeyboardObserver(new KeyboardObserver() {

			public void keyboardModelChanged(Keyboard l2) {
				updatePanel(keyboard, l2, bh);

			}
		});
		return keyboard;
	}

	private void updatePanel(KeyPanelBase keyboard, Keyboard layout,
			ButtonHandler bh) {
		keyboard.reset();
		int index = 0;
		for (Row row : layout.getModel().getRows()) {
			double offset = 0;
			for (WeightedButton wb : row.getButtons()) {
				if (Action.NONE.name().equals(wb.getActionName())) {
					offset = wb.getWeight();
				} else {
					KeyBoardButtonBase button = makeButton(wb, bh);
					if (offset > 0) {
						button.getElement().getStyle()
								.setMarginLeft(offset * 50, Unit.PX);
					}
					button.getElement().getStyle().setWidth(wb.getWeight() * 50,
							Unit.PX);
					keyboard.addToRow(index, button);
					offset = 0;
				}
			}
			index++;
		}

	}

	private KeyBoardButtonBase makeButton(WeightedButton wb, ButtonHandler b) {
		switch (wb.getResourceType()) {


		case TRANSLATION_MENU_KEY:
			return new KeyBoardButtonBase(locale.getMenu(wb.getActionName()),
					wb.getActionName().replace("Function.", ""), b);
		case TRANSLATION_COMMAND_KEY:
			return new KeyBoardButtonBase(locale.getCommand(wb.getActionName()),
					wb.getActionName(), b);
		case DEFINED_CONSTANT:
			return functionButton(wb, b);
		case TEXT:
		default:
			if (wb.getActionName().equals(Action.TOGGLE_ACCENT_ACUTE.name())) {
				return accentButton(ButtonConstants.ACCENT_ACUTE, b);
			}
			if (wb.getActionName().equals(Action.TOGGLE_ACCENT_CARON.name())) {
				return accentButton(ButtonConstants.ACCENT_CARON, b);
			}
			if (wb.getActionName()
					.equals(Action.TOGGLE_ACCENT_CIRCUMFLEX.name())) {
				return accentButton(ButtonConstants.ACCENT_CIRCUMFLEX, b);
			}
			if (wb.getActionName().equals(Action.TOGGLE_ACCENT_GRAVE.name())) {
				return accentButton(ButtonConstants.ACCENT_GRAVE, b);
			}
			if (wb.getActionName().equals("*")) {
				return new KeyBoardButtonBase(Unicode.MULTIPLY + "", "*", b);
			}
			if (wb.getActionName().equals("/")) {
				return new KeyBoardButtonBase(Unicode.DIVIDE, "*", b);
			}
			if (wb.getActionName().equals("|")) {
				return new KeyBoardButtonBase("|a|", "|", b);
			}
			if (wb.getActionName().equals("-")) {
				return new KeyBoardButtonBase(Unicode.MINUS + "", "-", b);
			}
			if (wb.getActionName().equals(Unicode.EULER_STRING)) {
				return new KeyBoardButtonBase("e", Unicode.EULER_STRING, b);
			}
			return new KeyBoardButtonBase(wb.getActionName(),
					wb.getActionName(), b);
		}

	}

	protected boolean isAccent(String txt) {
		return ButtonConstants.ACCENT_GRAVE.equals(txt)
				|| ButtonConstants.ACCENT_ACUTE.equals(txt)
				|| ButtonConstants.ACCENT_CIRCUMFLEX.equals(txt)
				|| ButtonConstants.ACCENT_CARON.equals(txt);
	}
	private KeyBoardButtonBase accentButton(String accent, ButtonHandler b) {
		return new KeyBoardButtonBase(accent, accent, b);
	}

	protected void processShift() {
		for (Keyboard layout : layouts) {
			layout.toggleCapsLock();
		}
	}

	protected void disableCapsLock() {
		for (Keyboard layout : layouts) {
			layout.disableCapsLock();
		}
	}

	protected void processAccent(String text) {
		for (Keyboard layout : layouts) {
			layout.toggleAccent(text);
		}

	}

	private KeyBoardButtonBase functionButton(WeightedButton button,
			ButtonHandler bh) {
		String resourceName = button.getResourceName();
		if (resourceName.equals(Resource.RETURN_ENTER.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.keyboard_enter(), bh,
					Action.RETURN_ENTER);
		} else if (resourceName.equals(Resource.BACKSPACE_DELETE.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.keyboard_backspace(), bh,
					Action.BACKSPACE_DELETE);
		} else if (resourceName.equals(Resource.LEFT_ARROW.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.keyboard_arrowLeft(), bh,
					Action.LEFT_CURSOR);
		} else if (resourceName.equals(Resource.RIGHT_ARROW.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.keyboard_arrowRight(), bh,
					Action.RIGHT_CURSOR);
		} else if (resourceName.equals(Resource.POWA2.name())) {
			return new KeyBoardButtonBase("a^2", "^2", bh);
		} else if (resourceName.equals(Resource.POWAB.name())) {
			return new KeyBoardButtonBase("a^b", "a^x", bh);
		}
		else if (resourceName.equals(Resource.CAPS_LOCK.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.keyboard_shift(), bh,
					Action.CAPS_LOCK);
		} else if (resourceName.equals(Resource.CAPS_LOCK_ENABLED.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.keyboard_shiftDown(), bh,
					Action.CAPS_LOCK);
		} else if (resourceName.equals(Resource.POW10_X.name())) {
			return new KeyBoardButtonBase("10^x", "10^", bh);
		} else if (resourceName.equals(Resource.POWE_X.name())) {
			return new KeyBoardButtonBase("e^x",
					Unicode.EULER_STRING + "^", bh);
		}
		else if (resourceName.equals(Resource.LOG_10.name())) {
			return new KeyBoardButtonBase("log_10", "log10", bh);
		}
		else if (resourceName.equals(Resource.LOG_B.name())) {
			return new KeyBoardButtonBase("log_b", "log_", bh);
		}
		else if (resourceName.equals(Resource.A_N.name())) {
			return new KeyBoardButtonBase("a_n", "_", bh);
		}
		else if (resourceName.equals(Resource.N_ROOT.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.nroot(), button.getActionName(),
					bh);
		}
		else if (resourceName.equals(Resource.INTEGRAL.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.integral(),
					button.getActionName(), bh);
		} else if (resourceName.equals(Resource.DERIVATIVE.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.derivative(),
					button.getActionName(), bh);
		}
		if (resourceName.equals(Resource.ROOT.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.sqrt(),
					button.getActionName(), bh);
		}

		return new KeyBoardButtonBase(button.getActionName(),
				button.getActionName(), bh);
	}

	/**
	 * 
	 */
	public void updateSize() {
		if (app.getInnerWidth() < 0) {
			return;
		}
		// -2 for applet border
		this.setWidth(app.getInnerWidth() + "px");
		boolean shouldBeSmall = app.needsSmallKeyboard();
		if (shouldBeSmall && !isSmallKeyboard) {
			this.addStyleName("lowerHeight");
			this.isSmallKeyboard = true;
		} else if (!shouldBeSmall && isSmallKeyboard) {
			this.removeStyleName("lowerHeight");
			this.isSmallKeyboard = false;
		}
		updateHeight();
	}

	private void updateHeight() {
		if (app != null) {
			app.updateKeyboardHeight();
		}
	}

	/**
	 * loads the translation-files for the active language if it is different
	 * from the last loaded language and sets the {@link #keyboardLocale} to the
	 * new language
	 */
	protected void checkLanguage() {
		if (bh == null) {
			return;
		}
		// TODO validate?
		String newKeyboardLocale = app.getLocalization().getLocaleStr();

		if (newKeyboardLocale != null
				&& keyboardLocale.equals(newKeyboardLocale)) {
			return;
		}
		if (newKeyboardLocale != null) {
			this.keyboardLocale = newKeyboardLocale;
		} else {
			this.keyboardLocale = Language.English_US.localeGWT;
		}

		clear();
		buildGUI(bh, app);

	}
}
