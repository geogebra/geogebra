package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CmdOneNumber;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * Divisors[number] DivisorsSum[number]
 * 
 * @author Zbynek
 */
public class CmdDivisorsOrDivisorsSum extends CmdOneNumber {

	private boolean sum;

	/**
	 * @param kernel
	 *            kernel
	 * @param sum
	 *            true for DivisorsSum
	 */
	public CmdDivisorsOrDivisorsSum(Kernel kernel, boolean sum) {
		super(kernel);
		this.sum = sum;
	}

	@Override
	protected GeoElement getResult(GeoNumberValue num, String label) {
		AlgoDivisorsSum algo = new AlgoDivisorsSum(cons, label, num, sum);
		return algo.getResult();
	}

}
