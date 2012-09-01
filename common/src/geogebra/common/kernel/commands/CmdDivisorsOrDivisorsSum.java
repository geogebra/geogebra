package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoDivisorsSum;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;

/**
 * Divisors[number]
 * DivisorsSum[number]
 * @author Zbynek
 */
public class CmdDivisorsOrDivisorsSum extends CmdOneNumber {

	private boolean sum;

	/**
	 * @param kernel kernel
	 * @param sum true for DivisorsSum
	 */
	public CmdDivisorsOrDivisorsSum(Kernel kernel,boolean sum) {
		super(kernel);
		this.sum = sum;
	}

	@Override
	protected GeoElement getResult(NumberValue num,String label){
		AlgoDivisorsSum algo = new AlgoDivisorsSum(cons, label,num , sum);
		return algo.getResult();
	}

}
