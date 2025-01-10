package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CmdOneNumber;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * DivisorsList[number]
 * 
 * @author zbynek
 *
 */
public class CmdDivisorsList extends CmdOneNumber {
	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDivisorsList(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement getResult(GeoNumberValue num, String label) {
		AlgoDivisorsList algo = new AlgoDivisorsList(cons, label, num);
		return algo.getResult();
	}

}
