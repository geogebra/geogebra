package com.himamis.retex.editor.share.controller;

import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathContainer;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathSequence;

public class EditorState {

    private MetaModel metaModel;
    private MathSequence rootComponent;

    private MathSequence currentField;
    private Integer currentOffset;

	private MathComponent currentSelStart, currentSelEnd;
	private MathComponent selectionAnchor;

    public EditorState(MetaModel metaModel) {
        this.metaModel = metaModel;
    }

    public MathSequence getRootComponent() {
        return rootComponent;
    }

    public void setRootComponent(MathSequence rootComponent) {
        this.rootComponent = rootComponent;
    }

    public MathSequence getCurrentField() {
        return currentField;
    }

    public void setCurrentField(MathSequence currentField) {
        this.currentField = currentField;
    }

    public Integer getCurrentOffset() {
        return currentOffset;
    }

    public void setCurrentOffset(Integer currentOffset) {
        this.currentOffset = currentOffset;
    }

    public void incCurrentOffset() {
        currentOffset++;
    }

    public void addCurrentOffset(int size) {
        currentOffset += size;
    }

    public void decCurrentOffset() {
        currentOffset--;
    }

    public void addArgument(MathComponent mathComponent) {
        currentField.addArgument(currentOffset, mathComponent);
        incCurrentOffset();
    }

    public MetaModel getMetaModel() {
        return metaModel;
    }

	public MathComponent getSelectionStart() {
		return currentSelStart;
	}

	public MathComponent getSelectionEnd() {
		return currentSelEnd;
	}

	public void setSelectionStart(MathComponent selStart) {
		currentSelStart = selStart;
	}

	public void setSelectionEnd(MathComponent selEnd) {
		currentSelEnd = selEnd;
	}

	/**
	 * Extends selection from current cursor position
	 * 
	 * @param left
	 *            true to go to the left from cursor
	 */
	public void extendSelection(boolean left) {
		MathComponent cursorField = getCurrentField()
				.getArgument(Math.max(0,
						Math.min(getCurrentOffset() + (left ? 0 : -1),
								getCurrentField().size() - 1)));
		if (selectionAnchor == null) {
			selectionAnchor = cursorField;
			currentSelStart = cursorField;
			currentSelEnd = cursorField;
			return;
		}

		currentSelStart = selectionAnchor;
		// go from selection start to the root until we find common root
		MathContainer commonParent = currentSelStart.getParent();
		while (commonParent != null && !contains(commonParent, cursorField)) {
			currentSelStart = currentSelStart.getParent();
			commonParent = currentSelStart.getParent();
		}
		if (commonParent == null) {
			commonParent = rootComponent;
		}

		currentSelEnd = cursorField;
		// special case: start is inside end -> select single component
		if (currentSelEnd == commonParent
				|| commonParent instanceof MathFunction
				&& "\\frac".equals(((MathFunction) commonParent).getTexName())) {
			currentSelStart = commonParent;
			currentSelEnd = commonParent;
			return;
		}

		// go from selection end to the root
		while (currentSelEnd != null
				&& commonParent.indexOf(currentSelEnd) < 0) {
			currentSelEnd = currentSelEnd.getParent();
		}

		// swap start and end when necessary
		int to = commonParent.indexOf(currentSelEnd);
		int from = commonParent.indexOf(currentSelStart);
		if (from > to) {
			MathComponent swap = currentSelStart;
			currentSelStart = currentSelEnd;
			currentSelEnd = swap;
		}

	}

	private boolean contains(MathContainer commonParent,
			MathComponent cursorField) {
		while (cursorField != null) {
			if (cursorField == commonParent) {
				return true;
			}
			cursorField = cursorField.getParent();
		}
		return false;
	}

	public void resetSelection() {
		selectionAnchor = null;
		currentSelEnd = null;
		currentSelStart = null;

	}
}
