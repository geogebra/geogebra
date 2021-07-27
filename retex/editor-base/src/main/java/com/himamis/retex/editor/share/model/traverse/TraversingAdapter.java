package com.himamis.retex.editor.share.model.traverse;

import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathContainer;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathSequence;

/**
 * Adapts to the different Traversable types for easier processing.
 */
public abstract class TraversingAdapter implements Traversing {

    @Override
	public MathComponent process(MathComponent mathComponent) {
        if (mathComponent instanceof MathCharacter) {
            return processMathCharacter((MathCharacter) mathComponent);
        } else if (mathComponent instanceof MathContainer) {
            return processMathContainer((MathContainer) mathComponent);
        } else {
            throw new UnsupportedOperationException("Unknown class type");
        }
    }

	/**
	 * @param mathContainer
	 *            part of formula
	 * @return processed container
	 */
    public MathComponent processMathContainer(MathContainer mathContainer) {
        if (mathContainer instanceof MathFunction) {
            return processMathFunction((MathFunction) mathContainer);
        } else if (mathContainer instanceof MathArray) {
            return processMathArray((MathArray) mathContainer);
        } else if (mathContainer instanceof MathSequence) {
            return processMathSequence((MathSequence) mathContainer);
        } else {
            throw new UnsupportedOperationException("Unknown class type");
        }
    }

    protected abstract MathComponent processMathArray(MathArray mathArray);

    protected abstract MathComponent processMathFunction(MathFunction mathFunction);

    protected abstract MathComponent processMathSequence(MathSequence mathSequence);

    protected abstract MathComponent processMathCharacter(MathCharacter mathCharacter);
}
