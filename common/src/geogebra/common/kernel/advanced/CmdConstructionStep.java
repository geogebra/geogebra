package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;

/**
 * ConstructionStep[]
 * ConstructionStep[ &lt;Object> ]
 * @author Michael Borcherds
 * @version 2008-03-06
 */
public class CmdConstructionStep extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdConstructionStep(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 0:
			
			AlgoConstructionStep algo = new AlgoConstructionStep(cons, c.getLabel());
			GeoElement[] ret = {algo.getResult() };
			return ret;

		case 1:
			AlgoStepObject algo2 = new AlgoStepObject(cons, c.getLabel(), arg[0]);
			GeoElement[] ret2 = {  algo2.getResult() };
			return ret2;

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
