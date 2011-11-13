package geogebra.kernel.arithmetic;

public interface ReplaceableValue extends ExpressionValue {

	/**
	 * Replaces every oldOb by newOb in this replaceable object.
	 * @return resulting expression
	 */
	public ExpressionValue replace(ExpressionValue oldOb, ExpressionValue newOb);
	
}
