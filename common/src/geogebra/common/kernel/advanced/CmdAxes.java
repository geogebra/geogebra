package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoAxes;
import geogebra.common.kernel.algos.AlgoAxesQuadricND;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoQuadricND;
import geogebra.common.main.MyError;

/**
 * Axes[ <GeoConic> ]
 */
public class CmdAxes extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdAxes(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);

			// asymptotes to conic/quadric
			if (arg[0] instanceof GeoQuadricND) {
				
				AlgoAxesQuadricND algo = axesConic(cons, c.getLabels(), (GeoQuadricND) arg[0]);
				return (GeoElement[]) algo.getAxes();

			}
			throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
	
	/**
	 * 
	 * @param cons1 construction
	 * @param labels labels
	 * @param c conic
	 * @return axes algo
	 */
	protected AlgoAxesQuadricND axesConic(Construction cons1, String[] labels, GeoQuadricND c){
		
		return new AlgoAxes(cons1, labels, (GeoConic) c);
		
	}
}
