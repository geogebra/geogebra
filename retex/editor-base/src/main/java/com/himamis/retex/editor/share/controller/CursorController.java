package com.himamis.retex.editor.share.controller;

import java.util.ArrayList;

import com.himamis.retex.editor.share.meta.Tag;
import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathContainer;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathSequence;

/**
 * Cursor movement in the expression tree.
 */
public class CursorController {

	/**
	 * Next character &rarr; key.
	 *
	 * @param editorState
	 *            current state
	 * @return whether we moved right
	 */
	public static boolean nextCharacter(EditorState editorState) {
		int currentOffset = editorState.getCurrentOffset();
		MathSequence currentField = editorState.getCurrentField();
		if (currentOffset < currentField.size()) {
			MathComponent component = currentField.getArgument(currentOffset);
			return nextCharacterInCurrentField(component, editorState);
		} else {
			return nextField(editorState);
		}
	}

	private static boolean nextCharacterInCurrentField(
			MathComponent component, EditorState editorState) {

		MathContainer mathContainer = getMathContainer(component);
		if (mathContainer != null && (mathContainer.hasChildren())) {
			firstField(editorState, mathContainer);
			return true;
		} else {
			editorState.incCurrentOffset();
			return true;
		}
	}

	private static MathContainer getMathContainer(MathComponent component) {
		if (component instanceof MathContainer) {
			return (MathContainer) component;
		}
		return null;
	}

	/**
	 * Previous character &larr; key.
	 *
	 * @param editorState
	 *            current state
	 */
	public void prevCharacter(EditorState editorState) {
		int currentOffset = editorState.getCurrentOffset();
		MathSequence currentField = editorState.getCurrentField();
		if (currentOffset > 0) {
			MathComponent component = currentField.getArgument(currentOffset - 1);
			prevCharacterInCurrentField(component, editorState);
		} else {
			prevField(editorState);
		}
	}

	private static void prevCharacterInCurrentField(
			MathComponent component, EditorState editorState) {

		MathContainer mathContainer = getMathContainer(component);
		if (mathContainer != null && mathContainer.hasChildren()) {
			lastField(editorState, mathContainer);
		} else {
			editorState.decCurrentOffset();
		}
	}

	/**
	 * Move to the beginning of the whole expression
	 *
	 * @param editorState
	 *            current state
	 */
	public static void firstField(EditorState editorState) {
		MathSequence root = editorState.getRootComponent();
		firstField(editorState, root.extractLocked());
	}

	/**
	 * Move to the beginning of a subexpression
	 *
	 * @param editorState
	 *            current state
	 * @param component0
	 *            subexpression
	 */
	public static void firstField(EditorState editorState,
			MathContainer component0) {
		MathContainer component = component0;
		// surface to first symbol
		while (!(component instanceof MathSequence)) {
			int current = component.first();
			component = (MathContainer) component.getArgument(current);
		}
		editorState.setCurrentField((MathSequence) component);
		editorState.setCurrentOffset(0);
	}

	/**
	 * Move to the end of the whole expression
	 *
	 * @param editorState
	 *            current state
	 */
	public static void lastField(EditorState editorState) {
		MathSequence root = editorState.getRootComponent();
		lastField(editorState, root.extractLocked());
	}

	/**
	 * @param editorState
	 *            current state
	 * @param component0
	 *            subexpression
	 */
	public static void lastField(EditorState editorState,
			MathContainer component0) {
		MathContainer component = component0;
		// surface to last symbol
		while (!(component instanceof MathSequence)) {
			int current = component.last();
			component = (MathContainer) component.getArgument(current);
		}
		editorState.setCurrentField((MathSequence) component);
		editorState.setCurrentOffset(component.size());
	}

	/**
	 * Move cursor to the right..
	 *
	 * @param editorState
	 *            current state
	 * @return whether current component has next field
	 */
	public static boolean nextField(EditorState editorState) {
		return nextField(editorState, editorState.getCurrentField());
	}

