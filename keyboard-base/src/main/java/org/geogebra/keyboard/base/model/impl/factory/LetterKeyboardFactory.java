package org.geogebra.keyboard.base.model.impl.factory;

import static org.geogebra.keyboard.base.model.impl.factory.Characters.ALPHA;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.BETA;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.GAMMA;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addConstantCustomButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addCustomButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addInputButton;

import org.geogebra.keyboard.base.Accents;
import org.geogebra.keyboard.base.Action;
import org.geogebra.keyboard.base.Background;
import org.geogebra.keyboard.base.Resource;
import org.geogebra.keyboard.base.model.KeyboardModel;
import org.geogebra.keyboard.base.model.KeyboardModelFactory;
import org.geogebra.keyboard.base.model.impl.KeyboardModelImpl;
import org.geogebra.keyboard.base.model.impl.RowImpl;

public class LetterKeyboardFactory implements KeyboardModelFactory {

	/** Action that switches to another keyboard */
	public static final int ACTION_GREEK_LETTERS = 0;
	/** Action that turns caps lock on */
	public static final int ACTION_SHIFT = 1;
	/** Action that switches back to the ABC keyboard */
	public static final int ACTION_ABC_LETTERS = 2;

	protected static final String DEFAULT_CONTROL = ",'";
	private static final double MIN_PADDING_WEIGHT = 1.e-4;
	private static final float LARGE_ACTION_WEIGHT = 1.2f;
	private static final int MAX_CONTROL_ROW_LENGTH = 5;
	private static final String EXCEPTION_MESSAGE = "Wrong keyboard definition: too long row 3.";
	private String topRow;
	private String middleRow;
	private String bottomRow;
	private String controlRow;
	private Integer bottomActionLeft = ACTION_SHIFT;
	private Integer controlActionLeft = ACTION_GREEK_LETTERS;

	/**
	 * Calls
	 * {@link LetterKeyboardFactory#setKeyboardDefinition(String, String, String, boolean)}
	 * with parameter {@code withGreekLetters = true}.
	 */
	public void setKeyboardDefinition(String topRow, String middleRow, String bottomRow) {
		setKeyboardDefinition(topRow, middleRow, bottomRow, true);
	}

	/**
	 * Calls
	 * {@link LetterKeyboardFactory#setKeyboardDefinition(String, String, String, String, boolean)}
	 * with parameter {@code controlRow = ",'"}.
	 */
	public void setKeyboardDefinition(String topRow, String middleRow, String bottomRow, boolean
			withGreekLetters) {
		setKeyboardDefinition(topRow, middleRow, bottomRow, DEFAULT_CONTROL, withGreekLetters);
	}

	/**
	 * Sets the definition for the letters
	 *
	 * @param topRow a string containing the characters for the top row
	 * @param middleRow a string containing the characters for the middle row
	 * @param bottomRow a string containing the characters for the bottom row
	 * @param withGreekLetters true iff greek letters should be included
	 */
	public void setKeyboardDefinition(String topRow, String middleRow, String bottomRow, String
			controlRow, boolean withGreekLetters) {
		Integer controlAction = withGreekLetters ? ACTION_GREEK_LETTERS : null;
		setKeyboardDefinition(topRow, middleRow, bottomRow, controlRow,
				ACTION_SHIFT, controlAction);
	}

	/**
	 * Sets the definition for the created keyboard model.
	 *
	 * @param topRow a string containing the characters for the top row
	 * @param middleRow a string containing the characters for the middle row
	 * @param bottomRow a string containing the characters for the bottom row
	 * @param controlRow a string containing the characters for the control row
	 * @param bottomActionLeft constant action that appears in the bottom row
	 * @param controlActionLeft constant action that appears in the control row
	 */
	public void setKeyboardDefinition(String topRow, String middleRow, String bottomRow, String
			controlRow, Integer bottomActionLeft, Integer controlActionLeft) {
		this.topRow = topRow;
		this.middleRow = middleRow;
		this.bottomRow = bottomRow;
		this.controlRow = controlRow;
		this.bottomActionLeft = bottomActionLeft;
		this.controlActionLeft = controlActionLeft;
	}

