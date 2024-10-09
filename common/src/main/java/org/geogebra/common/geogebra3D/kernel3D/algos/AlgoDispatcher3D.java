package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.geogebra3D.kernel3D.commands.CommandProcessor3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoVector3D;
import org.geogebra.common.geogebra3D.kernel3D.implicit3D.AlgoIntersectImplicitSurface;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Manager3DInterface;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.advanced.AlgoAxis;
import org.geogebra.common.kernel.algos.AlgoAxesQuadricND;
import org.geogebra.common.kernel.algos.AlgoClosestPoint;
import org.geogebra.common.kernel.algos.AlgoDispatcher;
import org.geogebra.common.kernel.algos.AlgoPolygon;
import org.geogebra.common.kernel.algos.AlgoVertexPolygon;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoly;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoImplicitSurfaceND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.GeoClass;

/**
 * extending 2D AlgoDispatcher
 * 
 * @author mathieu
 *
 */
public class AlgoDispatcher3D extends AlgoDispatcher {

	private Coords tmpCoords;

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

		if (((GeoElement) path).isGeoElement3D() || point.isGeoElement3D()) {
			return new AlgoClosestPoint3D(cons2, path, point);
		}

		return super.getNewAlgoClosestPoint(cons2, path, point);
	}

	@Override
	public GeoNumeric distance(String label, GeoLineND g, GeoLineND h) {

		if (g.isGeoElement3D() || h.isGeoElement3D()) {
			AlgoDistanceLines3D algo = new AlgoDistanceLines3D(cons, g,
					h);
			algo.getDistance().setLabel(label);
			return algo.getDistance();
		}

		return super.distance(label, g, h);
	}

	@Override
	public GeoPointND intersectLines(String label, GeoLineND g, GeoLineND h) {

		if (g.isGeoElement3D() || h.isGeoElement3D()) {
			return (GeoPointND) getManager3D().intersect(label, g, h);
		}

		return super.intersectLines(label, g, h);
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
	public GeoPointND[] intersectConics(String[] labels, GeoConicND a,
			GeoConicND b) {

		if (a.isGeoElement3D() || b.isGeoElement3D()) {
			return getManager3D().intersectConics(labels, a, b);
		}
		return super.intersectConics(labels, a, b);
	}

	@Override
	public GeoPointND[] intersectLineConic(String[] labels, GeoLineND g,
			GeoConicND c) {

		if (g.isGeoElement3D() || c.isGeoElement3D()) {
			return getManager3D().intersectLineConic(null, g, c);
		}

		return super.intersectLineConic(labels, g, c);
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
	protected GeoElement[] segmentFixed(String pointLabel, String segmentLabel,
			GeoPointND A, GeoNumberValue n) {

		Kernel kernel = cons.getKernel();
		GeoDirectionND orientation = CommandProcessor3D
				.getCurrentViewOrientation(kernel, cons.getApplication());

		if (orientation == kernel.getSpace()) { // create a sphere
			return segmentFixedSphere(pointLabel, segmentLabel, A, n);
		}

		if (A.isGeoElement3D()) {

			if (orientation == null) { // create a sphere
				return segmentFixedSphere(pointLabel, segmentLabel, A, n);
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
					algoCircle.getCircle(), coords.getX(),
					coords.getY(), coords.getZ());
			algoPoint.getP().setLabel(pointLabel);
			// return segment and new point
			GeoElement[] ret = { (GeoElement) getManager3D()
					.segment3D(segmentLabel, A, algoPoint.getP()),
					(GeoElement) algoPoint.getP() };

			return ret;
		}

		return super.segmentFixed(pointLabel, segmentLabel, A, n);
	}

	private GeoElement[] segmentFixedSphere(String pointLabel,
			String segmentLabel, GeoPointND A, GeoNumberValue n) {
		// create a sphere around A with radius n
		AlgoSpherePointRadius algoSphere = new AlgoSpherePointRadius(cons, A,
				n);

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
		GeoElement[] ret = { (GeoElement) getManager3D().segment3D(segmentLabel,
				A, algoPoint.getP()), algoPoint.getP() };

		return ret;
	}

	@Override
	final public GeoElement[] polygon(String[] labels, GeoPointND[] P) {

		for (int i = 0; i < P.length; i++) {
			if (P[i].isGeoElement3D()) {
				return getManager3D().polygon3D(labels, P);
			}
		}

		return super.polygon(labels, P);
	}

	@Override
	final public GeoConicND circle(String label, GeoPointND M,
			GeoNumberValue r) {
		if (M.isGeoElement3D()) {
			return getManager3D().circle3D(label, M, r);
		}

		return super.circle(label, M, r);

	}

	@Override
	public GeoPointND pointIn(String label, Region region, Coords coords,
			boolean addToConstruction, boolean complex, boolean coords2D) {

		if (region.isRegion3D()) {
			return getManager3D().point3DIn(label, region, coords,
					addToConstruction, coords2D);
		}

		return super.pointIn(label, region, coords, addToConstruction, complex,
				coords2D);
	}

	@Override
	public GeoPointND point(String label, Path path, Coords coords,
			boolean addToConstruction, boolean complex, boolean coords2D) {

		if (path.isGeoElement3D()) {
			return getManager3D().point3D(label, path, coords.getX(),
					coords.getY(), coords.getZ(), addToConstruction, coords2D);
		}

		return super.point(label, path, coords, addToConstruction, complex,
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

			GeoPointND ret = getManager3D().point3D(
					point.getInhomX() + xOffset, point.getInhomY() + yOffset,
					point.getInhomZ(),
					point.getToStringMode() == Kernel.COORD_CARTESIAN);
			ret.setLabel(null);
			return ret;
		}

		return super.copyFreePoint(point, view);
	}

	/**
	 * @param label
	 *            label
	 * @return 3D vector (0,0,0)
	 */
	public GeoVectorND vector3D(String label) {
		GeoVectorND ret = (GeoVectorND) getManager3D().vector3D(0, 0, 0);
		ret.setLabel(label);
		return ret;
	}

	/**
	 * @return 3D vector (0,0,0)
	 */
	public GeoVectorND vector3D() {
		GeoVector3D v = new GeoVector3D(cons);
		v.setCoords(0, 0, 0, 0);
		v.setMode(Kernel.COORD_CARTESIAN_3D);
		return v;
	}

	@Override
	public GeoElement[] translateND(String label, GeoElementND geoTrans,
			GeoVectorND v) {
		return getManager3D().translate3D(label, geoTrans, v);
	}

	@Override
	protected GeoElement locusNoCheck(String label, GeoPointND Q,
			GeoNumeric P) {
		if (Q.isGeoElement3D()) {
			return getManager3D().locus3D(label, Q, P);
		}
		return super.locusNoCheck(label, Q, P);
	}

	@Override
	public GeoElement[] intersectImplicitSurfaceLine(String[] labels,
			GeoImplicitSurfaceND surf, GeoElementND line) {
		AlgoIntersectImplicitSurface algo = new AlgoIntersectImplicitSurface(
				cons, labels, surf, line);
		GeoElement[] out = algo.getIntersectionPoints();
		algo.setLabels(labels);
		return out;
	}

	@Override
	final public GeoElement[] polygon(String[] labels, GeoList pointList) {
		AlgoPolygon algo;
		if (pointList.getElementType() == GeoClass.POINT3D) {
			algo = new AlgoPolygon3D(cons, labels, null, pointList);
		} else {
			algo = new AlgoPolygon(cons, labels, pointList);
		}
		return algo.getOutput();
	}

	@Override
	public AlgoAxesQuadricND axesConic(GeoQuadricND c, String[] labels) {
		if (c.isGeoElement3D()) {
			return new AlgoAxes3D(cons, labels, c);
		}
		return super.axesConic(c, labels);
	}

	@Override
	public AlgoAxis axis(String label, GeoConicND conic, int axisId) {
		if (conic instanceof GeoConic3D) {
			return new AlgoAxis3D(cons, label, conic, axisId);
		}
		return super.axis(label, conic, axisId);
	}

	@Override
	public GeoElement polarLine(String label, GeoPointND P, GeoConicND c) {

		if (P.isGeoElement3D() || c.isGeoElement3D()) {
			AlgoPolarLine3D algo = new AlgoPolarLine3D(cons, label, c, P);
			return (GeoElement) algo.getLine();
		}

		return super.polarLine(label, P, c);
	}

	@Override
	public GeoElement polarPoint(String label, GeoLineND line,
			GeoConicND c) {

		if (line.isGeoElement3D() || c.isGeoElement3D()) {
			AlgoPolarPoint3D algo = new AlgoPolarPoint3D(cons, label, c, line);
			return (GeoElement) algo.getPoint();
		}

		return super.polarPoint(label, line, c);
	}

}
