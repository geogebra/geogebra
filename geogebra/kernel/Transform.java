package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.util.GgbMat;

import org.apache.commons.math.linear.LUDecompositionImpl;

/**
 * Container for transforms
 * 
 * @author kondr
 * 
 */
public abstract class Transform {

	/**
	 * Creates label for transformed geo by appending '. No more than
	 * three 's are appended. For functions we use _1 instead.
	 * @param geo source geo
	 * @return label for transformed geo
	 */
	public static String transformedGeoLabel(GeoElement geo) {
		if(geo.isGeoFunction()){
			if (geo.isLabelSet() && !geo.hasIndexLabel())
				return geo.getFreeLabel(geo.getLabel());
			return null;
		}
		
		if (geo.isLabelSet() && !geo.hasIndexLabel()
				&& !geo.label.endsWith("'''")) {
			return geo.getFreeLabel(geo.label + "'");
		} else {
			return null;
		}
	}
	/**
	 * Apply the transform to given element and set label for result 
	 * 
	 * @param geo
	 * @return transformed geo
	 */
	protected GeoElement doTransform(GeoElement geo){
		return getTransformAlgo(geo).getResult();
	}

	/** construction */
	protected Construction cons;

	/**
	 * @param label
	 * @param poly
	 * @return transformed polygon
	 */
	final public GeoElement[] transformPoly(String label, GeoPolygon poly) {
		return transformPoly(label, poly, transformPoints(poly.getPoints()));
	}

