package org.geogebra.common.geogebra3D.kernel3D.algos;

import java.util.Collection;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedron;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSegment3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * @author ggb3D
 * 
 *         Creates a new Prism
 *
 */
public class AlgoPolyhedronPointsPrism extends AlgoPolyhedronPoints {
	private Coords uptranslation;
	
	/**
	 * @param c
	 *            construction
	 * @param labels
	 *            output labels
	 * @param polygon
	 *            base polygon
	 * @param point
	 *            point on top
	 */
	public AlgoPolyhedronPointsPrism(Construction c, String[] labels,
			GeoPolygon polygon, GeoPointND point) {
		super(c, labels, polygon, point);
	}

	/**
	 * @param c
	 *            construction
	 * @param labels
	 *            output labels
	 * @param points
	 *            points (base + last one on top)
	 */
	public AlgoPolyhedronPointsPrism(Construction c, String[] labels,
			GeoPointND[] points) {
		super(c, labels, points);
	}

	/**
	 * @param c
	 *            construction
	 * @param labels
	 *            output labels
	 * @param polygon
	 *            base
	 * @param height
	 *            height
	 */
	public AlgoPolyhedronPointsPrism(Construction c, String[] labels,
			GeoPolygon polygon, NumberValue height) {
		super(c, labels, polygon, height);
	}

	@Override
	protected void createPolyhedron(GeoPointND[] bottomPoints) {

		setBottom(polyhedron);

		GeoPointND topPoint = getTopPoint();

		bottomPointsLength = bottomPoints.length;

		// /////////
		// vertices
		// /////////

		outputPoints.augmentOutputSize(bottomPointsLength - 1);

		points = new GeoPointND[bottomPointsLength * 2];
		for (int i = 0; i < bottomPointsLength; i++) {
			points[i] = bottomPoints[i];
		}
		points[bottomPointsLength] = topPoint;
		for (int i = 0; i < bottomPointsLength - 1; i++) {
			GeoPoint3D point = outputPoints.getElement(i + 1 - getShift());
			points[bottomPointsLength + 1 + i] = point;
		}

		// /////////
		// faces
		// /////////

		// bottom has already been set

		// sides of the prism
		for (int i = 0; i < bottomPointsLength; i++) {
			polyhedron.startNewFace();
			polyhedron.addPointToCurrentFace(points[i]);
			polyhedron.addPointToCurrentFace(
					points[(i + 1) % (bottomPointsLength)]);
			polyhedron.addPointToCurrentFace(points[bottomPointsLength
					+ ((i + 1) % (bottomPointsLength))]);
			polyhedron.addPointToCurrentFace(points[bottomPointsLength + i]);
			polyhedron.endCurrentFace();
		}

		// top of the prism
		polyhedron.startNewFace();
		for (int i = 0; i < bottomPointsLength; i++) {
			polyhedron.addPointToCurrentFace(points[bottomPointsLength + i]);
		}
		polyhedron.endCurrentFace();

		polyhedron.setCurrentFaceIsTopFace();
	}

	@Override
	protected int getPolyhedronType() {
		return GeoPolyhedron.TYPE_PRISM;
	}

	/**
	 * 
	 * @param index
	 *            index of the point
	 * @return top point #index
	 */
	protected GeoPointND getTopPoint(int index) {
		if (index == 0) {
			return getTopPoint();
		}
		return outputPoints.getElement(index - getShift());
	}