	/**
	 * Move cursor to the right of a component.
	 *
	 * @param editorState
	 *            current state
	 * @param component
	 *            component where we want the cursor
	 * @return whether component has next field
	 */
	public static boolean nextField(EditorState editorState,
			MathContainer component) {
		// retrieve parent
		MathContainer container = component.getParent();
		int current = component.getParentIndex();
		if (container == null) {
			// this component has no parent
			// previous component doesn't exist
			// no-op
			return false;
		} else if (container instanceof MathSequence) {
			editorState.setCurrentField((MathSequence) container);
			editorState.setCurrentOffset(component.getParentIndex() + 1);
			return container.size() > component.getParentIndex();
			// try to find next sibling
		} else if (container.hasNext(current)) {
			current = container.next(current);
			MathContainer component1 = (MathContainer) container
					.getArgument(current);
			firstField(editorState, component1);
			return true;
			// try to delve down the tree
		} else if (MathArray.isLocked(container)) {
			return false;
		} else {
			return nextField(editorState, container);
		}
	}

	/**
	 * Find previous field.
	 *
	 * @param editorState
	 *            current state
	 */
	public void prevField(EditorState editorState) {
		prevField(editorState, editorState.getCurrentField());
	}

	/* Search for previous component */
	private void prevField(EditorState editorState, MathContainer component) {
		// retrieve parent
		MathContainer container = component.getParent();
		int current = component.getParentIndex();

		if (container == null) {
			// this component has no parent
			// previous component doesn't exist
			// no-op
			return;
		}
		if (container instanceof MathSequence) {
			editorState.setCurrentField((MathSequence) container);
			editorState.setCurrentOffset(component.getParentIndex());

			// try to find previous sibling
		} else if (container.hasPrev(current)) {
			current = container.prev(current);
			MathContainer component1 = (MathContainer) container
					.getArgument(current);
			lastField(editorState, component1);

			// delve down the tree
		} else if (!MathArray.isLocked(container)) {
			prevField(editorState, container);
		}
	}

	/**
	 * Up field.
	 *
	 * @param editorState
	 *            current state
	 * @return whether move up is possible
	 */
	public boolean upField(EditorState editorState) {
		return upField(editorState, editorState.getCurrentField());
	}

	/**
	 * Down field.
	 *
	 * @param editorState
	 *            current state
	 * @return whether move down is possible
	 */
	public boolean downField(EditorState editorState) {
		return downField(editorState, editorState.getCurrentField());
	}

	/** Up field. */
	private boolean upField(EditorState editorState, MathContainer component) {
		if (component.getParent() instanceof MathFunction) {
			Tag name = ((MathFunction) component.getParent()).getName();
			if (name.equals(Tag.SUBSCRIPT)) {
				return moveOutOfSuperSubScript(editorState);
			}
		}
		if (component instanceof MathSequence) {
			if (component.getParent() instanceof MathFunction) {
				MathFunction function = (MathFunction) component.getParent();
				int upIndex = function.getUpIndex(component.getParentIndex());
				if (upIndex >= 0) {
					editorState.setCurrentField(function.getArgument(upIndex));
					editorState.setCurrentOffset(0);
					return true;
				}
			}
		}
		if (checkMoveArray(component, editorState, -1)) {
			return true;
		}
		if (component.getParent() != null) {
			return upField(editorState, component.getParent());
		}
		return false;
	}

	/** Down field. */
	private boolean downField(EditorState editorState,
			MathContainer component) {
		if (component.getParent() instanceof MathFunction) {
			Tag name = ((MathFunction) component.getParent()).getName();
			if (name.equals(Tag.SUPERSCRIPT)) {
				return moveOutOfSuperSubScript(editorState);
			}
		}
		if (component instanceof MathSequence) {
			if (component.getParent() instanceof MathFunction) {
				MathFunction function = (MathFunction) component.getParent();
				int downIndex = function
						.getDownIndex(component.getParentIndex());
				if (downIndex >= 0) {
					editorState
							.setCurrentField(function.getArgument(downIndex));
					editorState.setCurrentOffset(0);
					return true;
				}
			}

			// matrix goes here
		}
		if (checkMoveArray(component, editorState, +1)) {
			return true;
		}
		if (component.getParent() != null) {
			return downField(editorState, component.getParent());
		}
		return false;
	}

