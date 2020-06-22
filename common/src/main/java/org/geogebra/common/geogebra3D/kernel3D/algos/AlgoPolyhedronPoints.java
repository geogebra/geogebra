package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedron;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSegment3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoPolygonRegularND;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.ChangeableParent;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;

/**
 * @author ggb3D
 * 
 *         Creates a new GeoPolyhedron
 *
 */
public abstract class AlgoPolyhedronPoints extends AlgoPolyhedron {

	private GeoPointND[] bottomPoints;
	protected GeoPointND[] points;
	private GeoPointND topPoint;
	protected GeoPolygon bottom;
	protected NumberValue height;

	protected boolean bottomAsInput = false;
	protected int bottomPointsLength = -1;

	protected OutputHandler<GeoSegment3D> outputSegmentsBottom;
	protected OutputHandler<GeoSegment3D> outputSegmentsSide;
	protected OutputHandler<GeoSegment3D> outputSegmentsTop;
	protected OutputHandler<GeoPolygon3D> outputPolygonsBottom;
	protected OutputHandler<GeoPolygon3D> outputPolygonsSide;
	protected OutputHandler<GeoPolygon3D> outputPolygonsTop;
	ChangeableParent heightChangeableParent = null;
	private int shift;

	private class OutputPolygonsHandler extends OutputHandler<GeoPolygon3D> {

		public OutputPolygonsHandler() {
			super(new ElementFactory<GeoPolygon3D>() {
				@Override
				public GeoPolygon3D newElement() {
					GeoPolygon3D p = new GeoPolygon3D(cons);
					// p.setParentAlgorithm(AlgoPolyhedron.this);
					if (heightChangeableParent != null) {
						p.setChangeableParent(heightChangeableParent);
					}
					return p;
				}
			});
		}

		@Override
		public void addOutput(GeoPolygon3D geo, boolean setDependencies) {
			if (heightChangeableParent != null) {
				geo.setChangeableParent(heightChangeableParent);
			}
			super.addOutput(geo, setDependencies);
		}

	}

	@Override
	protected OutputHandler<GeoPolygon3D> createOutputPolygonsHandler() {
		return new OutputPolygonsHandler();
	}

	private class OutputSegmentsHandler extends OutputHandler<GeoSegment3D> {

		public OutputSegmentsHandler() {
			super(new ElementFactory<GeoSegment3D>() {
				@Override
				public GeoSegment3D newElement() {
					GeoSegment3D s = new GeoSegment3D(cons);
					if (heightChangeableParent != null) {
						s.setChangeableParentIfNull(
								heightChangeableParent);
					}
					return s;
				}
			});
		}

		@Override
		public void addOutput(GeoSegment3D geo, boolean setDependencies) {
			if (heightChangeableParent != null) {
				geo.setChangeableParentIfNull(heightChangeableParent);
			}
			super.addOutput(geo, setDependencies);
		}

	}

	@Override
	protected OutputHandler<GeoSegment3D> createOutputSegmentsHandler() {
		return new OutputSegmentsHandler();
	}

	private class OutputPointsHandler extends OutputHandler<GeoPoint3D> {

		public OutputPointsHandler() {
			super(new PointFactory() {
				@Override
				public GeoPoint3D newElement() {
					GeoPoint3D ret = super.newElement();
					if (heightChangeableParent != null) {
						ret.setChangeableParentIfNull(
								heightChangeableParent);
					}
					return ret;
				}
			});
		}

		@Override
		public void addOutput(GeoPoint3D geo, boolean setDependencies) {
			if (heightChangeableParent != null) {
				geo.setChangeableParentIfNull(heightChangeableParent);
			}
			super.addOutput(geo, setDependencies);
		}

	}

	@Override
	protected OutputHandler<GeoPoint3D> createOutputPointsHandler() {
		return new OutputPointsHandler();
	}

	// ///////////////////////////////////////////
	// POLYHEDRON OF DETERMINED TYPE
	// //////////////////////////////////////////

	/**
	 * creates a polyhedron regarding vertices
	 * 
	 * @param c
	 *            construction
	 * @param labels
	 *            output labels
	 * @param points
	 *            points (base + one from top)
	 */
	public AlgoPolyhedronPoints(Construction c, String[] labels,
			GeoPointND[] points) {
		super(c);
		init();

		initCoords();

		bottomPoints = new GeoPointND[points.length - 1];
		for (int i = 0; i < points.length - 1; i++) {
			bottomPoints[i] = points[i];
		}
		setTopPoint(points[points.length - 1]);
		shift = 1; // output points are shifted of 1 to input points (one less)

		createPolyhedron();

		// input : inputPoints or list of faces
		input = new GeoElement[points.length];
		for (int i = 0; i < points.length; i++) {
			input[i] = (GeoElement) points[i];
		}
		addAlgoToInput();

		updateOutputPoints();
		createFaces();
		setOutput();

		// compute();

		setLabels(labels);

		update();

		// force update segments and polygons
		updateOutputSegmentsAndPolygonsParentAlgorithms();
	}

