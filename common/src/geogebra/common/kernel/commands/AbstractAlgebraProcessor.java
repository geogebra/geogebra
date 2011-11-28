package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.AbstractCommand;
import geogebra.common.kernel.geos.GeoElementInterface;



public abstract class AbstractAlgebraProcessor {
	public abstract GeoElementInterface[] processCommand(AbstractCommand cmd, boolean storeUndo);
	public abstract GeoElementInterface[] processAlgebraCommand(String cmd, boolean storeUndo);
	public abstract GeoElementInterface[] processAlgebraCommandNoExceptionHandling
	(String cmd, boolean storeUndo,boolean b,boolean c) throws Exception;
}
