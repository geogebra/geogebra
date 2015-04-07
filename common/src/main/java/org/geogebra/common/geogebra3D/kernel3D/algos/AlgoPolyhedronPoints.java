package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedron;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSegment3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoPolygonRegularND;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoPointND;

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

	protected OutputHandler<GeoSegment3D> outputSegmentsBottom,
			outputSegmentsSide, outputSegmentsTop;
	protected OutputHandler<GeoPolygon3D> outputPolygonsBottom,
			outputPolygonsSide, outputPolygonsTop;

	// ///////////////////////////////////////////
	// POLYHEDRON OF DETERMINED TYPE
	// //////////////////////////////////////////

	/**
	 * creates a polyhedron regarding vertices
	 * 
	 * @param c
	 *            construction
	 * @param labels
	 * @param points
	 */
	public AlgoPolyhedronPoints(Construction c, String[] labels,
			GeoPointND[] points) {
		super(c);

		initCoords();

		bottomPoints = new GeoPointND[points.length - 1];
		for (int i = 0; i < points.length - 1; i++)
			bottomPoints[i] = points[i];
		setTopPoint(points[points.length - 1]);
		shift = 1; // output points are shifted of 1 to input points (one less)

		createPolyhedron();

		// input : inputPoints or list of faces
		input = new GeoElement[points.length];
		for (int i = 0; i < points.length; i++)
			input[i] = (GeoElement) points[i];
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
	 * @param polygon
	 * @param point
	 */
	public AlgoPolyhedronPoints(Construction c, String[] labels,
			GeoPolygon polygon, GeoPointND point) {
		super(c);

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
		if (labels == null || labels.length <= 1)
			polyhedron.initLabels(labels);
		else {
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
			if (getBottom().getParentAlgorithm() instanceof AlgoPolygonRegularND) {
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
	 * creates a polyhedron regarding bottom face and top vertex
	 * 
	 * @param c
	 *            construction
	 * @param labels
	 * @param polygon
	 * @param height
	 */
	public AlgoPolyhedronPoints(Construction c, String[] labels,
			GeoPolygon polygon, NumberValue height) {
		super(c);

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

		if (height instanceof GeoNumeric) {
			if (((GeoNumeric) height).isIndependent()) {

				for (GeoPolygon p : polyhedron.getPolygons()) {
					p.setChangeableCoordParent((GeoNumeric) height, bottom);
				}

				// getTopFace().setCoordParentNumber((GeoNumeric) height);
				// getTopFace().setCoordParentDirector(bottom);
			}
		}

		setLabels(labels);

		update();

		// force update segments and polygons
		updateOutputSegmentsAndPolygonsParentAlgorithms();
	}

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
			if (getBottom().getParentAlgorithm() instanceof AlgoPolygonRegularND) {
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
	 * @return bottom key (if one)
	 */
	protected void setBottom(GeoPolyhedron polyhedron) {
		if (bottom != null) {
			polyhedron.addPolygonLinked(bottom);
		} else {
			GeoPointND[] bottomPoints = getBottomPoints();

			polyhedron.startNewFace();
			for (int i = 0; i < bottomPoints.length; i++) {
				polyhedron.addPointToCurrentFace(bottomPoints[i]);
			}
			polyhedron.endCurrentFace();
		}
	}

	protected GeoPolygon getBottom() {
		if (bottom != null)
			return bottom;
		return outputPolygonsBottom.getElement(0);
	}

	// ///////////////////////////////////////////
	// END OF THE CONSTRUCTION
	// //////////////////////////////////////////

	private int shift;

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
		}// else updateOutputPoints();

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
			polyhedron.setReverseNormalsForDrawing(height.getDouble() < 0);
		}

		return true;
	}

	/**
	 * updates the polyhedron's volume
	 * 
	 * @param height
	 *            height
	 */
	protected void updateVolume(double height) {
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
		if (bottom != null)
			return bottom.getPointsND();
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
		for (int i = 0; i < outputSegmentsBottom.size(); i++)
			outputSegmentsBottom.getElement(i).setEuclidianVisible(visible);
		for (int i = 0; i < outputSegmentsSide.size(); i++)
			outputSegmentsSide.getElement(i).setEuclidianVisible(visible);
		for (int i = 0; i < outputSegmentsTop.size(); i++)
			outputSegmentsTop.getElement(i).setEuclidianVisible(visible);
		for (int i = 0; i < outputPolygonsBottom.size(); i++)
			outputPolygonsBottom.getElement(i).setEuclidianVisible(visible,
					false);
		for (int i = 0; i < outputPolygonsSide.size(); i++)
			outputPolygonsSide.getElement(i)
					.setEuclidianVisible(visible, false);
		for (int i = 0; i < outputPolygonsTop.size(); i++)
			outputPolygonsTop.getElement(i).setEuclidianVisible(visible, false);
	}

	/**
	 * notify kernel update of output other than points
	 */
	public void notifyUpdateOutputOther() {
		for (int i = 0; i < outputSegmentsBottom.size(); i++)
			getKernel().notifyUpdate(outputSegmentsBottom.getElement(i));
		for (int i = 0; i < outputSegmentsSide.size(); i++)
			getKernel().notifyUpdate(outputSegmentsSide.getElement(i));
		for (int i = 0; i < outputSegmentsTop.size(); i++)
			getKernel().notifyUpdate(outputSegmentsTop.getElement(i));
		for (int i = 0; i < outputPolygonsBottom.size(); i++)
			getKernel().notifyUpdate(outputPolygonsBottom.getElement(i));
		for (int i = 0; i < outputPolygonsSide.size(); i++)
			getKernel().notifyUpdate(outputPolygonsSide.getElement(i));
		for (int i = 0; i < outputPolygonsTop.size(); i++)
			getKernel().notifyUpdate(outputPolygonsTop.getElement(i));
	}

	/**
	 * set output points invisible (use for previewable)
	 * 
	 * @param visible
	 *            flag
	 */
	public void setOutputPointsEuclidianVisible(boolean visible) {
		for (int i = 0; i < outputPoints.size(); i++)
			outputPoints.getElement(i).setEuclidianVisible(visible);
	}

	/**
	 * notify kernel update of output points
	 */
	public void notifyUpdateOutputPoints() {
		for (int i = 0; i < outputPoints.size(); i++)
			getKernel().notifyUpdate(outputPoints.getElement(i));
	}

	/**
	 * used for previewable of prism
	 * 
	 * @return the middle point of the bottom face (for prism)
	 */
	public Coords getBottomMiddlePoint() {
		Coords ret = new Coords(4);

		GeoPointND[] points = getBottomPoints();

		for (int i = 0; i < points.length; i++)
			ret = ret.add(points[i].getInhomCoordsInD3());

		return ret.mul((double) 1 / points.length);
	}

	/**
	 * used for previewable of prism
	 * 
	 * @return the middle point of the top face (for prism)
	 */
	public Coords getTopMiddlePoint() {
		Coords ret = new Coords(4);
		for (int i = 0; i < outputPoints.size(); i++)
			ret = ret.add(outputPoints.getElement(i).getInhomCoordsInD3());

		return ret.mul((double) 1 / outputPoints.size());
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

}