	@Override
	protected void updateOutput(int newBottomPointsLength) {

		// current length of top points
		int nOld = outputPoints.size() + getShift();

		GeoPointND[] bottomPoints = getBottomPoints();
		GeoSegmentND[] bottomSegments = getBottom().getSegments();

		if (newBottomPointsLength > nOld) {
			// update segments linked
			polyhedron.updateSegmentsLinked();

			int length = newBottomPointsLength - nOld;
			outputPoints.augmentOutputSize(length, false);
			if (getPolyhedron().allLabelsAreSet()) {
				outputPoints.setLabels(null);
			}

			updateOutputPoints();

			// new sides of the prism
			int l = nOld + length;
			for (int i = nOld; i < l; i++) {
				polyhedron.startNewFace();
				polyhedron.addPointToCurrentFace(bottomPoints[i]);
				polyhedron.addPointToCurrentFace(bottomPoints[(i + 1) % l]);
				polyhedron.addPointToCurrentFace(getTopPoint((i + 1) % l));
				polyhedron.addPointToCurrentFace(getTopPoint(i));
				polyhedron.endCurrentFace();
				GeoPolygon3D polygon = polyhedron.createPolygon(i + 1); // i+1
																		// due
																		// to
																		// top
																		// face
				if (polyhedron.allLabelsAreSet()) {
					polygon.setLabel(null);
				}
				outputPolygonsSide.addOutput(polygon, false);
				addToOutputIf3D(outputSegmentsSide, polygon.getSegments()[3]);
				outputSegmentsTop.addOutput(
						(GeoSegment3D) polygon.getSegments()[2], false);
			}

			if (getPolyhedron().allLabelsAreSet()) {
				outputSegmentsSide.setLabels(null);
				outputSegmentsTop.setLabels(null);
				outputPolygonsSide.setLabels(null);
			}

			refreshOutput();
		} else if (newBottomPointsLength < nOld) {

			for (int i = newBottomPointsLength; i < bottomPointsLength; i++) {
				outputPoints.getElement(i - getShift()).setUndefined();
			}

			updateOutputPoints();

			// update top side
			outputSegmentsTop.getElement(newBottomPointsLength - 1)
					.modifyInputPoints(getTopPoint(newBottomPointsLength - 1),
							getTopPoint());
			GeoPolygon polygon = getTopFace();
			GeoPointND[] p = new GeoPointND[newBottomPointsLength];
			p[0] = getTopPoint();
			for (int i = 0; i < newBottomPointsLength - 1; i++) {
				p[1 + i] = getTopPoint(i + 1);
			}
			polygon.modifyInputPoints(p);
			polygon.setSegments(outputSegmentsTop
					.getOutput(new GeoSegment3D[newBottomPointsLength]));
			polygon.calcArea();

			// update last side
			polygon = outputPolygonsSide.getElement(newBottomPointsLength - 1);
			p = new GeoPointND[4];
			p[0] = bottomPoints[newBottomPointsLength - 1];
			p[1] = bottomPoints[0];
			p[2] = getTopPoint();
			p[3] = getTopPoint(newBottomPointsLength - 1);
			polygon.setPoints(p, null, false); // don't create segments
			GeoSegmentND[] s = new GeoSegmentND[4];
			s[0] = getBottom().getSegments()[newBottomPointsLength - 1];
			s[1] = outputSegmentsSide.getElement(0);
			s[2] = outputSegmentsTop.getElement(newBottomPointsLength - 1);
			s[3] = outputSegmentsSide.getElement(newBottomPointsLength - 1);
			polygon.setSegments(s);
			polygon.calcArea();

		} else {
			updateOutputPoints();
		}

		if (bottomPointsLength < newBottomPointsLength) {

			// update top side
			updateTop(newBottomPointsLength);

			// update last sides
			for (int i = bottomPointsLength; i < newBottomPointsLength; i++) {
				updateSide(i, bottomPoints, bottomSegments);
			}
		}

		bottomPointsLength = newBottomPointsLength;
	}

	private void updateTop(int n) {

		GeoPolygon polygon = getTopFace();
		GeoPointND[] p = new GeoPointND[n];
		p[0] = getTopPoint();
		for (int i = 0; i < n - 1; i++) {
			p[1 + i] = getTopPoint(i + 1);
		}
		polygon.modifyInputPoints(p);
		polygon.setSegments(outputSegmentsTop.getOutput(new GeoSegment3D[n]));
		polygon.calcArea();
	}

	private void updateSide(int index, GeoPointND[] bottomPoints,
			GeoSegmentND[] bottomSegments) {
		outputSegmentsTop.getElement(index - 1)
				.modifyInputPoints(getTopPoint(index - 1), getTopPoint(index));
		GeoPolygon polygon = outputPolygonsSide.getElement(index - 1);
		GeoPointND[] p = new GeoPointND[4];
		p[0] = bottomPoints[index - 1];
		p[1] = bottomPoints[index];
		p[2] = getTopPoint(index);
		p[3] = getTopPoint(index - 1);
		polygon.modifyInputPoints(p); // don't create segments
		GeoSegmentND[] s = new GeoSegmentND[4];
		s[0] = bottomSegments[index - 1];
		s[1] = outputSegmentsSide.getElement(index);
		s[2] = outputSegmentsTop.getElement(index - 1);
		s[3] = outputSegmentsSide.getElement(index - 1);
		polygon.setSegments(s);
		polygon.calcArea();
	}

