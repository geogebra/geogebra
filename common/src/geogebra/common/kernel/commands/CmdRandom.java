package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoRandom;
import geogebra.common.kernel.arithmetic.BooleanValue;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.statistics.AlgoRandomFixed;
import geogebra.common.main.MyError;

/**
 * RandomBetween[a,b]
 * RandomBetween[a,b,fixed]
 */
public class CmdRandom extends CommandProcessor {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdRandom(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		
		switch (n) {
		
		case 3:			
			arg = resArgs(c);
			if (arg[2].isBooleanValue()) {
				
				if (((BooleanValue) arg[2]).getBoolean()) {
					// don't pass (BooleanValue)arg[2] (dummy variable, always true)
					
					AlgoRandomFixed algo = new AlgoRandomFixed(cons, c.getLabel(), (NumberValue)arg[0], (NumberValue)arg[1]);
					GeoElement[] ret = { algo.getResult() };

					return ret;					
				} 
				//else fall through to case 2:
				
				
			} else {
				throw argErr(app, c.getName(), arg[2]);				
			}
			
			// fall through if arg[2] == false

		case 2:			
			arg = resArgs(c);
			if ((arg[0].isNumberValue()) &&
				(arg[1].isNumberValue())) 
			{
				
				AlgoRandom algo = new AlgoRandom(cons, c.getLabel(), (NumberValue)arg[0], (NumberValue)arg[1]);
				GeoElement[] ret = { algo.getResult() };

				return ret;
				
			}
			throw argErr(app, c.getName(), arg[0].isNumberValue() ? arg[1] : arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
	
}
