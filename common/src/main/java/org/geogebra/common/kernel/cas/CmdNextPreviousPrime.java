package org.geogebra.common.kernel.cas;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CmdOneNumber;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * NextPrime[number] PreviousPrime[number]
 * 
 * @author zbynek
 *
 */
public class CmdNextPreviousPrime extends CmdOneNumber {

	private boolean next;

	/**
	 * @param kernel
	 *            kernel
	 * @param next
	 *            true for NextPrime, false for PreviousPrime
	 */
	public CmdNextPreviousPrime(Kernel kernel, boolean next) {
		super(kernel);
		this.next = next;
	}

	@Override
	protected GeoElement getResult(GeoNumberValue num, String label) {

		AlgoNextPreviousPrime algo = new AlgoNextPreviousPrime(cons, label,
				num, next);
		return algo.getResult();
	}

}