	/**
	 * Apply the transform to given element and set label for result 
	 * @param label
	 * @param geo
	 * @return transformed geo
	 */
	public GeoElement[] transform(GeoElement geo, String label) {
		//for polygons we transform
		if (geo instanceof GeoPolyLineInterface && this.isAffine()) {
			GeoPolyLineInterface poly = (GeoPolyLineInterface) geo;
			if(poly.isVertexCountFixed() && poly.isAllVertexLabelsSet())
				return transformPoly(label, poly, transformPoints(poly.getPoints()));
		}		
		if (label == null)
			label = transformedGeoLabel(geo);

		// handle segments, rays and arcs separately 
		// in case these are not e.g. parts of list
		if (geo.isLimitedPath() && ((LimitedPath)geo).isAllEndpointsLabelsSet()) {
			
			GeoElement[] geos = ((LimitedPath) geo)
					.createTransformedObject(this,label);
			//TODO: make sure orientation of arcs is OK
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
	 * @param geo
	 * @return algo that will be used for traansforming given geo
	 */
	protected abstract AlgoTransformation getTransformAlgo(GeoElement geo);
	
	private GeoElement[] transformPoly(String label, GeoPolyLineInterface oldPoly,
			GeoPoint[] transformedPoints) {
		// get label for polygon
		String[] polyLabel = null;
		if (label == null) {
			if (((GeoElement)oldPoly).isLabelSet()) {
				polyLabel = new String[1];
				polyLabel[0] = transformedGeoLabel((GeoElement)oldPoly);
			}
		} else {
			polyLabel = new String[1];
			polyLabel[0] = label;
		}

		// use visibility of points for transformed points
		GeoPointND[] oldPoints = oldPoly.getPoints();
		for (int i = 0; i < oldPoints.length; i++) {
			transformedPoints[i].setEuclidianVisible(((GeoElement) oldPoints[i])
					.isSetEuclidianVisible());
			transformedPoints[i].setVisualStyleForTransformations((GeoElement) oldPoints[i]);
			cons.getKernel().notifyUpdate(transformedPoints[i]);
		}
		
		GeoElement [] ret;

		// build the polygon from the transformed points
		if(oldPoly instanceof GeoPolygon)
			ret = cons.getKernel().Polygon(polyLabel, transformedPoints);
		else
			ret = cons.getKernel().PolyLine(polyLabel, transformedPoints);
		
		for (int i = 0; i < ret.length; i++) {
			ret[i].setEuclidianVisible(((GeoElement)oldPoly)
					.isSetEuclidianVisible());
			ret[i].setVisualStyleForTransformations(((GeoElement)oldPoly));
		}	
		
		return ret;
	}
	
	
	/**
	 * Applies the transform to all points
	 * @param points
	 * @return array of transformed points
	 */
	public GeoPoint[] transformPoints(GeoPointND[] points) {
		// dilate all points
		GeoPoint[] newPoints = new GeoPoint[points.length];
		for (int i = 0; i < points.length; i++) {
			String pointLabel = transformedGeoLabel((GeoElement) points[i]);
			newPoints[i] = (GeoPoint) transform((GeoElement) points[i], pointLabel)[0];
			newPoints[i].setVisualStyleForTransformations((GeoElement) points[i]);
		}
		return newPoints;
	}

	/**
	 * Applies the transform to a conic
	 * @param conic
	 * @return transformed conic
	 */
	public GeoConic getTransformedConic(GeoConic conic) {
		GeoConic ret = (GeoConic) doTransform(conic);
		ret.setVisualStyleForTransformations(conic);
		return ret;
	}

	/**
	 * Applies the transform to a line
	 * @param line
	 * @return transformed line
	 */
	public GeoElement getTransformedLine(GeoLine line) {
		GeoElement ret = doTransform(line);
		ret.setVisualStyleForTransformations(line);
		return ret;
	}
	
	/** 
	 * True if the transformation is affine
	 * @return true by default, overriden e.g. for circle inverse
	 */
	public boolean isAffine() {
		return true;
	}
	/**
	 * True if the transform preserves angles 
	 * @return true iff similar
	 */
	public boolean isSimilar() {		
		return true;
	}
	
	/**
	 * Returns true when orientation of e.g. semicircles is changed
	 * @return true iff changes orientation of objects
	 */
	public boolean changesOrientation() {
		return false;
	}	
}

/**
 * Rotation
 * 
 * @author kondr
 * 
 */
class TransformRotate extends Transform {

	private GeoPoint center;
	private NumberValue angle;

	/**
	 * @param cons 
	 * @param angle
	 */
	public TransformRotate(Construction cons,NumberValue angle) {
		this.angle = angle;
		this.cons = cons;
	}
	
	/**
	 * @param cons 
	 * @param angle
	 * @param center
	 */
	public TransformRotate(Construction cons,NumberValue angle,GeoPoint center) {
		this.angle = angle;
		this.center = center;
		this.cons = cons;
	}

	@Override
	protected AlgoTransformation getTransformAlgo(GeoElement geo) {
		AlgoTransformation algo = null;
		if (center == null) {
			algo = new AlgoRotate(cons,geo,angle);
		}
		else algo = new AlgoRotatePoint(cons,geo,angle,center);
		return algo;
	}

}

/**
 * Translation
 * 
 * @author kondr
 * 
 */
class TransformTranslate extends Transform {

	private GeoVec3D transVec;

	/**
	 * @param cons 
	 * @param transVec
	 */
	public TransformTranslate(Construction cons,GeoVec3D transVec) {
		this.transVec = transVec;
		this.cons = cons;
	}

	@Override
	protected AlgoTransformation getTransformAlgo(GeoElement geo) {
		AlgoTranslate algo = new AlgoTranslate(cons, geo, transVec);
		return algo;
	}

}

/**
 * Dilation
 * 
 * @author kondr
 * 
 */
class TransformDilate extends Transform {

	private NumberValue ratio;
	private GeoPoint center;

	/**
	 * @param cons 
	 * @param ratio
	 */
	public TransformDilate(Construction cons,NumberValue ratio) {
		this.ratio = ratio;
		this.cons = cons;
	}

	/**
	 * @param cons 
	 * @param ratio
	 * @param center
	 */
	public TransformDilate(Construction cons,NumberValue ratio, GeoPoint center) {
		this.ratio = ratio;
		this.center = center;
		this.cons = cons;
	}

	@Override
	protected AlgoTransformation getTransformAlgo(GeoElement geo) {
		AlgoDilate algo = new AlgoDilate(cons, geo, ratio, center);
		return algo;
	}

}

/**
 * Mirror
 * 
 * @author kondr
 * 
 */
class TransformMirror extends Transform {

	private GeoElement mirror;

	/**
	 * @param cons 
	 * @param mirrorPoint
	 */
	public TransformMirror(Construction cons,GeoPoint mirrorPoint) {
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

/**
 * Shear or stretch
 * 
 * @author kondr
 * 
 */
class TransformShearOrStretch extends Transform {

	private boolean shear;
	private GeoVec3D line;
	private NumberValue num;

	/**
	 * @param cons 
	 * @param line
	 * @param num
	 * @param shear
	 */
	public TransformShearOrStretch(Construction cons,GeoVec3D line, GeoNumeric num, boolean shear) {
		this.shear = shear;
		this.line = line;
		this.num = num;
		this.cons = cons;
	}

	@Override
	protected AlgoTransformation getTransformAlgo(GeoElement geo) {
		AlgoShearOrStretch algo = new AlgoShearOrStretch(cons, geo, line, num,
				shear);
		return algo;
	}
	
	@Override
	public boolean isSimilar() {
		return false;
	}
	
	public boolean changesOrientation() {
		return !shear && num.getDouble()<0;
	}	

}

/**
 * Generic affine transform
 * 
 * @author kondr
 * 
 */
class TransformApplyMatrix extends Transform {

	private GeoList matrix;

	
	/**
	 * @param cons 
	 * @param matrix
	 */
	public TransformApplyMatrix(Construction cons,GeoList matrix) {
		this.matrix = matrix;
		this.cons = cons;
	}

	@Override
	protected AlgoTransformation getTransformAlgo(GeoElement geo) {
		AlgoApplyMatrix algo = new AlgoApplyMatrix(cons, geo, matrix);
		return algo;
	}
	
	@Override
	public boolean isSimilar() {
		return false;
	}
	
	public boolean changesOrientation() {
		GgbMat ggbMatrix = new GgbMat(matrix);
		return new LUDecompositionImpl(ggbMatrix).getDeterminant()<0;
	}

}
