package org.geogebra.common.geogebra3D.kernel3D.algos;

import java.util.Collection;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedron;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedronNet;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSegment3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;

/**
 * Algo that compute the net for a polyhedron
 * 
 * @author Vincent
 *
 */
public abstract class AlgoPolyhedronNet extends AlgoElement3D {

	protected GeoPolyhedron p;
	protected NumberValue v;

	protected OutputHandler<GeoPolyhedronNet> outputNet;

	protected int bottomPointsLength;

	/** points generated as output */
	protected OutputHandler<GeoPoint3D> outputPointsBottom, outputPointsSide,
			outputPointsTop;
	protected OutputHandler<GeoSegment3D> outputSegmentsBottom,
			outputSegmentsSide, outputSegmentsTop;
	protected OutputHandler<GeoPolygon3D> outputPolygonsBottom,
			outputPolygonsSide, outputPolygonsTop;

	/**
	 * @param c
	 *            construction
	 */
	public AlgoPolyhedronNet(Construction c, String[] labels, GeoPolyhedron p,
			NumberValue v) {
		super(c);
		this.p = p;
		this.v = v;

		outputNet = new OutputHandler<GeoPolyhedronNet>(
				new elementFactory<GeoPolyhedronNet>() {
					public GeoPolyhedronNet newElement() {
						GeoPolyhedronNet p = new GeoPolyhedronNet(cons);
						p.setParentAlgorithm(AlgoPolyhedronNet.this);
						return p;
					}
				});

		outputNet.adjustOutputSize(1);

		outputPointsBottom = createOutputPoints();
		outputPointsSide = createOutputPoints();
		outputPointsTop = createOutputPoints();

		outputPolygonsBottom = createOutputPolygons();
		outputPolygonsSide = createOutputPolygons();
		outputPolygonsTop = createOutputPolygons();

		outputSegmentsBottom = createOutputSegments();
		outputSegmentsSide = createOutputSegments();
		outputSegmentsTop = createOutputSegments();

		bottomPointsLength = p.getBottomFace().getPointsLength();
		createNet(bottomPointsLength);

		input = new GeoElement[] { p, (GeoElement) v };
		for (int i = 0; i < input.length; i++) {
			input[i].addAlgorithm(this);
		}

		getNet().createFaces();

		setOutput(bottomPointsLength);

		// set labels
		setLabels(labels);

		update();

		updateOutputSegmentsAndPolygonsParentAlgorithms();

	}

	private void setLabels(String[] labels) {

		if (labels == null || labels.length <= 1)
			getNet().initLabels(labels);
		else {
			getNet().setAllLabelsAreSet(true);
			int n = getPointLengthFromLabelsLength(labels.length);
			if (n > bottomPointsLength) {
				adjustOutputSize(n); // augment output for labels
			}
			for (int i = 0; i < labels.length; i++) {
				getOutput(i).setLabel(labels[i]);
			}
		}

	}

	protected abstract int getPointLengthFromLabelsLength(int length);

	protected abstract void createNet(int n);

	protected abstract void setOutputSideTop(int n, GeoPolygon3D polygon,
			int step, GeoSegmentND[] segments);

	private void setOutput(int n) {

		GeoPolyhedronNet net = getNet();
		Collection<GeoPolygon3D> faces = net.getFacesCollection();
		int step = 1;

		for (GeoPolygon polygon : faces) {
			GeoSegmentND[] segments = polygon.getSegments();
			if (step == 1) { // bottom
				outputPolygonsBottom.addOutput((GeoPolygon3D) polygon, false);
				for (int i = 0; i < segments.length; i++) {
					outputSegmentsBottom.addOutput((GeoSegment3D) segments[i],
							false);
				}

			} else {// sides and top
				setOutputSideTop(n, (GeoPolygon3D) polygon, step, segments);

			}
			step++;
		}

		refreshOutput();
	}

	private OutputHandler<GeoPoint3D> createOutputPoints() {
		return new OutputHandler<GeoPoint3D>(new elementFactory<GeoPoint3D>() {
			public GeoPoint3D newElement() {
				GeoPoint3D p = new GeoPoint3D(cons);
				p.setCoords(0, 0, 0, 1);
				p.setParentAlgorithm(AlgoPolyhedronNet.this);
				getNet().addPointCreated(p);
				p.setLabelVisible(false);
				return p;
			}
		});
	}

	private OutputHandler<GeoSegment3D> createOutputSegments() {
		return new OutputHandler<GeoSegment3D>(
				new elementFactory<GeoSegment3D>() {
					public GeoSegment3D newElement() {
						GeoSegment3D s = new GeoSegment3D(cons);
						// s.setParentAlgorithm(AlgoPolyhedron.this);
						return s;
					}
				});
	}

	private OutputHandler<GeoPolygon3D> createOutputPolygons() {
		return new OutputHandler<GeoPolygon3D>(
				new elementFactory<GeoPolygon3D>() {
					public GeoPolygon3D newElement() {
						GeoPolygon3D p = new GeoPolygon3D(cons);
						// p.setParentAlgorithm(AlgoPolyhedron.this);
						return p;
					}
				});
	}

