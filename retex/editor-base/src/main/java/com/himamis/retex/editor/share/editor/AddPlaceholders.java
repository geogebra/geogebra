package com.himamis.retex.editor.share.editor;

import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathCharPlaceholder;
import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathPlaceholder;
import com.himamis.retex.editor.share.model.MathSequence;

public class AddPlaceholders {

	/**
	 * Searches and adds possible character placeholders in mathComponent.
	 * @param mathComponent to add in possible placeholders.
	 */
	public void process(MathComponent mathComponent) {
		if (mathComponent instanceof MathArray) {
			MathArray array = (MathArray) mathComponent;
			if (array.columns() == 1) {
				processSequence(array.getArgument(0));
			}
		}
	}

	private void processSequence(MathSequence sequence) {
		int lastPosition = sequence.size() - 1;
		MathComponent first = sequence.getArgument(0);
		MathComponent last = sequence.getArgument(lastPosition);

		if (isComma(first)) {
			addPlaceholder(sequence, 0);
		}

		if (isComma(last)) {
			appendPlaceholder(sequence);
		}

		for (int i = 1; i < lastPosition + 1; i++) {
			MathComponent current = sequence.getArgument(i);
			MathComponent next = sequence.getArgument(i + 1);
			if (isComma(current) && isComma(next)) {
				addPlaceholder(sequence, i + 1);
			}
		}
	}

	private boolean isComma(MathComponent mathComponent) {
		if (mathComponent instanceof MathCharacter) {
			return ",".equals(((MathCharacter) mathComponent).getTexName());
		}

		return false;
	}

	private void addPlaceholder(MathSequence sequence, int i) {
		sequence.addArgument(i, newPlaceholder());
	}

	private MathPlaceholder newPlaceholder() {
		return new MathCharPlaceholder();
	}

	private void appendPlaceholder(MathSequence sequence) {
		sequence.addArgument(newPlaceholder());
	}
}
