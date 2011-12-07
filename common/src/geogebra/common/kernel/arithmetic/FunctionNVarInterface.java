package geogebra.common.kernel.arithmetic;

public interface FunctionNVarInterface extends ExpressionValue{
	public int getVarNumber();
	public String getVarString();
	public String getVarString(int i);
	public boolean evaluateBoolean(double[] values);
	public FunctionVariable[] getFunctionVariables();
	public ExpressionNodeInterface getExpression();
}
