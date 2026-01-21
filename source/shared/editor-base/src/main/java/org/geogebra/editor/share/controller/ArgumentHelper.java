/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.editor.share.controller;

import org.geogebra.editor.share.catalog.Tag;
import org.geogebra.editor.share.tree.ArrayNode;
import org.geogebra.editor.share.tree.CharacterNode;
import org.geogebra.editor.share.tree.FunctionNode;
import org.geogebra.editor.share.tree.InternalNode;
import org.geogebra.editor.share.tree.Node;
import org.geogebra.editor.share.tree.SequenceNode;

/**
 * Library class for processing function arguments
 */
public class ArgumentHelper {

	/**
	 * Moves content from current editor field into a function argument
	 * @param editorState editor state
	 * @param container function
	 */
	public static void passArgument(EditorState editorState, InternalNode container) {
		// get pass to argument
		SequenceNode field = (SequenceNode) container
				.getChild(container.getInsertIndex());
		while (editorState.getComponentLeftOfCursor() instanceof CharacterNode characterNode
				&& characterNode.toString().length() == 1
				&& Character.isWhitespace(characterNode.toString().charAt(0))) {
			deleteLeftOfCursor(editorState);
		}

		// pass scripts first
		while (FunctionNode.isScript(editorState.getComponentLeftOfCursor())) {
			FunctionNode script = (FunctionNode) editorState.getComponentLeftOfCursor();
			deleteLeftOfCursor(editorState);
			field.addChild(0, script);
		}

		// if previous sequence arguments are braces pass their content
		Node leftOfCursor = editorState.getComponentLeftOfCursor();
		if (leftOfCursor instanceof ArrayNode array) {
			deleteLeftOfCursor(editorState);
			if (field.size() == 0) {
				// here we already have sequence, just set it
				if (array.size() > 1 || array.getOpenDelimiter().getCharacter() != '(') {
					SequenceNode wrap = new SequenceNode();
					wrap.addChild(array);
					container.setChild(container.getInsertIndex(), wrap);
				} else {
					container.setChild(container.getInsertIndex(),
							array.getChild(0));
				}
			} else {
				field.addChild(0, array);
			}

			// if previous sequence argument is function, pass it
		} else if (leftOfCursor instanceof FunctionNode) {
			passFunction(editorState, field);
			// Special case for recurring decimals, here we need to also pass
			// at least 2 characters preceding the function
			if (leftOfCursor.hasTag(Tag.RECURRING_DECIMAL)) {
				passCharacters(editorState, container);
			}
			// otherwise pass character sequence
		} else {

			passCharacters(editorState, container);
		}
	}

	/**
	 * Passes a function from the current editor field into a function argument
	 * @param state editor state
	 * @param field sequence node of where to pass the arguments into
	 */
	private static void passFunction(EditorState state, SequenceNode field) {
		Node function = state.getComponentLeftOfCursor();
		deleteLeftOfCursor(state);
		field.addChild(0, function);
	}

	private static void passCharacters(EditorState editorState, InternalNode container) {

		// get pass to argument
		SequenceNode field = (SequenceNode) container
				.getChild(container.getInsertIndex());

		while (editorState.getComponentLeftOfCursor() instanceof CharacterNode characterNode) {
			if (characterNode.isWordBreak()) {
				break;
			}
			deleteLeftOfCursor(editorState);
			field.addChild(0, characterNode);
		}
	}

	/**
	 * Used to pass a single character, needed for passing arguments for recurring decimals <br>
	 * Each overline has a single preceding character that needs to be passed
	 * @param state editor state
	 * @param field sequence node of where to pass the arguments into
	 */
	public static void passSingleCharacter(EditorState state, SequenceNode field) {
		if (state.getComponentLeftOfCursor() instanceof CharacterNode) {
			CharacterNode character = (CharacterNode) state.getComponentLeftOfCursor();
			if (character.isWordBreak()) {
				return;
			}
			deleteLeftOfCursor(state);
			field.addChild(character);
		}
	}

	/**
	 * Reads all characters to the right of the cursor until it encounters a
	 * symbol
	 * @param editorState current editor state
	 * @return last string of characters
	 */
	public static String readCharacters(EditorState editorState,
			int initialOffset) {
		StringBuilder stringBuilder = new StringBuilder();
		int offset = initialOffset;
		SequenceNode currentNode = editorState.getCurrentNode();
		while (offset > 0 && currentNode
				.getChild(offset - 1) instanceof CharacterNode) {

			CharacterNode character = (CharacterNode) currentNode
					.getChild(offset - 1);
			if (character.isWordBreak()) {
				break;
			}
			offset--;
			stringBuilder.insert(0, character.getUnicodeString());
		}
		return stringBuilder.toString();
	}

	/**
	 * Deletes character left to cursor and move cursor left.
	 * Similar to deleteSingleArg in {@link InputController},
	 * but not concerned about selection/function merging because this one is only called
	 * when parsing.
	 */
	private static void deleteLeftOfCursor(EditorState state) {
		state.getCurrentNode().deleteChild(state.getCurrentOffset() - 1);
		state.decCurrentOffset();
	}
}
