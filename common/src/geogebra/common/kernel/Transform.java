package geogebra.common.kernel;

import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoTransformation;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoly;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.LimitedPath;
import geogebra.common.kernel.kernelND.AlgoTransformable;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Container for transforms
 * 
 * @author kondr
 * 
 */
public abstract class Transform {

	/**
	 * Creates label for transformed geo by appending '. No more than three 's
	 * are appended. For functions we use _1 instead.
	 * 
	 * @param geo
	 *            source geo
	 * @return label for transformed geo
	 */
	public static String transformedGeoLabel(GeoElement geo) {
		if (geo.isGeoFunction()) {
			if (geo.isLabelSet() && !geo.hasIndexLabel())
				return geo.getFreeLabel(geo.getLabel(StringTemplate.defaultTemplate));
			return null;
		}

		if (geo.isLabelSet() && !geo.hasIndexLabel()
				&& !geo.getLabelSimple().endsWith("'''")) {
			return geo.getFreeLabel(geo.getLabelSimple() + "'");
		}
		return null;
	}

	/**
	 * Apply the transform to given element and set label for result
	 * 
	 * @param geo input geo
	 * @return transformed geo
	 */
	
	public GeoElement doTransform(GeoElement geo) {
		return getTransformAlgo(geo).getResult();
	}

	/** construction */
	protected Construction cons;

	/**
	 * @param label label for transformed polygon
	 * @param poly input polygon
	 * @return transformed polygon
	 */
	final public GeoElement[] transformPoly(String label, GeoPolygon poly) {
		return transformPoly(label, poly, transformPoints(poly.getPoints()));
	}

	/**
	 * Apply the transform to given element and set label for result
	 * 
	 * @param transformedLabel label for transformed geo
	 * @param geo input geo
	 * @return transformed geo
	 */
	
	public GeoElement[] transform(GeoElement geo, String transformedLabel) {
		String label = transformedLabel;
		
		// for geo with parent algorithm that handles the transformation
		AlgoElement algo = geo.getParentAlgorithm();
		if ((algo != null) && (algo instanceof AlgoTransformable)){
			return ((AlgoTransformable) algo).getTransformedOutput(this);
		}
		
		// for polygons we transform
		if (geo instanceof GeoPoly && this.isAffine()) {
			GeoPoly poly = (GeoPoly) geo;
			if (poly.isVertexCountFixed() && poly.isAllVertexLabelsSet())
				return transformPoly(label, poly,
						transformPoints(poly.getPointsND()));
		}
		if (label == null)
			label = transformedGeoLabel(geo);

		// handle segments, rays and arcs separately
		// in case these are not e.g. parts of list
		if (geo.isLimitedPath()
				&& ((LimitedPath) geo).isAllEndpointsLabelsSet()) {

			GeoElement[] geos = ((LimitedPath) geo).createTransformedObject(
					this, label);
			// TODO: make sure orientation of arcs is OK
			// if (geos[0] instanceof Orientable && geoMir instanceof
			// Orientable)
			// ((Orientable)geos[0]).setOppositeOrientation(
			// (Orientable)geoMir);

			return geos;
		}

		// standard case
		GeoElement ret = doTransform(geo);
		ret.setLabel(label);
		ret.setVisualStyleForTransformations(geo);
		GeoElement[] geos = { ret };
		return geos;

	}

	/**
	 * Returns algo that will be used for traansforming given geo
	 * 
	 * @param geo input geo
	 * @return algo that will be used for traansforming given geo
	 */
	protected abstract AlgoTransformation getTransformAlgo(GeoElement geo);

	private GeoElement[] transformPoly(String label,
			GeoPoly oldPoly, GeoPointND[] transformedPoints) {
		// get label for polygon
		String[] polyLabel = null;
		if (label == null) {
			if (((GeoElement) oldPoly).isLabelSet()) {
				polyLabel = new String[1];
				polyLabel[0] = transformedGeoLabel((GeoElement) oldPoly);
			}
		} else {
			polyLabel = new String[1];
			polyLabel[0] = label;
		}

		// use visibility of points for transformed points
		GeoPointND[] oldPoints = oldPoly.getPoints();
		for (int i = 0; i < oldPoints.length; i++) {
			setVisualStyleForTransformations(
					(GeoElement) oldPoints[i], 
					(GeoElement) transformedPoints[i]);
			cons.getKernel().notifyUpdate((GeoElement) transformedPoints[i]);
		}

		GeoElement[] ret;

		// build the polygon from the transformed points
		if (oldPoly instanceof GeoPolygon)
			ret = cons.getKernel().PolygonND(polyLabel, transformedPoints);
		else
			ret = cons.getKernel().PolyLineND(polyLabel, transformedPoints);

		for (int i = 0; i < ret.length; i++) {
			setVisualStyleForTransformations(
					(GeoElement) oldPoly, 
					ret[i]);
		}

		return ret;
	}

	/**
	 * Applies the transform to all points
	 * 
	 * @param points input points
	 * @return array of transformed points
	 */
	
	public GeoPointND[] transformPoints(GeoPointND[] points) {
		// dilate all points
		GeoPointND[] newPoints = new GeoPointND[points.length];
		for (int i = 0; i < points.length; i++) {
			String pointLabel = transformedGeoLabel((GeoElement) points[i]);
			newPoints[i] = (GeoPointND) transform((GeoElement) points[i],
					pointLabel)[0];
			((GeoElement) newPoints[i])
					.setVisualStyleForTransformations((GeoElement) points[i]);
		}
		return newPoints;
	}

	/**
	 * Applies the transform to a conic
	 * 
	 * @param conic input conic
	 * @return transformed conic
	 */
	
	public GeoConic getTransformedConic(GeoConic conic) {
		GeoConic ret = (GeoConic) doTransform(conic);
		ret.setVisualStyleForTransformations(conic);
		return ret;
	}

	/**
	 * Applies the transform to a line
	 * 
	 * @param line input line
	 * @return transformed line
	 */
	
	public GeoElement getTransformedLine(GeoLineND line) {
		GeoElement ret = doTransform((GeoElement) line);
		ret.setVisualStyleForTransformations((GeoElement) line);
		return ret;
	}

	/**
	 * True if the transformation is affine
	 * 
	 * @return true by default, overriden e.g. for circle inverse
	 */
	
	public boolean isAffine() {
		return true;
	}

	/**
	 * True if the transform preserves angles
	 * 
	 * @return true iff similar
	 */
	
	public boolean isSimilar() {
		return true;
	}

	/**
	 * Returns true when orientation of e.g. semicircles is changed
	 * 
	 * @return true iff changes orientation of objects
	 */
	
	public boolean changesOrientation() {
		return false;
	}
	

	/**
	 * set the visual style of transformed geo regarding input
	 * @param input input geo
	 * @param transformed transformed geo
	 */
	static final public void setVisualStyleForTransformations(GeoElement input, GeoElement transformed){
		transformed.setEuclidianVisible(input.isSetEuclidianVisible());
		transformed.setVisualStyleForTransformations(input);
	}
}
