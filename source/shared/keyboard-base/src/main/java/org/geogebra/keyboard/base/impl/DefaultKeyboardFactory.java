package org.geogebra.keyboard.base.impl;

import java.util.Map;
import java.util.function.Supplier;

import org.geogebra.keyboard.base.Keyboard;
import org.geogebra.keyboard.base.KeyboardFactory;
import org.geogebra.keyboard.base.KeyboardType;
import org.geogebra.keyboard.base.model.KeyModifier;
import org.geogebra.keyboard.base.model.KeyboardModel;
import org.geogebra.keyboard.base.model.KeyboardModelFactory;
import org.geogebra.keyboard.base.model.impl.AccentModifier;
import org.geogebra.keyboard.base.model.impl.CapsLockModifier;
import org.geogebra.keyboard.base.model.impl.factory.ButtonFactory;
import org.geogebra.keyboard.base.model.impl.factory.CharacterProvider;
import org.geogebra.keyboard.base.model.impl.factory.DefaultCharProvider;
import org.geogebra.keyboard.base.model.impl.factory.DefaultKeyboardModelFactory;
import org.geogebra.keyboard.base.model.impl.factory.FunctionKeyboardFactory;
import org.geogebra.keyboard.base.model.impl.factory.GreekKeyboardFactory;
import org.geogebra.keyboard.base.model.impl.factory.LetterKeyboardFactory;
import org.geogebra.keyboard.base.model.impl.factory.MathKeyboardFactory;
import org.geogebra.keyboard.base.model.impl.factory.SpecialSymbolsKeyboardFactory;

public class DefaultKeyboardFactory implements KeyboardFactory {
	private ButtonFactory defaultButtonFactory = new ButtonFactory(null);
	protected KeyboardModelFactory mathKeyboardFactory;
	protected KeyboardModelFactory defaultKeyboardModelFactory;
	protected KeyboardModelFactory greekKeyboardFactory;
	protected KeyboardModelFactory functionKeyboardFactory;
	protected LetterKeyboardFactory letterKeyboardFactory;
	protected KeyboardModelFactory specialSymbolsKeyboardFactory;

	/**
	 * Creates a CommonKeyboardFactory with default implementations
	 * for keyboard model factories.
	 * @param hasRealschuleTemplateFeature - see PreviewFeature.REALSCHULE_TEMPLATES
	 */
	public DefaultKeyboardFactory(boolean hasRealschuleTemplateFeature) {
		this(new DefaultCharProvider(), hasRealschuleTemplateFeature);
	}

	/**
	 * Creates a CommonKeyboardFactory with default implementations
	 * for keyboard model factories.
	 */
	public DefaultKeyboardFactory() {
		this(new DefaultCharProvider(), false);
	}

	/**
	 * Creates a CommonKeyboardFactory with default implementations
	 * for keyboard model factories.
	 * @param characterProvider character provider
	 */
	public DefaultKeyboardFactory(CharacterProvider characterProvider) {
		this(characterProvider, false);
	}

	/**
	 * Creates a CommonKeyboardFactory with default implementations
	 * for keyboard model factories.
	 * @param characterProvider character provider
	 * @param hasRealschuleTemplateFeature realschule templates feature
	 */
	public DefaultKeyboardFactory(CharacterProvider characterProvider,
			boolean hasRealschuleTemplateFeature) {
		defaultKeyboardModelFactory = new DefaultKeyboardModelFactory(characterProvider);
		mathKeyboardFactory = new MathKeyboardFactory(characterProvider);
		functionKeyboardFactory = new FunctionKeyboardFactory();
		letterKeyboardFactory = new LetterKeyboardFactory();
		greekKeyboardFactory = new GreekKeyboardFactory();
		specialSymbolsKeyboardFactory = new SpecialSymbolsKeyboardFactory(
				hasRealschuleTemplateFeature);
	}

	/**
	 * @param modelFactory model factory
	 * @param type the keyboard type (ABC, numeric, ...)
	 * @return default implementation
	 */
	private Keyboard getImpl(KeyboardModelFactory modelFactory, KeyboardType type) {
		return new KeyboardImpl(
				type,
				() -> modelFactory.createKeyboardModel(defaultButtonFactory), null,
				null);
	}

	/**
	 * Creates a math keyboard with numbers and operators.
	 * @return math keyboard
	 */
	@Override
	public Keyboard createMathKeyboard() {
		return getImpl(mathKeyboardFactory, KeyboardType.NUMBERS);
	}

