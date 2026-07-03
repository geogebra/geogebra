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

import static org.geogebra.editor.share.tree.inspect.Inspecting.containsNode;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.editor.share.catalog.Tag;
import org.geogebra.editor.share.editor.MathFieldInternal;
import org.geogebra.editor.share.serializer.TeXBuilder;
import org.geogebra.editor.share.tree.ArrayNode;
import org.geogebra.editor.share.tree.FunctionNode;
import org.geogebra.editor.share.tree.Node;
import org.geogebra.editor.share.tree.SequenceNode;

import com.himamis.retex.renderer.share.Box;
import com.himamis.retex.renderer.share.BoxPosition;
import com.himamis.retex.renderer.share.TeXIcon;
import com.himamis.retex.renderer.share.platform.graphics.Insets;

public final class MatrixResizeController {

	private final MathFieldInternal mathFieldInternal;
	private final Set<StateListener> listeners = new HashSet<>();

	private @Nonnull State state = State.NO_MATRIX;
	private @Nonnull State nextState = State.NO_MATRIX;
	private @CheckForNull DimensionsControllable targetNode;

	/**
	 * @param containsMatrix true if the formula contains matrix.
	 * @param popupState non-null, if the matrix resize popup can be shown.
	 */
	public record State(boolean containsMatrix, @CheckForNull PopupState popupState) {
		static final State NO_MATRIX = new State(false, null);
		static final State UNFOCUSED_MATRIX = new State(true, null);

		static State focusedMatrix(PopupState popupState) {
			return new State(true, popupState);
		}
	}

	/**
	 * The popup state.
	 * @param controlState state for control
	 * @param anchor absolute position of the matrix
	 * @param indicatorOffset vertical offset for resize indicator
	 */
	public record PopupState(
			@Nonnull ControlState controlState,
			@Nonnull GRectangle2D anchor,
			double indicatorOffset) { }

	/**
	 * Describes the controls state.
	 * @param rows the number of rows
	 * @param isRemoveRowEnabled true if minus button for rows is enabled
	 * @param isAddRowEnabled true if plus button for rows is enabled
	 * @param columns the number of columns
	 * @param isRemoveColumnEnabled true if minus button for columns is enabled
	 * @param isAddColumnEnabled true if plus button for columns is enabled
	 */
	public record ControlState(
			String rows, boolean isRemoveRowEnabled, boolean isAddRowEnabled,
			String columns, boolean isRemoveColumnEnabled, boolean isAddColumnEnabled) { }

	public interface StateListener {
		/**
		 * Callback when the state changes
		 * @param newState new state
		 */
		void stateChanged(@Nonnull State newState);
	}

	public MatrixResizeController(MathFieldInternal mathFieldInternal) {
		this.mathFieldInternal = mathFieldInternal;
	}

	/**
	 * @param listener state listener
	 */
	public void addListener(@Nonnull StateListener listener) {
		listeners.add(listener);
	}

	/**
	 * @param listener state listener
	 */
	public void removeListener(@Nonnull StateListener listener) {
		listeners.remove(listener);
	}

	/**
	 * @return the current state of the matrix resize popup. A {@code null}
	 * {@link State#popupState} means that the popup resize button is not visible.
	 */
	public @Nonnull State getState() {
		return state;
	}

	/**
	 * Adds a row.
	 */
	public void addRow() {
		if (targetNode == null
				|| state.popupState == null
				|| !state.popupState.controlState.isAddRowEnabled) {
			throw illegalStateException();
		}
		targetNode.addRow();
		mathFieldInternal.update();
	}

	/**
	 * Adds a column.
	 */
	public void addColumn() {
		if (targetNode == null
				|| state.popupState == null
				|| !state.popupState.controlState.isAddColumnEnabled) {
			throw illegalStateException();
		}
		targetNode.addColumn();
		mathFieldInternal.update();
	}

	/**
	 * Removes a row.
	 */
	public void removeRow() {
		if (targetNode == null
				|| state.popupState == null
				|| !state.popupState.controlState.isRemoveRowEnabled) {
			throw illegalStateException();
		}
		// Check if the row being removed contains the current field
		EditorState editorState = mathFieldInternal.getEditorState();
		targetNode.removeRow(editorState);
		mathFieldInternal.update();
	}

	/**
	 * Removes a column.
	 */
	public void removeColumn() {
		if (targetNode == null
				|| state.popupState == null
				|| !state.popupState.controlState.isRemoveColumnEnabled) {
			throw illegalStateException();
		}
		// Check if the column being removed contains the current field
		EditorState editorState = mathFieldInternal.getEditorState();
		targetNode.removeColumn(editorState);
		mathFieldInternal.update();
	}

	private IllegalStateException illegalStateException() {
		return new IllegalStateException("Control methods should only be called, when the popup "
				+ "is visible (popupState is not null) and the respective control is enabled.");
	}

	void updateState(TeXBuilder builder, TeXIcon renderer, SequenceNode currentField) {
		nextState = State.NO_MATRIX;
		targetNode = null;

		renderer.getBox().inspect((box, position) -> {
			Node node = builder.getNode(box.getAtom());
			DimensionsControllable dimensionsControllable = asDimensionsControllable(node);
			if (dimensionsControllable != null) {
				if (nextState == State.NO_MATRIX) {
					nextState = State.UNFOCUSED_MATRIX;
				}
				if (node.inspect(containsNode(currentField))) {
					targetNode = dimensionsControllable;
					nextState = createFocusedMatrixState(renderer, position, box, targetNode);
				}
			}
		}, BoxPosition.ZERO);

		if (nextState != state) {
			state = nextState;
			listeners.forEach(stateListener -> stateListener.stateChanged(state));
		}
	}

