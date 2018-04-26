package org.geogebra.keyboard.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.keyboard.KeyboardRowDefinitionProvider;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.lang.Language;
import org.geogebra.keyboard.base.Accents;
import org.geogebra.keyboard.base.Action;
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
import com.google.gwt.user.client.ui.CustomButton;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.share.util.Unicode;

/**
 * tabbed keyboard
 */
public class TabbedKeyboard extends FlowPanel {
	/**
	 * small height
	 */
	public static final int SMALL_HEIGHT = 131;
	/**
	 * big height
	 */
	public static final int BIG_HEIGHT = 186;
	private static final int TAB_NUMBERS = 0;
	private static final int TAB_FX = 1;
	private static final int TAB_ABC = 2;
	private static final int TAB_ALPHA = 3;
	private static final int TAB_SPECIAL = 4;
	private HashMap<String, String> upperKeys;
	/**
	 * minimum width of the whole application to use normal font (small font
	 * otherwise)
	 */
	protected static final int MIN_WIDTH_FONT = 485;

	/**
	 * base width
	 */
	protected static final int BASE_WIDTH = 70;
	/**
	 * localization
	 */
	KeyboardLocale locale;
	private boolean isSmallKeyboard;
	/**
	 * application
	 */
	protected HasKeyboard app;
	private ArrayList<Keyboard> layouts = new ArrayList<>(4);
	private Object keyboardLocale;
	private ButtonHandler btnHandler;
	private UpdateKeyBoardListener updateKeyBoardListener;
	private FlowPanel tabs;
	private KeyboardSwitcher switcher;
	/**
	 * keyboard
	 */
	protected KeyPanelBase currentKeyboard = null;
	/**
	 * true if keyboard wanted
	 */
	protected boolean keyboardWanted = false;
	private boolean doubleBrackets;
	/**
	 * has material tooltips
	 */
	boolean hasTooltips;

	private class KeyboardSwitcher extends FlowPanel {
		// private static final int SWITCHER_HEIGHT = 40;
		private FlowPanel contents;
		private List<Button> switches;
		private CustomButton closeButton;
		private ToggleButton moreButton;
		// private Integer width = null;
		boolean isSpecialActive = false;

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

