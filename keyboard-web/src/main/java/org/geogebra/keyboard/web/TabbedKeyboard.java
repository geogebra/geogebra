package org.geogebra.keyboard.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.geos.inputbox.InputBoxType;
import org.geogebra.common.keyboard.KeyboardRowDefinitionProvider;
import org.geogebra.common.main.AppKeyboardType;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.lang.Language;
import org.geogebra.keyboard.base.Accents;
import org.geogebra.keyboard.base.Action;
import org.geogebra.keyboard.base.Keyboard;
import org.geogebra.keyboard.base.KeyboardFactory;
import org.geogebra.keyboard.base.KeyboardType;
import org.geogebra.keyboard.base.Resource;
import org.geogebra.keyboard.base.listener.KeyboardObserver;
import org.geogebra.keyboard.base.model.Row;
import org.geogebra.keyboard.base.model.WeightedButton;
import org.geogebra.keyboard.base.model.impl.CapsLockModifier;
import org.geogebra.keyboard.base.model.impl.factory.LetterKeyboardFactory;
import org.geogebra.keyboard.scientific.model.ScientificFunctionKeyboardFactory;
import org.geogebra.keyboard.scientific.model.ScientificKeyboardFactory;
import org.geogebra.keyboard.scientific.model.ScientificLettersKeyboardFactory;
import org.geogebra.keyboard.web.factory.KeyboardInputBox;
import org.geogebra.keyboard.web.factory.KeyboardMow;
import org.geogebra.web.html5.gui.util.BrowserStorage;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.share.util.Unicode;

/**
 * tabbed keyboard
 */
