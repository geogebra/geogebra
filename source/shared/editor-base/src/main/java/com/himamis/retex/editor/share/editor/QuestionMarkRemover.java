package com.himamis.retex.editor.share.editor;

import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathContainer;
import com.himamis.retex.editor.share.model.traverse.Traversing;

public class QuestionMarkRemover implements Traversing {

	@Override
	public MathComponent process(MathComponent component) {
		if (!(component instanceof MathContainer)) {
			return component;
		}

		MathContainer container = (MathContainer) component;

		for (int i = 0; i < container.size(); i++) {
			MathComponent argument = container.getArgument(i);
			if (isQuestionMark(argument)) {
				addPlaceholder(container, i);
			}
		}
		return component;
	}

	private void addPlaceholder(MathContainer container, int i) {
		container.removeArgument(i);
	}

	private boolean isQuestionMark(MathComponent component) {
		return component instanceof MathCharacter && ((MathCharacter) component).isUnicode('?');
	}

}
