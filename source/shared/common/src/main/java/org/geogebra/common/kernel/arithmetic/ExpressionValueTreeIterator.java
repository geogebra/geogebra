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

	public ExpressionValueTreeIterator(ExpressionValue root) {
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
