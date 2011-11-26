package geogebra.common.kernel.arithmetic;

public abstract class AbstractCommand extends ValidExpression {
	public abstract String getName();
	public abstract int getArgumentNumber();
	public abstract ValidExpression getArgument(int index);
}
