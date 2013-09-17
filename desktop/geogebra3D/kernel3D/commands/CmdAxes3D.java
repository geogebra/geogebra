package geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.advanced.CmdAxes;
import geogebra.common.kernel.algos.AlgoAxesQuadricND;
import geogebra.common.kernel.kernelND.GeoQuadricND;
import geogebra3D.kernel3D.AlgoAxes3D;

/**
 * Axes[ <GeoConic> ]
 */
public class CmdAxes3D extends CmdAxes {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdAxes3D(Kernel kernel) {
		super(kernel);
	}


	@Override
	protected AlgoAxesQuadricND axesConic(Construction cons1, String[] labels, GeoQuadricND c){
		
		if (c.isGeoElement3D()){
			return new AlgoAxes3D(cons1, labels, c);
		}
		
		return super.axesConic(cons1, labels, c);
		
	}
}
