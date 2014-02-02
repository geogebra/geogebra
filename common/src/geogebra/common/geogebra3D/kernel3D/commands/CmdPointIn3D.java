package geogebra.common.geogebra3D.kernel3D.commands;


import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Region;
import geogebra.common.kernel.commands.CmdPointIn;
import geogebra.common.kernel.geos.GeoElement;


/*
 * PointIn[ <Region> ] 
 */
public class CmdPointIn3D extends CmdPointIn {

	
	
	public CmdPointIn3D(Kernel kernel) {
		super(kernel);
		
		
	}	
	
	
	@Override
	protected GeoElement[] pointIn(String label, Region region){

		if (region.isGeoElement3D()){
			GeoElement[] ret = { (GeoElement) kernelA.getManager3D().Point3DIn(label, region, false)};
			return ret;
		}
		
		return super.pointIn(label, region);
		
	}
}