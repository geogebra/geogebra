package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;

public class CmdIsPrime extends CmdOneNumber {

	/**
	 * @param kernel
	 */
	public CmdIsPrime(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement getResult(NumberValue num,String label){
		return kernelA.IsPrime(label, num);
	}

}