	protected void removeBottomPoints(int length) {
		for (int i = bottomPointsLength; i < bottomPointsLength + length; i++) {
			outputPoints.getElement(i - getShift()).setUndefined();
		}

	}

	// ///////////////////////////////////////////
	// END OF THE CONSTRUCTION
	// //////////////////////////////////////////

	@Override
	protected void updateOutputPoints() {

		// Application.printStacktrace("");

		if (height == null) {
			uptranslation = getTopPoint().getInhomCoordsInD3()
					.sub(getBottomPoints()[0].getInhomCoordsInD3());
		} else {
			uptranslation = bottom.getMainDirection().normalized()
					.mul(height.getDouble());
		}

		GeoPointND[] bottomPoints = getBottomPoints();

		// translation from bottom to top
		if (bottomPoints != null) {
			for (int i = 0; i < outputPoints.size()
					&& i + getShift() < bottomPoints.length; i++) {
				outputPoints.getElement(i)
						.setCoords(bottomPoints[i + getShift()]
								.getInhomCoordsInD3().add(uptranslation), true);
			}
		}

	}

	@Override
	public void compute() {

		if (!preCompute()) {
			for (int i = 0; i < bottomPointsLength - getShift(); i++) {
				outputPoints.getElement(i).setUndefined();
			}
			// bottomPointsLength=getBottom().getPointsLength();
			return;
		}

		if (!bottomAsInput) {
			updateOutputPoints();
		}

	}

	@Override
	public Commands getClassName() {
		return Commands.Prism;
	}

	@Override
	protected void updateOutput() {

		// Application.debug("ici");
		Collection<GeoPolygon3D> faces = polyhedron.getFacesCollection();
		int top = faces.size();
		int step = 1;
		for (GeoPolygon polygon : faces) {

			GeoSegmentND[] segments = polygon.getSegments();
			if (step == 1 && !bottomAsInput) { // bottom
				outputPolygonsBottom.addOutput((GeoPolygon3D) polygon, false);
				for (int i = 0; i < segments.length; i++) {
					outputSegmentsBottom.addOutput((GeoSegment3D) segments[i],
							false);
				}
				step++;
				continue;
			} else if (step == top) { // top
				outputPolygonsTop.addOutput((GeoPolygon3D) polygon, false);
				for (int i = 0; i < segments.length; i++) {
					outputSegmentsTop.addOutput((GeoSegment3D) segments[i],
							false);
				}
				step++;
				continue;
			}

			// sides
			outputPolygonsSide.addOutput((GeoPolygon3D) polygon, false);
			addToOutputIf3D(outputSegmentsSide, polygon.getSegments()[3]);
			step++;
		}

		refreshOutput();

	}

	@Override
	protected int getSideLengthFromLabelsLength(int length) {

		if (bottomAsInput) {
			return (length + getShift() - 2) / 4;
		}

		return (length + getShift() - 3) / 5;

	}

	@Override
	protected void updateVolume(double heightVal) {
		super.updateVolume(heightVal);
		getPolyhedron().setVolume(getBottom().getArea() * heightVal);
	}

	@Override
	protected void updateDependentGeos() {

		super.updateDependentGeos();
		outputPoints.update();

		// force update of segments and polygons when e.g. in a list
		if (!getPolyhedron().allLabelsAreSet()) {
			outputSegmentsBottom.updateParentAlgorithm();
			outputSegmentsSide.updateParentAlgorithm();
			outputSegmentsTop.updateParentAlgorithm();
			outputPolygonsBottom.updateParentAlgorithm();
			outputPolygonsSide.updateParentAlgorithm();
			outputPolygonsTop.updateParentAlgorithm();
		}

	}

	@Override
	protected void updateOutputSegmentsAndPolygonsParentAlgorithms() {
		outputSegmentsBottom.updateParentAlgorithm();
		outputSegmentsSide.updateParentAlgorithm();
		outputSegmentsTop.updateParentAlgorithm();
		outputPolygonsBottom.updateParentAlgorithm();
		outputPolygonsSide.updateParentAlgorithm();
		outputPolygonsTop.updateParentAlgorithm();
	}

}
