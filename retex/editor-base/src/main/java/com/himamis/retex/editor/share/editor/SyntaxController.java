package com.himamis.retex.editor.share.editor;

import com.himamis.retex.editor.share.controller.EditorState;
import com.himamis.retex.editor.share.meta.Tag;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.serializer.GeoGebraSerializer;

public class SyntaxController {
	private SyntaxHint hint = new SyntaxHint();
	private EditorState editorState;
	private String command = "";

	public SyntaxHint getHint() {
		return hint;
	}

	/**
	 * Updates hint.
	 *
	 * @param editorState the state
	 */
	public void update(EditorState editorState) {
		this.editorState = editorState;
		hint.clear();
		if (!isAccepted()) {
			return;
		}

		MathFunction fn = getMathFunction();
		if (fn.getName() == Tag.APPLY && !fn.getPlaceholders().isEmpty()) {
			int commas = editorState.countCommasBeforeCurrent();
			if (commas < fn.getPlaceholders().size()) {
				String serializedCommand = GeoGebraSerializer.serialize(fn.getArgument(0));
				if (!command.equals(serializedCommand)) {
					hint.clear();
					return;
				}
				hint.update(serializedCommand,
						fn.getPlaceholders(), commas);
			}
		}
	}

	private boolean isAccepted() {
		return isMathFunction() && isFunctionArgument();
	}

	private boolean isFunctionArgument() {
		return editorState.getCurrentField().getParentIndex() == 1;
	}

	private MathFunction getMathFunction() {
		return (MathFunction) editorState.getCurrentField().getParent();
	}

	private boolean isMathFunction() {
		return editorState.getCurrentField().getParent() instanceof MathFunction;
	}

	public void setCommand(String command) {
		this.command = command;
	}
}
