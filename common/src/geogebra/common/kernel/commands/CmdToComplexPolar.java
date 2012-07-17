package geogebra.common.kernel.commands;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoToComplexPolar;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.MyError;

/**
 * ToComplex[Vector]
 * ToComplex[List]
 *
 */
public class CmdToComplexPolar extends CommandProcessor {
	private boolean polar;
	
	/**
	 * @param kernel kernel
	 * @param polar true for ToPolar, false for ToComplex
	 */
	public CmdToComplexPolar(Kernel kernel,boolean polar) {
		super(kernel);
		this.polar = polar;
	}

	@Override
	public GeoElement[] process(Command c) throws MyError,
			CircularDefinitionException {
		if(c.getArgumentNumber()!=1)
			throw argNumErr(app,c.getName(),c.getArgumentNumber());
		GeoElement[] arg = resArgs(c);
		AlgoToComplexPolar algo = null;
		if(arg[0] instanceof GeoPoint)
			algo = new AlgoToComplexPolar(cons,c.getLabel(),(GeoPoint)arg[0],polar);
		if(arg[0] instanceof GeoVector)
			algo = new AlgoToComplexPolar(cons,c.getLabel(),(GeoVector)arg[0],polar);
		if(arg[0] instanceof GeoList)
			algo = new AlgoToComplexPolar(cons,c.getLabel(),(GeoList)arg[0],polar);
		return new GeoElement[]{algo.getResult()};
	}

}
