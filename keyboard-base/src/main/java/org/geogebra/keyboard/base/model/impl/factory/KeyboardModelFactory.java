package org.geogebra.keyboard.base.model.impl.factory;

import org.geogebra.keyboard.base.model.KeyboardModel;

/**
 * This class can create {@link KeyboardModel}s of different types. It is not
 * thread safe.
 */
public class KeyboardModelFactory {

	private MathKeyboardFactory mathKeyboardFactory;
	private GreekKeyboardFactory greekKeyboardFactory;
	private FunctionKeyboardFactory functionKeyboardFactory;
	private LetterKeyboardFactory letterKeyboardFactory;
	private SpecialSymbolsKeyboardFactory specialSymbolsKeyboardFactory;

	/**
	 * @param buttonFactory
	 *            button factory
	 * @return math keyboard model
	 */
	public KeyboardModel createMathKeyboard(ButtonFactory buttonFactory) {
		if (mathKeyboardFactory == null) {
			mathKeyboardFactory = new MathKeyboardFactory();
		}
		return mathKeyboardFactory.createMathKeyboard(buttonFactory);
	}

	/**
	 * @param buttonFactory
	 *            button factory
	 * @return greek keyboard model
	 */
	public KeyboardModel createGreekKeyboard(ButtonFactory buttonFactory) {
		if (greekKeyboardFactory == null) {
			greekKeyboardFactory = new GreekKeyboardFactory();
		}
		return greekKeyboardFactory.createGreekKeyboard(buttonFactory);
	}

	/**
	 * @param buttonFactory
	 *            button factory
	 * @return function keyboard model
	 */
	public KeyboardModel createFunctionKeyboard(ButtonFactory buttonFactory) {
		if (functionKeyboardFactory == null) {
			functionKeyboardFactory = new FunctionKeyboardFactory();
		}
		return functionKeyboardFactory.createFunctionKeyboard(buttonFactory);
	}

	/**
	 * @param buttonFactory
	 *            button factory
	 * @param topRow
	 *            top row definition
	 * @param middleRow
	 *            middle row defintion
	 * @param bottomRow
	 *            bottom row definition
	 * @return localized ABC keyboard model
	 */
	public KeyboardModel createLetterKeyboard(ButtonFactory buttonFactory,
			String topRow, String middleRow, String bottomRow) {
		if (letterKeyboardFactory == null) {
			letterKeyboardFactory = new LetterKeyboardFactory();
		}
		return letterKeyboardFactory.createLetterKeyboard(buttonFactory, topRow,
				middleRow, bottomRow);
	}

	/**
	 * @param buttonFactory
	 *            button factory
	 * @return symbol keyboard model
	 */
	public KeyboardModel createSpecialSymbolsKeyboard(
			ButtonFactory buttonFactory) {
		if (specialSymbolsKeyboardFactory == null) {
			specialSymbolsKeyboardFactory = new SpecialSymbolsKeyboardFactory();
		}
		return specialSymbolsKeyboardFactory
				.createSpecialSymbolsKeyboard(buttonFactory);
	}
}
