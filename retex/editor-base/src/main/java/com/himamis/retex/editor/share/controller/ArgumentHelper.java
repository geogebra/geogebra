package com.himamis.retex.editor.share.controller;

import com.himamis.retex.editor.share.meta.Tag;
import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathContainer;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathSequence;

/**
 * Library class for processing function arguments
 */
public class ArgumentHelper {

	/**
	 * Moves content from current editor field into a function argument
	 * @param editorState editor state
	 * @param container function
	 */
	public static void passArgument(EditorState editorState, MathContainer container) {
		// get pass to argument
		MathSequence field = (MathSequence) container
				.getArgument(container.getInsertIndex());
		while (editorState.getComponentLeftOfCursor() instanceof MathCharacter
				&& editorState.getComponentLeftOfCursor().toString().length() == 1
				&& Character.isWhitespace(editorState.getComponentLeftOfCursor()
					.toString().charAt(0))) {
			deleteLeftOfCursor(editorState);
		}

		// pass scripts first
		while (MathFunction.isScript(editorState.getComponentLeftOfCursor())) {
			MathFunction script = (MathFunction) editorState.getComponentLeftOfCursor();
			deleteLeftOfCursor(editorState);
			field.addArgument(0, script);
		}

		// if previous sequence arguments are braces pass their content
		MathComponent leftOfCursor = editorState.getComponentLeftOfCursor();
		if (leftOfCursor instanceof MathArray) {

			MathArray array = (MathArray) leftOfCursor;
			deleteLeftOfCursor(editorState);
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
		} else if (leftOfCursor instanceof MathFunction) {
			passFunction(editorState, field);
			// Special case for recurring decimals, here we need to also pass
			// at least 2 characters preceding the function
			if (leftOfCursor.hasTag(Tag.RECURRING_DECIMAL)) {
				passCharacters(editorState, container);
			}
			// otherwise pass character sequence
		} else {

			passCharacters(editorState, container);
		}
	}

	/**
	 * Passes a function from the current editor field into a function argument
	 * @param state editor state
	 * @param field MathSequence of where to pass the arguments into
	 */
	private static void passFunction(EditorState state, MathSequence field) {
		MathComponent function = state.getComponentLeftOfCursor();
		deleteLeftOfCursor(state);
		field.addArgument(0, function);
	}

	private static void passCharacters(EditorState editorState, MathContainer container) {

		// get pass to argument
		MathSequence field = (MathSequence) container
				.getArgument(container.getInsertIndex());

		while (editorState.getComponentLeftOfCursor() instanceof MathCharacter) {

			MathCharacter character = (MathCharacter) editorState.getComponentLeftOfCursor();
			if (character.isWordBreak()) {
				break;
			}
			deleteLeftOfCursor(editorState);
			field.addArgument(0, character);
		}
	}

	/**
	 * Used to pass a single character, needed for passing arguments for recurring decimals <br>
	 * Each overline has a single preceding character that needs to be passed
	 * @param state editor state
	 * @param field MathSequence of where to pass the arguments into
	 */
	public static void passSingleCharacter(EditorState state, MathSequence field) {
		if (state.getComponentLeftOfCursor() instanceof MathCharacter) {
			MathCharacter character = (MathCharacter) state.getComponentLeftOfCursor();
			if (character.isWordBreak()) {
				return;
			}
			deleteLeftOfCursor(state);
			field.addArgument(character);
		}
	}

	/**
	 * Reads all characters to the right of the cursor until it encounters a
	 * symbol
	 * @param editorState current editor state
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

	/**
	 * Deletes character left to cursor and move cursor left.
	 * Similar to deleteSingleArg in {@link InputController},
	 * but not concerned about selection/function merging because this one is only called
	 * when parsing.
	 */
	private static void deleteLeftOfCursor(EditorState state) {
		state.getCurrentField().delArgument(state.getCurrentOffset() - 1);
		state.decCurrentOffset();
	}
}