	/**
	 * creates a polyhedron regarding bottom face and top vertex
	 * 
	 * @param c
	 *            construction
	 * @param labels
	 *            output labels
	 * @param polygon
	 *            base
	 * @param point
	 *            first vertex on top
	 */
	public AlgoPolyhedronPoints(Construction c, String[] labels,
			GeoPolygon polygon, GeoPointND point) {
		super(c);
		init();

		initCoords();

		bottom = polygon;
		bottomAsInput = true;

		setTopPoint(point);
		shift = 1; // output points are shifted of 1 to input points (one less)

		createPolyhedron();

		// input : inputPoints or list of faces
		input = new GeoElement[2];
		input[0] = bottom;
		input[1] = (GeoElement) topPoint;
		addAlgoToInput();

		updateOutputPoints();
		createFaces();
		setOutput();

		setLabels(labels);

		update();

		// force update segments and polygons
		updateOutputSegmentsAndPolygonsParentAlgorithms();
	}

	/**
	 * creates a polyhedron regarding bottom face and top vertex
	 * 
	 * @param c
	 *            construction
	 * @param labels
	 *            output labels
	 * @param polygon
	 *            base
	 * @param height
	 *            height
	 */
	public AlgoPolyhedronPoints(Construction c, String[] labels, GeoPolygon polygon,
			NumberValue height) {
		super(c);
		init();

		// create ChangeableParent if possible
		GeoNumeric changeableHeight = ChangeableParent.getGeoNumeric(height);
		if (changeableHeight != null) {
			heightChangeableParent = new ChangeableParent(changeableHeight,
					polygon, new ExtrudeConverter());
		}

		initCoords();

		bottom = polygon;
		bottomAsInput = true;
		this.height = height;
		shift = 0; // output points correspond to input points

		outputPoints.augmentOutputSize(1, false);
		setTopPoint(outputPoints.getElement(0));
		createPolyhedron();

		// input : inputPoints or list of faces
		input = new GeoElement[2];
		input[0] = bottom;
		input[1] = (GeoElement) height;
		addAlgoToInput();

		updateOutputPoints();
		createFaces();
		setOutput();

		setLabels(labels);

		update();

		// force update segments and polygons
		updateOutputSegmentsAndPolygonsParentAlgorithms();
	}

	@Override
	protected void createOutputSegments() {
		outputSegmentsBottom = createOutputSegmentsHandler();
		outputSegmentsSide = createOutputSegmentsHandler();
		outputSegmentsTop = createOutputSegmentsHandler();
	}

	@Override
	protected void createOutputPolygons() {
		outputPolygonsBottom = createOutputPolygonsHandler();
		outputPolygonsSide = createOutputPolygonsHandler();
		outputPolygonsTop = createOutputPolygonsHandler();
	}

	/**
	 * 
	 * @param labels
	 *            labels
	 */
	protected void setLabels(String[] labels) {
		if (labels == null || labels.length <= 1) {
			polyhedron.initLabels(labels);
		} else {
			augmentOutputSize(labels.length);
			for (int i = 0; i < labels.length; i++) {
				getOutput(i).setLabel(labels[i]);
			}
			polyhedron.setAllLabelsAreSet(true);
		}

	}

	/**
	 * augment the output size if needed (in case of undefined but labelled
	 * outputs)
	 * 
	 * @param length
	 *            labels length
	 */
	protected void augmentOutputSize(int length) {
		int n = getSideLengthFromLabelsLength(length);

		// Application.debug("n="+n+",length="+length);

		if (n > outputSegmentsSide.size()) {
			if (getBottom()
					.getParentAlgorithm() instanceof AlgoPolygonRegularND) {
				AlgoPolygonRegularND algo = (AlgoPolygonRegularND) getBottom()
						.getParentAlgorithm();
				// if no sufficient bottom points, force augment outputs for
				// AlgoPolygonRegular
				int nOld = algo.getCurrentPointsLength();
				if (nOld < n) {
					algo.compute(n);
					updateOutput(n);
					algo.compute(nOld);
				} else {
					updateOutput(n);
				}
			} else {
				// bottom polygon is a set() polygon, so force augment its
				// points and segments length
				GeoPolygon polygon = getBottom();
				int nOld = polygon.getPointsLength();
				if (nOld < n) {
					polygon.setPointsAndSegmentsLength(n);
					updateOutput(n);
					polygon.setPointsAndSegmentsLength(nOld);
				}
				updateOutput(n);
			}

		}
	}

