package org.geogebra.common.kernel.arithmetic;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ExpressionNodeCollector<T> {
	private final ExpressionNode root;
	private List<ExpressionNode> filteredNodes = new ArrayList<>();

	public ExpressionNodeCollector(ExpressionNode node) {
		this.root = node;
	}

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

	public List<T> mapTo(Function<? super ExpressionNode, ?> mapper) {
		return (List<T>) filteredNodes.stream().map(mapper).collect(Collectors.toList());
	}

}
