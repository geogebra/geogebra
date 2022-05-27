package com.himamis.retex.editor.share.controller;

import java.util.ArrayList;

import com.himamis.retex.editor.share.meta.Tag;
import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathContainer;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathSequence;

public class RemoveContainer {

	/**
	 * Backspace to remove container
	 * @param editorState the state
	 */
	public static void withBackspace(EditorState editorState) {
		MathSequence currentField = editorState.getCurrentField();
		MathContainer parent = currentField.getParent();
		if (MathArray.isLocked(parent)) {
			return;
		}

		// if parent is function (cursor is at the beginning of the field)
		if (parent instanceof MathFunction) {
			removeFunction(currentField, editorState);
		} else if (isParentEmptyArray(currentField)) {
			deleteContainer(editorState, parent, ((MathArray) parent).getArgument(0));
		} else if (is1DArrayWithCursorInIt(currentField.getParent(),
				currentField.getParentIndex())) {
			deleteFromRowSequence(editorState);
		}
	}

	private static void removeFunction(MathSequence currentField, EditorState editorState) {
		MathFunction function = (MathFunction) currentField.getParent();

		// fraction has operator like behavior
		if (Tag.FRAC == function.getName()) {

			// if second operand is empty sequence
			if (currentField.getParentIndex() == 1
					&& currentField.size() == 0) {
				int size = function.getArgument(0).size();
				deleteContainer(editorState, function, function.getArgument(0));
				// move after included characters
				editorState.addCurrentOffset(size);
				// if first operand is empty sequence
			} else if (currentField.getParentIndex() == 1
					&& function.getArgument(0).size() == 0) {
				deleteContainer(editorState, function, currentField);
			}

		} else if (isGeneral(function.getName())) {
			if (currentField.getParentIndex() == function.getInsertIndex()) {
				deleteContainer(editorState, function, currentField);
			}
			// not a fraction, and cursor is right after the sign
		} else {
			if (currentField.getParentIndex() == 1) {
				removeParenthesesOfFunction(function, editorState);
			} else if (currentField.getParentIndex() > 1) {
				MathSequence prev = function
						.getArgument(currentField.getParentIndex() - 1);
				int len = prev.size();
				for (int i = 0; i < currentField.size(); i++) {
					prev.addArgument(currentField.getArgument(i));
				}
				function.removeArgument(currentField.getParentIndex());
				editorState.setCurrentField(prev);
				editorState.setCurrentOffset(len);
			} else if (CursorController.prevCharacter(editorState)) {
				new InputController(editorState.getMetaModel())
						.bkspCharacter(editorState);
				fuseMathFunction(editorState, function);
			}
		}
	}

	/**
	 * Fuses math function with previous characters
	 * @param editorState editor state
	 * @param mathFunction function
	 */
	public static void fuseMathFunction(EditorState editorState, MathFunction mathFunction) {
		ArrayList<MathCharacter> components = new ArrayList<>();
		MathSequence currentField = editorState.getCurrentField();
		int currentOffset = editorState.getCurrentOffset() - 1;
		while (currentOffset >= 0 && currentField.getArgument(
				currentOffset) instanceof MathCharacter) {
			MathCharacter character = (MathCharacter) currentField.getArgument(currentOffset);
			currentField.removeArgument(currentOffset);
			currentOffset -= 1;
			components.add(character);
		}
		if (components.isEmpty()) {
			return;
		}
		MathSequence name = mathFunction.getArgument(0);
		for (MathCharacter character : components) {
			name.addArgument(0, character);
		}
		editorState.setCurrentField(name);
		editorState.setCurrentOffset(components.size());
	}

	private static boolean isGeneral(Tag name) {
		return name != Tag.APPLY && name != Tag.APPLY_SQUARE;
	}

	private static boolean isParentEmptyArray(MathSequence currentField) {
		return isParentAnArray(currentField)
				&& currentField.getParent().size() == 1;
	}

	/**
	 * @param sequence MathSequence
	 * @return if sequence is an array
	 */
	static boolean isParentAnArray(MathSequence sequence) {
		return sequence.getParent() instanceof MathArray;
	}

