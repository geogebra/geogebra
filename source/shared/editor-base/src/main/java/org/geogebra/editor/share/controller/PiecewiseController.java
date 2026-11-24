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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.geogebra.editor.share.editor.MathFieldInternal;
import org.geogebra.editor.share.serializer.TeXBuilder;
import org.geogebra.editor.share.tree.FunctionNode;
import org.geogebra.editor.share.tree.SequenceNode;
import org.geogebra.editor.share.util.PiecewiseControl;
import org.geogebra.editor.share.util.PiecewiseHelper;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.google.j2objc.annotations.Weak;
import com.himamis.retex.renderer.share.Box;
import com.himamis.retex.renderer.share.BoxPosition;
import com.himamis.retex.renderer.share.TeXIcon;
import com.himamis.retex.renderer.share.platform.graphics.Insets;

/**
 * Adds and removes cases of piecewise functions, and decides which buttons are available
 * for it.
 * <p>
 * The buttons themselves are native to each platform, so that they are accessible. The formula
 * only leaves space for them. This class tells the platform, through its {@link State}, which
 * buttons to show and where that space is, and performs their actions:
 * <ul>
 * <li>"Add case" is shown below a piecewise function while the cursor is inside of it.</li>
 * <li>The button removing a case is shown while the cursor is in that case or the pointer is
 * over its row. The last remaining case cannot be removed.</li>
 * </ul>
 */
public final class PiecewiseController {
	@Weak
	private final MathFieldInternal mathFieldInternal;

	private final Set<StateListener> listeners = new HashSet<>();
	private final List<Region> regions = new ArrayList<>();
	private @Nullable SequenceNode focusedZone;
	private @Nullable PiecewiseControl hoveredCase;
	private boolean pointerInside;
	private double pointerX;
	private double pointerY;
	private @NonNull State state = State.NO_BUTTONS;

	/**
	 * Area of the formula, in the coordinates of pointer events: pixels from the left and top
	 * of the formula at its current font size.
	 * @param left left edge
	 * @param top top edge
	 * @param width width
	 * @param height height
	 */
	public record Bounds(double left, double top, double width, double height) {

		boolean contains(double x, double y) {
			return left <= x && x <= left + width && top <= y && y <= top + height;
		}

		Bounds union(Bounds other) {
			double minLeft = Math.min(left, other.left);
			double minTop = Math.min(top, other.top);
			return new Bounds(
					minLeft,
					minTop,
					Math.max(left + width, other.left + other.width) - minLeft,
					Math.max(top + height, other.top + other.height) - minTop);
		}
	}

	/**
	 * A button to show.
	 * @param control what the button does: {@link PiecewiseControl.Type#ADD_CASE} or
	 * {@link PiecewiseControl.Type#REMOVE_CASE}; pass it to {@link #performAction}
	 * @param bounds space left for the button in the formula. A button that removes a case
	 * is centered in it; the button that adds a case starts at its left edge and is as high.
	 * @param enabled whether the button can be activated
	 */
	public record Button(
			@NonNull PiecewiseControl control, @NonNull Bounds bounds, boolean enabled) {}

	/**
	 * @param buttons the buttons to show, none if there is no piecewise function to act on
	 */
	public record State(@NonNull List<Button> buttons) {
		static final State NO_BUTTONS = new State(List.of());
	}

	/** Listens to changes of the buttons to show. */
	public interface StateListener {
		/**
		 * @param newState new state
		 */
		void stateChanged(@NonNull State newState);
	}

	private record Region(
			@NonNull PiecewiseControl control, @NonNull Bounds bounds) {}

	/**
	 * @param mathFieldInternal editor
	 */
	public PiecewiseController(@NonNull MathFieldInternal mathFieldInternal) {
		this.mathFieldInternal = mathFieldInternal;
	}

	/**
	 * @param listener state listener
	 */
	public void addListener(@NonNull StateListener listener) {
		listeners.add(listener);
	}

	/**
	 * @param listener state listener
	 */
	public void removeListener(@NonNull StateListener listener) {
		listeners.remove(listener);
	}

	/**
	 * @return the buttons to show
	 */
	public @NonNull State getState() {
		return state;
	}

