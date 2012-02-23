package geogebra3D.euclidianFor3D;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.Region;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoVectorND;
import geogebra.euclidian.EuclidianController;

/**
 * class for Euclidian Controller used in ggb3D
 * @author matthieu
 *
 */
public class EuclidianControllerFor3D extends EuclidianController {

	/**
	 * constructor
	 * @param kernel kernel
	 */
	public EuclidianControllerFor3D(Kernel kernel) {
		super(kernel);
	}
	
	@Override
	protected GeoAngle createAngle(GeoPointND A, GeoPointND B, GeoPointND C){
		if (((GeoElement) A).isGeoElement3D() || ((GeoElement) B).isGeoElement3D() || ((GeoElement) C).isGeoElement3D()) {			
			return kernel.getManager3D().Angle3D(null, A, B, C);
		}
			
		return kernel.Angle(null, (GeoPoint2) A, (GeoPoint2) B, (GeoPoint2) C);
		
	}
	
	@Override
	protected GeoElement[] translate(GeoElement geo, GeoVectorND vec){
		if (geo.isGeoElement3D() || ((GeoElement) vec).isGeoElement3D()) {
			return kernel.getManager3D().Translate3D(null, geo, vec);
		}
			
		return kernel.Translate(null, geo, (GeoVector) vec);
		
	}
	
	@Override
	protected boolean attach(GeoPointND p, Path path) {
		if (!((GeoElement) p).isGeoElement3D())
			return super.attach(p, path);
		return false;
	}
	
	@Override
	protected boolean attach(GeoPointND p, Region region) {
		if (!((GeoElement) p).isGeoElement3D())
			return super.attach(p, region);
		return false;
	}
	
	@Override
	protected boolean detach(GeoPointND p) {
		if (!((GeoElement) p).isGeoElement3D())
			return super.detach(p);
		return false;
	}

	
}
