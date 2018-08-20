package com.himamis.retex.editor.share.controller;

import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.meta.Tag;
import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathContainer;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.share.serializer.GeoGebraSerializer;
import com.himamis.retex.editor.share.serializer.ScreenReaderSerializer;

public class EditorState {

	private MetaModel metaModel;
	private MathSequence rootComponent;

	private MathSequence currentField;
	private int currentOffset;

	private MathComponent currentSelStart;
	private MathComponent currentSelEnd;
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

	public int getCurrentOffset() {
		return currentOffset;
	}

	public void setCurrentOffset(int currentOffset) {
		this.currentOffset = currentOffset >= 0 ? currentOffset : 0;
	}

	/**
	 * Increase current offset.
	 */
	public void incCurrentOffset() {
		currentOffset++;
	}

	public void addCurrentOffset(int size) {
		currentOffset += size;
	}

	/**
	 * Decrease current offset.
	 */
	public void decCurrentOffset() {
		if (currentOffset > 0) {
			currentOffset--;
		}
	}

	/**
	 * @param mathComponent
	 *            new argument
	 */
	public void addArgument(MathComponent mathComponent) {
		if (currentField.addArgument(currentOffset, mathComponent)) {
			incCurrentOffset();
		}
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
		MathComponent cursorField = getCursorField(left);
		extendSelection(cursorField);
	}