	/**
	 * Perform the action of a button.
	 * @param control control of a button in the current state
	 */
	public void performAction(@NonNull PiecewiseControl control) {
		if (control.type() == PiecewiseControl.Type.ADD_CASE) {
			addCase(control.piecewise());
		} else if (control.type() == PiecewiseControl.Type.REMOVE_CASE) {
			removeCase(control.piecewise(), control.caseIndex());
		}
	}

	/**
	 * Append an empty case and move the cursor to its expression. All other cases keep
	 * their content.
	 * @param piecewise piecewise function
	 */
	public void addCase(@NonNull FunctionNode piecewise) {
		moveCursorTo(PiecewiseHelper.addCase(piecewise), false);
	}

	/**
	 * Remove a case and move the cursor to the expression of the case that follows, or of
	 * the previous one if the final case was removed. The only case cannot be removed.
	 * @param piecewise piecewise function
	 * @param caseIndex index of the case to remove
	 * @return whether the case was removed
	 */
	public boolean removeCase(@NonNull FunctionNode piecewise, int caseIndex) {
		if (!PiecewiseHelper.canRemoveCase(piecewise)
				|| caseIndex < 0
				|| caseIndex >= PiecewiseHelper.getCaseCount(piecewise)) {
			return false;
		}
		PiecewiseHelper.removeCase(piecewise, caseIndex);
		int nextCase = Math.min(caseIndex, PiecewiseHelper.getCaseCount(piecewise) - 1);
		moveCursorTo(PiecewiseHelper.getExpression(piecewise, nextCase), true);
		return true;
	}

	private void moveCursorTo(SequenceNode zone, boolean atEnd) {
		EditorState editorState = mathFieldInternal.getEditorState();
		editorState.resetSelection();
		editorState.setCurrentNode(zone);
		editorState.setCurrentOffset(atEnd ? zone.size() : 0);
		mathFieldInternal.notifyAndUpdate(null);
	}

	/**
	 * Editors that scroll to keep the cursor in view can use this to keep the button that
	 * removes the case with the cursor in view as well.
	 * @return right edge of the space for that button, in the coordinates of pointer events;
	 * 0 if the cursor is not in a case
	 */
	public double getFocusedCaseRightEdge() {
		for (Region region : regions) {
			if (region.control.type() == PiecewiseControl.Type.REMOVE_CASE
					&& isCase(focusedZone, region.control)) {
				return region.bounds.left + region.bounds.width;
			}
		}
		return 0;
	}

	/**
	 * The pointer moved over the formula without being pressed. Only pointers that can
	 * hover should report this, so that touch input leaves no hover state behind.
	 * @param x x-coordinate in the formula
	 * @param y y-coordinate in the formula
	 */
	public void onHover(double x, double y) {
		pointerInside = true;
		pointerX = x;
		pointerY = y;
		updateState();
	}

	/**
	 * The pointer left the formula and its buttons, or stopped being able to hover.
	 */
	public void onHoverEnd() {
		pointerInside = false;
		updateState();
	}

	/**
	 * A pointer anywhere in the row of a case is meant for that case, e.g. on the "otherwise"
	 * label or in the space between the zones, even where a cursor position outside of the
	 * case is closer.
	 * @param x x-coordinate in the formula
	 * @param y y-coordinate in the formula
	 * @param field field with a possible cursor position
	 * @return whether the cursor may move to the field for a pointer at the given position
	 */
	public boolean isCursorTarget(double x, double y, @NonNull SequenceNode field) {
		PiecewiseControl row = getCaseAt(x, y);
		if (row == null) {
			return true;
		}
		SequenceNode zone = PiecewiseHelper.getEnclosingZone(field);
		while (zone != null && zone.getParent() != row.piecewise()) {
			zone = PiecewiseHelper.getEnclosingZone(zone.getParent());
		}
		return zone != null && PiecewiseHelper.getCase(zone) == row.caseIndex();
	}

	private @Nullable PiecewiseControl getCaseAt(double x, double y) {
		PiecewiseControl row = null;
		for (Region region : regions) {
			if (region.control.type() == PiecewiseControl.Type.CASE && region.bounds.contains(x, y)) {
				// rows of a nested piecewise function come after the row that contains them
				row = region.control;
			}
		}
		return row;
	}

