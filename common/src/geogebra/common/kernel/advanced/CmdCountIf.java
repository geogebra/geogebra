package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.kernel.commands.CmdKeepIf;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoList;

/**
 * CountIf[ <GeoBoolean>, <GeoList> ]
 */
public class CmdCountIf extends CmdKeepIf {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCountIf(Kernel kernel) {
		super(kernel);
	}

	protected GeoElement[] getResult2(ValidExpression c, GeoFunction booleanFun, GeoElement[] args) {
		AlgoCountIf algo = new AlgoCountIf(cons, c.getLabel(), booleanFun, ((GeoList) args[1]));
		GeoElement[] ret = { algo.getResult() };
		
		return ret;
	}

	protected GeoElement[] getResult3(ValidExpression c, GeoBoolean arg, GeoElement[] vars, GeoList[] over) {
		AlgoCountIf3 algo = new AlgoCountIf3(cons, c.getLabel(), arg, vars[0], over[0]);
		GeoElement[] ret = { algo.getResult() };
		
		return ret;
	}
}
