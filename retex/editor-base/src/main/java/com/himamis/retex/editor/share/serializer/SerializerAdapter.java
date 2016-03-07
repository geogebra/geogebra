package com.himamis.retex.editor.share.serializer;

import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathContainer;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathSequence;

public abstract class SerializerAdapter implements Serializer {

    protected MathSequence currentField = null;
    protected int currentOffset = 0;
    protected boolean currentBraces = true;

    public String serialize(MathFormula formula) {
        return serialize(formula, null, 0);
    }

    public String serialize(MathFormula formula, MathSequence currentField,
                            int currentOffset) {
        this.currentField = currentField;
        this.currentOffset = currentOffset;
        currentBraces = currentField != null;
        StringBuilder buffer = new StringBuilder();
        serialize(formula.getRootComponent(), buffer);
        return buffer.toString();
    }

    public String serialize(MathContainer container, MathSequence currentField,
                            int currentOffset) {
        this.currentField = currentField;
        this.currentOffset = currentOffset;
        currentBraces = currentField != null;
        StringBuilder stringBuilder = new StringBuilder();
        serialize(container, stringBuilder);
        return stringBuilder.toString();
    }

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

    abstract void serialize(MathSequence sequence, StringBuilder stringBuilder, int from, int to);

    abstract void serialize(MathFunction function, StringBuilder stringBuilder);

    abstract void serialize(MathArray array, StringBuilder stringBuilder);
}
