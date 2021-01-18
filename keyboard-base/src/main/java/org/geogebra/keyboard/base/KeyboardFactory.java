package org.geogebra.keyboard.base;

import java.util.Map;

import org.geogebra.keyboard.base.impl.KeyboardImpl;
import org.geogebra.keyboard.base.model.KeyModifier;
import org.geogebra.keyboard.base.model.KeyboardModel;
import org.geogebra.keyboard.base.model.KeyboardModelFactory;
import org.geogebra.keyboard.base.model.impl.AccentModifier;
import org.geogebra.keyboard.base.model.impl.CapsLockModifier;
import org.geogebra.keyboard.base.model.impl.factory.ButtonFactory;
import org.geogebra.keyboard.base.model.impl.factory.DefaultKeyboardFactory;
import org.geogebra.keyboard.base.model.impl.factory.FunctionKeyboardFactory;
import org.geogebra.keyboard.base.model.impl.factory.GreekKeyboardFactory;
import org.geogebra.keyboard.base.model.impl.factory.LetterKeyboardFactory;
import org.geogebra.keyboard.base.model.impl.factory.MathKeyboardFactory;
import org.geogebra.keyboard.base.model.impl.factory.SpecialSymbolsKeyboardFactory;

/**
 * Creates {@link Keyboard} classes.
 */
public class KeyboardFactory {

	private ButtonFactory defaultButtonFactory = new ButtonFactory(null);
	private KeyboardModelFactory mathKeyboardFactory;
	private KeyboardModelFactory defaultKeyboardFactory;
	private KeyboardModelFactory greekKeyboardFactory;
	private KeyboardModelFactory functionKeyboardFactory;
	private LetterKeyboardFactory letterKeyboardFactory;
	private KeyboardModelFactory specialSymbolsKeyboardFactory;

	/**
	 * Creates a KeyboardFactory with default implementations
	 * for keyboard model factories.
	 */
	public KeyboardFactory() {
		mathKeyboardFactory = new MathKeyboardFactory();
		defaultKeyboardFactory = new DefaultKeyboardFactory();
		greekKeyboardFactory = new GreekKeyboardFactory();
		functionKeyboardFactory = new FunctionKeyboardFactory();
		letterKeyboardFactory = new LetterKeyboardFactory();
		specialSymbolsKeyboardFactory = new SpecialSymbolsKeyboardFactory();
	}

	/**
	 * Sets the factory for the math keyboard.
	 *
	 * @param mathKeyboardFactory math keyboard factory
	 */
	public void setMathKeyboardFactory(KeyboardModelFactory mathKeyboardFactory) {
		this.mathKeyboardFactory = mathKeyboardFactory;
	}

	/**
	 * Sets the factory for the default keyboard.
	 * @param defaultKeyboardFactory - default keyboard factory
	 */
	public void setDefaultKeyboardFactory(KeyboardModelFactory defaultKeyboardFactory) {
		this.defaultKeyboardFactory = defaultKeyboardFactory;
	}

	/**
	 * Sets the factory for the greek keyboard.
	 *
	 * @param greekKeyboardFactory greek keyboard factory
	 */
	public void setGreekKeyboardFactory(KeyboardModelFactory greekKeyboardFactory) {
		this.greekKeyboardFactory = greekKeyboardFactory;
	}

	/**
	 * Sets the factory for the function keyboard.
	 *
	 * @param functionKeyboardFactory function keyboard factory
	 */
	public void setFunctionKeyboardFactory(KeyboardModelFactory functionKeyboardFactory) {
		this.functionKeyboardFactory = functionKeyboardFactory;
	}

	/**
	 * Sets the factory for the letter keyboard.
	 *
	 * @param letterKeyboardFactory letter keyboard factory
	 */
	public void setLetterKeyboardFactory(LetterKeyboardFactory letterKeyboardFactory) {
		this.letterKeyboardFactory = letterKeyboardFactory;
	}

	/**
	 * Sets the factory for the special symbols keyboard.
	 *
	 * @param specialSymbolsKeyboardFactory special symbols keyboard factory
	 */
	public void setSpecialSymbolsKeyboardFactory(
	        KeyboardModelFactory specialSymbolsKeyboardFactory) {
		this.specialSymbolsKeyboardFactory = specialSymbolsKeyboardFactory;
	}

	/**
	 * Creates a math keyboard with numbers and operators.
	 *
	 * @return math keyboard
	 */
	public Keyboard createMathKeyboard() {
		return getImpl(mathKeyboardFactory);
	}

	/**
	 * Creates a math keyboard with numbers and operators and without ANS button.
	 *
	 * @return math keyboard without ANS
	 */
	public Keyboard createDefaultKeyboard() {
		return getImpl(defaultKeyboardFactory);
	}

