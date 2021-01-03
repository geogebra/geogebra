package com.himamis.retex.editor.share.serializer;

import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathContainer;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathSequence;

public abstract class SerializerAdapter implements Serializer {

	protected MathContainer mCurrentField = null;
	protected MathComponent currentSelStart = null;
	protected MathComponent currentSelEnd = null;
    protected int mCurrentOffset = 0;

    @Override
	public String serialize(MathFormula formula) {
		return serialize(formula, null, 0, null, null);
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
		return serialize(formula, currentField, currentOffset, null, null);
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
			int currentOffset, MathComponent selStart, MathComponent selEnd) {
        this.mCurrentField = currentField;
        this.mCurrentOffset = currentOffset;
		this.currentSelEnd = selEnd;
		this.currentSelStart = selStart;
        StringBuilder buffer = new StringBuilder();
        serialize(formula.getRootComponent(), buffer);
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
        StringBuilder stringBuilder = new StringBuilder();
        serialize(container, stringBuilder);
        return stringBuilder.toString();
    }

	/**
	 * @param container
	 *            part of formula
	 * @param stringBuilder
	 *            output string builder
	 */
    public void serialize(MathComponent container, StringBuilder stringBuilder) {
        if (container instanceof MathCharacter) {
            serialize((MathCharacter) container, stringBuilder);

        } else if (container instanceof MathSequence) {
            serialize((MathSequence) container, stringBuilder);

        } else if (container instanceof MathArray) {
            serialize((MathArray) container, stringBuilder);

        } else if (container instanceof MathFunction) {
            serialize((MathFunction) container, stringBuilder);
        }
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
}