public class TabbedKeyboard extends FlowPanel
		implements ButtonHandler, RequiresResize {

	/**
	 * small height
	 */
	public static final int SMALL_HEIGHT = 131;
	/**
	 * big height
	 */
	public static final int BIG_HEIGHT = 186;

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
	Localization locale;
	private boolean isSmallKeyboard;
	/**
	 * application
	 */
	protected HasKeyboard hasKeyboard;
	private ArrayList<Keyboard> layouts = new ArrayList<>(4);
	private Object keyboardLocale;
	private UpdateKeyBoardListener updateKeyBoardListener;
	protected KeyboardListener processField;
	private FlowPanel tabs;
	protected KeyboardSwitcher switcher;
	private Map<KeyboardType, Widget> keyboardMap;
	/**
	 * true if keyboard wanted
	 */
	protected boolean keyboardWanted = false;
	private ButtonRepeater repeater;
	private boolean hasMoreButton;

	private KeyboardSwitcher.SwitcherButton ansSwitcher;
	private KeyboardSwitcher.SwitcherButton defaultSwitcher;

	/**
	 * @param appKeyboard
	 *            {@link HasKeyboard}
	 * @param hasMoreButton
	 *            whether to show help button
	 */
	public TabbedKeyboard(HasKeyboard appKeyboard, boolean hasMoreButton) {
		this.hasKeyboard = appKeyboard;
		this.locale = hasKeyboard.getLocalization();
		this.keyboardLocale = locale.getLocaleStr();
		this.switcher = new KeyboardSwitcher(this);
		this.hasMoreButton = hasMoreButton;
		this.keyboardMap = new HashMap<>();
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
		BrowserStorage.LOCAL.setItem(BrowserStorage.KEYBOARD_WANTED, "false");
	}

	private KeyboardFactory initKeyboardFactory(InputBoxType inputBoxType) {
		KeyboardFactory factory;
		if (inputBoxType != null) {
			factory = new KeyboardInputBox(inputBoxType, hasKeyboard.getInputBoxFunctionVars());
		} else {
			switch (hasKeyboard.getKeyboardType()) {
			case MOW:
				factory = new KeyboardMow();
				break;
			default:
				factory = new KeyboardFactory();
			}
		}
		return factory;
	}

	private void buildGUIGgb(InputBoxType inputBoxType) {
		// more button must be first because of float (Firefox)
		if (hasMoreButton) {
			switcher.addMoreButton();
		}
		tabs = new FlowPanel();

		KeyboardFactory factory = initKeyboardFactory(inputBoxType);

		createAnsMathKeyboard(factory);
		createDefaultKeyboard(factory);
		createFunctionsKeyboard(factory);
		if (locale.isLatinKeyboard()) {
			createLocalizedAbcKeyboard(factory, true);
		} else {
			createLatinKeyboard(factory);
			createLocalizedAbcKeyboard(factory, false);
		}
		createSpecialSymbolsKeyboard(factory);
		createGreekKeyboard(factory);

		switcher.select(KeyboardType.NUMBERS);
		layout();
	}

	private void createAnsMathKeyboard(KeyboardFactory factory) {
		KeyPanelBase keyboard = buildPanel(factory.createMathKeyboard(), this);
		addTab(keyboard, KeyboardType.NUMBERS);
		ansSwitcher = switcher.addSwitch(keyboard, KeyboardType.NUMBERS, "123");
		ansSwitcher.setVisible(false);
		setDataTest(ansSwitcher, "keyboard-123-ans");
	}

	private void createFunctionsKeyboard(KeyboardFactory factory) {
		KeyPanelBase functionKeyboard = buildPanel(factory.createFunctionsKeyboard(), this);
		addTab(functionKeyboard, KeyboardType.OPERATORS);
		functionKeyboard.setVisible(false);
		KeyboardSwitcher.SwitcherButton function = switcher.addSwitch(functionKeyboard,
				KeyboardType.OPERATORS, "f(x)");
		setDataTest(function, "keyboard-fx");
	}

	private void createDefaultKeyboard(KeyboardFactory factory) {
		KeyPanelBase defaultKeyboard = buildPanel(factory.createDefaultKeyboard(), this);
		addTab(defaultKeyboard, KeyboardType.NUMBERS_DEFAULT);
		defaultKeyboard.setVisible(false);
		defaultSwitcher = switcher.addSwitch(defaultKeyboard, KeyboardType.NUMBERS_DEFAULT, "123");
		setDataTest(defaultSwitcher, "keyboard-123");
	}

	private void createLocalizedAbcKeyboard(KeyboardFactory factory, boolean withGreek) {
		upperKeys = new HashMap<>();
		String firstRow = locale.getKeyboardRow(1);
		String middleRow = locale.getKeyboardRow(2);
		String lastRow = locale.getKeyboardRow(3);
		KeyPanelBase keyboard = buildPanel(factory.createLettersKeyboard(
				filter(firstRow.replace("'", "")),
				filter(middleRow),
				filter(lastRow), upperKeys, withGreek),
				this);
		addTab(keyboard, KeyboardType.ABC);
		keyboard.setVisible(false);
		switcher.addSwitch(keyboard, KeyboardType.ABC,
				locale.getMenuDefault("Keyboard.ABC", "ABC"));
	}

	private void createSpecialSymbolsKeyboard(KeyboardFactory factory) {
		KeyPanelBase keyboard = buildPanel(factory.createSpecialSymbolsKeyboard(), this);
		addTab(keyboard, KeyboardType.SPECIAL);
		keyboard.setVisible(false);
		switcher.addSwitch(keyboard, KeyboardType.SPECIAL,
				KeyboardConstants.SWITCH_TO_SPECIAL_SYMBOLS);
	}

	private void createGreekKeyboard(KeyboardFactory factory) {
		KeyPanelBase keyboard = buildPanel(factory.createGreekKeyboard(),
				this);
		keyboard.setVisible(false);
		addTab(keyboard, KeyboardType.GREEK);
	}

	private void createLatinKeyboard(KeyboardFactory factory) {
		KeyboardRowDefinitionProvider latinProvider = new KeyboardRowDefinitionProvider(
				locale);
		String[] rows = latinProvider.getDefaultLowerKeys();
		Keyboard keyboardModel = factory.createLettersKeyboard(rows[0], rows[1],
				rows[2], latinProvider.getUpperKeys());
		KeyPanelBase keyboard = buildPanel(keyboardModel, this);
		addTab(keyboard, KeyboardType.LATIN);
		keyboard.setVisible(false);
		switcher.addSwitch(keyboard, KeyboardType.LATIN, "ABC");
	}

	private void addTab(KeyPanelBase keyboardPanel, KeyboardType keyboardType) {
		tabs.add(keyboardPanel);
		keyboardMap.put(keyboardType, keyboardPanel);
	}

	private void setDataTest(Widget widget, String value) {
		widget.getElement().setAttribute("data-test", value);
	}

	private void buildGUIScientific() {
		KeyboardFactory kbf = new KeyboardFactory();
		this.tabs = new FlowPanel();

		KeyPanelBase keyboard = buildPanel(
				kbf.getImpl(new ScientificKeyboardFactory()), this);
		addTab(keyboard, KeyboardType.NUMBERS);
		//skip more button
		switcher.addSwitch(keyboard, KeyboardType.NUMBERS, "123");
		
		keyboard = buildPanel(
				kbf.getImpl(new ScientificFunctionKeyboardFactory()), this);
		addTab(keyboard, KeyboardType.OPERATORS);
		keyboard.setVisible(false);
		switcher.addSwitch(keyboard, KeyboardType.OPERATORS, "f(x)");
		upperKeys = new HashMap<>();
		ScientificLettersKeyboardFactory letterFactory = new ScientificLettersKeyboardFactory();
		letterFactory.setKeyboardDefinition(filter(locale.getKeyboardRow(1).replace("'", "")),
				filter(locale.getKeyboardRow(2)),
				filter(locale.getKeyboardRow(3)), ",'",
				LetterKeyboardFactory.ACTION_SHIFT, null);
		keyboard = buildPanel(
				kbf.getImpl(letterFactory, new CapsLockModifier(upperKeys)),
				this);
		addTab(keyboard, KeyboardType.ABC);
		keyboard.setVisible(false);
		switcher.addSwitch(keyboard, KeyboardType.ABC, "ABC");

		layout();
	}

	private void layout() {
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

			@Override
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
		return (int) ((hasKeyboard.getInnerWidth() - 10) > BASE_WIDTH * maxWeightSum
				? BASE_WIDTH : (hasKeyboard.getInnerWidth() - 10) / maxWeightSum);
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
				if (!Action.NONE.name().equals(wb.getPrimaryActionName())) {
					KeyBoardButtonBase button = makeButton(wb, bh);
					addSecondary(button, wb);
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
		double weightSum = 6; // initial guess
		for (Row row : keyboard.getLayout().getModel().getRows()) {
			weightSum = Math.max(row.getRowWeightSum(), weightSum);
		}
		int baseSize = getBaseSize(weightSum);
		for (Row row : keyboard.getLayout().getModel().getRows()) {
			double offset = 0;
			for (WeightedButton wb : row.getButtons()) {
				if (Action.NONE.name().equals(wb.getPrimaryActionName())) {
					offset = wb.getWeight();
				} else {
					button = keyboard.getButtons().get(buttonIndex);
					if (offset > 0) {
						button.getElement().getStyle().setMarginLeft(
								offset * baseSize + margins / 2d, Unit.PX);
					}
					button.getElement().getStyle().setWidth(
							wb.getWeight() * baseSize - margins, Unit.PX);
					offset = 0;
					buttonIndex++;
				}
			}
			if (Action.NONE.name().equals(row.getButtons()
					.get(row.getButtons().size() - 1).getPrimaryActionName())) {
				button.getElement().getStyle().setMarginRight(
						offset * baseSize + margins / 2d, Unit.PX);
			}
		}
		if (hasKeyboard.getInnerWidth() < getMinWidthWithoutScaling()) {
			addStyleName("scale");
			removeStyleName("normal");
			removeStyleName("smallerFont");
			if (hasKeyboard.getInnerWidth() < MIN_WIDTH_FONT) {
				addStyleName("smallerFont");
			}
		} else {
			addStyleName("normal");
			removeStyleName("scale");
			removeStyleName("smallerFont");
		}
		// set width of switcher contents
		if (hasKeyboard.getInnerWidth() > 700) {
			switcher.getContent().getElement().getStyle().setWidth(644,
					Unit.PX);
		} else {
			switcher.getContent().getElement().getStyle()
					.setWidth(Math.min(644, hasKeyboard.getInnerWidth() - 10), Unit.PX);
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

			final String name = wb.getPrimaryActionName();

			String altText = wb.getAltText();
			if (altText == null || altText.isEmpty()) {
				altText = name;
			} else {
				// eg "inverse sine"
				altText = locale.getAltText(wb.getAltText());
			}
			return new KeyBoardButtonBase(locale.getFunction(name), altText,
					name, b);
		case TRANSLATION_COMMAND_KEY:
			return new KeyBoardButtonBase(
					locale.getCommand(wb.getPrimaryActionName()),
					wb.getPrimaryActionName(), b);
		case DEFINED_CONSTANT:
			return functionButton(wb, b);
		case TEXT:
		default:
			return textButton(wb, b);
		}
	}

	private static void addSecondary(KeyBoardButtonBase btn,
			WeightedButton wb) {
		if (wb.getActionsSize() > 1) {
			btn.setSecondaryAction(wb.getActionName(1));
		}
	}

	private KeyBoardButtonBase textButton(WeightedButton wb, ButtonHandler b) {
		String name = wb.getPrimaryActionName();
		if (name.equals(Action.TOGGLE_ACCENT_ACUTE.name())) {
			return accentButton(Accents.ACCENT_ACUTE, Action.TOGGLE_ACCENT_ACUTE, b);
		}
		if (name.equals(Action.TOGGLE_ACCENT_CARON.name())) {
			return accentButton(Accents.ACCENT_CARON, Action.TOGGLE_ACCENT_CARON, b);
		}
		if (name.equals(Action.TOGGLE_ACCENT_CIRCUMFLEX.name())) {
			return accentButton(Accents.ACCENT_CIRCUMFLEX, Action.TOGGLE_ACCENT_CIRCUMFLEX, b);
		}
		if (name.equals(Action.TOGGLE_ACCENT_GRAVE.name())) {
			return accentButton(Accents.ACCENT_GRAVE, Action.TOGGLE_ACCENT_GRAVE, b);
		}
		if ((Unicode.DIVIDE + "").equals(name)) {
			// division button in scientific
			return new KeyBoardButtonBase(Unicode.DIVIDE + "",
					Unicode.DIVIDE + "", b);
		}
		if ("/".equals(name)) {
			// division button in graphing
			return new KeyBoardButtonBase(Unicode.DIVIDE + "", "/", b);
		}
		if ("|".equals(name)) {
			return new KeyBoardButtonBase("abs", "abs", b);
		}
		if ("-".equals(name)) {
			return new KeyBoardButtonBase(Unicode.MINUS + "", b);
		}
		if (Unicode.EULER_STRING.equals(name)) {
			return new KeyBoardButtonBase("e", Unicode.EULER_STRING, b);
		}
		if (name.equals(Action.SWITCH_TO_SPECIAL_SYMBOLS.name())
				|| name.equals(Action.SWITCH_TO_GREEK_CHARACTERS.name())
				|| name.equals(Action.SWITCH_TO_ABC.name())
				|| name.equals(Action.ANS.name())) {
			return functionButton(wb, this);
		}

		String caption = wb.getResourceName();
		String altText = wb.getAltText();

		if (altText == null || altText.isEmpty()) {
			// default behaviour for most keys
			altText = name;
		} else if (altText.startsWith("altText.")) {
			// translate if necessary
			// eg altText.imaginaryi
			altText = locale.getAltText(altText);
		}

		return new KeyBoardButtonBase(caption, altText, name, b);
	}

	private static KeyBoardButtonBase accentButton(String accent, Action action,
			ButtonHandler b) {
		return new KeyBoardButtonFunctionalBase(accent, b, action);
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
		Localization loc = hasKeyboard.getLocalization();
		String resourceName = button.getResourceName();
		if (resourceName.equals(Resource.RETURN_ENTER.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.keyboard_enter_black(), bh,
					Action.RETURN_ENTER, loc, "altText.Enter");
		} else if (resourceName.equals(Resource.BACKSPACE_DELETE.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.keyboard_backspace(), bh,
					Action.BACKSPACE_DELETE, loc, "altText.Backspace");
		} else if (resourceName.equals(Resource.LEFT_ARROW.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.keyboard_arrowLeft_black(), bh,
					Action.LEFT_CURSOR, loc, "altText.LeftArrow");
		} else if (resourceName.equals(Resource.RIGHT_ARROW.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.keyboard_arrowRight_black(), bh,
					Action.RIGHT_CURSOR, loc, "altText.RightArrow");
		}  else if (resourceName.equals(Resource.DOWN_ARROW.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.keyboard_arrowDown_black(), bh,
					Action.DOWN_CURSOR, loc, "altText.DownArrow");
		}  else if (resourceName.equals(Resource.UP_ARROW.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.keyboard_arrowUp_black(), bh,
					Action.UP_CURSOR, loc, "altText.UpArrow");
		} else if (resourceName.equals(Resource.POWA2.name())) {
			return new KeyBoardButtonFunctionalBase(
							KeyboardResources.INSTANCE.square(),
					button.getPrimaryActionName(), bh, false, loc,
					"altText.Square");
		} else if (resourceName.equals(Resource.FRACTION.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.fraction(),
					"/", bh, false, loc,
					"altText.Fraction");
		} else if (resourceName.equals(Resource.INVERSE.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.inverse(),
					Unicode.SUPERSCRIPT_MINUS_ONE_STRING, bh, false, 
					loc, "altText.Inverse");
		} else if (resourceName.equals(Resource.POWAB.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.power(),
					button.getPrimaryActionName(), bh, false, loc, "altText.Power");
		} else if (resourceName.equals(Resource.CAPS_LOCK.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.keyboard_shift(), bh,
					Action.CAPS_LOCK, loc, "altText.CapsLockInactive");
		} else if (resourceName.equals(Resource.CAPS_LOCK_ENABLED.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.keyboard_shiftDown(), bh,
					Action.CAPS_LOCK, loc, "altText.CapsLockActive");
		} else if (resourceName.equals(Resource.POW10_X.name())) {
			return new KeyBoardButtonFunctionalBase(
							KeyboardResources.INSTANCE.ten_power(),
					button.getPrimaryActionName(), bh, false, loc,
					"altText.PowTen");
		} else if (resourceName.equals(Resource.POWE_X.name())) {
			return  new KeyBoardButtonFunctionalBase(
							KeyboardResources.INSTANCE.e_power(),
					button.getPrimaryActionName(), bh, false, loc,
					"altText.PowE");
		} else if (resourceName.equals(Resource.LOG_10.name())) {
			return new KeyBoardButtonBase("log_10",
					loc.getAltText("altText.log10"),
					button.getPrimaryActionName(), bh);
		} else if (resourceName.equals(Resource.LOG_B.name())) {
			return  new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.log(),
					button.getPrimaryActionName(), bh, true, loc, "altText.LogB");
		} else if (resourceName.equals(Resource.A_N.name())) {
			return new KeyBoardButtonFunctionalBase(
							KeyboardResources.INSTANCE.subscript(),
					"_", bh, false, loc, "altText.Subscript");
		} else if (resourceName.equals(Resource.N_ROOT.name())) {
			return  new KeyBoardButtonFunctionalBase(
							KeyboardResources.INSTANCE.n_root(),
					button.getPrimaryActionName(), bh, false, loc,
					"altText.Root");
		} else if (resourceName.equals(Resource.INTEGRAL.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.integral(),
					button.getPrimaryActionName(), bh, loc, "Integral");
		} else if (resourceName.equals(Resource.DERIVATIVE.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.derivative(),
					button.getPrimaryActionName(), bh, loc, "Derivative");
		} else if (resourceName.equals(Resource.ABS.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.abs(),
					"abs", bh, false, loc, "altText.Abs");
		} else if (resourceName.equals(Resource.CEIL.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.ceil(),
					button.getPrimaryActionName(), bh, false, loc,
					"altText.Ceil");
		} else if (resourceName.equals(Resource.FLOOR.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.floor(),
					button.getPrimaryActionName(), bh, false, loc,
					"altText.Floor");
		} else if (resourceName.equals(Resource.DEFINITE_INTEGRAL.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.definite_integral(),
					button.getPrimaryActionName(), bh, false, loc,
					"altText.DefiniteIntegral");
		} else if (resourceName.equals(Resource.LIM.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.lim(),
					button.getPrimaryActionName(), bh, false, loc,
					"altText.Lim");
		} else if (resourceName.equals(Resource.PRODUCT.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.product(),
					button.getPrimaryActionName(), bh, false, loc,
					"altText.Product");
		} else if (resourceName.equals(Resource.SUM.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.sum(),
					button.getPrimaryActionName(), bh, false, loc,
					"altText.Sum");
		} else if (resourceName.equals(Resource.VECTOR.name())) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardResources.INSTANCE.vector(),
					button.getPrimaryActionName(), bh, false, loc,
					"altText.Vector");
		}

  		if (resourceName.equals(Resource.ROOT.name())) {
			return new KeyBoardButtonFunctionalBase(
							KeyboardResources.INSTANCE.sqrt(),
					button.getPrimaryActionName(), bh, false, loc,
					"altText.SquareRoot");
		}
		if (KeyboardConstants.SWITCH_TO_SPECIAL_SYMBOLS.equals(resourceName)) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardConstants.SWITCH_TO_SPECIAL_SYMBOLS, bh,
					Action.SWITCH_TO_SPECIAL_SYMBOLS);
		}
		if (KeyboardConstants.SWITCH_TO_GREEK_CHARACTERS.equals(resourceName)) {
			return new KeyBoardButtonFunctionalBase(
					KeyboardConstants.SWITCH_TO_GREEK_CHARACTERS, bh,
					Action.SWITCH_TO_GREEK_CHARACTERS);
		}
		if ("ABC".equals(resourceName)) {
			return new KeyBoardButtonFunctionalBase("ABC", bh,
					Action.SWITCH_TO_ABC);
		}
		if ("ans".equals(resourceName)) {
			return new KeyBoardButtonFunctionalBase("ans", bh, Action.ANS);
		}
		return new KeyBoardButtonBase(button.getPrimaryActionName(),
				button.getPrimaryActionName(), bh);
	}

	@Override
	public void onResize() {
		if (hasKeyboard.getInnerWidth() < 0) {
			return;
		}
		// -2 for applet border
		setWidth(hasKeyboard.getInnerWidth() + "px");
		boolean shouldBeSmall = hasKeyboard.needsSmallKeyboard();
		if (shouldBeSmall && !isSmallKeyboard) {
			addStyleName("lowerHeight");
			this.isSmallKeyboard = true;
		} else if (!shouldBeSmall && isSmallKeyboard) {
			removeStyleName("lowerHeight");
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
		if (hasKeyboard != null) {
			hasKeyboard.updateKeyboardHeight();
		}
	}

	/**
	 * loads the translation-files for the active language if it is different
	 * from the last loaded language and sets the {@link Localization} to the
	 * new language
	 */
	public void checkLanguage() {
		switcher.reset();

		// TODO validate?
		String newKeyboardLocale = hasKeyboard.getLocalization().getLocaleStr();
		if (newKeyboardLocale != null
				&& newKeyboardLocale.equals(keyboardLocale)) {
			return;
		}

		switcher.clear();
		switcher.setup();
		if (newKeyboardLocale != null) {
			this.keyboardLocale = newKeyboardLocale;
		} else {
			this.keyboardLocale = Language.English_US.getLocaleGWT();
		}

		clear();
		buildGUI(hasKeyboard.getInputBoxType());
	}

	/**
	 * rebuilds the keyboard layout based on the inputbox type
	 */
	public void clearAndUpdate() {
		switcher.clear();
		switcher.setup();
		clear();
		buildGUI(hasKeyboard.getInputBoxType());
	}

	/**
	 * (Re)build the UI.
	 */
	public void buildGUI(InputBoxType inputBoxType) {
		if (hasKeyboard.getKeyboardType().equals(AppKeyboardType.SCIENTIFIC)) {
			buildGUIScientific();
		} else {
			buildGUIGgb(inputBoxType);
		}
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

	/**
	 * @param keyboardType
	 *            keyboard type
	 */
	public void selectTab(KeyboardType keyboardType) {
		switcher.select(keyboardType);
	}

	/**
	 * Get keyboard panel.
	 *
	 * @param keyboardType type of the keyboard
	 * @return panel
	 */
	public Widget getKeyboard(KeyboardType keyboardType) {
		return keyboardMap.get(keyboardType);
	}

	/**
	 * check the minimum width. Either width of ABC panel or 123 panel. 70 =
	 * width of button; 82 = padding
	 *
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
	public void hideKeyboards() {
		for (int i = 0; i < tabs.getWidgetCount(); i++) {
			tabs.getWidget(i).setVisible(false);
		}
	}

	/**
	 * Stop editing.
	 */
	public void endEditing() {
		if (processField != null) {
			processField.endEditing();
		}
	}

	/**
	 * @param field
	 *            editor listening to KB events
	 */
	public void setProcessing(KeyboardListener field) {
		if (processField != null && processField.getField() != null) {
			if (field == null || processField.getField() != field.getField()) {
				endEditing();
			}
		}
		this.processField = field;
		updateKeyboard();
	}

	private void updateKeyboard() {
		if (processField == null || ansSwitcher == null) {
			return;
		}
		boolean requestsAns = processField.requestsAns();
		ansSwitcher.setVisible(requestsAns);
		defaultSwitcher.setVisible(!requestsAns);
		if (requestsAns && switcher.isSelected(defaultSwitcher)) {
			setSelected(ansSwitcher, true);
			setSelected(defaultSwitcher, false);
		} else if (!requestsAns && switcher.isSelected(ansSwitcher)) {
			setSelected(ansSwitcher, false);
			setSelected(defaultSwitcher, true);
		}
	}

	private void setSelected(KeyboardSwitcher.SwitcherButton btn, boolean selected) {
		btn.getKeyboard().setVisible(selected);
		switcher.setSelected(btn, selected);
	}

	@Override
	public void onClick(KeyBoardButtonBase btn, PointerEventType type) {
		if (processField == null) {
			return;
		}
		if (btn instanceof KeyBoardButtonFunctionalBase
				&& ((KeyBoardButtonFunctionalBase) btn).getAction() != null) {
			KeyBoardButtonFunctionalBase button = (KeyBoardButtonFunctionalBase) btn;

			if (Accents.isAccent(btn.getFeedback())) {
				processAccent(btn.getFeedback());
			} else {
				process(button.getAction());
			}
		} else {
			String text = btn.getFeedback();

			// translate commands and functions as appropriate
			if ("Integral".equals(text) || "Derivative".equals(text)) {
				if (hasKeyboard.attachedToEqEditor()) {
					text = "Integral".equals(text) ? String.valueOf(Unicode.INTEGRAL)
							: "d/dx";
				} else {
					text = hasKeyboard.getLocalization().getCommand(text);
				}
			} else
			// matches sin, cos, tan, asin, acos, atan
			if ((text.length() == 3 || text.length() == 4)
					&& "asin acos atan".indexOf(text) > -1) {
				text = hasKeyboard.getLocalization().getFunction(text);
			}

			processField.insertString(text);
			processAccent(null);
			disableCapsLock();

			processField.setFocus(true);
		}
		if (Action.SWITCH_TO_123.name().equals(btn.getSecondaryAction())) {
			selectTab(KeyboardType.NUMBERS);
		}

		Scheduler.get().scheduleDeferred(this::scrollCursorIntoView);
	}

	private void process(Action action) {
		switch (action) {
		case CAPS_LOCK:
			processShift();
			break;
		case BACKSPACE_DELETE:
		case LEFT_CURSOR:
		case RIGHT_CURSOR:
		case UP_CURSOR:
		case DOWN_CURSOR:
			startRepeater(action);
			break;
		case RETURN_ENTER:
			// make sure enter is processed correctly
			processField.onEnter();
			if (processField.resetAfterEnter()) {
				getUpdateKeyBoardListener().keyBoardNeeded(false, null);
			}
			break;
		case SWITCH_TO_SPECIAL_SYMBOLS:
			selectTab(KeyboardType.SPECIAL);
			break;
		case SWITCH_TO_GREEK_CHARACTERS:
			selectTab(KeyboardType.GREEK);
			break;
		case SWITCH_TO_ABC:
			selectTab(getSwitchToAbcSource());
			break;
		case ANS:
			processField.ansPressed();
		}
	}

	private KeyboardType getSwitchToAbcSource() {
		if (locale.isLatinKeyboard()) {
			return KeyboardType.ABC;
		} else {
			return KeyboardType.LATIN;
		}
	}

	private void startRepeater(Action action) {
		repeater = new ButtonRepeater(action, this);
		repeater.start();
	}

	/**
	 * Execute action immediately without repeating
	 * 
	 * @param action
	 *            key action
	 */
	public void executeOnce(Action action) {
		switch (action) {
		case BACKSPACE_DELETE:
			processField.onBackSpace();
			break;
		case LEFT_CURSOR:
			processField.onArrow(KeyboardListener.ArrowType.left);
			break;
		case RIGHT_CURSOR:
			processField.onArrow(KeyboardListener.ArrowType.right);
			break;
		case UP_CURSOR:
			processField.onArrow(KeyboardListener.ArrowType.up);
			break;
		case DOWN_CURSOR:
			processField.onArrow(KeyboardListener.ArrowType.down);
			break;
		}
	}

	/**
	 * Handle ANS key
	 */
	protected void ansPressed() {
		// platform dependent
	}

	/**
	 * Scroll cursor of selected textfield into view
	 */
	protected void scrollCursorIntoView() {
		processField.scrollCursorIntoView();
	}

	/**
	 * Make the keyboard visible.
	 */
	public void show() {
		setVisible(true);
	}

	@Override
	public void buttonPressEnded() {
		if (repeater != null) {
			repeater.cancel();
		}
	}

	/**
	 * show 3dot button on keyboard
	 */
	public void showMoreButton() {
		switcher.showMoreButton();
	}

	/**
	 * hide 3dot button on keyboard
	 */
	public void hideMoreButton() {
		switcher.hideMoreButton();
	}
}