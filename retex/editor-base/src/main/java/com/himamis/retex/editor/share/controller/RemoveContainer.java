package com.himamis.retex.editor.share.controller;

import com.himamis.retex.editor.share.meta.Tag;
import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathContainer;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathSequence;

public class RemoveContainer {

	private MathSequence currentField;
	private EditorState editorState;

	/**
	 * Backspace to remove container
	 *
	 * @param editorState the state
	 */
	public void withBackspace(EditorState editorState) {
		setEditorState(editorState);
		MathContainer parent = currentField.getParent();
		if (MathArray.isLocked(parent)) {
			return;
		}

		// if parent is function (cursor is at the beginning of the field)
		if (parent instanceof MathFunction) {
			removeFunction();
		} else if (isParentEmptyArray()) {
			delContainer(this.editorState, parent, ((MathArray) parent).getArgument(0));
		} else if (is1DArrayWithCursorInIt(currentField.getParent(),
				currentField.getParentIndex())) {
			deleteFromRowSequence();
		}
	}

	private void setEditorState(EditorState editorState) {
		this.editorState = editorState;
		this.currentField = this.editorState.getCurrentField();
	}

	private void removeFunction() {
		MathFunction function = (MathFunction) currentField.getParent();

		// fraction has operator like behavior
		if (Tag.FRAC == function.getName()) {

			// if second operand is empty sequence
			if (currentField.getParentIndex() == 1
					&& currentField.size() == 0) {
				int size = function.getArgument(0).size();
				delContainer(editorState, function, function.getArgument(0));
				// move after included characters
				editorState.addCurrentOffset(size);
				// if first operand is empty sequence
			} else if (currentField.getParentIndex() == 1
					&& function.getArgument(0).size() == 0) {
				delContainer(editorState, function, currentField);
			}

		} else if (isGeneral(function.getName())) {
			if (currentField.getParentIndex() == function.getInsertIndex()) {
				delContainer(editorState, function, currentField);
			}
			// not a fraction, and cursor is right after the sign
		} else {
			if (currentField.getParentIndex() == 1) {
				removeParenthesesOfFunction(function);
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
			}
		}
	}

	private boolean isGeneral(Tag name) {
		return name != Tag.APPLY && name != Tag.APPLY_SQUARE;
	}

	private boolean isParentEmptyArray() {
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
	private boolean is1DArrayWithCursorInIt(MathContainer container, int parentIndex) {
		if (!(container instanceof MathArray)) {
			return false;
		}
		MathArray array = (MathArray) container;
		return (array.is1DArray() || array.isVector())
				&& parentIndex > 0
				&& !MathArray.isLocked(array);
	}

	private void deleteFromRowSequence() {
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

	private void removeParenthesesOfFunction(MathFunction function) {
		MathSequence functionName = function.getArgument(0);
		int offset = function.getParentIndex() + functionName.size();
		delContainer(editorState, function, functionName);
		editorState.setCurrentOffset(offset);
	}

	/**
	 * Deletes the current field
	 * @param editorState editor state
	 */
	public void delContainer(EditorState editorState) {
		setEditorState(editorState);

		// if parent is function (cursor is at the end of the field)
		if (currentField.getParent() instanceof MathFunction) {
			MathFunction parent = (MathFunction) currentField.getParent();

			// fraction has operator like behavior
			if (Tag.FRAC.equals(parent.getName())) {

				// first operand is current, second operand is empty sequence
				if (currentField.getParentIndex() == 0
						&& parent.getArgument(1).size() == 0) {
					int size = parent.getArgument(0).size();
					delContainer(editorState, parent, currentField);
					// move after included characters
					editorState.addCurrentOffset(size);

					// first operand is current, and first operand is empty
					// sequence
				} else if (currentField.getParentIndex() == 0
						&& (currentField).size() == 0) {
					delContainer(editorState, parent, parent.getArgument(1));
				}
			}

			// if parent are empty braces
		} else if (isParentAnArray(currentField)
				&& currentField.getParent().size() == 1
				&& currentField.size() == 0) {
			MathArray parent = (MathArray) currentField.getParent();
			int size = parent.getArgument(0).size();
			delContainer(editorState, parent, parent.getArgument(0));
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

	private static void delContainer(EditorState editorState,
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
