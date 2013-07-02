package geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.CmdDilate;
import geogebra.common.kernel.geos.GeoElement;


/**
 * Mirror at 3D point or 3D line
 * @author mathieu
 *
 */
public class CmdDilate3D extends CmdDilate {
	
	
	
	/**
	 * constructor
	 * @param kernel kernel
	 */
	public CmdDilate3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement[] Dilate(String label, GeoElement geoDil,
			NumberValue r, GeoElement point) {
		
		if (geoDil.isGeoElement3D() || point.isGeoElement3D()){
			
		}
		
		return super.Dilate(label, geoDil, r, point);

	}
	
}
