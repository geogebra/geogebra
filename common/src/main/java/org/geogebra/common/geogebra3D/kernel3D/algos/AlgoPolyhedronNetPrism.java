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
public class AlgoPolyhedronNetPrism extends AlgoPolyhedronNet {

	/**
	 * @param c
	 *            construction
	 */
	public AlgoPolyhedronNetPrism(Construction c, String[] labels,
			GeoPolyhedron p, NumberValue v) {
		super(c, labels, p, v);
	}

	@Override
	protected int getPointLengthFromLabelsLength(int length) {
		return length / 10;
	}

	@Override
	protected void createNet(int n) {

		GeoPolyhedronNet net = getNet();

		outputPointsBottom.adjustOutputSize(n, false);
		outputPointsSide.adjustOutputSize(2 * n, false);
		outputPointsTop.adjustOutputSize(n - 2, false);

		// create bottom face
		net.startNewFace();
		for (int i = 0; i < n; i++) {
			net.addPointToCurrentFace(outputPointsBottom.getElement(i));
		}
		net.endCurrentFace();

		// create side faces
		for (int i = 0; i < n; i++) {
			createSideFace(net, i, n);
		}

		// create top face
		net.startNewFace();
		net.addPointToCurrentFace(outputPointsSide.getElement(0));
		net.addPointToCurrentFace(outputPointsSide.getElement(1));
		for (int i = 0; i < n - 2; i++) {
			net.addPointToCurrentFace(outputPointsTop.getElement(i));
		}
		net.endCurrentFace();
	}

	@Override
	protected void setOutputSideTop(int n, GeoPolygon3D polygon, int step,
			GeoSegmentND[] segments) {
		if (step == n + 2) { // top
			outputPolygonsTop.addOutput(polygon, false);
			for (int i = 1; i < segments.length; i++) {
				outputSegmentsTop.addOutput((GeoSegment3D) segments[i], false);
			}
		} else {
			setOutputSide(polygon);
		}
	}

	private void setOutputSide(GeoPolygon3D polygon) {

		outputPolygonsSide.addOutput(polygon, false);
		outputSegmentsSide.addOutput((GeoSegment3D) polygon.getSegments()[3],
				false);
		outputSegmentsSide.addOutput((GeoSegment3D) polygon.getSegments()[2],
				false);
		outputSegmentsSide.addOutput((GeoSegment3D) polygon.getSegments()[1],
				false);
	}

	private Coords pp1;

