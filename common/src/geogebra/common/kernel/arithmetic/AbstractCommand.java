package geogebra.common.kernel.arithmetic;

import geogebra.common.kernel.geos.GeoElement;

public abstract class AbstractCommand extends ValidExpression {
	public abstract String getName();
	public abstract int getArgumentNumber();
	public abstract ValidExpression getArgument(int index);
	public abstract void replaceChildrenByValues(GeoElement geo);
	public abstract boolean replaceGeoDummyVariables(String var, ExpressionValue newOb);

}
