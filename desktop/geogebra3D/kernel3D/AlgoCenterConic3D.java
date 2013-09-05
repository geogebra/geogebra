package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoCenterConic;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Center of 3D conic
 * @author mathieu
 *
 */
public class AlgoCenterConic3D extends AlgoCenterConic{

	/**
	 * constructor
	 * @param cons
	 * @param label
	 * @param c
	 */
	public AlgoCenterConic3D(Construction cons, String label, GeoConicND c) {
		super(cons, label, c);
	}
	
    @Override
	public GeoPointND newGeoPoint(Construction cons){
    	return new GeoPoint3D(cons);
    }

    @Override
	protected void setCoords(double x, double y){
    	((GeoPoint3D) midpoint).setCoords(((GeoConicND) c).getCoordSys().getPoint(x, y));
    }
}
