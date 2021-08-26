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
import com.himamis.retex.renderer.share.platform.FactoryProvider;

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

	/**
	 * @return current offset or selection start
	 */
	public int getCurrentOffsetOrSelection() {
		if (currentSelStart != null && currentSelStart.getParent() == currentField) {
			return currentSelStart.getParentIndex();
		}
		return currentOffset;
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
		if (left && currentField.size() == currentOffset) {
			currentOffset--;
		}
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
				|| commonParent instanceof MathFunction
					&& ((MathFunction) commonParent).getName() == Tag.FRAC) {
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
	 * Select from cursor position to end of current sub-formula
	 */
	public void selectToStart() {
		extendSelection(getCursorField(false));
		extendSelection(getCurrentField().getArgument(0));
	}

	/**
	 * Select from cursor position to start of current sub-formula
	 */
	public void selectToEnd() {
		extendSelection(getCursorField(true));
		extendSelection(getCurrentField().getArgument(getCurrentField().size() - 1));
	}

	/**
	 * @param left whether to search left
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
	 * @param er
	 *            expression reader
	 * @return description of cursor position
	 */
	public String getDescription(ExpressionReader er) {
		MathComponent prev = currentField.getArgument(currentOffset - 1);
		MathComponent next = currentField.getArgument(currentOffset);
		StringBuilder sb = new StringBuilder();
		if (currentField.getParent() == null) {
			if (prev == null) {
				return er
						.localize("start of formula %0",
								ScreenReaderSerializer.fullDescription(er,
										currentField))
						.trim();
			}
			if (next == null) {
				return er
						.localize("end of formula %0",
								ScreenReaderSerializer.fullDescription(er,
										currentField))
						.trim();
			}
		}
		if (next == null && prev == null) {
			return describeParent(ExpRelation.EMPTY, currentField.getParent(),
					er);
		}
		if (next == null) {
			sb.append(
					describeParent(ExpRelation.END_OF, currentField.getParent(),
							er));
			sb.append(" ");
		}
		if (prev != null) {
			sb.append(describePrev(prev, er));
		} else {
			sb.append(describeParent(ExpRelation.START_OF,
					currentField.getParent(),
					er));
		}
		sb.append(" ");

		if (next != null) {
			sb.append(describeNext(next, er));
		} else if (endOfFunctionName()) {
			sb.append(
					er.localize(ExpRelation.BEFORE.toString(), "parenthesis"));
		}
		return sb.toString().trim();
	}

	private boolean endOfFunctionName() {
		return currentField.getParent() instanceof MathFunction
				&& currentField.getParent().hasTag(Tag.APPLY)
				&& currentField.getParentIndex() == 0;
	}

	private String describePrev(MathComponent parent, ExpressionReader er) {
		if (parent instanceof MathFunction
				&& Tag.SUPERSCRIPT == ((MathFunction) parent).getName()) {
			return er.localize(ExpRelation.AFTER.toString(), er.power(
					GeoGebraSerializer.serialize(currentField
							.getArgument(currentField.indexOf(parent) - 1)),
					GeoGebraSerializer
							.serialize(
									((MathFunction) parent).getArgument(0))));
		}
		if (parent instanceof MathCharacter) {
			StringBuilder sb = new StringBuilder();
			int i = currentField.indexOf(parent);
			while (MathFieldInternal.appendChar(sb, currentField, i)) {
				i--;
			}
			if (sb.length() > 0 && !isInsideQuotes()) {
				try {
					return er.localize(ExpRelation.AFTER.toString(),
							er.mathExpression(sb.reverse().toString()));
				} catch (Exception e) {
					FactoryProvider.getInstance()
							.debug("Invalid: " + sb.reverse().toString());
				}
			}
		}
		return describe(ExpRelation.AFTER, parent, er);
	}

	private String describeNext(MathComponent parent, ExpressionReader er) {
		if (parent instanceof MathCharacter) {
			StringBuilder sb = new StringBuilder();
			int i = currentField.indexOf(parent);
			while (MathFieldInternal.appendChar(sb, currentField, i)) {
				i++;
			}
			if (sb.length() > 0 && !isInsideQuotes()) {
				try {
					return er.localize(ExpRelation.BEFORE.toString(),
							er.mathExpression(sb.toString()));
				} catch (Exception e) {
					// no math alt text, fall back to reading as is
				}
			}
		}
		return describe(ExpRelation.BEFORE, parent, er);
	}

	private static String describe(ExpRelation pattern, MathComponent prev,
			ExpressionReader er) {
		String name = describe(pattern, prev, -1);
		if (name != null) {
			return er.localize(pattern.toString(), name);
		}
		return er.localize(pattern.toString(),
				ScreenReaderSerializer.fullDescription(er, prev));
	}

	private static String describe(ExpRelation pattern, MathComponent prev,
			int index) {
		if (prev instanceof MathFunction) {
			switch (((MathFunction) prev).getName()) {
			case FRAC:
				return new String[] { "fraction", "numerator",
						"denominator" }[index + 1];
			case NROOT:
				return new String[] { "root", "index", "radicand" }[index + 1];
			case SQRT:
				return "square root";
			case CBRT:
				return "cube root";
			case SUPERSCRIPT:
				return "superscript";
			case ABS:
				return "absolute value";
			case APPLY:
				if ((index == 1 && pattern == ExpRelation.START_OF)
						|| (index == ((MathFunction) prev).size() - 1
								&& pattern == ExpRelation.END_OF)) {
					return "parentheses";
				}
				return index >= 0 ? "" : "function";
			default:
				return "function";
			}
		}
		if (prev instanceof MathArray) {
			if (((MathArray) prev).getOpenKey() == '"') {
				return "quotes";
			}
			return pattern == ExpRelation.BEFORE
					|| pattern == ExpRelation.AFTER ? "parenthesis"
							: "parentheses";
		}
		return null;
	}

	private String describeParent(ExpRelation pattern, MathContainer parent,
			ExpressionReader er) {
		if (parent instanceof MathFunction) {
			String name = describe(pattern, parent,
					parent.indexOf(currentField));
			if (name != null && name.isEmpty()) {
				return "";
			}
			return er.localize(pattern.toString(), name);
		}

		return describe(pattern, parent, er);
	}

}
