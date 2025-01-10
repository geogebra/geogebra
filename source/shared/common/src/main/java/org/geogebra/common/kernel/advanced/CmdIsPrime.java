package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CmdOneNumber;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * IsPrime[number]
 * 
 * @author Zbynek Konecny
 *
 */
public class CmdIsPrime extends CmdOneNumber {

	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdIsPrime(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement getResult(GeoNumberValue num, String label) {
		AlgoIsPrime algo = new AlgoIsPrime(cons, label, num);
		return algo.getResult();
	}

}
