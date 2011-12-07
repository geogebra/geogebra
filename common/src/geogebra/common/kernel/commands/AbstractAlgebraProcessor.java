package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.AbstractCommand;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.kernel.geos.GeoElementInterface;
import geogebra.common.main.MyError;



public abstract class AbstractAlgebraProcessor {
	public abstract GeoElementInterface[] processCommand(AbstractCommand cmd, boolean storeUndo);
	public abstract GeoElementInterface[] processAlgebraCommand(String cmd, boolean storeUndo);
	public abstract GeoElementInterface[] processAlgebraCommandNoExceptionHandling
	(String cmd, boolean storeUndo,boolean b,boolean c) throws Exception;
	public abstract GeoElementInterface[] doProcessValidExpression(ValidExpression ve)
	throws MyError,Exception;
	public abstract boolean isCommandAvailable(String cmdName);
	public abstract NumberValue evaluateToNumeric(String string, boolean b);
	public abstract double evaluateToDouble(String string, boolean b);
}
