package com.himamis.retex.editor.share.controller;

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
	 */
	public static void passArgument(EditorState editorState, MathContainer container) {
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

				// if previous sequence argument is, function pass it
			} else if (currentField
					.getArgument(currentOffset - 1) instanceof MathFunction) {

				MathFunction function = (MathFunction) currentField
						.getArgument(currentOffset - 1);
				currentField.delArgument(currentOffset - 1);
				currentOffset--;
				field.addArgument(0, function);

				// otherwise pass character sequence
			} else {

				passCharacters(editorState, container);
				currentOffset = editorState.getCurrentOffset();
			}
		}
		editorState.setCurrentOffset(currentOffset);
	}

	private static void passCharacters(EditorState editorState, MathContainer container) {
		int currentOffset = editorState.getCurrentOffset();
		MathSequence currentField = editorState.getCurrentField();
		// get pass to argument
		MathSequence field = (MathSequence) container
				.getArgument(container.getInsertIndex());

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
		editorState.setCurrentOffset(currentOffset);
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
		if (currentField.getArgument(offset) instanceof MathCharacter
				&& ((MathCharacter) currentField.getArgument(offset))
						.isOperator()) {
			return "";
		}
		while (offset > 0 && currentField
				.getArgument(offset - 1) instanceof MathCharacter) {

			MathCharacter character = (MathCharacter) currentField
					.getArgument(offset - 1);
			if (character.isSymbol() || character.isWordBreak()) {
				break;
			}
			offset--;
			stringBuilder.insert(0, character.getName());
		}
		return stringBuilder.toString();
	}
}