	private static boolean checkMoveArray(MathComponent component,
			EditorState editorState, int rowChange) {
		if (component.getParent() instanceof MathArray) {
			MathArray function = (MathArray) component.getParent();

			if (function.rows() > 1) {
				int downIndex = component.getParentIndex()
						+ function.columns() * rowChange;
				if (downIndex >= 0 && downIndex < function.size()) {
					editorState
							.setCurrentField(function.getArgument(downIndex));
					editorState.setCurrentOffset(0);
					return true;
				}
			}
		}
		return false;
	}

	private boolean moveOutOfSuperSubScript(EditorState editorState) {
		MathComponent cursorFieldLeft = editorState.getCurrentField().getArgument(
				editorState.getCurrentOffset() - 1);
		MathComponent cursorFieldRight = editorState.getCurrentField().getArgument(
				editorState.getCurrentOffset());
		MathContainer parentFunction = cursorFieldLeft != null ? cursorFieldLeft.getParent()
				: cursorFieldRight.getParent();
		if (parentFunction.getArgument(0).equals(cursorFieldRight)
				&& cursorFieldLeft == null) {
			prevCharacter(editorState);
			return true;
		}
		if (parentFunction.getArgument(parentFunction.size() - 1)
				.equals(cursorFieldLeft) && cursorFieldRight == null) {
			return nextCharacter(editorState);
		}
		return false;
	}

	/**
	 * set position in editor state from tree path
	 *
	 * @param list
	 *            tree path
	 * @param ct
	 *            starting container
	 * @param editorState
	 *            editor state
	 */
	public static void setPath(ArrayList<Integer> list, MathContainer ct,
			EditorState editorState) {
		MathContainer current = ct;
		int i = list.size() - 1;
		while (i >= 0) {
			int index = list.get(i);
			if (index < current.size()) {
				MathComponent child = current.getArgument(index);
				if (child instanceof MathCharacter) {
					editorState.setCurrentField((MathSequence) current);
					editorState.setCurrentOffset(index);
					return;
				} else if (child instanceof MathSequence) {
					current = (MathSequence) child;
					i--;
				} else {
					i--;
					if (i >= 0) {
						current = (MathSequence) ((MathContainer) child)
								.getArgument(list.get(i));
						i--;
					} else if (current instanceof MathSequence) {
						editorState.setCurrentField((MathSequence) current);
						editorState.setCurrentOffset(index);
					}
				}
			} else if (index == current.size()) {
				editorState.setCurrentField((MathSequence) current);
				editorState.setCurrentOffset(index);
				return;
			} else {
				return;
			}
		}

	}

	/**
	 * set position in editor state from tree path, starting at root component
	 *
	 * @param list
	 *            tree path
	 * @param editorState
	 *            editor state
	 */
	public static void setPath(ArrayList<Integer> list,
			EditorState editorState) {
		editorState.setCurrentOffset(0);
		setPath(list, editorState.getRootComponent(), editorState);
	}

	/**
	 * @param editorState
	 *            editor state
	 * @return indices of subtrees that contain the cursor (in reversed order)
	 */
	public static ArrayList<Integer> getPath(EditorState editorState) {

		ArrayList<Integer> path = new ArrayList<>();

		path.add(editorState.getCurrentOffset());
		MathContainer field = editorState.getCurrentField();
		MathContainer parent = field.getParent();
		while (parent != null) {
			path.add(field.getParentIndex());
			field = parent;
			parent = field.getParent();
		}

		return path;
	}

}
