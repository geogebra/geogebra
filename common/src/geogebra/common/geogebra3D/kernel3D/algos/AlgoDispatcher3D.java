package geogebra.common.geogebra3D.kernel3D.algos;

import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.geogebra3D.kernel3D.commands.CommandProcessor3D;
import geogebra.common.geogebra3D.kernel3D.geos.GeoVector3D;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Manager3DInterface;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.Region;
import geogebra.common.kernel.Matrix.CoordSys;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoClosestPoint;
import geogebra.common.kernel.algos.AlgoDispatcher;
import geogebra.common.kernel.algos.AlgoVertexPolygon;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoly;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoCoordSys2D;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoVectorND;

/**
 * extending 2D AlgoDispatcher
 * 
 * @author mathieu
 *
 */
public class AlgoDispatcher3D extends AlgoDispatcher {

	/**
	 * Constructor
	 * 
	 * @param cons
	 *            Construction
	 */
	public AlgoDispatcher3D(Construction cons) {
		super(cons);

	}

	@Override
	public AlgoClosestPoint getNewAlgoClosestPoint(Construction cons2,
			Path path, GeoPointND point) {

		if (((GeoElement) path).isGeoElement3D() || point.isGeoElement3D())
			return new AlgoClosestPoint3D(cons2, path, point);

		return super.getNewAlgoClosestPoint(cons2, path, point);
	}

	@Override
	public GeoNumeric Distance(String label, GeoLineND g, GeoLineND h) {

		if (g.isGeoElement3D() || h.isGeoElement3D()) {
			AlgoDistanceLines3D algo = new AlgoDistanceLines3D(cons, label, g,
					h);
			return algo.getDistance();
		}

		return super.Distance(label, g, h);
	}

	@Override
	public GeoPointND IntersectLines(String label, GeoLineND g, GeoLineND h) {

		if (((GeoElement) g).isGeoElement3D()
				|| ((GeoElement) h).isGeoElement3D()) {
			return (GeoPointND) getManager3D().Intersect(label, (GeoElement) g,
					(GeoElement) h);
		}

		return super.IntersectLines(label, g, h);

	}

	@Override
	protected GeoVectorND createVector(String label, GeoPointND P) {
		if (P.isGeoElement3D()) {
			AlgoVectorPoint3D algo = new AlgoVectorPoint3D(cons, label, P);
			return algo.getVector();
		}

		return super.createVector(label, P);

	}

	@Override
	public GeoPointND[] IntersectConics(String[] labels, GeoConicND a,
			GeoConicND b) {

		if (((GeoElement) a).isGeoElement3D()
				|| ((GeoElement) b).isGeoElement3D())
			return getManager3D().IntersectConics(labels, a, b);
		return super.IntersectConics(labels, a, b);
	}

	@Override
	public GeoPointND[] IntersectLineConic(String[] labels, GeoLineND g,
			GeoConicND c) {

		if (((GeoElement) g).isGeoElement3D()
				|| ((GeoElement) c).isGeoElement3D())
			return getManager3D().IntersectLineConic(null, g, c);

		return super.IntersectLineConic(labels, g, c);
	}

	private Manager3DInterface getManager3D() {
		return cons.getKernel().getManager3D();
	}

	@Override
	public AlgoVertexPolygon newAlgoVertexPolygon(Construction cons1,
			String[] labels, GeoPoly p) {

		if (p.isGeoElement3D()) {
			return new AlgoVertexPolygon3D(cons1, labels, p);
		}

		return super.newAlgoVertexPolygon(cons1, labels, p);
	}

	@Override
	protected GeoElement[] SegmentFixed(String pointLabel, String segmentLabel,
			GeoPointND A, NumberValue n) {

		Kernel kernel = cons.getKernel();
		GeoDirectionND orientation = CommandProcessor3D
				.getCurrentViewOrientation(kernel, cons.getApplication());

		if (orientation == kernel.getSpace()) { // create a sphere
			return SegmentFixedSphere(pointLabel, segmentLabel, A, n);
		}

		if (A.isGeoElement3D()) {

			if (orientation == null) { // create a sphere
				return SegmentFixedSphere(pointLabel, segmentLabel, A, n);
			}

			// create a circle around A with radius n
			AlgoCircle3DPointRadiusDirection algoCircle = new AlgoCircle3DPointRadiusDirection(
					cons, A, n, orientation);

			cons.removeFromConstructionList(algoCircle);
			// place the new point on the circle
			Coords coords = A.getInhomCoordsInD3();
			if (orientation instanceof GeoCoordSys2D) {
				CoordSys cs = ((GeoCoordSys2D) orientation).getCoordSys();
				Coords project = cs.getNormalProjection(coords)[1];
				coords = cs.getPoint(project.getX() + n.getDouble(),
						project.getY());
			} else {
				coords = coords.copyVector();
				coords.setX(coords.getX() + n.getDouble());
			}
			AlgoPoint3DOnPath algoPoint = new AlgoPoint3DOnPath(cons,
					pointLabel, algoCircle.getCircle(), coords.getX(),
					coords.getY(), coords.getZ());

			// return segment and new point
			GeoElement[] ret = {
					(GeoElement) getManager3D().Segment3D(segmentLabel, A,
							algoPoint.getP()), (GeoElement) algoPoint.getP() };

			return ret;
		}

		return super.SegmentFixed(pointLabel, segmentLabel, A, n);
	}

