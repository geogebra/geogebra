package geogebra.common.kernel.commands;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;

public class CmdFractionalPart extends CmdOneNumber {

	public CmdFractionalPart(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement getResult(NumberValue num,String label){
		return kernelA.FractionalPart(label, num);
	}

}
