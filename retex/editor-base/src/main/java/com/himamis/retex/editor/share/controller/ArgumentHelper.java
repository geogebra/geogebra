package com.himamis.retex.editor.share.controller;

import com.himamis.retex.editor.share.meta.Tag;
import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathContainer;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathSequence;

/**
 * Library class for processing function arguments
 */
public class ArgumentHelper {

	/**
	 * Moves content from current editor field into a function argument
	 * 
	 * @param editorState
	 *            editor state
	 * @param container
	 *            function
	 * @param passSingleArg Wheter or not to pass just a single argument
	 */
	public static void passArgument(EditorState editorState, MathContainer container,
			boolean passSingleArg) {
		MathSequence currentField = editorState.getCurrentField();
		int currentOffset = editorState.getCurrentOffset();
		// get pass to argument
		MathSequence field = (MathSequence) container
				.getArgument(container.getInsertIndex());
		while (currentOffset > 0
				&& currentField.getArgument(currentOffset - 1) instanceof MathCharacter
				&& " ".equals(currentField
						.getArgument(currentOffset - 1).toString())) {
			currentField.delArgument(currentOffset - 1);
			currentOffset--;
		}
		// pass scripts first
		while (currentOffset > 0 && currentField.isScript(currentOffset - 1)) {
			MathFunction script = (MathFunction) currentField
					.getArgument(currentOffset - 1);
			currentField.delArgument(currentOffset - 1);
			currentOffset--;
			field.addArgument(0, script);
		}
		editorState.setCurrentOffset(currentOffset);

		if (currentOffset > 0) {
			// if previous sequence argument are braces pass their content
			if (currentField
					.getArgument(currentOffset - 1) instanceof MathArray) {

				MathArray array = (MathArray) currentField
						.getArgument(currentOffset - 1);
				currentField.delArgument(currentOffset - 1);
				currentOffset--;
				if (field.size() == 0) {
					// here we already have sequence, just set it
					if (array.size() > 1 || array.getOpenKey() != '(') {
						MathSequence wrap = new MathSequence();
						wrap.addArgument(array);
						container.setArgument(container.getInsertIndex(), wrap);
					} else {
						container.setArgument(container.getInsertIndex(),
								array.getArgument(0));
					}
				} else {
					field.addArgument(0, array);
				}

				// if previous sequence argument is function, pass it
			} else if (currentField.getArgument(currentOffset - 1) instanceof MathFunction) {

				// Special case for recurring decimals, here we need to pass a single function and
				// at least two characters
				if (currentField.getArgument(currentOffset - 1).hasTag(Tag.RECURRING_DECIMAL)) {
					currentOffset = passFunction(currentField, currentOffset, field);
					editorState.setCurrentOffset(currentOffset);
					passCharacters(editorState, container, passSingleArg);
					currentOffset = editorState.getCurrentOffset();
				} else {
					currentOffset = passFunction(currentField, currentOffset, field);
				}

				// otherwise pass character sequence
			} else {

				passCharacters(editorState, container, passSingleArg);
				currentOffset = editorState.getCurrentOffset();
			}
		}
		editorState.setCurrentOffset(currentOffset);
	}

	/**
	 * Passes a function from the current editor field into a function argument
	 * @param currentField MathSequence
	 * @param currentOffset Current offset
	 * @param field MathSequence of where to pass the arguments into
	 * @return Current offset - 1
	 */
	private static int passFunction(MathSequence currentField, int currentOffset,
			MathSequence field) {
		MathFunction function = (MathFunction) currentField
				.getArgument(currentOffset - 1);
		currentField.delArgument(currentOffset - 1);
		field.addArgument(0, function);
		return currentOffset - 1;
	}

	private static void passCharacters(EditorState editorState, MathContainer container,
			boolean passSingleArg) {
		int currentOffset = editorState.getCurrentOffset();
		MathSequence currentField = editorState.getCurrentField();
		// get pass to argument
		MathSequence field = (MathSequence) container
				.getArgument(container.getInsertIndex());

		int offset;
		if (passSingleArg) {
			offset = passSingleCharacter(currentField, currentOffset, field);
		} else {
			offset = passCharacters(currentField, currentOffset, field);
		}
		editorState.setCurrentOffset(offset);
	}

	private static int passCharacters(MathSequence currentField, int initialOffset,
			MathSequence field) {
		int currentOffset = initialOffset;
		while (currentOffset > 0 && currentField
				.getArgument(currentOffset - 1) instanceof MathCharacter) {

			MathCharacter character = (MathCharacter) currentField
					.getArgument(currentOffset - 1);
			if (character.isWordBreak()) {
				break;
			}
			currentField.delArgument(currentOffset - 1);
			currentOffset--;
			field.addArgument(0, character);
		}
		return currentOffset;
	}

	/**
	 * Used to pass a single character, needed for passing arguments for recurring decimals <br>
	 * Each overline has a single preceding character that needs to be passed
	 * @param currentField MathSequence which's arguments are to be passed
	 * @param currentOffset Current Offset
	 * @param field MathSequence of where to pass the arguments into
	 * @param condition MathCharacter::isWordBreak
	 * @return Initial Offset or Initial Offset - 1 (passing a maximum of one argument here)
	 */
	private static int passSingleCharacter(MathSequence currentField, int currentOffset,
			MathSequence field) {
		if (currentField.getArgument(currentOffset - 1) instanceof MathCharacter) {

			MathCharacter character = (MathCharacter) currentField
					.getArgument(currentOffset - 1);
			if (character.isWordBreak()) {
				return currentOffset;
			}
			currentField.delArgument(currentOffset - 1);
			field.addArgument(character);
		}
		return currentOffset - 1;
	}

	/**
	 * Reads all characters to the right of the cursor until it encounters a
	 * symbol
	 * 
	 * @param editorState
	 *            current editor state
	 * @return last string of characters
	 */
	public static String readCharacters(EditorState editorState,
			int initialOffset) {
		StringBuilder stringBuilder = new StringBuilder();
		int offset = initialOffset;
		MathSequence currentField = editorState.getCurrentField();
		while (offset > 0 && currentField
				.getArgument(offset - 1) instanceof MathCharacter) {

			MathCharacter character = (MathCharacter) currentField
					.getArgument(offset - 1);
			if (character.isWordBreak()) {
				break;
			}
			offset--;
			stringBuilder.insert(0, character.getUnicodeString());
		}
		return stringBuilder.toString();
	}
}
