package geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdRotate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.main.MyError;

/**
 * @author mathieu
 * 
 * Extends rotation for 3D objects
 * 
 * Rotate[ <GeoPoint>, <NumberValue> ]
 *
 */
public class CmdRotate3D extends CmdRotate{

	/**
	 * default constructor
	 * @param kernel kernel
	 */
	public CmdRotate3D(Kernel kernel) {
		super(kernel);
	}
	

}