	/**
	 * 
	 * @param length
	 *            labels length
	 * @return side segments length
	 */
	abstract protected int getSideLengthFromLabelsLength(int length);

	/**
	 * init Coords values
	 */
	protected void initCoords() {
		// none here
	}

	/**
	 * translate all output points
	 */
	abstract protected void updateOutputPoints();

	/**
	 * update output segments and parents algorithms
	 */
	abstract protected void updateOutputSegmentsAndPolygonsParentAlgorithms();

	/**
	 * create the polyhedron (faces and edges)
	 */
	final private void createPolyhedron() {

		GeoPointND[] bottomPoints1 = getBottomPoints();

		if (bottomPoints1 == null) {
			// force polygon regular to have at least 3 points
			if (getBottom()
					.getParentAlgorithm() instanceof AlgoPolygonRegularND) {
				AlgoPolygonRegularND algo = (AlgoPolygonRegularND) getBottom()
						.getParentAlgorithm();
				algo.compute(3);
				bottomPoints1 = getBottomPoints();
				createPolyhedron(bottomPoints1);
				algo.compute(2);
			}
		} else {
			createPolyhedron(bottomPoints1);
		}
	}

	/**
	 * create the polyhedron (faces and edges) with given bottom points
	 * 
	 * @param bottomPoints1
	 *            bottom points
	 */
	protected abstract void createPolyhedron(GeoPointND[] bottomPoints1);

	/**
	 * update output
	 * 
	 * @param newBottomPointsLength
	 *            new bottom points length
	 */
	protected abstract void updateOutput(int newBottomPointsLength);

	/**
	 * sets the bottom of the polyhedron
	 * 
	 * @param polyhedron
	 *            polyhedron
	 */
	protected void setBottom(GeoPolyhedron polyhedron) {
		if (bottom != null) {
			polyhedron.addPolygonLinked(bottom);
		} else {
			GeoPointND[] bottomPoints1 = getBottomPoints();

			polyhedron.startNewFace();
			for (int i = 0; i < bottomPoints1.length; i++) {
				polyhedron.addPointToCurrentFace(bottomPoints1[i]);
			}
			polyhedron.endCurrentFace();
		}
	}

	protected GeoPolygon getBottom() {
		if (bottom != null) {
			return bottom;
		}
		return outputPolygonsBottom.getElement(0);
	}

	// ///////////////////////////////////////////
	// END OF THE CONSTRUCTION
	// //////////////////////////////////////////

	/**
	 * shift used when first top point is input
	 * 
	 * @return 1 when first top point is input, 0 else
	 */
	protected int getShift() {
		return shift;
	}

	/**
	 * pre computation
	 * 
	 * @return true if the polyhedron is defined
	 */
	public boolean preCompute() {

		// check if bottom points length has changed (e.g. with regular polygon)
		if (bottomAsInput) {
			if (!getBottom().isDefined()) {
				polyhedron.setUndefined();
				return false;
			}
			polyhedron.setDefined();
			updateOutput(bottom.getPointsLength());
			if (height == null && !getBottom().wasInitLabelsCalled()) {
				updateOutputSegmentsAndPolygonsParentAlgorithms();
			}
		} // else updateOutputPoints();

		// update height and volume
		double h;
		if (height != null) {
			h = height.getDouble();
		} else {
			h = getTopPoint().getInhomCoordsInD3().distPlaneOriented(
					getBottomPoints()[0].getInhomCoordsInD3(),
					getBottom().getDirectionInD3());
		}
		updateVolume(Math.abs(h));
		polyhedron.setOrientedHeight(h);

		// if prism/pyramid is down-oriented, reverse normals for blending
		if (height != null) {
			boolean isBottomInverse = bottomAsInput && bottom != null
					&& bottom.isConvexInverseDirection();
			polyhedron.setReverseNormalsForDrawing(isBottomInverse ^ height.getDouble() < 0);
		}

		return true;
	}

	/**
	 * updates the polyhedron's volume
	 * 
	 * @param heightVal
	 *            height
	 */
	protected void updateVolume(double heightVal) {
		// calc bottom area if needed
		if (!bottomAsInput) {
			((GeoPolygon3D) getBottom()).updateCoordSys();
			getBottom().calcArea();
		}
	}

	/**
	 * 
	 * @return bottom points
	 */
	protected GeoPointND[] getBottomPoints() {
		if (bottom != null) {
			return bottom.getPointsND();
		}
		return bottomPoints;
	}

