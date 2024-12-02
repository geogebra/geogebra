package com.himamis.retex.editor.share.controller;

import com.google.j2objc.annotations.Weak;
import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathCharPlaceholder;
import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathContainer;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathSequence;

/**
 * Class to handle Ctrl-A in inputboxes
 *
 * @author laszlo
 */
public class SelectAllHandler {
	@Weak
	private final EditorState editorState;

	/**
	 *
	 * @param editorState {@link EditorState}
	 */
	public SelectAllHandler(EditorState editorState) {
		this.editorState = editorState;
	}

	/**
	 * Select all elements based on the context:
	 * Matrix elements for matrices, coordinate components for points, etc.
	 */
	public void execute() {
		MathSequence root = editorState.getRootComponent();
		if (root.isProtected()) {
			selectProtectedContent();
		} else {
			if (isCharPlaceholder(root)) {
				return;
			}
			setSelectionStart(root);
			setSelectionEnd(root);
		}
	}

	private void selectProtectedContent() {
		MathComponent first = editorState.getRootComponent().getArgument(0);
		MathComponent selectionStart = editorState.getCurrentField().getArgument(0);

		setSelectionStart(selectionStart);
		if (first instanceof MathArray) {
			MathArray array = (MathArray) first;
			if (array.isMatrix()) {
				editorState.selectUpToRootComponent();
			} else {
				selectListElement(array.getArgument(0));
			}
		} else {
			setSelectionEnd(selectionStart);
		}
	}

	private boolean isCharPlaceholder(MathComponent selectionStart) {
		return selectionStart instanceof MathCharPlaceholder
				|| (selectionStart instanceof MathFunction
		&& isCharPlaceholder(
				((MathFunction) selectionStart).getArgument(0)));
	}

	private void setSelectionStart(MathComponent component) {
		editorState.setSelectionStart(component);
	}

	private void setSelectionEnd(MathComponent component) {
		editorState.setSelectionEnd(component);
	}

	private void selectListElement(MathSequence sequence) {
		if (getCurrentField() != sequence) {
			selectAllCompositeElement(sequence);
		} else {
			MathSequence content = sequenceWithoutBrackets(sequence);
			int left = firstSeparatorOnLeft(content);
			setSelectionStart(content.getArgument(left));
			int right = firstSeparatorOnRight(content);
			setSelectionEnd(content.getArgument(right));

			if (isCharPlaceholder(editorState.getSelectionStart()) || right < left) {
				editorState.setSelectionStart(null);
			}
		}
	}

	private MathComponent getCurrentField() {
		return editorState.getCurrentField();
	}

	private MathSequence sequenceWithoutBrackets(MathSequence sequence) {
		return sequence.size() == 1 && sequence.getArgument(0) instanceof MathArray
				? ((MathArray) sequence.getArgument(0)).getArgument(0)
				: sequence;
	}

	private void selectAllCompositeElement(MathSequence sequence) {
		MathComponent field = getCurrentField();
		MathContainer parent = field.getParent();
		while (parent != sequence && parent != null) {
			field = parent;
			parent = parent.getParent();

		}
		setSelectionStart(field);
		setSelectionEnd(field);
	}

	private int firstSeparatorOnRight(MathSequence sequence) {
		int offset = editorState.getCurrentOffset();
		if (isSeparatorAt(sequence, offset)) {
			return offset - 1;
		}

		int i = offset;
		while (i < sequence.getArgumentCount() && !isSeparatorAt(sequence, i)) {
			i++;
		}

		return i - 1;
	}

	private boolean isSeparatorAt(MathSequence sequence, int index) {
		MathComponent argument = sequence.getArgument(index);
		return argument instanceof MathCharacter && ((MathCharacter) argument).isSeparator();
	}

	private int firstSeparatorOnLeft(MathSequence sequence) {
		int offset = editorState.getCurrentOffset();
		int charIndex = isSeparatorAt(sequence, offset)
				? offset - 1
				: offset;
		while (charIndex > 0 && !isSeparatorAt(sequence, charIndex)) {
			charIndex--;
		}
		if (charIndex == 0 && isSeparatorAt(sequence, 0)) {
			return 1;
		}
		return charIndex == 0 ? 0 : charIndex + 1;
	}
}
