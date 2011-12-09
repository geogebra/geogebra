package geogebra.kernel;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoTransformation;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.kernel.algos.AlgoMirror;

/**
 * Mirror
 * 
 * @author kondr
 * 
 */
public class TransformMirror extends Transform {

	private GeoElement mirror;

	/**
	 * @param cons 
	 * @param mirrorPoint
	 */
	public TransformMirror(Construction cons,GeoPoint2 mirrorPoint) {
		mirror = mirrorPoint;
		this.cons = cons;
	}

	/**
	 * @param cons 
	 * @param mirrorCircle
	 */
	public TransformMirror(Construction cons,GeoConic mirrorCircle) {
		mirror = mirrorCircle;
		this.cons = cons;
	}

	/**
	 * @param cons 
	 * @param mirrorLine
	 */
	public TransformMirror(Construction cons,GeoLine mirrorLine) {
		mirror = mirrorLine;
		this.cons = cons;
	}

	@Override
	protected AlgoTransformation getTransformAlgo(GeoElement geo) {
		AlgoMirror algo = null;
		if (mirror.isGeoLine()) {
			algo = new AlgoMirror(cons, geo, (GeoLine) mirror, null, null);
		} else if (mirror.isGeoPoint()) {
			algo = new AlgoMirror(cons, geo, null, (GeoPoint2) mirror, null);
		} else {
			algo = new AlgoMirror(cons, geo, null, null, (GeoConic) mirror);
		}
		return algo;
	}
	
	@Override
	public boolean isAffine() {
		return ! mirror.isGeoConic();
	}
	
	@Override
	public boolean changesOrientation() {
		return mirror.isGeoLine() || mirror.isGeoConic();
	}	

}

