package geogebra.common.kernel.arithmetic;

/**
 * Allows checking whether at least one part of structured expression value has certain property.
 *
 */
public interface Inspecting {
	/**
	 * Do the local check
	 * @param v expression
	 * @return whether this expression itself has given property (not the subparts)
	 */
	public boolean check(ExpressionValue v);
	
	/**
	 * Checks whether the expression contains operations <,<=, >, >=
	 *
	 */
	public enum IneqFinder implements Inspecting{
		/** singleton instance */
		INSTANCE;
		public boolean check(ExpressionValue v) {
			return v.isExpressionNode() && ((ExpressionNode)v).getOperation().isInequality();
		}
		
	}
}
