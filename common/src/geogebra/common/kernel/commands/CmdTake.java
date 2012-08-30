package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoTake;
import geogebra.common.kernel.algos.AlgoTakeString;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.MyError;

/**
 * Take[ <List>,m,n ]
 * Michael Borcherds
 * 2008-03-04
 */
public class CmdTake extends CommandProcessor {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdTake(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);
		
		switch (n) {
		case 2:

			if ( (ok[0] = arg[0].isGeoList()) && (ok[1] = arg[1].isGeoNumeric()) ) {
				GeoElement[] ret = { 
						Take(c.getLabel(),
						(GeoList) arg[0], (GeoNumeric) arg[1], null) };
				return ret;
			} else if ( (ok[0] = arg[0].isGeoText()) && (ok[1] = arg[1].isGeoNumeric())  ) {
				GeoElement[] ret = { 
						Take(c.getLabel(),
						(GeoText) arg[0], (GeoNumeric) arg[1], null ) };
				return ret;
			} else
				throw argErr(app, c.getName(), getBadArg(ok, arg));
		
		case 3:

			if ( (ok[0] = arg[0].isGeoList()) && (ok[1] = arg[1].isGeoNumeric()) && (ok[2] = arg[2].isGeoNumeric()) ) {
				GeoElement[] ret = { 
						Take(c.getLabel(),
						(GeoList) arg[0], (GeoNumeric) arg[1], (GeoNumeric) arg[2] ) };
				return ret;
			} else if ( (ok[0] = arg[0].isGeoText()) && (ok[1] = arg[1].isGeoNumeric()) && (ok[2] = arg[2].isGeoNumeric()) ) {
				GeoElement[] ret = { 
						Take(c.getLabel(),
						(GeoText) arg[0], (GeoNumeric) arg[1], (GeoNumeric) arg[2] ) };
				return ret;
			} else
				throw argErr(app, c.getName(), getBadArg(ok, arg));
		
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
	
	/**
	 * First[string,n] Michael Borcherds
	 */
	final private GeoText Take(String label, GeoText list, GeoNumeric m,
			GeoNumeric n) {
		AlgoTakeString algo = new AlgoTakeString(cons, label, list, m, n);
		GeoText list2 = algo.getResult();
		return list2;
	}

	/**
	 * Take[list,m,n] Michael Borcherds
	 */
	final private GeoList Take(String label, GeoList list, GeoNumeric m,
			GeoNumeric n) {
		AlgoTake algo = new AlgoTake(cons, label, list, m, n);
		GeoList list2 = algo.getResult();
		return list2;
	}


}
