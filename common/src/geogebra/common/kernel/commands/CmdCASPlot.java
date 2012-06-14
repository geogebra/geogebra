package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoCASPlot;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoBoolean;

public class CmdCASPlot extends CommandProcessor {

	public CmdCASPlot(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) {
		GeoElement[] args = resArgs(c);
		if(args.length!=2)
			throw argNumErr(app,c.getName(),args.length);
		if(!args[0].isNumberValue())
			throw argErr(app,c.getName(),args[0]);
		if(!args[1].isGeoBoolean())
			throw argErr(app,c.getName(),args[1]);
		return new GeoElement[]{plot(c.getLabel(),(NumberValue)args[0],(GeoBoolean)args[1])};
	}
	
	public GeoElement plot(String label,NumberValue num,GeoBoolean out){
		AlgoCASPlot algo = new AlgoCASPlot(kernelA.getConstruction(),label,num,out);
		return algo.getResult();
	}

}
