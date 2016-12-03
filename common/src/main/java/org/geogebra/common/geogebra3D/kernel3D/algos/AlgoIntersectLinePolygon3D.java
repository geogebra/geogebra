package org.geogebra.common.geogebra3D.kernel3D.algos;

import java.util.TreeMap;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSegment3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.CoordMatrixUtil;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.kernelND.HasSegments;

public class AlgoIntersectLinePolygon3D extends AlgoElement3D {

	protected int spaceDim = 3;
	protected HasSegments p;
	protected GeoLineND g;

	protected OutputHandler<GeoElement> outputPoints; // output

	private TreeMap<Double, Coords> newCoords;

	public AlgoIntersectLinePolygon3D(Construction c, String[] labels,
			GeoLineND g, GeoPolygon p) {

		this(c, labels, (GeoElement) g, p);

	}

	public AlgoIntersectLinePolygon3D(Construction c, String[] labels,
			GeoElement g, HasSegments p) {
		super(c);

		outputPoints = createOutputPoints();

		setFirstInput(g);
		this.p = p;

		newCoords = new TreeMap<Double, Coords>(
				Kernel.DoubleComparator(Kernel.STANDARD_PRECISION));

		compute();

		setInputOutput(); // for AlgoElement

		setLabels(labels);
		update();

	}

	/**
	 * set the first input
	 * 
	 * @param geo
	 *            geo
	 */
	protected void setFirstInput(GeoElement geo) {
		this.g = (GeoLineND) geo;

	}

	/**
	 * 
	 * @return first input
	 */
	protected GeoElement getFirstInput() {
		return (GeoElement) g;
	}

	protected OutputHandler<GeoElement> createOutputPoints() {
		return new OutputHandler<GeoElement>(new elementFactory<GeoElement>() {
			public GeoPoint3D newElement() {
				GeoPoint3D p = new GeoPoint3D(cons);
				p.setCoords(0, 0, 0, 1);
				p.setParentAlgorithm(AlgoIntersectLinePolygon3D.this);
				return p;
			}
		});
	}

	protected OutputHandler<GeoElement> createOutputSegments() {
		return new OutputHandler<GeoElement>(new elementFactory<GeoElement>() {
			public GeoSegment3D newElement() {

				GeoPoint3D aS = new GeoPoint3D(cons);
				aS.setCoords(0, 0, 0, 1);
				GeoPoint3D aE = new GeoPoint3D(cons);
				aE.setCoords(0, 0, 0, 1);
				GeoSegment3D a = new GeoSegment3D(cons, aS, aE);
				a.setParentAlgorithm(AlgoIntersectLinePolygon3D.this);
				return a;
			}
		});
	}

	protected Coords o1, d1;

	protected void setIntersectionLine() {

		o1 = g.getPointInD(3, 0).getInhomCoordsInSameDimension();
		d1 = g.getPointInD(3, 1).getInhomCoordsInSameDimension().sub(o1);
	}

	/**
	 * calc intersection coords
	 * 
	 * @param p
	 *            polygon
	 * @param newCoords
	 *            coords
	 */
	protected void intersectionsCoords(HasSegments hasSegments,
			TreeMap<Double, Coords> newCoords) {

		// TODO: move these to intersectLinePolyline3D

		GeoPolygon p = (GeoPolygon) hasSegments;

		// check if the line is contained by the polygon plane
		switch (AlgoIntersectCS1D2D.getConfigLinePlane(g, p)) {
		case GENERAL: // intersect line/interior of polygon
			intersectionsCoordsGeneral(p, newCoords);
			break;
		case CONTAINED: // intesect line/segments
			intersectionsCoordsContained(p, newCoords);
			break;
		case PARALLEL: // no intersection
			break;

		}
	}

	/**
	 * calc intersection coords when line is contained in polygon's plane
	 * 
	 * @param p
	 *            polygon
	 * @param newCoords
	 *            coords
	 */
	protected void intersectionsCoordsContained(HasSegments p,
			TreeMap<Double, Coords> newCoords) {

		// line origin and direction
		setIntersectionLine();

		for (int i = 0; i < p.getSegments().length; i++) {
			GeoSegmentND seg = p.getSegments()[i];

			Coords o2 = seg.getPointInD(3, 0).getInhomCoordsInSameDimension();
			Coords d2 = seg.getPointInD(3, 1).getInhomCoordsInSameDimension()
					.sub(o2);

			Coords[] project = CoordMatrixUtil.nearestPointsFromTwoLines(o1,
					d1, o2, d2);

			// check if projection is intersection point
			if (project != null
					&& project[0].equalsForKernel(project[1],
							Kernel.STANDARD_PRECISION)) {

				double t1 = project[2].get(1); // parameter on line
				double t2 = project[2].get(2); // parameter on segment

				if (checkParameter(t1) && seg.respectLimitedPath(t2))
					newCoords.put(t1, project[0]);

			}
		}

	}

	/**
	 * calc intersection coords when line is not contained in polygon's plane
	 * 
	 * @param p
	 *            polygon
	 * @param newCoords
	 *            coords
	 */
	protected void intersectionsCoordsGeneral(GeoPolygon p,
			TreeMap<Double, Coords> newCoords) {

		Coords globalCoords = new Coords(4);
		Coords inPlaneCoords = new Coords(4);

		Coords singlePoint = AlgoIntersectCS1D2D.getIntersectLinePlane(g, p,
				globalCoords, inPlaneCoords);

		// check if projection is intersection point
		if (singlePoint != null)
			newCoords.put(0d, singlePoint);

	}

	/**
	 * check the first parameter
	 * 
	 * @param t1
	 *            parameter
	 * @return true if ok
	 */
	protected boolean checkParameter(double t1) {
		return g.respectLimitedPath(t1);
	}

	@Override
	public void compute() {

		// clear the points map
		newCoords.clear();

		// init index
		int index = 0;

		if (((GeoElement) p).isDefined() && getFirstInput().isDefined()) {
			// fill a new points map
			// intersectionsCoords(p, newCoords);
			intersectionsCoords(p, newCoords);

			// affect new computed points
			outputPoints.adjustOutputSize(newCoords.size());
			for (Coords coords : newCoords.values()) {
				GeoPointND point = (GeoPointND) outputPoints.getElement(index);
				point.setCoords(coords, false);
				point.updateCoords();
				index++;
			}
		}

		// other points are undefined (eventually all if input are undefined)
		for (; index < outputPoints.size(); index++)
			outputPoints.getElement(index).setUndefined();

	}

	@Override
	public Commands getClassName() {
		return Commands.Intersect;
	}

	protected void setLabels(String[] labels) {
		// if only one label (e.g. "A") for more than one output, new labels
		// will be A_1, A_2, ...
		if (labels != null && labels.length == 1 && outputPoints.size() > 1
				&& labels[0] != null && !labels[0].equals("")) {
			outputPoints.setIndexLabels(labels[0]);

		} else {
			outputPoints.setLabels(labels);
		}

	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = getFirstInput();
		input[1] = (GeoElement) p;

		setDependencies(); // done by AlgoElement
	}

	

}
