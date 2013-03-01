package geogebra.common.kernel.arithmetic;

public interface Inspecting {
	public boolean check(ExpressionValue v);
	
	public enum IneqFinder implements Inspecting{
		INSTANCE;
		public boolean check(ExpressionValue v) {
			return v.isExpressionNode() && ((ExpressionNode)v).getOperation().isInequality();
		}
		
	}
}