	@Override
	public void compute(double f, GeoPolygon bottomPolygon, Coords[] points) {

		if (pp1 == null) {
			pp1 = new Coords(4);
		}

		int sz = points.length;

		Coords[] topP = getPointsCoords(p.getTopFace());

		Coords topCo = topP[0];
		topCo.projectPlane(bottomPolygon.getCoordSys().getMatrixOrthonormal(),
				pp1);
		double dd1 = p.getOrientedHeight();
		if (dd1 < 0) { // top point below the bottom face : negative rotation
			f *= -1;
			dd1 *= -1;
		}

		Coords faceDirection = bottomPolygon.getDirectionInD3();
		if (bottomPolygon.isConvexInverseDirection()) {
			f *= -1;
			faceDirection = faceDirection.mul(-1);
		}

		GeoPoint3D wpoint1 = null;
		GeoPoint3D wpoint2 = null;
		GeoPoint3D wpoint3 = null;
		Coords cCoord = null; // Coords of the current top point
		for (int i = 0; i < sz; i++) {
			// triple creation of top points
			wpoint1 = outputPointsSide.getElement(2 * i);
			int j = 2 * i - 1;
			if (j < 0) {
				j = 2 * sz - 1;
			}
			wpoint2 = outputPointsSide.getElement(j);
			cCoord = topP[i];
			wpoint1.setCoords(cCoord);
			wpoint2.setCoords(cCoord);
			if (i > 1) { // wpoint3 is for the top face, except 2 first points
							// (already exist)
				wpoint3 = outputPointsTop.getElement(i - 2);
				wpoint3.setCoords(cCoord);
			}
		}

		Coords[] bottomSegsDirections = new Coords[sz];
		Coords p1 = points[sz - 1];
		Coords p2 = points[0];
		bottomSegsDirections[sz - 1] = p2.sub(p1).normalized();
		for (int i = 0; i < sz - 1; i++) {
			p1 = p2;
			p2 = points[i + 1];
			bottomSegsDirections[i] = p2.sub(p1).normalized();
		}

		// rotation of the top face around first top segment
		Coords o = topP[1];
		Coords vs = bottomSegsDirections[0];
		GeoPolygon side0 = p.getFirstSideFace();
		for (int i = 0; i < sz - 2; i++) {
			wpoint3 = outputPointsTop.getElement(i);
			cCoord = wpoint3.getInhomCoordsInD3();
			cCoord.projectPlane(side0.getCoordSys().getMatrixOrthonormal(), pp1);
			double dist = pp1.distance(cCoord);
			rotate(wpoint3, cCoord, pp1, o, vs, f, side0.getDirectionInD3(),
					dist, true);
		}
		for (int i = 0; i < 2 * sz; i += 2) {
			// rotate wpoint1
			// angle between side face and bottom face
			o = points[i / 2];
			vs = bottomSegsDirections[i / 2];
			wpoint1 = outputPointsSide.getElement(i);
			cCoord = wpoint1.getInhomCoordsInD3();
			cCoord.projectPlane(bottomPolygon.getCoordSys()
					.getMatrixOrthonormal(), pp1);
			rotate(wpoint1, cCoord, pp1, o, vs, f, faceDirection, dd1, false);
			// rotate wpoint2
			wpoint2 = outputPointsSide.getElement(i + 1);
			cCoord = wpoint2.getInhomCoordsInD3();
			cCoord.projectPlane(bottomPolygon.getCoordSys()
					.getMatrixOrthonormal(), pp1);
			rotate(wpoint2, cCoord, pp1, o, vs, f, faceDirection, dd1, false);

			if (i == 0) { // the rotation for the top face is made with the same
							// angle
				for (int j = 0; j < sz - 2; j++) {
					wpoint3 = outputPointsTop.getElement(j);
					rotate(wpoint3, cCoord, pp1, o, vs, f, faceDirection, dd1,
							false);
				}
			}
		}

		getNet().setArea(p.getArea());

	}

	@Override
	protected void adjustOutputSize(int newBottomPointsLength) {

		super.adjustOutputSize(newBottomPointsLength);

		// current length
		int nOld = outputPointsSide.size() / 2;

		if (newBottomPointsLength > nOld) {
			// adjust top points length
			outputPointsTop.adjustOutputSize(newBottomPointsLength - 2, false);
			outputPointsTop.setLabels(null);

			// create new top segments
			GeoPolyhedronNet net = getNet();
			for (int i = nOld; i < newBottomPointsLength - 1; i++) {
				GeoSegment3D segmentTop = (GeoSegment3D) net.createSegment(
						outputPointsTop.getElement(i - 2),
						outputPointsTop.getElement(i - 1));
				outputSegmentsTop.addOutput(segmentTop, false);
			}
			GeoSegment3D segmentTop = (GeoSegment3D) net.createSegment(
					outputPointsTop.getElement(newBottomPointsLength - 3),
					outputPointsSide.getElement(0));
			outputSegmentsTop.addOutput(segmentTop, false);

			outputSegmentsTop.setLabels(null);
			// refreshOutput() will be done below
		}

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
				// update top segments
				GeoSegmentND segmenTop = outputSegmentsTop.getElement(i - 2);
				segmenTop.modifyInputPoints(outputPointsTop.getElement(i - 3),
						outputPointsTop.getElement(i - 2));
			}