		public void addSwitch(final KeyPanelBase keyboard, String string) {
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
					hideTabs();
					// selectAll(false);
					unselectAll();
					currentKeyboard = keyboard;
					keyboard.setVisible(true);
					setSelected(ret, true);
				}
			});
			return ret;
		}

		public void setSelected(Button btn, boolean value) {
			if (value) {
				btn.addStyleName("selected");
			} else {
				btn.removeStyleName("selected");
			}
		}

		public void setSelected(int idx, boolean value) {
			if (idx == TAB_SPECIAL) {
				if (value) {
					setSelected(switches.get(TAB_ABC), value);
					isSpecialActive = true;
				}
				return;
			} else if (idx == TAB_ABC && isSpecialActive) {
				setSelected(switches.get(TAB_ABC), value);
				isSpecialActive = false;
			} else {
				setSelected(switches.get(idx), value);
			}
		}

		public void unselectAll() {
			for (Widget btn : switches) {
				btn.removeStyleName("selected");
			}
		}

		private Widget makeCloseButton() {
			Image img = new Image(KeyboardResources.INSTANCE
					.keyboard_close_black().getSafeUri().asString());
			img.setAltText(locale.getMenu("Close"));
			Image hoverImg = new Image(KeyboardResources.INSTANCE
					.keyboard_close_purple().getSafeUri().asString());
			hoverImg.setAltText(locale.getMenu("Close"));
			closeButton = new CustomButton() {
				// it's abstract for some reason
			};
			closeButton.getElement().setAttribute("aria-label",
					locale.getMenu("Close"));

			closeButton.getUpFace().setImage(img);
			closeButton.getUpHoveringFace().setImage(hoverImg);
			closeButton.addStyleName("closeTabbedKeyboardButton");
			ClickStartHandler.init(closeButton, new ClickStartHandler() {

				@Override
				public void onClickStart(int x, int y, PointerEventType type) {
					closeButtonClicked();
				}
			});
			return closeButton;
		}

		private Widget makeMoreButton() {
			Image img = new Image(KeyboardResources.INSTANCE.keyboard_more()
					.getSafeUri().asString());
			img.setAltText(locale.getMenu("Commands"));
			Image hoverImg = new Image(KeyboardResources.INSTANCE
					.keyboard_more_purple().getSafeUri().asString());
			hoverImg.setAltText(locale.getMenu("Commands"));
			moreButton = new ToggleButton(img, hoverImg);
			moreButton.getElement().setAttribute("aria-label",
					locale.getMenu("Commands"));

			moreButton.getUpHoveringFace().setImage(hoverImg);
			moreButton.addStyleName("moreKeyboardButton");
			ClickStartHandler.init(moreButton, new ClickStartHandler() {

				@Override
				public void onClickStart(int x, int y, PointerEventType type) {
					// unselectAll();
					showHelp(
							getMoreButton().getAbsoluteLeft()
									+ getMoreButton().getOffsetWidth(),
							getMoreButton().getAbsoluteTop());
				}
			});
			return moreButton;
		}

		public ToggleButton getMoreButton() {
			return moreButton;
		}

		public void reset() {
			if (moreButton != null) {
				moreButton.setValue(false);
			}
		}

		public void select(int idx) {
			for (int i = 0; i < getTabs().getWidgetCount(); i++) {
				getTabs().getWidget(i).setVisible(i == idx);
				setSelected(i, i == idx);
			}
		}

		public FlowPanel getContent() {
			return contents;
		}
	}

	/**
	 * @return {@link UpdateKeyBoardListener}
	 */
	public UpdateKeyBoardListener getUpdateKeyBoardListener() {
		return updateKeyBoardListener;
	}

	/**
	 * @param listener
	 *            {@link UpdateKeyBoardListener}
	 */
	public void setListener(UpdateKeyBoardListener listener) {
		this.updateKeyBoardListener = listener;
	}

	/**
	 * on close
	 */
	protected void closeButtonClicked() {
		if (updateKeyBoardListener != null) {
			updateKeyBoardListener.keyBoardNeeded(false, null);
		}
		keyboardWanted = false;
	}

	/**
	 * @param bh
	 *            {@link ButtonHandler}
	 * @param appKeyboard
	 *            {@link HasKeyboard}
	 */
	public void buildGUI(ButtonHandler bh, HasKeyboard appKeyboard) {
		KeyboardFactory kbf = new KeyboardFactory();
		this.tabs = new FlowPanel();
		this.app = appKeyboard;
		this.hasTooltips = ((App) app).has(Feature.TOOLTIP_DESIGN);
		this.locale = app.getLocalization();
		this.keyboardLocale = locale.getLocaleStr();
		switcher = new KeyboardSwitcher();
		this.btnHandler = bh;
		this.doubleBrackets = ((App) app).has(Feature.DOUBLE_ROUND_BRACKETS);
		KeyPanelBase keyboard = buildPanel(kbf.createMathKeyboard(),
				bh);
		tabs.add(keyboard);
		// more butto must be first because of float (Firefox)
		switcher.addMoreButton();
		switcher.addSwitch(keyboard, "123");
		keyboard = buildPanel(kbf.createFunctionsKeyboard(), bh);
		tabs.add(keyboard);
		keyboard.setVisible(false);
		switcher.addSwitch(keyboard, "f(x)");
		upperKeys = new HashMap<>();
		String middleRow = locale.getKeyboardRow(2);
		keyboard = buildPanel(kbf.createLettersKeyboard(
				filter(locale.getKeyboardRow(1).replace("'", "")),
				filter(middleRow), filter(locale.getKeyboardRow(3)), upperKeys),
				bh);
		tabs.add(keyboard);
		keyboard.setVisible(false);
		switcher.addSwitch(keyboard, locale.getMenu("Keyboard.ABC"));
		keyboard = buildPanel(kbf.createGreekKeyboard(), bh);
		tabs.add(keyboard);
		keyboard.setVisible(false);
		switcher.addSwitch(keyboard, Unicode.ALPHA_BETA_GAMMA);
		switcher.setSelected(0, true);
		// add special char tab
		keyboard = buildPanel(kbf.createSpecialSymbolsKeyboard(),
				bh);
		keyboard.setVisible(false);
		tabs.add(keyboard);
		if (shouldHaveLatinExtension(middleRow)) {
			KeyboardRowDefinitionProvider latinProvider = new KeyboardRowDefinitionProvider(
					(Localization) locale);
			String[] rows = latinProvider.getDefaultLowerKeys();
			keyboard = buildPanel(kbf.createLettersKeyboard(rows[0], rows[1],
					rows[2], latinProvider.getUpperKeys()), bh);
			tabs.add(keyboard);
			keyboard.setVisible(false);
			switcher.addSwitch(keyboard, "ABC");
		}
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
			if (keys.length() > i + 1) {
				upperKeys.put(keys.charAt(i) + "", keys.charAt(i + 1) + "");
			}
		}
		// TODO remove the replace once ggbtrans is fixed
		return sb.toString().replace("'", "");
	}

	private KeyPanelBase buildPanel(Keyboard layout, final ButtonHandler bh) {
		final KeyPanelBase keyboard = new KeyPanelBase(layout);
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

	/**
	 * 
	 * @param maxWeightSum
	 *            weight sum of the widest row
	 * @return button base size
	 */
	int getBaseSize(double maxWeightSum) {
		return (int) ((app.getInnerWidth() - 10) > BASE_WIDTH * maxWeightSum
				? BASE_WIDTH : (app.getInnerWidth() - 10) / maxWeightSum);
	}

	/**
	 * @param keyboard
	 *            {@link KeyPanelBase}
	 * @param layout
	 *            {@link Keyboard}
	 * @param bh
	 *            {@link ButtonHandler}
	 */
	void updatePanel(KeyPanelBase keyboard, Keyboard layout, ButtonHandler bh) {
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
		updatePanelSize(keyboard);
	}

	/**
	 * This is much faster than updatePanel as it doesn't clear the model. It
	 * assumes the model and button layout are in sync.
	 */
	private void updatePanelSize(KeyPanelBase keyboard) {
		int buttonIndex = 0;
		int margins = 4;
		if (keyboard.getLayout() == null) {
			return;
		}
		KeyBoardButtonBase button = null;
		double weightSum = 7; // initial guess
		for (Row row : keyboard.getLayout().getModel().getRows()) {
			weightSum = Math.max(row.getRowWeightSum(), weightSum);
		}
		int baseSize = getBaseSize(weightSum);
		for (Row row : keyboard.getLayout().getModel().getRows()) {
			double offset = 0;
			for (WeightedButton wb : row.getButtons()) {
				if (Action.NONE.name().equals(wb.getActionName())) {
					offset = wb.getWeight();
				} else {
					button = keyboard.getButtons().get(buttonIndex);
					if (offset > 0) {
						button.getElement().getStyle().setMarginLeft(
								offset * baseSize + margins / 2, Unit.PX);
					}
					button.getElement().getStyle().setWidth(
							wb.getWeight() * baseSize - margins, Unit.PX);
					offset = 0;
					buttonIndex++;
				}
			}
			if (Action.NONE.name().equals(row.getButtons()
					.get(row.getButtons().size() - 1).getActionName())) {
				button.getElement().getStyle().setMarginRight(
						offset * baseSize + margins / 2, Unit.PX);
			}
		}
		if (app.getInnerWidth() < getMinWidthWithoutScaling()) {
			addStyleName("scale");
			removeStyleName("normal");
			removeStyleName("smallerFont");
			if (app.getInnerWidth() < MIN_WIDTH_FONT) {
				addStyleName("smallerFont");
			}
		} else {
			addStyleName("normal");
			removeStyleName("scale");
			removeStyleName("smallerFont");
		}
		// set width of switcher contents
		if (app.getInnerWidth() > 700) {
			switcher.getContent().getElement().getStyle().setWidth(644,
					Unit.PX);
		} else {
			switcher.getContent().getElement().getStyle()
					.setWidth(Math.min(644, app.getInnerWidth() - 10), Unit.PX);
		}
	}

	private KeyBoardButtonBase makeButton(WeightedButton wb, ButtonHandler b) {
		switch (wb.getResourceType()) {
		case TRANSLATION_MENU_KEY:
			if (wb.getResourceName().equals("Translate.currency")) {
				return new KeyBoardButtonBase(
						Language.getCurrency(keyboardLocale.toString()),
						Language.getCurrency(keyboardLocale.toString()), b);
			}
			return new KeyBoardButtonBase(locale.getMenu(wb.getActionName()),
					wb.getActionName().replace("Function.", ""), b);
		case TRANSLATION_COMMAND_KEY:
			return new KeyBoardButtonBase(locale.getCommand(wb.getActionName()),
					wb.getActionName(), b);
		case DEFINED_CONSTANT:
			return functionButton(wb, b);
		case TEXT:
		default:
			return textButton(wb, b);
		}
	}

	private KeyBoardButtonBase textButton(WeightedButton wb, ButtonHandler b) {
		String name = wb.getActionName();
		if (name.equals(Action.TOGGLE_ACCENT_ACUTE.name())) {
			return accentButton(Accents.ACCENT_ACUTE, b);
		}
		if (name.equals(Action.TOGGLE_ACCENT_CARON.name())) {
			return accentButton(Accents.ACCENT_CARON, b);
		}
		if (name.equals(Action.TOGGLE_ACCENT_CIRCUMFLEX.name())) {
			return accentButton(Accents.ACCENT_CIRCUMFLEX, b);
		}
		if (name.equals(Action.TOGGLE_ACCENT_GRAVE.name())) {
			return accentButton(Accents.ACCENT_GRAVE, b);
		}
		if ("*".equals(name)) {
			return new KeyBoardButtonBase(Unicode.MULTIPLY + "", b);
		}
		if ("/".equals(name)) {
			return new KeyBoardButtonBase(Unicode.DIVIDE + "", b);
		}
		if ("|".equals(name)) {
			return new KeyBoardButtonBase("|a|", "abs", b);
		}
		if ("-".equals(name)) {
			return new KeyBoardButtonBase(Unicode.MINUS + "", b);
		}
		if (Unicode.EULER_STRING.equals(name)) {
			return new KeyBoardButtonBase("e", Unicode.EULER_STRING, b);
		}
		if (name.equals(Action.SWITCH_TO_SPECIAL_SYMBOLS.name())
				|| name.equals(Action.SWITCH_TO_ABC.name())) {
			return functionButton(wb, btnHandler);
		}
		if (("" + Unicode.LFLOOR).equals(name)) {
			return new KeyBoardButtonBase(KeyboardConstants.FLOOR, name,
					btnHandler);
		}
		if (("" + Unicode.LCEIL).equals(name)) {
			return new KeyBoardButtonBase(KeyboardConstants.CEIL, name,
					btnHandler);
		}
		if (doubleBrackets) {
			if ("(".equals(name)) {
				return new KeyBoardButtonBase("(", "()", b);
			}
			if ("{".equals(name)) {
				return new KeyBoardButtonBase("{", "{}", b);
			}
			if ("[".equals(name)) {
				return new KeyBoardButtonBase("[", "[]", b);
			}
		}
		return new KeyBoardButtonBase(name, b);
	}

	private static KeyBoardButtonBase accentButton(String accent,
			ButtonHandler b) {
		return new KeyBoardButtonBase(accent, accent, b);
	}

	/**
	 * process shift
	 */
	protected void processShift() {
		for (Keyboard layout : layouts) {
			layout.toggleCapsLock();
		}
	}

	/**
	 * turn off capslock
	 */
	protected void disableCapsLock() {
		for (Keyboard layout : layouts) {
			layout.disableCapsLock();
		}
	}

	/**
	 * @param text
	 *            letter
	 */
	protected void processAccent(String text) {
		for (Keyboard layout : layouts) {
			layout.toggleAccent(text);
		}
	}

	private KeyBoardButtonBase functionButton(WeightedButton button,
			ButtonHandler bh) {
		Localization loc = ((App) app).getLocalization();
		String resourceName = button.getResourceName();
		if (resourceName.equals(Resource.RETURN_ENTER.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.keyboard_enter_black(), bh,
					Action.RETURN_ENTER, loc, "EnterAltText");
		} else if (resourceName.equals(Resource.BACKSPACE_DELETE.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.keyboard_backspace(), bh,
					Action.BACKSPACE_DELETE, loc, "BackspaceAltText");
		} else if (resourceName.equals(Resource.LEFT_ARROW.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.keyboard_arrowLeft_black(), bh,
					Action.LEFT_CURSOR, loc, "LeftArrowAltText");
		} else if (resourceName.equals(Resource.RIGHT_ARROW.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.keyboard_arrowRight_black(), bh,
					Action.RIGHT_CURSOR, loc, "RightArrowAltText");
		} else if (resourceName.equals(Resource.POWA2.name())) {
			return new KeyBoardButtonFunctionalBase(
							KeyboardResources.INSTANCE.square(),
							button.getActionName(), bh, false, loc,
					"SquareAltText");
			
		} else if (resourceName.equals(Resource.POWAB.name())) {
			return new KeyBoardButtonFunctionalBase(
							KeyboardResources.INSTANCE.xPower(),
							"a^x", bh, false, loc, "PowerAltText");			
		} else if (resourceName.equals(Resource.CAPS_LOCK.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.keyboard_shift(), bh,
					Action.CAPS_LOCK, loc, "CapsLockUnactiveAltText");
		} else if (resourceName.equals(Resource.CAPS_LOCK_ENABLED.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.keyboard_shiftDown(), bh,
					Action.CAPS_LOCK, loc, "CapsLockActiveAltText");
		} else if (resourceName.equals(Resource.POW10_X.name())) {
			return new KeyBoardButtonFunctionalBase(
							KeyboardResources.INSTANCE.ten_power(),
							button.getActionName(), bh, false, loc,
							"PowTenAltText");
		} else if (resourceName.equals(Resource.POWE_X.name())) {
			return  new KeyBoardButtonFunctionalBase(
							KeyboardResources.INSTANCE.e_power(),
							button.getActionName(), bh, false, loc,
							"PowEAltText");
		} else if (resourceName.equals(Resource.LOG_10.name())) {
			return new KeyBoardButtonBase("log_10", "log10", bh);
		} else if (resourceName.equals(Resource.LOG_B.name())) {
			return  new KeyBoardButtonFunctionalBase(
							KeyboardResources.INSTANCE.log(),
					"log_", bh, true, loc, "LogBAltText");
		} else if (resourceName.equals(Resource.A_N.name())) {
			return new KeyBoardButtonFunctionalBase(
							KeyboardResources.INSTANCE.a_index(),
							"_", bh, false, loc, "SubscriptAltText");
		} else if (resourceName.equals(Resource.N_ROOT.name())) {
			return  new KeyBoardButtonFunctionalBase(
							KeyboardResources.INSTANCE.n_root(),
							button.getActionName(), bh, false, loc,
							"RootAltText");
		} else if (resourceName.equals(Resource.INTEGRAL.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.integral(),
					button.getActionName(), bh, loc, "Integral");
		} else if (resourceName.equals(Resource.DERIVATIVE.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.derivative(),
					button.getActionName(), bh, loc, "Derivative");
		} else if (resourceName.equals(Resource.ABS.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.abs(),
					"abs", bh, false, loc, "AbsAltText");
		} else if (resourceName.equals(Resource.CEIL.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.ceil(),
					button.getActionName(), bh, false, loc, "CeilAltText");
		} else if (resourceName.equals(Resource.FLOOR.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.floor(),
					button.getActionName(), bh, false, loc, "FloorAltText");
		}
		if (resourceName.equals(Resource.ROOT.name())) {
			return new KeyBoardButtonFunctionalBase(
							KeyboardResources.INSTANCE.sqrt(),
							button.getActionName(), bh, false, loc,
							"SquareRootAltText");
		}
		if (KeyboardConstants.SWITCH_TO_SPECIAL_SYMBOLS.equals(resourceName)) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardConstants.SWITCH_TO_SPECIAL_SYMBOLS, bh,
					Action.SWITCH_TO_SPECIAL_SYMBOLS);
		}
		if ("ABC".equals(resourceName)) {
			return new KeyBoardButtonFunctionalBase("ABC", bh,
					Action.SWITCH_TO_ABC);
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
				updatePanelSize((KeyPanelBase) wdgt);
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
	 * from the last loaded language and sets the {@link KeyboardLocale} to the
	 * new language
	 */
	public void checkLanguage() {
		switcher.reset();
		if (btnHandler == null) {
			return;
		}
		// TODO validate?
		String newKeyboardLocale = app.getLocalization().getLocaleStr();
		if (newKeyboardLocale != null
				&& newKeyboardLocale.equals(keyboardLocale)) {
			return;
		}
		if (newKeyboardLocale != null) {
			this.keyboardLocale = newKeyboardLocale;
		} else {
			this.keyboardLocale = Language.English_US.getLocaleGWT();
		}
		clear();
		buildGUI(btnHandler, app);
	}

	@Override
	public void setVisible(boolean b) {
		switcher.reset();
		super.setVisible(b);
	}

	/**
	 * @param x
	 *            coord
	 * @param y
	 *            coord
	 */
	protected void showHelp(int x, int y) {
		// do nothing
	}

	private void selectTab(int idx) {
		switcher.select(idx);
	}

	/**
	 * @return tabs
	 */
	public FlowPanel getTabs() {
		return tabs;
	}

	/**
	 * select numbers tab
	 */
	public void selectNumbers() {
		selectTab(TAB_NUMBERS);
	}

	/**
	 * select math tab with functions
	 */
	public void selectFunctions() {
		selectTab(TAB_FX);
	}

	/**
	 * select abc letters tab
	 */
	public void selectAbc() {
		selectTab(TAB_ABC);
	}

	/**
	 * select greek letters tab
	 */
	public void selectGreek() {
		selectTab(TAB_ALPHA);
	}

	/**
	 * select special characters tab
	 */
	public void selectSpecial() {
		selectTab(TAB_SPECIAL);
	}

	/**
	 * check the minimum width. Either width of ABC panel or 123 panel. 70 =
	 * width of button; 82 = padding
	 * 
	 * @return
	 */
	private static int getMinWidthWithoutScaling() {
		int abc = 10 * 70 + 82;
		int numbers = 850;
		return Math.max(abc, numbers);
	}

	/**
	 * @return true if keyboard wanted
	 */
	public final boolean shouldBeShown() {
		return this.keyboardWanted;
	}

	/**
	 * keyboard wanted in focus
	 */
	public final void showOnFocus() {
		this.keyboardWanted = true;
	}

	/**
	 * Hide all keyboard panels.
	 */
	void hideTabs() {
		for (int i = 0; i < tabs.getWidgetCount(); i++) {
			tabs.getWidget(i).setVisible(false);
		}
	}

	private static boolean shouldHaveLatinExtension(String middleRow) {
		int first = middleRow.codePointAt(0);
		return first < 0 || first > 0x00FF;
	}
}
