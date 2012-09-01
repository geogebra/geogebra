package geogebra.common.kernel.cas;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.CasEvaluableFunction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.main.MyError;

/**
 * TrigCombine[<Function>]
 * TrigCombine[<Function>, <Target Function>]
 * @author Zbynek Konecny
 */
public class CmdTrigCombine extends CommandProcessor {

	/**
	 * @param kernel kernel
	 */
	public CmdTrigCombine(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError,
			CircularDefinitionException {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:
			if ((arg[0].isCasEvaluableObject())) {
				AlgoTrigCombine algo= new AlgoTrigCombine(kernelA.getConstruction(),c.getLabel(),
						(CasEvaluableFunction) arg[0],null); 
				return new GeoElement[]{algo.getResult()};
			} 
		case 2:
			if ((arg[0].isCasEvaluableObject()) && (arg[1] instanceof GeoFunction)) {
				AlgoTrigCombine algo= new AlgoTrigCombine(kernelA.getConstruction(),c.getLabel(),
						(CasEvaluableFunction) arg[0],(GeoFunction)arg[1]); 
				return new GeoElement[]{algo.getResult()};
			} 	
			throw argErr(app, c.getName(), arg[0]);

			// more than one argument
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
