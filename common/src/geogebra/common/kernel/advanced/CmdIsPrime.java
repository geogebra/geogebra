package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoIsPrime;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.CmdOneNumber;
import geogebra.common.kernel.geos.GeoElement;
/**
 * IsPrime[number]
 * @author Zbynek Konecny
 *
 */
public class CmdIsPrime extends CmdOneNumber {

	/**
	 * @param kernel kernel
	 */
	public CmdIsPrime(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement getResult(NumberValue num,String label){
		AlgoIsPrime algo = new AlgoIsPrime(cons,label,num);
		return algo.getResult();
	}

}
