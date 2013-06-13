package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.commands.CmdOneNumber;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumberValue;
/**
 * DivisorsList[number]
 * @author zbynek
 *
 */
public class CmdDivisorsList extends CmdOneNumber {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdDivisorsList(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement getResult(GeoNumberValue num,String label){
		AlgoDivisorsList algo = new AlgoDivisorsList(cons, label, num);
		return algo.getResult();
	}

}
