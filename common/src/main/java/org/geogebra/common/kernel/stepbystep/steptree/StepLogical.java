package org.geogebra.common.kernel.stepbystep.steptree;

public abstract class StepLogical extends StepNode {

	@Override
	public abstract StepLogical deepCopy();

	public abstract boolean contains(StepExpression se);

	public boolean isSetOperation(SetOperation operation) {
		return this instanceof StepSetOperation
				&& ((StepSetOperation) this).getOperation() == operation;
	}

}