	/**
	 * 
	 * @return top point
	 */
	protected GeoPointND getTopPoint() {
		return topPoint;
	}

	private void setTopPoint(GeoPointND p) {
		topPoint = p;
	}

	// /////////////////////////////////////////////////////
	// FOR PREVIEWABLE
	// /////////////////////////////////////////////////////

	/**
	 * set visibility of output other than points
	 * 
	 * @param visible
	 *            flag
	 */
	public void setOutputOtherEuclidianVisible(boolean visible) {
		for (int i = 0; i < outputSegmentsBottom.size(); i++) {
			outputSegmentsBottom.getElement(i).setEuclidianVisible(visible);
		}
		for (int i = 0; i < outputSegmentsSide.size(); i++) {
			outputSegmentsSide.getElement(i).setEuclidianVisible(visible);
		}
		for (int i = 0; i < outputSegmentsTop.size(); i++) {
			outputSegmentsTop.getElement(i).setEuclidianVisible(visible);
		}
		for (int i = 0; i < outputPolygonsBottom.size(); i++) {
			outputPolygonsBottom.getElement(i).setEuclidianVisible(visible,
					false);
		}
		for (int i = 0; i < outputPolygonsSide.size(); i++) {
			outputPolygonsSide.getElement(i).setEuclidianVisible(visible,
					false);
		}
		for (int i = 0; i < outputPolygonsTop.size(); i++) {
			outputPolygonsTop.getElement(i).setEuclidianVisible(visible, false);
		}
	}

	/**
	 * notify kernel update of output other than points
	 */
	public void notifyUpdateOutputOther() {
		for (int i = 0; i < outputSegmentsBottom.size(); i++) {
			getKernel().notifyUpdate(outputSegmentsBottom.getElement(i));
		}
		for (int i = 0; i < outputSegmentsSide.size(); i++) {
			getKernel().notifyUpdate(outputSegmentsSide.getElement(i));
		}
		for (int i = 0; i < outputSegmentsTop.size(); i++) {
			getKernel().notifyUpdate(outputSegmentsTop.getElement(i));
		}
		for (int i = 0; i < outputPolygonsBottom.size(); i++) {
			getKernel().notifyUpdate(outputPolygonsBottom.getElement(i));
		}
		for (int i = 0; i < outputPolygonsSide.size(); i++) {
			getKernel().notifyUpdate(outputPolygonsSide.getElement(i));
		}
		for (int i = 0; i < outputPolygonsTop.size(); i++) {
			getKernel().notifyUpdate(outputPolygonsTop.getElement(i));
		}
	}

	/**
	 * set output points invisible (use for previewable)
	 * 
	 * @param visible
	 *            flag
	 */
	public void setOutputPointsEuclidianVisible(boolean visible) {
		for (int i = 0; i < outputPoints.size(); i++) {
			outputPoints.getElement(i).setEuclidianVisible(visible);
		}
	}

	/**
	 * notify kernel update of output points
	 */
	public void notifyUpdateOutputPoints() {
		for (int i = 0; i < outputPoints.size(); i++) {
			getKernel().notifyUpdate(outputPoints.getElement(i));
		}
	}

	/**
	 * 
	 * @return top face
	 */
	public GeoPolygon getTopFace() {
		return outputPolygonsTop.getElement(0);

	}

	/**
	 * 
	 * @param i
	 *            side id
	 * @return i-th side of the prism/pyramid
	 */
	public GeoPolygon3D getSide(int i) {
		return outputPolygonsSide.getElement(i);
	}

	/**
	 * @return height
	 */
	public NumberValue getHeight() {
		return height;
	}

	@Override
	final protected boolean isFirstInputPointVisible() {
		GeoElement point = (GeoElement) getBottomPoints()[0];
		return point.isEuclidianVisible() && point.isLabelSet();
	}

	@Override
	final protected boolean isFirstInputPointLabelVisible() {
		return ((GeoElement) getBottomPoints()[0]).getLabelVisible();
	}

	/**
	 * if the user clicks a 2D polygon for bottom, and one of the bottom vertex
	 * for apex, segment can be a 2D segment
	 * 
	 * @param outputSegmentsHandler
	 *            output handler
	 * @param segment
	 *            segment to add
	 */
	static protected void addToOutputIf3D(
			OutputHandler<GeoSegment3D> outputSegmentsHandler,
			GeoSegmentND segment) {
		if (segment instanceof GeoSegment3D) {
			outputSegmentsHandler.addOutput((GeoSegment3D) segment, false);
		}
	}
}
