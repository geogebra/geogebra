package geogebra.common.kernel.arithmetic;

public interface EquationInterface {
	public void setFunctionDependent(boolean isFunctionDependent) ;
	public boolean isFunctionDependent() ;
	public abstract boolean replaceGeoDummyVariables(String var, ExpressionValue newOb);
}
