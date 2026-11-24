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

package org.geogebra.editor.share.util;

import java.util.List;

import org.geogebra.editor.share.catalog.Tag;
import org.geogebra.editor.share.catalog.TemplateCatalog;
import org.geogebra.editor.share.tree.FunctionNode;
import org.geogebra.editor.share.tree.InternalNode;
import org.geogebra.editor.share.tree.Node;
import org.geogebra.editor.share.tree.PlaceholderNode;
import org.geogebra.editor.share.tree.SequenceNode;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Structure of editable piecewise functions.
 * <p>
 * A piecewise function is a {@link FunctionNode} tagged {@link Tag#PIECEWISE}. Each case
 * contributes two fields ("zones") to it, the expression followed by its condition, so the
 * field at index {@code i} belongs to case {@code i / 2}.
 */
public final class PiecewiseHelper {
	/** Number of cases in a newly inserted template. */
	public static final int DEFAULT_CASE_COUNT = 2;

	private static final int ZONES_PER_CASE = 2;

	private PiecewiseHelper() {
		// utility class
	}

	/**
	 * @param node editor tree node
	 * @return whether the node is a piecewise function
	 */
	public static boolean isPiecewise(@Nullable Node node) {
		return node instanceof FunctionNode function && function.getName() == Tag.PIECEWISE;
	}

	/**
	 * @param field editor field
	 * @return the piecewise function that the field is a zone of, null for any other field
	 */
	public static @Nullable FunctionNode getPiecewise(@Nullable InternalNode field) {
		return field instanceof SequenceNode && isPiecewise(field.getParent())
				? (FunctionNode) field.getParent()
				: null;
	}

	/**
	 * @param node node inside of the editor tree
	 * @return the zone of the innermost piecewise function containing the node (the node
	 * itself if it is a zone), null if the node is not inside a piecewise function
	 */
	public static @Nullable SequenceNode getEnclosingZone(@Nullable Node node) {
		Node current = node;
		while (current != null) {
			if (current instanceof SequenceNode sequence && isPiecewise(sequence.getParent())) {
				return sequence;
			}
			current = current.getParent();
		}
		return null;
	}

	/**
	 * @param piecewise piecewise function
	 * @return number of cases
	 */
	public static int getCaseCount(@NonNull FunctionNode piecewise) {
		return piecewise.size() / ZONES_PER_CASE;
	}

	/**
	 * @param zone zone of a piecewise function
	 * @return index of the case that the zone belongs to
	 */
	public static int getCase(@NonNull SequenceNode zone) {
		return zone.getParentIndex() / ZONES_PER_CASE;
	}

	/**
	 * @param zone zone of a piecewise function
	 * @return whether the zone is a condition (as opposed to an expression)
	 */
	public static boolean isCondition(@NonNull SequenceNode zone) {
		return zone.getParentIndex() % ZONES_PER_CASE != 0;
	}

	/**
	 * @param piecewise piecewise function
	 * @param caseIndex index of a case
	 * @return expression of the case
	 */
	public static @NonNull SequenceNode getExpression(
			@NonNull FunctionNode piecewise, int caseIndex) {
		return piecewise.getChild(caseIndex * ZONES_PER_CASE);
	}

	/**
	 * @param piecewise piecewise function
	 * @param caseIndex index of a case
	 * @return condition of the case
	 */
	public static @NonNull SequenceNode getCondition(@NonNull FunctionNode piecewise, int caseIndex) {
		return piecewise.getChild(caseIndex * ZONES_PER_CASE + 1);
	}

	/**
	 * @param piecewise piecewise function
	 * @param caseIndex index of a case
	 * @param condition whether to return the condition or the expression
	 * @return the requested zone of the case
	 */
	public static @NonNull SequenceNode getZone(
			@NonNull FunctionNode piecewise, int caseIndex, boolean condition) {
		return condition ? getCondition(piecewise, caseIndex) : getExpression(piecewise, caseIndex);
	}

	/**
	 * The condition of the final case may stay empty, in which case that case applies to
	 * everything not covered by the other cases. A single case has no such fallback: its empty
	 * condition means that the function is not restricted.
	 * @param zone zone of a piecewise function
	 * @return whether the zone is an empty condition that stands for "otherwise"
	 */
	public static boolean isOtherwise(@NonNull SequenceNode zone) {
		FunctionNode piecewise = getPiecewise(zone);
		return piecewise != null
				&& getCaseCount(piecewise) > 1
				&& zone.getParentIndex() == piecewise.size() - 1
				&& zone.size() == 0;
	}

	/**
	 * @param piecewise piecewise function
	 * @return whether no zone has any content; syntax placeholders do not count as content
	 */
	public static boolean isEmpty(@NonNull FunctionNode piecewise) {
		for (int i = 0; i < piecewise.size(); i++) {
			for (Node child : piecewise.getChild(i)) {
				if (!(child instanceof PlaceholderNode)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Create a piecewise function with empty zones.
	 * @param catalog template catalog
	 * @param caseCount number of cases, at least one
	 * @return new piecewise function
	 */
	public static @NonNull FunctionNode create(@NonNull TemplateCatalog catalog, int caseCount) {
		FunctionNode piecewise = new FunctionNode(catalog.getGeneral(Tag.PIECEWISE));
		for (int i = 0; i < piecewise.size(); i++) {
			piecewise.setChild(i, new SequenceNode());
		}
		while (getCaseCount(piecewise) < caseCount) {
			addCase(piecewise);
		}
		return piecewise;
	}

	/**
	 * Create a piecewise function from its zones, listed as expression and condition of each
	 * case in turn. The condition of the final case may be left out.
	 * @param catalog template catalog
	 * @param zones zones; they become part of the new piecewise function
	 * @return new piecewise function
	 */
	public static @NonNull FunctionNode create(
			@NonNull TemplateCatalog catalog, @NonNull List<SequenceNode> zones) {
		FunctionNode piecewise = create(catalog, (zones.size() + 1) / ZONES_PER_CASE);
		for (int i = 0; i < zones.size(); i++) {
			piecewise.setChild(i, zones.get(i));
		}
		return piecewise;
	}

	/**
	 * Append an empty case.
	 * @param piecewise piecewise function
	 * @return expression of the new case
	 */
	public static @NonNull SequenceNode addCase(@NonNull FunctionNode piecewise) {
		SequenceNode expression = new SequenceNode();
		piecewise.addChild(expression);
		piecewise.addChild(new SequenceNode());
		return expression;
	}

	/**
	 * @param piecewise piecewise function
	 * @return whether a case can be removed; the last remaining case cannot
	 */
	public static boolean canRemoveCase(@NonNull FunctionNode piecewise) {
		return getCaseCount(piecewise) > 1;
	}

	/**
	 * Remove a case together with its content.
	 * @param piecewise piecewise function
	 * @param caseIndex index of the case to remove
	 */
	public static void removeCase(@NonNull FunctionNode piecewise, int caseIndex) {
		piecewise.removeChild(caseIndex * ZONES_PER_CASE + 1);
		piecewise.removeChild(caseIndex * ZONES_PER_CASE);
	}
}