	// if parent is 1DArray or Vector and cursor is at the beginning of
	// intermediate the field
	private static boolean is1DArrayWithCursorInIt(MathContainer container, int parentIndex) {
		if (!(container instanceof MathArray)) {
			return false;
		}
		MathArray array = (MathArray) container;
		return (array.is1DArray() || array.isVector())
				&& parentIndex > 0
				&& !MathArray.isLocked(array);
	}

	private static void deleteFromRowSequence(EditorState editorState) {
		MathSequence currentField = editorState.getCurrentField();
		int index = currentField.getParentIndex();
		MathArray parent = (MathArray) currentField.getParent();
		MathSequence field = parent.getArgument(index - 1);
		int size = field.size();
		editorState.setCurrentOffset(0);
		while (currentField.size() > 0) {

			MathComponent component = currentField.getArgument(0);
			currentField.delArgument(0);
			field.addArgument(field.size(), component);
		}
		parent.delArgument(index);
		editorState.setCurrentField(field);
		editorState.setCurrentOffset(size);
	}

	private static void removeParenthesesOfFunction(MathFunction function,
			EditorState editorState) {
		MathSequence functionName = function.getArgument(0);
		int offset = function.getParentIndex() + functionName.size();
		deleteContainer(editorState, function, functionName);
		editorState.setCurrentOffset(offset);
	}

	/**
	 * Deletes the current field
	 * @param editorState editor state
	 */
	public static void deleteContainer(EditorState editorState) {
		MathSequence currentField = editorState.getCurrentField();

		// if parent is function (cursor is at the end of the field)
		if (currentField.getParent() instanceof MathFunction) {
			MathFunction parent = (MathFunction) currentField.getParent();

			// fraction has operator like behavior
			if (Tag.FRAC.equals(parent.getName())) {

				// first operand is current, second operand is empty sequence
				if (currentField.getParentIndex() == 0
						&& parent.getArgument(1).size() == 0) {
					int size = parent.getArgument(0).size();
					deleteContainer(editorState, parent, currentField);
					// move after included characters
					editorState.addCurrentOffset(size);

					// first operand is current, and first operand is empty
					// sequence
				} else if (currentField.getParentIndex() == 0
						&& currentField.size() == 0) {
					deleteContainer(editorState, parent, parent.getArgument(1));
				}
			}

			// if parent are empty braces
		} else if (isParentAnArray(currentField)
				&& currentField.getParent().size() == 1
				&& currentField.size() == 0) {
			MathArray parent = (MathArray) currentField.getParent();
			int size = parent.getArgument(0).size();
			deleteContainer(editorState, parent, parent.getArgument(0));
			// move after included characters
			editorState.addCurrentOffset(size);

			// if parent is 1DArray or Vector and cursor is at the end of the
			// field
		} else if (isParentAnArray(currentField)
				&& (((MathArray) currentField.getParent()).is1DArray()
				|| ((MathArray) currentField.getParent()).isVector())
				&& currentField.getParentIndex() + 1 < currentField.getParent()
				.size()) {

			int index = currentField.getParentIndex();
			MathArray parent = (MathArray) currentField.getParent();
			MathSequence field = parent.getArgument(index + 1);
			int size = currentField.size();
			while (currentField.size() > 0) {

				MathComponent component = currentField.getArgument(0);
				currentField.delArgument(0);
				field.addArgument(field.size(), component);
			}
			parent.delArgument(index);
			editorState.setCurrentField(field);
			editorState.setCurrentOffset(size);
		}
	}

	private static void deleteContainer(EditorState editorState,
			MathContainer container, MathSequence operand) {
		if (container.getParent() instanceof MathSequence) {
			// when parent is sequence
			MathSequence parent = (MathSequence) container.getParent();
			int offset = container.getParentIndex();
			// delete container
			parent.delArgument(offset);
			// add content of operand
			while (operand.size() > 0) {
				int lastArgumentIndex = operand.size() - 1;
				MathComponent element = operand.getArgument(lastArgumentIndex);
				operand.delArgument(lastArgumentIndex);
				parent.addArgument(offset, element);
			}
			editorState.setCurrentField(parent);
			editorState.setCurrentOffset(offset);
		}
	}
}
