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
			int columns = array.columns();
			if (columns == 1) {
				MathSequence sequence = array.getArgument(0);

				MathComponent last = sequence.getArgument(0);
				int size = sequence.size();

				if (isComma(last)) {
					addPlaceholder(sequence, 0);
				}

				for (int i = 1; i < size; i++) {
					MathComponent argument = sequence.getArgument(i);
					if ((last == null || isComma(last)) && isComma(argument)) {
						addPlaceholder(sequence, i + 1);
					}
					last = argument;
				}
				if (isComma(last)) {
					appendPlaceholder(sequence);
				}

			}

		}
	}

	private boolean isComma(MathComponent mathComponent) {
		if (!(mathComponent instanceof MathCharacter)) {
			return false;
		}

		return ",".equals(((MathCharacter) mathComponent).getTexName());
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