	/**
	 * @param modelFactory
	 *            model factory
	 * @return default implementation
	 */
	public Keyboard getImpl(KeyboardModelFactory modelFactory) {
		return new KeyboardImpl(
				modelFactory.createKeyboardModel(defaultButtonFactory), null,
				null);
	}

	/**
	 * @param modelFactory
	 *            model factory
	 * @param capsLock
	 *            capslock modifier
	 * @return keyboard
	 */
	public Keyboard getImpl(KeyboardModelFactory modelFactory,
			CapsLockModifier capsLock) {
		return new KeyboardImpl(
				modelFactory.createKeyboardModel(
						new ButtonFactory(new KeyModifier[] { capsLock })),
				capsLock, null);
	}

	/**
	 * Creates a function keyboard with the function buttons.
	 *
	 * @return function keyboard
	 */
	public Keyboard createFunctionsKeyboard() {
		return getImpl(functionKeyboardFactory);
	}

	/**
	 * Creates a greek keyboard with the greek letters and control buttons.
	 *
	 * @return greek keyboard
	 */
	public Keyboard createGreekKeyboard() {
		AccentModifier accentModifier = new AccentModifier();
		CapsLockModifier capsLockModifier = new CapsLockModifier();
		ButtonFactory buttonFactory = new ButtonFactory(
				new KeyModifier[] { accentModifier, capsLockModifier });
		KeyboardModel model = greekKeyboardFactory.createKeyboardModel(buttonFactory);
		return new KeyboardImpl(model, capsLockModifier, accentModifier);
	}

	/**
	 * Creates a letter (or ABC) keyboard with letters on it. There is a
	 * restriction on the row definitions that are passed as a String, namely he
	 * bottom row has to be shorter than the top or middle row. If the
	 * restrictions are not met, a {@link RuntimeException} is thrown.
	 *
	 * @param topRow
	 *            a list of characters that will be the buttons of the top row
	 * @param middleRow
	 *            a list of characters that will the buttons of the middle row
	 * @param bottomRow
	 *            a list of characters that will be the buttons of the last row
	 * @param upperKeys
	 *            a map relating each character from the rows to an uppercase
	 *            character.
	 * @param withGreekSwitch
	 *            if switch to greek layout should be included
	 * @return letter keyboard
	 */
	public Keyboard createLettersKeyboard(String topRow, String middleRow,
			String bottomRow, Map<String, String> upperKeys, boolean withGreekSwitch) {
		AccentModifier accentModifier = new AccentModifier();
		CapsLockModifier capsLockModifier = new CapsLockModifier(upperKeys);
		ButtonFactory buttonFactory = new ButtonFactory(
				new KeyModifier[] { accentModifier, capsLockModifier });
		letterKeyboardFactory.setKeyboardDefinition(topRow, middleRow, bottomRow, withGreekSwitch);
		KeyboardModel model = letterKeyboardFactory.createKeyboardModel(buttonFactory);
		return new KeyboardImpl(model, capsLockModifier, accentModifier);
	}

	/**
	 * Calls {@link #createLettersKeyboard(String, String, String, Map, boolean)} with true to
	 * include greek keyboard.
	 *
	 * @param topRow
	 *            a list of characters that will be the buttons of the top row
	 * @param middleRow
	 *            a list of characters that will the buttons of the middle row
	 * @param bottomRow
	 *            a list of characters that will be the buttons of the last row
	 * @param upperKeys
	 *            a map relating each character from the rows to an uppercase
	 *            character.
	 * @return letter keyboard
	 */
	public Keyboard createLettersKeyboard(String topRow, String middleRow,
			String bottomRow, Map<String, String> upperKeys) {
		return createLettersKeyboard(topRow, middleRow, bottomRow, upperKeys, true);
	}

	/**
	 * Calls {@link #createLettersKeyboard(String, String, String, Map)} with a
	 * null upper keys. In this case {@link Character#toUpperCase(char)} is
	 * used.
	 * 
	 * @param topRow
	 *            a list of characters that will be the buttons of the top row
	 * @param middleRow
	 *            a list of characters that will the buttons of the middle row
	 * @param bottomRow
	 *            a list of characters that will be the buttons of the last row
	 * @return letter keyboard
	 */
	public Keyboard createLettersKeyboard(String topRow, String middleRow,
			String bottomRow) {
		return createLettersKeyboard(topRow, middleRow, bottomRow, null);
	}

	/**
	 * Creates a special symbols keyboard with symbols control buttons, and a
	 * button to switch to the letters keyboard.
	 *
	 * @return special symbols keyboard
	 */
	public Keyboard createSpecialSymbolsKeyboard() {
		return getImpl(specialSymbolsKeyboardFactory);
	}
}
