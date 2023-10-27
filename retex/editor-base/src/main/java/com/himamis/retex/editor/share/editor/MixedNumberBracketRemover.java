package com.himamis.retex.editor.share.editor;

import com.himamis.retex.editor.share.meta.Tag;
import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.share.model.traverse.Traversing;

/**
 * Just like with fractions, we want to remove unnecessary brackets for mixed numbers <br>
 * Implemented similar to FractionBracketRemover
 * @see FractionBracketRemover
 */
public class MixedNumberBracketRemover implements Traversing {

	@Override
	public MathComponent process(MathComponent mathComponent) {
		if (mathComponent instanceof MathArray
				&& ((MathArray) mathComponent).getOpenKey() == '('
				&& ((MathArray) mathComponent).size() == 1) {
			if (isFollowedByScript(mathComponent)) {
				return mathComponent;
			}
			MathSequence bracketContent = ((MathArray) mathComponent).getArgument(0);
			if (isMixedNumber(bracketContent.getArgument(0)) && bracketContent.size() == 1) {
				MathComponent component = bracketContent.getArgument(0);
				return component.traverse(this);
			}
		}

		return mathComponent;
	}

	private boolean isFollowedByScript(MathComponent mathComponent) {
		return mathComponent.getParentIndex() < mathComponent.getParent().size()
				&& mathComponent.getParent().isScript(mathComponent.getParentIndex() + 1);
	}

	private boolean isMixedNumber(MathComponent argument) {
		return argument instanceof MathFunction
				&& ((MathFunction) argument).getName() == Tag.MIXED_NUMBER;
	}
}