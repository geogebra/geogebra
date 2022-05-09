package com.himamis.retex.editor.share.syntax;

import com.himamis.retex.editor.share.controller.EditorState;
import com.himamis.retex.editor.share.editor.MathFieldInternalListener;
import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.meta.Tag;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.serializer.GeoGebraSerializer;

public class SyntaxController implements MathFieldInternalListener {

	private SyntaxHintImpl hint = new SyntaxHintImpl();
	private SyntaxTooltipUpdater updater;
	private String command;

	/**
	 * Set the syntax tooltip updater, called when the syntax hint changes.
	 * @param updater updater
	 */
	public void setUpdater(SyntaxTooltipUpdater updater) {
		this.updater = updater;
	}

	/**
	 * Set the actual command inserted.
	 * Syntax hint will only be shown for this command.
	 * @param command command
	 */
	public void setCommand(String command) {
		this.command = command;
	}

	/**
	 * Return the actual syntax hint.
	 * @return the syntax hint
	 */
	public SyntaxHint getSyntaxHint() {
		return hint;
	}

	@Override
	public void inputChanged(MathFieldInternal mathFieldInternal) {
		update(mathFieldInternal);
	}

	private void update(MathFieldInternal mathFieldInternal) {
		EditorState editorState = mathFieldInternal.getEditorState();
		updateHint(editorState);
		updateSyntaxTooltip();
	}

	private void updateHint(EditorState editorState) {
		hint.clear();
		if (!isAccepted(editorState)) {
			return;
		}

		MathFunction fn = getMathFunction(editorState);
		if (fn.getName() == Tag.APPLY && !fn.getPlaceholders().isEmpty()) {
			int commas = editorState.countCommasBeforeCurrent();
			if (commas < fn.getPlaceholders().size()) {
				String serializedCommand = GeoGebraSerializer.serialize(fn.getArgument(0));
				if (command.equals(serializedCommand)) {
					hint.update(serializedCommand, fn.getPlaceholders(), commas);
				}
			}
		}
	}

	private boolean isAccepted(EditorState editorState) {
		return isMathFunction(editorState) && isFunctionArgument(editorState);
	}

	private boolean isFunctionArgument(EditorState editorState) {
		return editorState.getCurrentField().getParentIndex() == 1;
	}

	private MathFunction getMathFunction(EditorState editorState) {
		return (MathFunction) editorState.getCurrentField().getParent();
	}

	private boolean isMathFunction(EditorState editorState) {
		return editorState.getCurrentField().getParent() instanceof MathFunction;
	}

	private void updateSyntaxTooltip() {
		if (updater != null) {
			updater.updateSyntaxTooltip(hint);
		}
	}
}
