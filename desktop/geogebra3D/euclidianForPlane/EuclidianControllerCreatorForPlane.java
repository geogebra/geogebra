package geogebra3D.euclidianForPlane;

import geogebra.common.euclidian.EuclidianController;
import geogebra.common.geogebra3D.euclidianFor3D.EuclidianControllerCreatorFor3D;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoPointND;

/**
 * controller creator for view from plane
 * @author mathieu
 *
 */
public class EuclidianControllerCreatorForPlane extends EuclidianControllerCreatorFor3D{

	/**
	 * @param ec euclidian controller
	 */
	public EuclidianControllerCreatorForPlane(EuclidianController ec) {
		super(ec);
	}

	@Override
	protected GeoElement[] createCircle2(GeoPointND p0, GeoPointND p1){
		return createCircle2For3D(p0, p1);
	}
	
	@Override
	protected GeoConicND circle(Construction cons, GeoPointND center, NumberValue radius){
			return circleFor3D(cons, center, radius);
	}
	

	@Override
	public GeoPointND getSingleIntersectionPoint(GeoElement a, GeoElement b, boolean coords2D) {
		return super.getSingleIntersectionPoint(a, b, false);
	}	

}
