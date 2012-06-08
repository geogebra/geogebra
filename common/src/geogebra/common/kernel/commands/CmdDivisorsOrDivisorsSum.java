package geogebra.common.kernel.commands;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;

public class CmdDivisorsOrDivisorsSum extends CommandProcessor {

	private boolean sum;

	public CmdDivisorsOrDivisorsSum(Kernel kernel,boolean sum) {
		super(kernel);
		this.sum = sum;
	}

	@Override
	public GeoElement[] process(Command c) throws MyError,
			CircularDefinitionException {
		GeoElement[] args = resArgs(c);
		if(args.length!=1)
			throw argNumErr(app,c.getName(),args.length);
		if(!args[0].isNumberValue())
			throw argErr(app,c.getName(),args[0]);
		return new GeoElement[]{kernelA.DivisorsOrDivisorsSum(c.getLabel(), (NumberValue)args[0],sum)};
	}

}