	/**
	 * Extends selection to include a field
	 * 
	 * @param cursorField
	 *            newly selected field
	 */
	public void extendSelection(MathComponent cursorField) {

		if (selectionAnchor == null) {
			currentSelStart = cursorField;
			currentSelEnd = cursorField;
			anchor(true);
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
				|| commonParent instanceof MathFunction && "\\frac"
						.equals(((MathFunction) commonParent).getTexName())) {
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

	/**
	 * Select the whole formula
	 */
	public void selectAll() {
		currentSelStart = getRootComponent();
		currentSelEnd = currentSelStart;
		anchor(true);
	}

	/**
	 * @param left
	 *            whether to search left
	 * @return field directly left or right to the caret
	 */
	public MathComponent getCursorField(boolean left) {
		return getCurrentField().getArgument(
				Math.max(0, Math.min(getCurrentOffset() + (left ? 0 : -1),
						getCurrentField().size() - 1)));
	}

	/**
	 * @return content of selection as text
	 */
	public String getSelectedText() {
		StringBuilder sb = new StringBuilder();
		if (currentSelStart != null && currentSelEnd != null
				&& currentSelStart.getParent() != null) {
			for (int i = currentSelStart.getParentIndex(); i <= currentSelEnd
					.getParentIndex(); i++) {
				sb.append(currentSelStart.getParent().getArgument(i));
			}
		}
		return sb.toString();
	}

	private static boolean contains(MathContainer commonParent,
			MathComponent cursorField0) {
		MathComponent cursorField = cursorField0;
		while (cursorField != null) {
			if (cursorField == commonParent) {
				return true;
			}
			cursorField = cursorField.getParent();
		}
		return false;
	}

	/**
	 * Reset selection start/end/anchor pointers (NOT the caret)
	 */
	public void resetSelection() {

		selectionAnchor = null;

		currentSelEnd = null;
		currentSelStart = null;

	}

	/**
	 * @return true if has selection
	 */
	public boolean hasSelection() {
		return currentSelStart != null;
	}

	/**
	 * Update selection anchor (starting point of selection by drag)
	 * 
	 * @param start
	 *            whether to anchor the start or the end of selection
	 */
	public void anchor(boolean start) {
		this.selectionAnchor = start ? this.currentSelStart
				: this.currentSelEnd;
	}

	/**
	 * Move cursor to the start of the selection.
	 */
	public void cursorToSelectionStart() {
		if (this.currentSelStart != null) {
			if (this.currentSelStart.getParent() != null) {
				currentField = (MathSequence) this.currentSelStart.getParent();
			} else {
				this.currentField = (MathSequence) this.currentSelStart;
			}
			this.currentOffset = currentField.indexOf(currentSelStart) + 1;
		}
	}

	/**
	 * Move cursor to the end of the selection.
	 */
	public void cursorToSelectionEnd() {
		if (currentSelEnd != null) {
			if (this.currentSelEnd.getParent() != null) {
				this.currentField = (MathSequence) this.currentSelEnd
						.getParent();
			} else {
				this.currentField = (MathSequence) this.currentSelEnd;
			}
			this.currentOffset = currentField.indexOf(currentSelEnd) + 1;
		}
	}

	public MathComponent getSelectionAnchor() {
		return selectionAnchor;
	}

	/**
	 * @return whether cursor is between quotes
	 */
	public boolean isInsideQuotes() {
		MathContainer fieldParent = currentField;
		while (fieldParent != null) {
			if (fieldParent instanceof MathArray
					&& ((MathArray) fieldParent).getOpenKey() == '"') {
				return true;
			}
			fieldParent = fieldParent.getParent();
		}
		return false;
	}

	/**
	 * @return description of cursor position
	 */
	public String getDescription(ExpressionReader ed) {
		MathComponent prev = currentField.getArgument(currentOffset - 1);
		MathComponent next = currentField.getArgument(currentOffset);
		StringBuilder sb = new StringBuilder();
		if (currentField.getParent() == null) {
			if (prev == null) {
				return ed
						.localize("start of %0",
								ScreenReaderSerializer.fullDescription(ed,
										currentField))
						.trim();
			}
			if (next == null) {
				return ed
						.localize("end of %0",
								ScreenReaderSerializer.fullDescription(ed,
										currentField))
						.trim();
			}
		}
		if (next == null && prev == null) {
			return ed.localize("empty %0",
					describeParent(currentField.getParent(), ed));
		}
		if (next == null) {
			sb.append(ed.localize("end of %0",
					describeParent(currentField.getParent(), ed)));
			sb.append(" ");
		}
		if (prev != null) {
			sb.append(ed.localize("after %0", describePrev(prev, ed)));
		} else {
			sb.append(ed.localize("start of %0",
					describeParent(currentField.getParent(), ed)));
		}
		sb.append(" ");

		if (next != null) {
			sb.append(ed.localize("before %0", describeNext(next, ed)));
		}
		return sb.toString().trim();
	}

	private String describePrev(MathComponent parent, ExpressionReader er) {
		if (parent instanceof MathFunction
				&& Tag.SUPERSCRIPT == ((MathFunction) parent).getName()) {
			return er.power(
					GeoGebraSerializer.serialize(currentField
							.getArgument(currentField.indexOf(parent) - 1)),
					GeoGebraSerializer
							.serialize(((MathFunction) parent).getArgument(0)));
		}
		if (parent instanceof MathCharacter) {
			StringBuilder sb = new StringBuilder();
			int i = currentField.indexOf(parent);
			while (MathFieldInternal.appendChar(sb, currentField, i)) {
				i--;
			}
			if (sb.length() > 0) {
				return er.mathExpression(sb.reverse().toString());
			}
		}
		return describe(parent, er);
	}

	private String describeNext(MathComponent parent, ExpressionReader er) {
		if (parent instanceof MathCharacter) {
			StringBuilder sb = new StringBuilder();
			int i = currentField.indexOf(parent);
			while (MathFieldInternal.appendChar(sb, currentField, i)) {
				i++;
			}
			if (sb.length() > 0) {
				return er.mathExpression(sb.toString());
			}
		}
		return describe(parent, er);
	}

	private static String describe(MathComponent prev, ExpressionReader er) {
		if (prev instanceof MathFunction) {
			switch (((MathFunction) prev).getName()) {
			case FRAC:
				return "fraction";
			case SQRT:
				return "square root";
			case SUPERSCRIPT:
				return "superscript";
			default:
				return "function";
			}
		}
		return ScreenReaderSerializer.fullDescription(er, prev);
	}

	private String describeParent(MathContainer parent, ExpressionReader er) {
		if (parent instanceof MathFunction
				&& Tag.FRAC == ((MathFunction) parent).getName()) {
			return parent.indexOf(currentField) == 0 ? "numerator"
					: "denominator";
		}
		return describe(parent, er);
	}

}
