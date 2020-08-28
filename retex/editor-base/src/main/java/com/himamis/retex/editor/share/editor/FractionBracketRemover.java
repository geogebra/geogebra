package com.himamis.retex.editor.share.editor;

import com.himamis.retex.editor.share.meta.Tag;
import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.share.model.traverse.Traversing;

public class FractionBracketRemover implements Traversing {

	@Override
	public MathComponent process(MathComponent mathComponent) {
		if (mathComponent instanceof MathArray
				&& ((MathArray) mathComponent).getOpenKey() == '('
				&& ((MathArray) mathComponent).size() == 1) {
			if (isFollowedByScript(mathComponent)) {
				return mathComponent;
			}
			MathSequence bracketContent = ((MathArray) mathComponent).getArgument(0);
			if (isFraction(bracketContent.getArgument(0)) && bracketContent.size() == 1) {
				return bracketContent.getArgument(0);
			}
		}
		return mathComponent;
	}

	private boolean isFollowedByScript(MathComponent mathComponent) {
		return mathComponent.getParentIndex() < mathComponent.getParent().size()
				&& mathComponent.getParent().isScript(mathComponent.getParentIndex() + 1);
	}

	private boolean isFraction(MathComponent argument) {
		return argument instanceof MathFunction
				&& ((MathFunction) argument).getName() == Tag.FRAC;
	}
}
