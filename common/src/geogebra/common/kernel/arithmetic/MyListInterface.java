package geogebra.common.kernel.arithmetic;

public interface MyListInterface extends ExpressionValue{
	public int size();
	public ExpressionValue getListElement(int i);
	public int replaceVariables(String varName, FunctionVariable fVar);
	public int replacePolynomials(FunctionVariable fVar); 

}
