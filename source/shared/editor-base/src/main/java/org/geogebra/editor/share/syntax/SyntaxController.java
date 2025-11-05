/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
*/

package org.geogebra.editor.share.syntax;

import org.geogebra.editor.share.catalog.Tag;
import org.geogebra.editor.share.controller.EditorState;
import org.geogebra.editor.share.editor.EditorFeatures;
import org.geogebra.editor.share.editor.MathFieldInternal;
import org.geogebra.editor.share.editor.MathFieldInternalListener;
import org.geogebra.editor.share.serializer.GeoGebraSerializer;
import org.geogebra.editor.share.tree.FunctionNode;

public class SyntaxController implements MathFieldInternalListener {

	private SyntaxHintImpl hint = new SyntaxHintImpl();
	private SyntaxTooltipUpdater updater;

	/**
	 * Set the syntax tooltip updater, called when the syntax hint changes.
	 * @param updater updater
	 */
	public void setUpdater(SyntaxTooltipUpdater updater) {
		this.updater = updater;
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

		FunctionNode fn = getMathFunction(editorState);
		if (fn.getName() == Tag.APPLY && !fn.getPlaceholders().isEmpty()) {
			int commasBefore = editorState.countCommasBeforeCurrent();
			int commasAfter = editorState.countCommasAfterCurrent();
			if (commasBefore < fn.getPlaceholders().size()
					&& (commasBefore + commasAfter + 1 == fn.getPlaceholders().size())) {
				String serializedCommand = GeoGebraSerializer.serialize(fn.getChild(0),
						(EditorFeatures) null);
				if (serializedCommand.equals(fn.getCommandForSyntax())) {
					hint.update(serializedCommand, fn.getPlaceholders(), commasBefore);
				}
			}
		}
	}

	private boolean isAccepted(EditorState editorState) {
		return isMathFunction(editorState) && isFunctionArgument(editorState);
	}

	private boolean isFunctionArgument(EditorState editorState) {
		return editorState.getCurrentNode().getParentIndex() == 1;
	}

	private FunctionNode getMathFunction(EditorState editorState) {
		return (FunctionNode) editorState.getCurrentNode().getParent();
	}

	private boolean isMathFunction(EditorState editorState) {
		return editorState.getCurrentNode().getParent() instanceof FunctionNode;
	}

	private void updateSyntaxTooltip() {
		if (updater != null) {
			updater.updateSyntaxTooltip(hint);
		}
	}
}
