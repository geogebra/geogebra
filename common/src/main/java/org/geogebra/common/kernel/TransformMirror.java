package org.geogebra.common.kernel;

import org.geogebra.common.kernel.algos.AlgoMirror;
import org.geogebra.common.kernel.algos.AlgoTransformation;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;

/**
 * Mirror
 * 
 * @author Zbynek
 * 
 */
public class TransformMirror extends Transform {

	/** Element used for mirroring */
	protected GeoElement mirror;

	/**
	 * @param cons
	 *            construction
	 * @param mirror
	 *            mirror
	 */
	protected TransformMirror(Construction cons, GeoElement mirror) {
		this.mirror = mirror;
		this.cons = cons;
	}

	/**
	 * @param cons
	 *            construction
	 * @param mirrorPoint
	 *            mirror point
	 */
	public TransformMirror(Construction cons, GeoPoint mirrorPoint) {
		mirror = mirrorPoint;
		this.cons = cons;
	}

	/**
	 * @param cons
	 *            construction
	 * @param mirrorCircle
	 *            mirror circle
	 */
	public TransformMirror(Construction cons, GeoConic mirrorCircle) {
		mirror = mirrorCircle;
		this.cons = cons;
	}

	/**
	 * @param cons
	 *            construction
	 * @param mirrorLine
	 *            mirror line
	 */
	public TransformMirror(Construction cons, GeoLine mirrorLine) {
		mirror = mirrorLine;
		this.cons = cons;
	}

	@Override
	protected AlgoTransformation getTransformAlgo(GeoElement geo) {
		AlgoMirror algo = null;
		if (mirror.isGeoLine()) {
			algo = new AlgoMirror(cons, geo, (GeoLine) mirror);
		} else if (mirror.isGeoPoint()) {
			algo = new AlgoMirror(cons, geo, (GeoPoint) mirror);
		} else {
			algo = new AlgoMirror(cons, geo, (GeoConic) mirror);
		}
		return algo;
	}

	@Override
	public boolean isAffine() {
		return !mirror.isGeoConic();
	}

	@Override
	public boolean changesOrientation() {
		return mirror.isGeoLine() || mirror.isGeoConic();
	}

}
