package org.geogebra.common.kernel.arithmetic;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Filter and collect property T from an ExpressionNode tree
 * @param <T> type of the property to collect.
 */
public final class ExpressionNodeCollector<T> {
	private final ExpressionNode root;
	private List<ExpressionNode> filteredNodes = new ArrayList<>();

	/**
	 *
	 * @param node to collect from.
	 */
	public ExpressionNodeCollector(ExpressionNode node) {
		this.root = node;
	}

	/**
	 * Filter the node tree by a given condition.
	 *
	 * @param filter condition.
	 * @return itself to chain methods.
	 */
	public ExpressionNodeCollector<T> filter(Inspecting filter) {
		root.inspect(v -> {
			if (v == null) {
				return false;
			}
			if (filter.check(v)) {
				ExpressionNode node = v.wrap();
				filteredNodes.add(node);
				ExpressionValue left = node.getLeft();
				ExpressionValue right = node.getRight();
				return left != null && filter.check(left) && right != null && filter.check(right);
			}
			return false;
		});
		return this;
	}

	/**
	 * Map the node tree by mapper and collect to a list typed by T.
	 * This method is usually called after filter()
	 * @param mapper to extract a property T from the node tree.
	 * @return the list of properties.
	 */
	public List<T> mapTo(Function<? super ExpressionNode, ?> mapper) {
		return (List<T>) filteredNodes.stream().map(mapper).collect(Collectors.toList());
	}

}
