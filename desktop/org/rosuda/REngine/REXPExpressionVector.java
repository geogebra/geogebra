package org.rosuda.REngine;

/** REXPExpressionVector represents a vector of expressions in R. It is essentially a special kind of generic vector - its elements are expected to be valid R expressions. */
public class REXPExpressionVector extends REXPGenericVector {
	/** create a new vector of expressions from a list of expressions.
	 *  @param list list of expressions to store in this vector */
	public REXPExpressionVector(RList list) { super(list); }	

	/** create a new vector of expressions from a list of expressions.
	 *  @param list list of expressions to store in this vector
	 *  @param attr attributes for the R object */
	public REXPExpressionVector(RList list, REXPList attr) { super(list, attr); }

	public boolean isExpression() { return true; }
}
