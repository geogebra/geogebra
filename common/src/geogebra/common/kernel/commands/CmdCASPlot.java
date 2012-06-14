package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoCASPlot;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;

public class CmdCASPlot extends CmdOneNumber {

	public CmdCASPlot(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement getResult(NumberValue num, String label) {
		AlgoCASPlot algo = new AlgoCASPlot(kernelA.getConstruction(),label,num);
		return algo.getResult();
	}

}
