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

package org.geogebra.common.kernel.arithmetic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A depth first iterator of an expression value tree.
 */
final class ExpressionValueTreeIterator implements Iterator<ExpressionValue> {

	// The last element is the first that needs to be processed
	private final List<ExpressionValue> stack = new ArrayList<>(10);

	ExpressionValueTreeIterator(ExpressionValue root) {
		stack.add(root);
	}

	@Override
	public boolean hasNext() {
		return !stack.isEmpty();
	}

	@Override
	public ExpressionValue next() {
		ExpressionValue current = stack.remove(stack.size() - 1);
		addChildrenToStack(current);
		return current;
	}

	private void addChildrenToStack(ExpressionValue parent) {
		int childCount = parent.getChildCount();
		for (int i = childCount - 1; i >= 0; i--) {
			stack.add(parent.getChild(i));
		}
	}
}
