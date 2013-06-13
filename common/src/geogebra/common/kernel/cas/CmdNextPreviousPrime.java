package geogebra.common.kernel.cas;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.commands.CmdOneNumber;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumberValue;

/**
 * NextPrime[number]
 * PreviousPrime[number]
 * @author zbynek
 *
 */
public class CmdNextPreviousPrime extends CmdOneNumber {

	private boolean next;
	/**
	 * @param kernel kernel
	 * @param next true for NextPrime, false for PreviousPrime
	 */
	public CmdNextPreviousPrime(Kernel kernel,boolean next) {
		super(kernel);
		this.next = next;
	}

	@Override
	protected GeoElement getResult(GeoNumberValue num,String label) {
		
		AlgoNextPreviousPrime algo = new AlgoNextPreviousPrime(cons, label, num, next);
		return algo.getResult();
	}

}
