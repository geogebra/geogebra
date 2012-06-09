package geogebra.common.kernel.commands;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;

public class CmdDivisorsList extends CmdOneNumber {

	public CmdDivisorsList(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement getResult(NumberValue num,String label){
		return kernelA.DivisorsList(label, num);
	}

}