	/**
	 * @param point
	 *            point to rotate
	 * @param pointCoords
	 *            coordinates of this point
	 * @param projectCoords
	 *            coordinates of the projected point on bottom face
	 * @param o
	 *            coordinates of the origin of the rotation line
	 * @param vs
	 *            direction of the rotation line
	 * @param f
	 *            value of the cursor used in the rotation
	 * @param fd
	 *            direction of the bottom face
	 * @param dist
	 *            distance between point and projected point
	 * @param test
	 *            value (XOR)
	 */
	protected void rotate(GeoPoint3D point, Coords pointCoords,
			Coords projectCoords, Coords o, Coords vs, double f, Coords fd,
			double dist, boolean test) {

		Coords v2 = projectCoords.sub(o);
		double d2 = pointCoords.distLine(o, vs);
		double angle;
		if (Kernel.isEqual(dist, d2)) {
			angle = Math.PI / 2;
		} else {
			angle = Math.asin(dist / d2);
		}
		if (test ^ (v2.crossProduct(vs).dotproduct(fd) < 0)) { // top point is
																// inside bottom
																// face
			angle = Math.PI - angle;
		}

		point.rotate(f * angle, o, vs);
	}

	/**
	 * compute with f value for opening, and bottom points
	 * 
	 * @param f
	 *            value for opening
	 * @param bottomPolygon
	 *            bottom face of the pyramid/prism
	 * @param points
	 *            bottom points
	 */
	protected abstract void compute(double f, GeoPolygon bottomPolygon,
			Coords[] points);

	/**
	 * adjust output for n bottom points
	 * 
	 * @param n
	 *            new bottom points length
	 */
	protected void adjustOutputSize(int n) {

		if (n > outputPointsBottom.size()) { // augment output points bottom
			outputPointsBottom.adjustOutputSize(n, false);
		}

	}

	/**
	 * update bottom face for new length
	 * 
	 * @param newBottomPointsLength
	 *            new bottom points length
	 */
	protected void updateBottom(int newBottomPointsLength) {

		GeoPolygon polygon = outputPolygonsBottom.getElement(0);
		GeoPoint3D[] points = new GeoPoint3D[newBottomPointsLength];
		GeoSegment3D[] segments = new GeoSegment3D[newBottomPointsLength];
		for (int i = 0; i < newBottomPointsLength; i++) {
			points[i] = outputPointsBottom.getElement(i);
			segments[i] = outputSegmentsBottom.getElement(i);
		}
		polygon.modifyInputPoints(points);
		polygon.setSegments(segments);
		polygon.calcArea();

	}

	@Override
	public void compute() {

		if (!p.isDefined()) {
			setUndefined();
			return;
		}

		double f = v.getDouble();

		if (Kernel.isGreater(f, 1) || Kernel.isGreater(0, f)) {
			setUndefined();
			return;
		}

		// update bottom points
		GeoPolygon bottomFace = p.getBottomFace();
		if (bottomFace.isConvex()) {
			getNet().setDefined();
			Coords[] points = getPointsCoords(bottomFace);
			adjustOutputSize(points.length);
			outputPointsBottom.setLabels(null);
			for (int i = 0; i < points.length; i++) {
				outputPointsBottom.getElement(i).setCoords(points[i]);
			}
			compute(f, bottomFace, points);
		} else {
			setUndefined();
		}
	}

	private void setUndefined() {
		getNet().setUndefined();
		outputPointsBottom.setUndefined();
		outputPointsSide.setUndefined();
		outputPointsTop.setUndefined();
	}

	/**
	 * 
	 * @param polygon
	 *            polygon
	 * @return 3D coords of all points
	 */
	protected static final Coords[] getPointsCoords(GeoPolygon polygon) {
		int l = polygon.getPointsLength();
		Coords[] points = new Coords[l];
		for (int i = 0; i < l; i++) {
			points[i] = polygon.getPoint3D(i);
		}
		return points;
	}

	@Override
	public GetCommand getClassName() {
		return Commands.Net;
	}

	/**
	 * @return the polyhedron
	 */
	public GeoPolyhedronNet getNet() {
		return outputNet.getElement(0);
	}

	/**
	 * force update for segments and polygons at creation
	 */
	private void updateOutputSegmentsAndPolygonsParentAlgorithms() {
		outputSegmentsBottom.updateParentAlgorithm();
		outputSegmentsSide.updateParentAlgorithm();
		outputPolygonsBottom.updateParentAlgorithm();
		outputPolygonsSide.updateParentAlgorithm();

		if (p.getType() == GeoPolyhedron.TYPE_PRISM) {
			outputSegmentsTop.updateParentAlgorithm();
			outputPolygonsTop.updateParentAlgorithm();
		}
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_NET;
	}

}