	/**
	 * Creates a math keyboard with numbers and operators and without ANS button.
	 * @return math keyboard without ANS
	 */
	@Override
	public Keyboard createDefaultKeyboard() {
		return getImpl(defaultKeyboardModelFactory, KeyboardType.NUMBERS_DEFAULT);
	}

	/**
	 * Creates a function keyboard with the function buttons.
	 * @return function keyboard
	 */
	@Override
	public Keyboard createFunctionsKeyboard() {
		return getImpl(functionKeyboardFactory, KeyboardType.OPERATORS);
	}

	/**
	 * Creates a greek keyboard with the greek letters and control buttons.
	 * @return greek keyboard
	 */
	@Override
	public Keyboard createGreekKeyboard() {
		AccentModifier accentModifier = new AccentModifier();
		CapsLockModifier capsLockModifier = new CapsLockModifier();
		ButtonFactory buttonFactory = new ButtonFactory(
				new KeyModifier[]{accentModifier, capsLockModifier});
		Supplier<KeyboardModel> model = () -> greekKeyboardFactory
				.createKeyboardModel(buttonFactory);
		return new KeyboardImpl(KeyboardType.GREEK, model, capsLockModifier, accentModifier);
	}

	/**
	 * Creates a letter (or ABC) keyboard with letters on it. There is a
	 * restriction on the row definitions that are passed as a String, namely he
	 * bottom row has to be shorter than the top or middle row. If the
	 * restrictions are not met, a {@link RuntimeException} is thrown.
	 * @param topRow a list of characters that will be the buttons of the top row
	 * @param middleRow a list of characters that will the buttons of the middle row
	 * @param bottomRow a list of characters that will be the buttons of the last row
	 * @param upperKeys a map relating each character from the rows to an uppercase
	 * character.
	 * @param withGreekSwitch if switch to greek layout should be included
	 * @return letter keyboard
	 */
	@Override
	public Keyboard createLettersKeyboard(String topRow, String middleRow, String bottomRow,
			Map<String, String> upperKeys, boolean withGreekSwitch) {
		AccentModifier accentModifier = new AccentModifier();
		CapsLockModifier capsLockModifier = new CapsLockModifier(upperKeys);
		Supplier<KeyboardModel> model = () -> {
			ButtonFactory buttonFactory = new ButtonFactory(
					new KeyModifier[]{accentModifier, capsLockModifier});
			letterKeyboardFactory.setUpperKeys(upperKeys);
			letterKeyboardFactory.setKeyboardDefinition(topRow, middleRow, bottomRow,
					withGreekSwitch);
			return letterKeyboardFactory.createKeyboardModel(buttonFactory);
		};
		return new KeyboardImpl(KeyboardType.ABC, model, capsLockModifier, accentModifier);
	}

	/**
	 * Calls {@link #createLettersKeyboard(String, String, String, Map, boolean)} with true to
	 * include greek keyboard.
	 * @param topRow a list of characters that will be the buttons of the top row
	 * @param middleRow a list of characters that will the buttons of the middle row
	 * @param bottomRow a list of characters that will be the buttons of the last row
	 * @param upperKeys a map relating each character from the rows to an uppercase
	 * character.
	 * @return letter keyboard
	 */
	@Override
	public Keyboard createLettersKeyboard(String topRow, String middleRow,
			String bottomRow, Map<String, String> upperKeys) {
		return createLettersKeyboard(topRow, middleRow, bottomRow, upperKeys, true);
	}

	/**
	 * Calls {@link #createLettersKeyboard(String, String, String, Map)} with a
	 * null upper keys. In this case {@link Character#toUpperCase(char)} is
	 * used.
	 * @param topRow a list of characters that will be the buttons of the top row
	 * @param middleRow a list of characters that will the buttons of the middle row
	 * @param bottomRow a list of characters that will be the buttons of the last row
	 * @return letter keyboard
	 */
	@Override
	public Keyboard createLettersKeyboard(String topRow, String middleRow,
			String bottomRow) {
		return createLettersKeyboard(topRow, middleRow, bottomRow, null);
	}

	/**
	 * Creates a special symbols keyboard with symbols control buttons, and a
	 * button to switch to the letters keyboard.
	 * @return special symbols keyboard
	 */
	@Override
	public Keyboard createSpecialSymbolsKeyboard() {
		return getImpl(specialSymbolsKeyboardFactory, KeyboardType.SPECIAL);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		return o != null && getClass() == o.getClass();
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
