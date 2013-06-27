package geogebra.common.kernel;

import geogebra.common.kernel.algos.AlgoMirror;
import geogebra.common.kernel.algos.AlgoTransformation;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;

/**
 * Mirror
 * 
 * @author kondr
 * 
 */
public class TransformMirror extends Transform {

	protected GeoElement mirror;
	
	/**
	 * @param cons construction
	 * @param mirror mirror
	 */
	protected TransformMirror(Construction cons, GeoElement mirror) {
		this.mirror = mirror;
		this.cons = cons;
	}

	/**
	 * @param cons construction
	 * @param mirrorPoint mirror point
	 */
	public TransformMirror(Construction cons,GeoPoint mirrorPoint) {
		mirror = mirrorPoint;
		this.cons = cons;
	}

	/**
	 * @param cons construction
	 * @param mirrorCircle mirror circle
	 */
	public TransformMirror(Construction cons,GeoConic mirrorCircle) {
		mirror = mirrorCircle;
		this.cons = cons;
	}

	/**
	 * @param cons construction
	 * @param mirrorLine mirror line
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
			algo = new AlgoMirror(cons, geo, null, (GeoPoint) mirror, null);
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

