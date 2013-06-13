package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.commands.CmdOneNumber;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumberValue;
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
	protected GeoElement getResult(GeoNumberValue num,String label){
		AlgoIsPrime algo = new AlgoIsPrime(cons,label,num);
		return algo.getResult();
	}

}
