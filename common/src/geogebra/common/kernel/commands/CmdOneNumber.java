package geogebra.common.kernel.commands;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;

public abstract class CmdOneNumber extends CommandProcessor {

	public CmdOneNumber(Kernel kernel) {
		super(kernel);
		// TODO Auto-generated constructor stub
	}

	@Override
	public GeoElement[] process(Command c) throws MyError,
			CircularDefinitionException {
		GeoElement[] args = resArgs(c);
		if(args.length!=1)
			throw argNumErr(app,c.getName(),args.length);
		if(!args[0].isNumberValue())
			throw argErr(app,c.getName(),args[0]);
		return new GeoElement[]{getResult((NumberValue)args[0],c.getLabel())};
	}
	
	protected abstract GeoElement getResult(NumberValue num,String label);

}
