package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedron;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedronNet;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSegment3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;

/**
 * Algo that compute the net for a polyhedron
 * 
 * @author Vincent
 *
 */
public class AlgoPolyhedronNetPyramid extends AlgoPolyhedronNet {

	/**
	 * @param c
	 *            construction
	 */
	public AlgoPolyhedronNetPyramid(Construction c, String[] labels,
			GeoPolyhedron p, NumberValue v) {
		super(c, labels, p, v);

	}

	@Override
	protected int getPointLengthFromLabelsLength(int length) {

		return (length - 2) / 6;
	}

	@Override
	protected void createNet(int n) {

		GeoPolyhedronNet net = getNet();

		// create bottom face
		outputPointsBottom.adjustOutputSize(n, false);
		outputPointsSide.adjustOutputSize(n, false);
		net.startNewFace();
		for (int i = 0; i < n; i++) {
			net.addPointToCurrentFace(outputPointsBottom.getElement(i));
		}
		net.endCurrentFace();

		// create side faces
		for (int i = 0; i < n; i++) {
			createSideFace(net, i, n);
		}
	}

	private void createSideFace(GeoPolyhedronNet net, int index,
			int newBottomPointsLength) {
		net.startNewFace();
		net.addPointToCurrentFace(outputPointsBottom.getElement(index));
		net.addPointToCurrentFace(outputPointsBottom.getElement((index + 1)
				% newBottomPointsLength));
		net.addPointToCurrentFace(outputPointsSide.getElement(index));
		net.endCurrentFace();
	}

	@Override
	protected void setOutputSideTop(int n, GeoPolygon3D polygon, int step,
			GeoSegmentND[] segments) {

		setOutputSide(polygon);
	}

	private void setOutputSide(GeoPolygon3D polygon) {

		outputPolygonsSide.addOutput(polygon, false);
		outputSegmentsSide.addOutput((GeoSegment3D) polygon.getSegments()[2],
				false);
		outputSegmentsSide.addOutput((GeoSegment3D) polygon.getSegments()[1],
				false);
	}

	@Override
	protected void adjustOutputSize(int newBottomPointsLength) {

		super.adjustOutputSize(newBottomPointsLength);

		// current length
		int nOld = outputPointsSide.size();

		// update existing segments / sides
		if (newBottomPointsLength > bottomPointsLength) {
			for (int i = bottomPointsLength; i < newBottomPointsLength
					&& i <= nOld; i++) {
				// update bottom segments
				GeoSegmentND segmentBottom = outputSegmentsBottom
						.getElement(i - 1);
				segmentBottom.modifyInputPoints(
						outputPointsBottom.getElement(i - 1),
						outputPointsBottom.getElement(i));
			}

			for (int i = bottomPointsLength - 1; i < newBottomPointsLength
					&& i < nOld; i++) {
				// update last sides
				updateSide(i, newBottomPointsLength);
			}

		}

		// create sides if needed
		if (newBottomPointsLength > nOld) {

			// update side points
			outputPointsSide.adjustOutputSize(newBottomPointsLength, false);
			outputPointsSide.setLabels(null);

			// create new sides
			GeoPolyhedronNet net = getNet();
			for (int i = nOld; i < newBottomPointsLength; i++) {
				createSideFace(net, i, newBottomPointsLength);
				GeoPolygon3D polygon = net.createPolygon(i + 1); // +1 shift
																	// since
																	// bottom is
																	// face #0
				setOutputSide(polygon);
				outputSegmentsBottom.addOutput(
						(GeoSegment3D) polygon.getSegments()[0], false); // add
																			// segment
																			// to
																			// bottom
																			// list
																			// now
			}
			outputSegmentsBottom.setLabels(null);
			outputSegmentsSide.setLabels(null);
			outputPolygonsSide.setLabels(null);
			refreshOutput();

		}

		// updates
		if (newBottomPointsLength > bottomPointsLength) {

			// update bottom
			updateBottom(newBottomPointsLength);

		} else if (newBottomPointsLength < bottomPointsLength) {
			// update side points
			for (int i = newBottomPointsLength; i < bottomPointsLength; i++) {
				outputPointsSide.getElement(i).setUndefined();
				outputPointsBottom.getElement(i).setUndefined();
			}

			// update bottom segment
			GeoSegmentND segmentBottom = outputSegmentsBottom
					.getElement(newBottomPointsLength - 1);
			segmentBottom.modifyInputPoints(
					outputPointsBottom.getElement(newBottomPointsLength - 1),
					outputPointsBottom.getElement(0));

			// update bottom face
			updateBottom(newBottomPointsLength);

			// update last side
			updateSide(newBottomPointsLength - 1, newBottomPointsLength);

		}

		bottomPointsLength = newBottomPointsLength;
	}

	private void updateSide(int index, int newBottomPointsLength) {

		GeoPointND pointBottom1 = outputPointsBottom.getElement(index);
		GeoPointND pointBottom2 = outputPointsBottom.getElement((index + 1)
				% newBottomPointsLength);
		GeoPointND pointSide = outputPointsSide.getElement(index);

		// update segments
		GeoSegmentND segmentBottom = outputSegmentsBottom.getElement(index);
		GeoSegmentND segmentSide1 = outputSegmentsSide.getElement(2 * index);
		GeoSegmentND segmentSide2 = outputSegmentsSide
				.getElement((2 * index + 1) % (2 * newBottomPointsLength));
		segmentSide2.modifyInputPoints(pointBottom2, pointSide);
		segmentSide1.modifyInputPoints(pointSide, pointBottom1);

		// update side
		GeoPolygon polygon = outputPolygonsSide.getElement(index);
		GeoPointND[] points = new GeoPointND[3];
		points[0] = pointBottom1;
		points[1] = pointBottom2;
		points[2] = pointSide;
		polygon.modifyInputPoints(points);
		GeoSegmentND[] s = new GeoSegmentND[3];
		s[0] = segmentBottom;
		s[1] = segmentSide2;
		s[2] = segmentSide1;
		polygon.setSegments(s);
		polygon.calcArea();

	}

	private Coords p1;

	@Override
	public void compute(double f, GeoPolygon bottomPolygon, Coords[] points) {

		if (p1 == null) {
			p1 = new Coords(4);
		}

		Coords topCoords = p.getTopPoint();
		topCoords.projectPlane(bottomPolygon.getCoordSys()
				.getMatrixOrthonormal(), p1);
		double d1 = p.getOrientedHeight();

		Coords faceDirection = bottomPolygon.getDirectionInD3();
		if (d1 < 0) { // top point below the bottom face : negative rotation
			f *= -1;
			d1 *= -1;
		}

		if (bottomPolygon.isConvexInverseDirection()) {
			f *= -1;
			faceDirection = faceDirection.mul(-1);
		}

		int n = points.length;
		Coords o2 = points[0];

		for (int i = 0; i < n; i++) {
			GeoPoint3D wpoint = outputPointsSide.getElement(i);
			wpoint.setCoords(topCoords, false);

			// angle between side face and bottom face
			Coords o = o2;
			o2 = points[(i + 1) % n];
			Coords vs = o2.sub(o).normalized();
			rotate(wpoint, topCoords, p1, o, vs, f, faceDirection, d1, false);
		}

		getNet().setArea(p.getArea());

	}

}