	@Override
	public KeyboardModel createKeyboardModel(ButtonFactory buttonFactory) {
		int topRowLength = topRow.length();
		int middleRowLength = middleRow.length();
		int bottomRowLength = bottomRow.length();
		int controlRowLength = controlRow.length();
		int bottomSpecialLength = bottomActionLeft == null ? 1 : 2;

		// sanity checks
		if (bottomRowLength > topRowLength && bottomRowLength > middleRowLength) {
			throw new RuntimeException(EXCEPTION_MESSAGE);
		}
		if (controlRowLength > MAX_CONTROL_ROW_LENGTH) {
			throw new RuntimeException("Control row too long");
		}

		int rowWeightSum = Math.max(topRowLength, middleRowLength);
		rowWeightSum = Math.max(rowWeightSum, bottomRowLength + bottomSpecialLength);

		float topRowPadding = (rowWeightSum - topRowLength) / 2.0f;
		float middleRowPadding = (rowWeightSum - middleRowLength) / 2.0f;

		float actionButtonSize;
		float actionButtonMargin;
		if (rowWeightSum - bottomRowLength - bottomSpecialLength == 0) {
			actionButtonSize = 1.0f;
			actionButtonMargin = 0.0f;
		} else if (rowWeightSum - bottomRowLength - bottomSpecialLength > 0) {
			actionButtonSize = LARGE_ACTION_WEIGHT;
			actionButtonMargin = (rowWeightSum - bottomRowLength
					- bottomSpecialLength * LARGE_ACTION_WEIGHT) / bottomSpecialLength;
		} else {
			throw new RuntimeException(EXCEPTION_MESSAGE);
		}

		KeyboardModelImpl letterKeyboard = new KeyboardModelImpl();

		createRow(letterKeyboard, buttonFactory, topRow, rowWeightSum,
				topRowPadding);
		createRow(letterKeyboard, buttonFactory, middleRow, rowWeightSum,
				middleRowPadding);

		RowImpl bottomRowImpl = letterKeyboard.nextRow(rowWeightSum);

		if (bottomActionLeft != null) {
			addActionButton(bottomRowImpl, buttonFactory, bottomActionLeft, actionButtonSize);
		}
		addButton(bottomRowImpl,
				buttonFactory.createEmptySpace(actionButtonMargin));
		addButtons(bottomRowImpl, buttonFactory, bottomRow);
		addButton(bottomRowImpl,
				buttonFactory.createEmptySpace(actionButtonMargin));
		addConstantCustomButton(bottomRowImpl, buttonFactory,
				Resource.BACKSPACE_DELETE, Action.BACKSPACE_DELETE,
				actionButtonSize);

		RowImpl controlRowImpl = letterKeyboard.nextRow(rowWeightSum);
		if (controlActionLeft != null) {
			addActionButton(controlRowImpl, buttonFactory, controlActionLeft, actionButtonSize);
		}
		addControlButtons(controlRowImpl, buttonFactory, controlRow);
		// this contains left, right and return enter
		int controlSpecialLength = (controlActionLeft == null ? 0 : 1) + 3;
		float spaceSize = rowWeightSum - controlRowLength - controlSpecialLength;
		addInputButton(controlRowImpl, buttonFactory, " ", spaceSize);
		addConstantCustomButton(controlRowImpl, buttonFactory, Resource.LEFT_ARROW,
				Action.LEFT_CURSOR);
		addConstantCustomButton(controlRowImpl, buttonFactory, Resource.RIGHT_ARROW,
				Action.RIGHT_CURSOR);
		addConstantCustomButton(controlRowImpl, buttonFactory,
				Resource.RETURN_ENTER, Action.RETURN_ENTER);

		return letterKeyboard;
	}

	private void createRow(KeyboardModelImpl keyboard,
			ButtonFactory buttonFactory, String definition, float rowWeightSum,
			float rowPadding) {
		RowImpl rowImpl = keyboard.nextRow(rowWeightSum);
		addPaddingIfNecessary(rowImpl, buttonFactory, rowPadding);
		addButtons(rowImpl, buttonFactory, definition);
		addPaddingIfNecessary(rowImpl, buttonFactory, rowPadding);
	}

	private void addButtons(RowImpl rowImpl, ButtonFactory buttonFactory,
			String definition) {
		for (int i = 0; i < definition.length(); i++) {
			addButtonCharacter(rowImpl, buttonFactory, definition.charAt(i));
		}
	}

	/**
	 * add control buttons
	 * @param rowImpl row
	 * @param buttonFactory factory
	 * @param definition characters of control buttons
	 */
	public void addControlButtons(RowImpl rowImpl, ButtonFactory buttonFactory,
			String definition) {
		addButtons(rowImpl, buttonFactory, definition);
	}

	private void addActionButton(RowImpl rowImpl, ButtonFactory buttonFactory, Integer action,
			float actionButtonSize) {
		if (action == ACTION_SHIFT) {
			addConstantCustomButton(rowImpl, buttonFactory, Resource.CAPS_LOCK,
					Action.CAPS_LOCK, actionButtonSize);
		} else if (action == ACTION_GREEK_LETTERS) {
			String label = ""
					+ ALPHA
					+ BETA
					+ GAMMA;
			addCustomButton(rowImpl, buttonFactory, label, Action.SWITCH_TO_GREEK_CHARACTERS);
		} else if (action == ACTION_ABC_LETTERS) {
			addCustomButton(rowImpl, buttonFactory, "ABC", Action.SWITCH_TO_ABC);
		}
	}

	private void addButtonCharacter(RowImpl rowImpl,
			ButtonFactory buttonFactory, char character) {
		String resource = String.valueOf(character);
		switch (resource) {
		case Accents.ACCENT_ACUTE:
			addCustomButton(rowImpl, buttonFactory, resource,
					Action.TOGGLE_ACCENT_ACUTE.name(), Background.STANDARD);
			break;
		case Accents.ACCENT_CARON:
			addCustomButton(rowImpl, buttonFactory, resource,
					Action.TOGGLE_ACCENT_CARON.name(), Background.STANDARD);
			break;
		case Accents.ACCENT_CIRCUMFLEX:
			addCustomButton(rowImpl, buttonFactory, resource,
					Action.TOGGLE_ACCENT_CIRCUMFLEX.name(),
					Background.STANDARD);
			break;
		case Accents.ACCENT_GRAVE:
			addCustomButton(rowImpl, buttonFactory, resource,
					Action.TOGGLE_ACCENT_GRAVE.name(), Background.STANDARD);
			break;
		default:
			addInputButton(rowImpl, buttonFactory, resource);
		}
	}

	private void addPaddingIfNecessary(RowImpl rowImpl,
			ButtonFactory buttonFactory, float paddingWeight) {
		if (paddingWeight > MIN_PADDING_WEIGHT) {
			addButton(rowImpl, buttonFactory.createEmptySpace(paddingWeight));
		}
	}
}
