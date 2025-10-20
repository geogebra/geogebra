package com.himamis.retex.editor.share.serializer;

import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathCharPlaceholder;
import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathContainer;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathPlaceholder;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.share.util.Unicode;

public abstract class SerializerAdapter implements Serializer {

	protected MathContainer mCurrentField = null;
	protected MathComponent currentSelStart = null;
	protected MathComponent currentSelEnd = null;
	protected int mCurrentOffset = 0;

	@Override
	public String serialize(MathFormula formula) {
		return serialize(formula, null, 0, null, null, false);
	}

	/**
	 * @param formula
	 *            formula
	 * @param currentField
	 *            field with cursor
	 * @param currentOffset
	 *            cursor offset
	 * @return serialized formula
	 */
	public String serialize(MathFormula formula, MathSequence currentField,
			int currentOffset) {
		return serialize(formula, currentField, currentOffset, null, null, false);
	}

	/**
	 * @param formula
	 *            formula
	 * @param currentField
	 *            field with cursor
	 * @param currentOffset
	 *            cursor offset
	 * @param selStart
	 *            selected area start
	 * @param selEnd
	 *            selected area end
	 * @return serialized formula
	 */
	public String serialize(MathFormula formula, MathSequence currentField,
			int currentOffset, MathComponent selStart, MathComponent selEnd, boolean textMode) {
		this.mCurrentField = currentField;
		this.mCurrentOffset = currentOffset;
		this.currentSelEnd = selEnd;
		this.currentSelStart = selStart;
		StringBuilder buffer = new StringBuilder();

		if (textMode) {
			buffer.append("\\text{");
		}
		serialize(formula.getRootComponent(), buffer);
		if (textMode) {
			buffer.append("}");
		}

		return buffer.toString();
	}

	/**
	 * @param container
	 *            part of formula
	 * @param currentField
	 *            field with cursor
	 * @param currentOffset
	 *            cursor offset
	 * @return serialized formula
	 */
	public String serialize(MathContainer container, MathSequence currentField,
							int currentOffset) {
		this.mCurrentField = currentField;
		this.mCurrentOffset = currentOffset;
		return serialize(container, new StringBuilder()).toString();
	}

	/**
	 * @param container
	 *            part of formula
	 * @param stringBuilder
	 *            output string builder
	 * @return stringBuilder for convenience
	 */
	public StringBuilder serialize(MathComponent container, StringBuilder stringBuilder) {
		if (container instanceof MathCharPlaceholder) {
			serialize((MathCharPlaceholder) container, stringBuilder);

		} else if (container instanceof MathCharacter) {
			serialize((MathCharacter) container, stringBuilder);

		} else if (container instanceof MathPlaceholder) {
			serialize((MathPlaceholder) container, stringBuilder);

		} else if (container instanceof MathSequence) {
			serialize((MathSequence) container, stringBuilder);

		} else if (container instanceof MathArray) {
			serialize((MathArray) container, stringBuilder);

		} else if (container instanceof MathFunction) {
			serialize((MathFunction) container, stringBuilder);
		}
		return stringBuilder;
	}

	abstract void serialize(MathCharacter mathCharacter, StringBuilder stringBuilder);

	abstract void serialize(MathSequence sequence, StringBuilder stringBuilder);

	/**
	 * @param sequence
	 *            math sequence
	 * @param stringBuilder
	 *            builder
	 * @param from
	 *            start index
	 * @param to
	 *            end index
	 */
	public void serialize(MathSequence sequence, StringBuilder stringBuilder,
			int from, int to) {
		for (int i = from; i < to; i++) {
			serialize(sequence.getArgument(i), stringBuilder);
		}
	}

	abstract void serialize(MathFunction function, StringBuilder stringBuilder);

	abstract void serialize(MathArray array, StringBuilder stringBuilder);

	void serialize(MathPlaceholder placeholder, StringBuilder stringBuilder) {
		// only in LaTeX
	}

	abstract boolean buildMixedNumber(StringBuilder stringBuilder, MathFunction mathFunction);

	void serialize(MathCharPlaceholder placeholder, StringBuilder stringBuilder) {
		// only in LaTeX
	}

	/**
	 * Checks if the stringBuilder contains a mixed number e.g. 3 1/2
	 * @param stringBuilder StringBuilder
	 * @return Index &gt;= 0 of where to put opening parentheses if there is a mixed number, -1 else
	 */
	public int isMixedNumber(StringBuilder stringBuilder) {
		boolean isMixedNumber = false;
		for (int i = stringBuilder.length() - 1; i >= 0; i--) {
			if (stringBuilder.charAt(i) == ' ' && !isMixedNumber) {
				continue; // Expecting a space preceding the fraction
			} else if (Character.isDigit(stringBuilder.charAt(i))) {
				isMixedNumber = true; // Only allow digits 0 - 9 here
			} else if (isMixedNumber // Square bracket "[" needed for the SolverSerializer
					&& " +-*/()[]{}=,;".contains(Character.toString(stringBuilder.charAt(i)))) {
				return i + 1;
			} else {
				isMixedNumber = false;
				break;
			}
		}
		return isMixedNumber ? 0 : -1;
	}

	/**
	 * Used to determine if a function contains of digits and invisible plus only
	 * (for mixed numbers)
	 * @param mathFunction MathFunction
	 * @return True if digits (and invisible plus) only are found, false else
	 */
	public boolean isValidMixedNumber(MathFunction mathFunction) {
		String compare;
		for (int i = 0; i < mathFunction.size(); i++) {
			for (int j = 0; j < mathFunction.getArgument(i).size(); j++) {
				compare = mathFunction.getArgument(i).getArgument(j).toString();
				if (!Character.isDigit(compare.charAt(0))
						&& !compare.equals(Character.toString(Unicode.INVISIBLE_PLUS))) {
					return false;
				}
			}
		}
		return true;
	}
}
