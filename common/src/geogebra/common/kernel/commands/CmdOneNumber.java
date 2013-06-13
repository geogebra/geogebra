package geogebra.common.kernel.commands;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumberValue;
import geogebra.common.main.MyError;

/**
 * Commands with only one syntax that allows only one numeric input
 * @author zbynek
 */
public abstract class CmdOneNumber extends CommandProcessor {

	/**
	 * @param kernel kernel
	 */
	public CmdOneNumber(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError,
			CircularDefinitionException {
		GeoElement[] args = resArgs(c);
		if(args.length!=1)
			throw argNumErr(app,c.getName(),args.length);
		if(!(args[0] instanceof GeoNumberValue))
			throw argErr(app,c.getName(),args[0]);
		return new GeoElement[]{getResult((GeoNumberValue)args[0],c.getLabel())};
	}
	
	/**
	 * Returns output of the computation
	 * @param num input number
	 * @param label label for output
	 * @return resulting geo
	 */
	protected abstract GeoElement getResult(GeoNumberValue num,String label);

}
