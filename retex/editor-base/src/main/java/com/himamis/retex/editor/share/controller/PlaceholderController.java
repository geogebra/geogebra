package com.himamis.retex.editor.share.controller;

import java.util.List;

import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathPlaceholder;

public class PlaceholderController {

	/**
	 * Insert placeholders. It ignores placeholders if there are already some characters present.
	 * @param editorState editor state
	 * @param placeholders the list of placeholders to insert
	 */
	public static void insertPlaceholders(EditorState editorState, List<String> placeholders) {
		MetaModel metaModel = editorState.getMetaModel();
		int firstPlaceholderOffset = -1;
		for (int i = 0; i < placeholders.size(); i++) {
			if (i != 0) {
				MathCharacter comma = new MathCharacter(metaModel.getCharacter(","));
				editorState.addArgument(comma);
			}
			int currentOffset = editorState.getCurrentOffset();
			int currentSize = editorState.getCurrentField().size();
			if (currentOffset < currentSize) {
				editorState.setCurrentOffset(currentSize);
			} else {
				editorState.addArgument(new MathPlaceholder(placeholders.get(i)));
				if (firstPlaceholderOffset == -1) {
					firstPlaceholderOffset = editorState.getCurrentField().size() - 1;
				}
			}
		}
		if (firstPlaceholderOffset != -1) {
			editorState.setCurrentOffset(firstPlaceholderOffset);
		}
	}
}