	/**
	 * Find out where the cases and the space for the buttons ended up in the formula that
	 * was just laid out.
	 * @param builder formula builder
	 * @param renderer formula
	 * @param currentField field with the cursor, null if the editor shows no cursor
	 */
	void updateLayout(
			@NonNull TeXBuilder builder, @NonNull TeXIcon renderer, @Nullable SequenceNode currentField) {
		regions.clear();
		focusedZone = PiecewiseHelper.getEnclosingZone(currentField);
		Box root = renderer.getBox();
		double size = renderer.getPointSize();
		Insets insets = renderer.getInsets();
		root.inspect(
				(box, position) -> {
					PiecewiseControl control = builder.getPiecewiseControl(box.getAtom());
					if (control != null) {
						addRegion(new Region(
								control,
								new Bounds(
										position.x() * size + insets.left,
										(root.getHeight() + position.y() - box.getHeight()) * size + insets.top,
										box.getWidth() * size,
										(box.getHeight() + box.getDepth()) * size)));
					}
				},
				BoxPosition.ZERO);
		closeGapsBetweenCases();
		updateState();
	}

	/** The cells of a case all report the same control: join them into the row of the case. */
	private void addRegion(Region region) {
		if (region.control.type() == PiecewiseControl.Type.CASE) {
			for (int i = 0; i < regions.size(); i++) {
				if (regions.get(i).control.equals(region.control)) {
					regions.set(i, new Region(region.control, regions.get(i).bounds.union(region.bounds)));
					return;
				}
			}
		}
		regions.add(region);
	}

	/** Moving the pointer from one case to the next should not pass through a dead zone. */
	private void closeGapsBetweenCases() {
		for (int i = 0; i < regions.size(); i++) {
			for (int j = 0; j < regions.size(); j++) {
				Region region = regions.get(i);
				Region next = regions.get(j);
				double bottom = region.bounds.top + region.bounds.height;
				if (region.control.type() == PiecewiseControl.Type.CASE
						&& next.control.type() == PiecewiseControl.Type.CASE
						&& next.control.piecewise() == region.control.piecewise()
						&& next.control.caseIndex() == region.control.caseIndex() + 1
						&& next.bounds.top > bottom) {
					double gap = (next.bounds.top - bottom) / 2;
					regions.set(
							i,
							new Region(
									region.control,
									new Bounds(
											region.bounds.left,
											region.bounds.top,
											region.bounds.width,
											region.bounds.height + gap)));
					regions.set(
							j,
							new Region(
									next.control,
									new Bounds(
											next.bounds.left,
											next.bounds.top - gap,
											next.bounds.width,
											next.bounds.height + gap)));
				}
			}
		}
	}

	private void updateState() {
		hoveredCase = pointerInside ? getCaseAt(pointerX, pointerY) : null;
		List<Button> buttons = new ArrayList<>();
		if (!mathFieldInternal.isReadOnly()) {
			for (Region region : regions) {
				if (isShown(region.control)) {
					buttons.add(new Button(
							region.control,
							region.bounds,
							region.control.type() == PiecewiseControl.Type.ADD_CASE
									|| PiecewiseHelper.canRemoveCase(region.control.piecewise())));
				}
			}
		}
		State newState = buttons.isEmpty() ? State.NO_BUTTONS : new State(buttons);
		if (!newState.equals(state)) {
			state = newState;
			for (StateListener listener : new ArrayList<>(listeners)) {
				listener.stateChanged(state);
			}
		}
	}

	private boolean isShown(PiecewiseControl control) {
		return switch (control.type()) {
			// there is only space for this button while the cursor is in the structure
			case ADD_CASE -> true;
			case REMOVE_CASE -> isCase(focusedZone, control) || isCase(hoveredCase, control);
			case CASE -> false;
		};
	}

	private static boolean isCase(@Nullable SequenceNode zone, PiecewiseControl control) {
		return zone != null
				&& zone.getParent() == control.piecewise()
				&& PiecewiseHelper.getCase(zone) == control.caseIndex();
	}

	private static boolean isCase(@Nullable PiecewiseControl row, PiecewiseControl control) {
		return row != null
				&& row.piecewise() == control.piecewise()
				&& row.caseIndex() == control.caseIndex();
	}
}
