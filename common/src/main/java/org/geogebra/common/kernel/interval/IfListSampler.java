package org.geogebra.common.kernel.interval;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.plugin.Operation;

public class IfListSampler implements ExpressionSampler {
	@Override
	public boolean isAccepted(ExpressionNode node) {
		return node != null && Operation.IF_LIST.equals(node.getOperation());
	}

	@Override
	public MultiSampler create(ExpressionNode node) {
		List<ConditionalSampler> list = new ArrayList<>();
		MyList conditions = (MyList) node.getLeft();
		MyList conditionBodies = (MyList) node.getRight();
		for (int i = 0; i < conditions.size(); i++) {
			list.add(createItem(conditions, conditionBodies, i));
		}
		return new MultiSampler(null, list);
	}


	private ConditionalSampler createItem(MyList conditions, MyList conditionBodies, int index) {
		return new ConditionalSampler(new IntervalConditionalExpression(
				conditions.getItem(index).wrap(),
				conditionBodies.getItem(index).wrap()));
	}
}
