package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.MyError;

/**
 * Commands with only one syntax that allows only one numeric input
 * 
 * @author zbynek
 */
public abstract class CmdOneNumber extends CommandProcessor {

	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdOneNumber(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info)
			throws MyError, CircularDefinitionException {
		GeoElement[] args = resArgs(c, info);
		if (args.length != 1) {
			throw argNumErr(c);
		}
		if (!(args[0] instanceof GeoNumberValue)) {
			throw argErr(c, args[0]);
		}
		return new GeoElement[] {
				getResult((GeoNumberValue) args[0], c.getLabel()) };
	}

	/**
	 * Returns output of the computation
	 * 
	 * @param num
	 *            input number
	 * @param label
	 *            label for output
	 * @return resulting geo
	 */
	protected abstract GeoElement getResult(GeoNumberValue num, String label);

}