	private Coords tmpCoords;

	private GeoElement[] SegmentFixedSphere(String pointLabel,
			String segmentLabel, GeoPointND A, NumberValue n) {
		// create a sphere around A with radius n
		AlgoSpherePointRadius algoSphere = new AlgoSpherePointRadius(cons, A, n);

		cons.removeFromConstructionList(algoSphere);
		// place the new point on the circle
		Coords coords = A.getInhomCoordsInD3();
		if (tmpCoords == null) {
			tmpCoords = Coords.createInhomCoorsInD3();
		} else {
			tmpCoords.setW(1.0);
		}
		tmpCoords.setX(coords.getX() + n.getDouble());
		tmpCoords.setY(coords.getY());
		tmpCoords.setZ(coords.getZ());
		AlgoPoint3DInRegion algoPoint = new AlgoPoint3DInRegion(cons,
				pointLabel, algoSphere.getSphere(), tmpCoords);

		// return segment and new point
		GeoElement[] ret = {
				(GeoElement) getManager3D().Segment3D(segmentLabel, A,
						algoPoint.getP()), algoPoint.getP() };

		return ret;
	}

	@Override
	final public GeoElement[] Polygon(String[] labels, GeoPointND[] P) {

		for (int i = 0; i < P.length; i++) {
			if (P[i].isGeoElement3D()) {
				return getManager3D().Polygon3D(labels, P);
			}
		}

		return super.Polygon(labels, P);
	}

	@Override
	final public GeoConicND Circle(String label, GeoPointND M, NumberValue r) {
		if (M.isGeoElement3D()) {
			return getManager3D().Circle3D(label, M, r);
		}

		return super.Circle(label, M, r);

	}

	@Override
	public GeoPointND PointIn(String label, Region region, Coords coords,
			boolean addToConstruction, boolean complex, boolean coords2D) {

		if (region.isGeoElement3D()) {
			return getManager3D().Point3DIn(label, region, coords,
					addToConstruction, coords2D);
		}

		return super.PointIn(label, region, coords, addToConstruction, complex,
				coords2D);
	}

	@Override
	public GeoPointND Point(String label, Path path, Coords coords,
			boolean addToConstruction, boolean complex, boolean coords2D) {

		if (path.isGeoElement3D()) {
			return getManager3D().Point3D(label, path, coords.getX(),
					coords.getY(), coords.getZ(), addToConstruction, coords2D);
		}

		return super.Point(label, path, coords, addToConstruction, complex,
				coords2D);
	}

	@Override
	protected GeoPointND copyFreePoint(GeoPointND point,
			EuclidianViewInterfaceCommon view) {
		if (point.isGeoElement3D()) {
			double xOffset = 0, yOffset = 0;
			if (!view.isEuclidianView3D()) {
				xOffset = DETACH_OFFSET * view.getInvXscale();
				yOffset = DETACH_OFFSET * view.getInvYscale();
			}

			return (GeoPointND) getManager3D().Point3D(null,
					point.getInhomX() + xOffset, point.getInhomY() + yOffset,
					point.getInhomZ(),
					point.getMode() == Kernel.COORD_CARTESIAN);
		}

		return super.copyFreePoint(point, view);
	}

	public GeoVectorND Vector3D(String label) {
		return (GeoVectorND) getManager3D().Vector3D(label, 0, 0, 0);
	}

	public GeoVectorND Vector3D() {
		GeoVector3D v = new GeoVector3D(cons);
		v.setCoords(0, 0, 0, 0);
		v.setMode(Kernel.COORD_CARTESIAN_3D);
		return v;
	}

	@Override
	public GeoElement[] TranslateND(String label, GeoElement geoTrans,
			GeoVectorND v) {
		return getManager3D().Translate3D(label, geoTrans, v);
	}

}