			for (int i = bottomPointsLength - 1; i < newBottomPointsLength
					&& i < nOld; i++) {
				// update last sides
				updateSide(i, newBottomPointsLength);
			}

		}

		// create sides if needed
		if (newBottomPointsLength > nOld) {

			// adjust side points length
			outputPointsSide.adjustOutputSize(newBottomPointsLength * 2, false);
			outputPointsSide.setLabels(null);

			// create new sides
			GeoPolyhedronNet net = getNet();
			for (int i = nOld; i < newBottomPointsLength; i++) {
				createSideFace(net, i, newBottomPointsLength);
				GeoPolygon3D polygon = net.createPolygon(i + 2); // +2 shift for
																	// bottom
																	// and top
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
			updateTop(newBottomPointsLength);

		} else if (newBottomPointsLength < bottomPointsLength) {
			// update points
			for (int i = newBottomPointsLength; i < bottomPointsLength; i++) {
				outputPointsBottom.getElement(i).setUndefined();
				outputPointsSide.getElement(2 * i).setUndefined();
				outputPointsSide.getElement(2 * i + 1).setUndefined();
				outputPointsTop.getElement(i - 2).setUndefined();
			}

			// update bottom segment
			GeoSegmentND segmentBottom = outputSegmentsBottom
					.getElement(newBottomPointsLength - 1);
			segmentBottom.modifyInputPoints(
					outputPointsBottom.getElement(newBottomPointsLength - 1),
					outputPointsBottom.getElement(0));

			// update bottom face
			updateBottom(newBottomPointsLength);

			// update top segment
			GeoSegmentND segmentTop = outputSegmentsTop
					.getElement(newBottomPointsLength - 2);
			segmentTop.modifyInputPoints(
					outputPointsTop.getElement(newBottomPointsLength - 3),
					outputPointsSide.getElement(0));

			// update top face
			updateTop(newBottomPointsLength);

			// update last side
			updateSide(newBottomPointsLength - 1, newBottomPointsLength);

		}

		bottomPointsLength = newBottomPointsLength;
	}

	private void updateSide(int index, int newBottomPointsLength) {

		GeoPointND pointBottom1 = outputPointsBottom.getElement(index);
		GeoPointND pointBottom2 = outputPointsBottom.getElement((index + 1)
				% newBottomPointsLength);
		GeoPointND pointSide2 = outputPointsSide.getElement(2 * index);
		GeoPointND pointSide1 = outputPointsSide.getElement((2 * index + 1)
				% (2 * newBottomPointsLength));

		// update segments
		GeoSegmentND segmentBottom = outputSegmentsBottom.getElement(index);
		GeoSegmentND segmentSide3 = outputSegmentsSide.getElement(3 * index);
		GeoSegmentND segmentSide2 = outputSegmentsSide
				.getElement(3 * index + 1);
		GeoSegmentND segmentSide1 = outputSegmentsSide
				.getElement((3 * index + 2) % (3 * newBottomPointsLength));
		segmentSide1.modifyInputPoints(pointBottom2, pointSide1);
		segmentSide2.modifyInputPoints(pointSide1, pointSide2);
		segmentSide3.modifyInputPoints(pointSide2, pointBottom1);

		// update side
		GeoPolygon polygon = outputPolygonsSide.getElement(index);
		GeoPointND[] points = new GeoPointND[4];
		points[0] = pointBottom1;
		points[1] = pointBottom2;
		points[2] = pointSide1;
		points[3] = pointSide2;
		polygon.modifyInputPoints(points);
		GeoSegmentND[] s = new GeoSegmentND[4];
		s[0] = segmentBottom;
		s[1] = segmentSide1;
		s[2] = segmentSide2;
		s[2] = segmentSide3;
		polygon.setSegments(s);
		polygon.calcArea();

	}

	private void createSideFace(GeoPolyhedronNet net, int index,
			int newBottomPointsLength) {

		net.startNewFace();
		net.addPointToCurrentFace(outputPointsBottom.getElement(index));
		net.addPointToCurrentFace(outputPointsBottom.getElement((index + 1)
				% newBottomPointsLength));
		net.addPointToCurrentFace(outputPointsSide.getElement((2 * index + 1)
				% (2 * newBottomPointsLength)));
		net.addPointToCurrentFace(outputPointsSide.getElement(2 * index));
		net.endCurrentFace();
	}

	/**
	 * update top face for new length
	 * 
	 * @param newBottomPointsLength
	 *            new bottom points length
	 */
	protected void updateTop(int newBottomPointsLength) {

		GeoPolygon polygon = outputPolygonsTop.getElement(0);
		GeoPoint3D[] points = new GeoPoint3D[newBottomPointsLength];
		GeoSegment3D[] segments = new GeoSegment3D[newBottomPointsLength];

		points[0] = outputPointsSide.getElement(0);
		points[1] = outputPointsSide.getElement(1);
		for (int i = 2; i < newBottomPointsLength; i++) {
			points[i] = outputPointsTop.getElement(i - 2);
		}

		segments[0] = outputSegmentsSide.getElement(0);
		for (int i = 1; i < newBottomPointsLength; i++) {
			segments[i] = outputSegmentsTop.getElement(i - 1);
		}

		polygon.modifyInputPoints(points);
		polygon.setSegments(segments);
		polygon.calcArea();

	}

}
