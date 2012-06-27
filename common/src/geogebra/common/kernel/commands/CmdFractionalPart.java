package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
/**
 * FractionalPart[number]
 * @author Zbynek Konecny
 *
 */
public class CmdFractionalPart extends CmdOneNumber {

	/**
	 * @param kernel kernel
	 */
	public CmdFractionalPart(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement getResult(NumberValue num,String label){
		return kernelA.FractionalPart(label, num);
	}

}
