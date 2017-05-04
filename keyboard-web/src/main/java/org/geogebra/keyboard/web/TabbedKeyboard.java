package org.geogebra.keyboard.web;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.util.lang.Language;
import org.geogebra.common.util.lang.Unicode;
import org.geogebra.keyboard.base.Action;
import org.geogebra.keyboard.base.Accents;
import org.geogebra.keyboard.base.Keyboard;
import org.geogebra.keyboard.base.KeyboardFactory;
import org.geogebra.keyboard.base.Resource;
import org.geogebra.keyboard.base.listener.KeyboardObserver;
import org.geogebra.keyboard.base.model.Row;
import org.geogebra.keyboard.base.model.WeightedButton;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.KeyboardLocale;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CustomButton;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class TabbedKeyboard extends FlowPanel {
	private static final int TAB_NUMBERS = 0;
	private static final int TAB_FX = 1;
	private static final int TAB_ABC = 2;
	private static final int TAB_ALPHA = 3;
	
	private class KeyboardSwitcher extends FlowPanel {
		private static final int SWITCHER_HEIGHT = 40;
		private FlowPanel contents;
		private List<Button> switches;
		private CustomButton closeButton;
		private ToggleButton moreButton;
		private Integer width = null;
		private int selectedIdx;
		private Button selectedButton;
		public KeyboardSwitcher() {
			addStyleName("KeyboardSwitcher");
			add(makeCloseButton());
			contents = new FlowPanel();
			contents.addStyleName("switcherContents");
			add(contents);
			switches = new ArrayList<>();
		}

		public void addMoreButton() {
			contents.add(makeMoreButton());
		
		}
		public void addSwitch(final KeyPanelBase keyboard,
				String string) {
			Button btn = makeSwitcherButton(keyboard, string);
			switches.add(btn);
			contents.add(btn);
		}
		
		private Button makeSwitcherButton(final KeyPanelBase keyboard,
				String string) {
			final Button ret = new Button(string);
			ClickStartHandler.init(ret, new ClickStartHandler(true, true) {

				@Override
				public void onClickStart(int x, int y, PointerEventType type) {
					for (int i = 0; i < switches.size(); i++) {
						((FlowPanel) keyboard.getParent()).getWidget(i)
								.setVisible(false);
						setSelected(i, false);
					}
					currentKeyboard = keyboard;
					keyboard.setVisible(true);
					setSelected(ret, true);
				}
			});
			return ret;
		}
		
		private void setSelected(Button btn, boolean value) {
			if (value) {
				btn.addStyleName("selected");
				selectedButton = btn;
			} else {
				btn.removeStyleName("selected");
			}
		}

		private void setSelected(int idx, boolean value) {
			setSelected(switches.get(idx), value);
			if (value) {
				selectedIdx = idx;
			}
		}
		
		private void unselectAll() {
			for (Widget btn: switches) {
				btn.removeStyleName("selected");
			}
		}
		
		private Widget makeCloseButton() {
			Image img = new Image(KeyboardResources.INSTANCE
					.keyboard_close_black().getSafeUri().asString());
			Image hoverImg = new Image(KeyboardResources.INSTANCE
					.keyboard_close_purple().getSafeUri().asString());
			closeButton = new CustomButton(){};
			closeButton.getUpFace().setImage(img);
			closeButton.getUpHoveringFace().setImage(hoverImg);
			closeButton.addStyleName("closeTabbedKeyboardButton");
			ClickStartHandler.init(closeButton, new ClickStartHandler() {

				@Override
				public void onClickStart(int x, int y, PointerEventType type) {
					updateKeyBoardListener.keyBoardNeeded(false, null);

				}
				
			});
			
			return closeButton;
		}

		private Widget makeMoreButton() {
			Image img = new Image(KeyboardResources.INSTANCE
					.keyboard_more().getSafeUri().asString());
			Image hoverImg = new Image(KeyboardResources.INSTANCE
					.keyboard_more_purple().getSafeUri().asString());
			moreButton = new ToggleButton(img, hoverImg);
			moreButton.getUpHoveringFace().setImage(hoverImg);
			moreButton.addStyleName("moreKeyboardButton");
			ClickStartHandler.init(moreButton, new ClickStartHandler() {

				@Override
				public void onClickStart(int x, int y, PointerEventType type) {
//					unselectAll();
					showHelp(moreButton.getAbsoluteLeft() + moreButton.getOffsetWidth(),
							moreButton.getAbsoluteTop());	
				}
			});
			
//			moreButton.addMouseOverHandler(new MouseOverHandler() {
//				
//				@Override
//				public void onMouseOver(MouseOverEvent event) {
//					unselectAll();
//				}
//			});
//			
//			moreButton.addMouseOutHandler(new MouseOutHandler() {
//				
//				@Override
//				public void onMouseOut(MouseOutEvent event) {
//					setSelected(selectedButton, true);
//				}
//			});
			return moreButton;
		}
		
		public void setWidth(int width) {
//			if (this.width != null) {
//				return;
//			}
//			this.width = width;
//			//	contents.getElement().getStyle().setProperty("height", getOffsetHeight(), Unit.PX);
//			contents.getElement().getStyle().setProperty("width", width, Unit.PX);
//	
		}

		public void reset() {
			if (moreButton != null) { 
				moreButton.setValue(false);
			}
		}

		public void select(int idx) {
			for (int i = 0; i < tabs.getWidgetCount();i++) {
				tabs.getWidget(i).setVisible(i == idx);
				setSelected(i, i == idx);
			}
		}

	}

	private static final int BASE_WIDTH = 70;
	private KeyboardLocale locale;
	private boolean isSmallKeyboard;
	protected HasKeyboard app;
	private ArrayList<Keyboard> layouts = new ArrayList<Keyboard>(4);
	private Object keyboardLocale;
	private ButtonHandler bh;
	private UpdateKeyBoardListener updateKeyBoardListener;
	private FlowPanel tabs;
	private KeyboardSwitcher switcher;
	protected KeyPanelBase currentKeyboard=null;

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
		this.tabs = new FlowPanel();
		switcher = new KeyboardSwitcher();
		this.app = app;
		this.bh = bh;
		this.locale = app.getLocalization();
		this.keyboardLocale = locale.getLocaleStr();


		KeyPanelBase keyboard = buildPanel(kbf.createMathKeyboard(), bh);
		tabs.add(keyboard);
		switcher.addSwitch(keyboard, "123");

		keyboard = buildPanel(kbf.createFunctionsKeyboard(), bh);
		tabs.add(keyboard);
		keyboard.setVisible(false);
		switcher.addSwitch(keyboard, "fx");

		keyboard = buildPanel(
				kbf.createLettersKeyboard(filter(locale.getKeyboardRow(1).replace("'", "")),
						filter(locale.getKeyboardRow(2)),
						filter(locale.getKeyboardRow(3))),
				bh);
		tabs.add(keyboard);
		keyboard.setVisible(false);
		switcher.addSwitch(keyboard, "ABC");
		keyboard = buildPanel(kbf.createGreekKeyboard(), bh);
		tabs.add(keyboard);
		keyboard.setVisible(false);
		switcher.addSwitch(keyboard, Unicode.alphaBetaGamma);
		switcher.setSelected(0, true);
		
		// add special char tab
		keyboard = buildPanel(kbf.createSpecialSymbolsKeyboard(), bh);
		keyboard.setVisible(false);
		tabs.add(keyboard);

		switcher.addMoreButton();

		add(switcher);
		add(tabs);
		addStyleName("KeyBoard");
		addStyleName("TabbedKeyBoard");
		addStyleName("gwt-PopupPanel");
		
	}



	private String filter(String keys) {
		StringBuilder sb = new StringBuilder(11);
		for (int i = 0; i < keys.length(); i += 2) {
			sb.append(keys.charAt(i));
		}
		// TODO remove the replace once ggbtrans is fixed
		return sb.toString().replace("'", "");
	}


	private KeyPanelBase buildPanel(Keyboard layout, final ButtonHandler bh) {
		final KeyPanelBase keyboard = new KeyPanelBase(layout);
		layouts.add(layout);
		keyboard.addStyleName("KeyPanel");
		keyboard.addStyleName("normal");
		updatePanel(keyboard, layout, bh, getBaseSize());
		layout.registerKeyboardObserver(new KeyboardObserver() {

			public void keyboardModelChanged(Keyboard l2) {
				updatePanel(keyboard, l2, bh, getBaseSize());

			}
		});
		return keyboard;
	}

	/**
	 * 
	 * @return button base size
	 */
	int getBaseSize() {
		final int n = 11;
		return (int) ((app.getInnerWidth() - 50) > BASE_WIDTH * n ? BASE_WIDTH
				: (app.getInnerWidth() - 50) / n);
	}

	void updatePanel(KeyPanelBase keyboard, Keyboard layout,
			ButtonHandler bh, int baseSize) {
		keyboard.reset(layout);
		int index = 0;
		for (Row row : layout.getModel().getRows()) {
			for (WeightedButton wb : row.getButtons()) {
				if (!Action.NONE.name().equals(wb.getActionName())) {
					KeyBoardButtonBase button = makeButton(wb, bh);
					keyboard.addToRow(index, button);
				}
			}
			index++;
		}
		updatePanelSize(keyboard, baseSize);
	}

	/**
	 * This is much faster than updatePanel as it doesn't clear the model. It
	 * assumes the model and button layout are in sync.
	 */
	private void updatePanelSize(KeyPanelBase keyboard, int baseSize) {
		int buttonIndex = 0;
		int margins = 4;
		if (keyboard.getLayout() == null) {
			return;
		}
		KeyBoardButtonBase button = null;
		for (Row row : keyboard.getLayout().getModel().getRows()) {
			double offset = 0;

			for (WeightedButton wb : row.getButtons()) {
				if (Action.NONE.name().equals(wb.getActionName())) {
					offset = wb.getWeight();
				} else {
					 button = keyboard.getButtons()
							.get(buttonIndex);
					
					if (offset > 0) {
						button.getElement().getStyle()
								.setMarginLeft(offset * baseSize + margins / 2, Unit.PX);
					}
	
					button.getElement().getStyle()
							.setWidth(wb.getWeight() * baseSize - margins, Unit.PX);
					offset = 0;
					buttonIndex++;
				}
			}
			if (Action.NONE.name().equals(row.getButtons()
					.get(row.getButtons().size() - 1).getActionName())) {
				button.getElement().getStyle()
						.setMarginRight(offset * baseSize + margins / 2,
								Unit.PX);
			}
		}
		if (app.getInnerWidth() < getMinWidthWithoutScaling()) {
			addStyleName("scale");
		} else {
			removeStyleName("scale");
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
				return accentButton(Accents.ACCENT_ACUTE, b);
			}
			if (wb.getActionName().equals(Action.TOGGLE_ACCENT_CARON.name())) {
				return accentButton(Accents.ACCENT_CARON, b);
			}
			if (wb.getActionName()
					.equals(Action.TOGGLE_ACCENT_CIRCUMFLEX.name())) {
				return accentButton(Accents.ACCENT_CIRCUMFLEX, b);
			}
			if (wb.getActionName().equals(Action.TOGGLE_ACCENT_GRAVE.name())) {
				return accentButton(Accents.ACCENT_GRAVE, b);
			}
			if (wb.getActionName().equals("*")) {
				return new KeyBoardButtonBase(Unicode.MULTIPLY + "", "*", b);
			}
			if (wb.getActionName().equals("/")) {
				return new KeyBoardButtonBase(Unicode.DIVIDE, Unicode.DIVIDE, b);
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
			if (wb.getActionName().equals(Action.SWITCH_TO_SPECIAL_SYMBOLS.name())) {
				return functionButton(wb, bh);
			}
			return new KeyBoardButtonBase(wb.getActionName(),
					wb.getActionName(), b);
		}

	}

	protected boolean isAccent(String txt) {
		return Accents.ACCENT_GRAVE.equals(txt)
				|| Accents.ACCENT_ACUTE.equals(txt)
				|| Accents.ACCENT_CIRCUMFLEX.equals(txt)
				|| Accents.ACCENT_CARON.equals(txt);
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
					KeyboardResources.INSTANCE.keyboard_enter_black(), bh,
					Action.RETURN_ENTER);
		} else if (resourceName.equals(Resource.BACKSPACE_DELETE.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.keyboard_backspace(), bh,
					Action.BACKSPACE_DELETE);
		} else if (resourceName.equals(Resource.LEFT_ARROW.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.keyboard_arrowLeft_black(), bh,
					Action.LEFT_CURSOR);
		} else if (resourceName.equals(Resource.RIGHT_ARROW.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.keyboard_arrowRight_black(), bh,
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
		if (resourceName.equals(KeyboardConstants.SWITCH_TO_SPECIAL_SYMBOLS)) {
			return new KeyBoardButtonFunctionalBase(KeyboardConstants.SWITCH_TO_SPECIAL_SYMBOLS, bh, Action.SWITCH_TO_SPECIAL_SYMBOLS);
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
		for (int i = 0; tabs != null && i < tabs.getWidgetCount(); i++) {
			Widget wdgt = tabs.getWidget(i);
			if (wdgt instanceof KeyPanelBase) {
				updatePanelSize((KeyPanelBase) wdgt, getBaseSize());
			}
		}
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
	public void checkLanguage() {
		switcher.reset();
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
	
	@Override
	public void setVisible(boolean b) {
		switcher.reset();
		super.setVisible(b);
	}
	
	protected void showHelp(int x, int y) {
	}
	
	private void selectTab(int idx) {
		switcher.select(idx);
	}
	
	public void selectNumbers() {
		selectTab(TAB_NUMBERS);
	}
	
	public void selectFunctions() {
		selectTab(TAB_FX);
	}
	
	public void selectAbc() {
		selectTab(TAB_ABC);
	}
	
	public void selectGreek() {
		selectTab(TAB_ALPHA);
	}

	/**
	 * check the minimum width. Either width of ABC panel or 123 panel. 70 =
	 * width of button; 82 = padding
	 * 
	 * @return
	 */
	private int getMinWidthWithoutScaling() {
		int abc = 10 * 70 + 82;
		int numbers = 850;
		return Math.max(abc, numbers);
	}

}