	private static State createFocusedMatrixState(TeXIcon renderer, BoxPosition position,
			Box box, DimensionsControllable node) {
		ControlState controlState = new ControlState(
				String.valueOf(node.getRows()),
				node.isRemovingRowsPossible(),
				node.isAddingRowsPossible(),
				String.valueOf(node.getColumns()),
				node.isRemovingColumnsPossible(),
				node.isAddingColumnsPossible()
		);

		Insets insets = renderer.getInsets();
		double pointSize = renderer.getPointSize();

		GRectangle2D anchor = AwtFactory.getPrototype().newRectangle2D();
		anchor.setRect(
				position.x() * pointSize + insets.left,
				position.y() * pointSize + insets.top,
				box.getWidth() * pointSize,
				box.getHeight() * pointSize
		);
		PopupState popupState = new PopupState(controlState, anchor, renderer.getIconHeight());

		return State.focusedMatrix(popupState);
	}
	
	private static DimensionsControllable asDimensionsControllable(Node node) {
		if (node instanceof ArrayNode arrayNode && arrayNode.isMatrix()) {
			return new Matrix(arrayNode);
		} else if (node instanceof FunctionNode functionNode
				&& functionNode.getName() == Tag.VECTOR) {
			return new Vector(functionNode);
		}
		return null;
	}

	private interface DimensionsControllable {
		int getRows();
		
		int getColumns();

		void addRow();
		
		void removeRow(EditorState editorState);
		
		void addColumn();
		
		void removeColumn(EditorState editorState);
		
		boolean isAddingRowsPossible();
		
		boolean isRemovingRowsPossible();
		
		boolean isAddingColumnsPossible();
		
		boolean isRemovingColumnsPossible();
	}

	private record Matrix(ArrayNode arrayNode) implements DimensionsControllable {
		private static final int MAX_DIMENSION = 99;
		private static final int MIN_DIMENSION = 1;

		@Override
		public int getRows() {
			return arrayNode.getRows();
		}

		@Override
		public int getColumns() {
			return arrayNode.getColumns();
		}

		@Override
		public void addRow() {
			arrayNode.addRow();
		}

		@Override
		public void removeRow(EditorState editorState) {
			for (int i = 0; i < arrayNode.getColumns(); i++) {
				Node node = arrayNode.getChild(arrayNode.getRows() - 1, i);
				if (node.inspect(containsNode(editorState.getCurrentNode()))) {
					SequenceNode focusNode =
							arrayNode.getChild(arrayNode.getRows() - 2, i);
					editorState.setCurrentNode(focusNode);
					editorState.setCurrentOffset(focusNode.size());
					break;
				}
			}
			arrayNode.removeRow();
		}

		@Override
		public void addColumn() {
			arrayNode.addColumn();
		}

		@Override
		public void removeColumn(EditorState editorState) {
			for (int i = 0; i < arrayNode.getRows(); i++) {
				Node node = arrayNode.getChild(i, arrayNode.getColumns() - 1);
				if (node.inspect(containsNode(editorState.getCurrentNode()))) {
					SequenceNode focusRow = arrayNode.getChild(i, arrayNode.getColumns() - 2);
					editorState.setCurrentNode(focusRow);
					editorState.setCurrentOffset(focusRow.size());
					break;
				}
			}
			arrayNode.removeColumn();
		}

		@Override
		public boolean isAddingRowsPossible() {
			return getRows() < MAX_DIMENSION;
		}

		@Override
		public boolean isRemovingRowsPossible() {
			return getRows() > MIN_DIMENSION;
		}

		@Override
		public boolean isAddingColumnsPossible() {
			return getColumns() < MAX_DIMENSION;
		}

		@Override
		public boolean isRemovingColumnsPossible() {
			return getColumns() > MIN_DIMENSION;
		}
	}
	
	private record Vector(FunctionNode functionNode) implements DimensionsControllable {
		@Override
		public boolean isAddingColumnsPossible() {
			return false;
		}

		@Override
		public boolean isRemovingColumnsPossible() {
			return false;
		}

		@Override
		public boolean isAddingRowsPossible() {
			return getRows() < 3;
		}

		@Override
		public boolean isRemovingRowsPossible() {
			return getRows() > 2;
		}

		@Override
		public int getRows() {
			return functionNode.size();
		}

		@Override
		public int getColumns() {
			return 1; // Fixed size vector
		}

		@Override
		public void addRow() {
			functionNode.addChild(new SequenceNode());
		}

		@Override
		public void removeRow(EditorState editorState) {
			Node row = functionNode.getChild(functionNode.size() - 1);
			if (row.inspect(containsNode(editorState.getCurrentNode()))) {
				SequenceNode focusRow = functionNode.getChild(functionNode.size() - 2);
				editorState.setCurrentNode(focusRow);
				editorState.setCurrentOffset(focusRow.size());
			}
			functionNode.removeChild(functionNode.size() - 1);
		}

		@Override
		public void addColumn() {
			throw new IllegalStateException("Cannot add column to vectors.");
		}

		@Override
		public void removeColumn(EditorState editorState) {
			throw new IllegalStateException("Cannot remove column from vectors.");
		}
	}
}
