package org.geogebra.common.geogebra3D.euclidian3D.draw;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.plot.CurveSegmentPlotter;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.Hitting;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterBrush;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterSurface;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoCurveCartesian3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSurfaceCartesian3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.Traversing.VariableReplacer;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.kernelND.SurfaceEvaluable;
import org.geogebra.common.kernel.kernelND.SurfaceEvaluable.LevelOfDetail;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.kernel.matrix.Coords3;
import org.geogebra.common.kernel.matrix.CoordsDouble3;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;

/**
 * Class for drawing a 2-var function
 * 
 * @author mathieu
 * 
 */
public class DrawSurface3D extends Drawable3DSurfaces implements HasZPick {

	final static private boolean DEBUG = false;

	/** The function being rendered */
	SurfaceEvaluable surfaceGeo;

	// number of intervals in root mesh (for each parameters, if parameters
	// delta are equals)
	private static final short ROOT_MESH_INTERVALS_SPEED = 10;
	private static final short ROOT_MESH_INTERVALS_SPEED_SQUARE = ROOT_MESH_INTERVALS_SPEED
			* ROOT_MESH_INTERVALS_SPEED;

	// number of split for boundary
	private static final short BOUNDARY_SPLIT = 10;

	// max split array size ( size +=4 for one last split)
	private static final int MAX_SPLIT_SPEED = 4096;
	private static final int MAX_SPLIT_QUALITY = MAX_SPLIT_SPEED * 2;

	private static final int MAX_SPLIT_IN_ONE_UPDATE_SPEED = 512;
	private static final int MAX_SPLIT_IN_ONE_UPDATE_QUALITY = MAX_SPLIT_IN_ONE_UPDATE_SPEED
			* 2;
	final private static int HIT_SAMPLES = 10;
	final private static double DELTA_SAMPLES = 1.0 / HIT_SAMPLES;

	private SurfaceEvaluable.LevelOfDetail levelOfDetail = SurfaceEvaluable.LevelOfDetail.QUALITY;

	private int maxSplit;

	// draw array size ( size +=1 for one last draw)
	private int maxDraw;

	private int cornerListSize;

	/**
	 * max splits in one update loop
	 */
	private int maxSplitsInOneUpdate;

	private DrawSurface3D.Corner[] currentSplit;
	private DrawSurface3D.Corner[] nextSplit;
	protected DrawSurface3D.Corner[] cornerList;

	/**
	 * list of things to draw
	 */
	protected CornerAndCenter[] drawList;

	private int currentSplitIndex;
	private int nextSplitIndex;
	protected int cornerListIndex;
	private int currentSplitStoppedIndex;
	protected int loopSplitIndex;
	protected int drawListIndex;
	private boolean drawFromScratch = true;
	private boolean drawUpToDate = false;
	// previously set thickness (-1 means needs update)
	private int oldThickness = -1;

	private boolean wireframeVisible = false;

	/** Current culling box - set to view3d.(x|y|z)(max|min) */
	private double[] cullingBox = new double[6];
	// corners for drawing wireframe (bottom and right sides)
	private Corner[] wireframeBottomCorners;
	private Corner[] wireframeRightCorners;
	private int wireframeBottomCornersLength;
	private int wireframeRightCornersLength;

	private Coords3 evaluatedPoint = newCoords3();
	private Coords3 evaluatedNormal = newCoords3();
	/**
	 * used to draw "still to split" corners
	 */
	protected Corner[] cornerForStillToSplit;
	protected Corner[] cornerToDrawStillToSplit;

	/**
	 * max distance in real world from view
	 */
	private double maxRWPixelDistance;
	/**
	 * max distance in real world for splitting
	 */
	private double maxRWDistance;
	/**
	 * max distance in real world under which we don't check angles
	 */
	protected double maxRWDistanceNoAngleCheck;
	protected double maxBend;
	protected int notDrawn;

	private boolean splitsStartedNotFinished;
	private boolean stillRoomLeft;

	private Coords boundsMin = new Coords(3);
	private Coords boundsMax = new Coords(3);
	/**
	 * first corner from root mesh
	 */
	private Corner firstCorner;

	private CurveHitting curveHitting;

	private ArrayList<GeoCurveCartesian3D> borders = new ArrayList<>();

	private SurfaceParameter uParam = new SurfaceParameter();
	private SurfaceParameter vParam = new SurfaceParameter();

	private static class NotEnoughCornersException extends Exception {
		private static final long serialVersionUID = 1L;
		private DrawSurface3D surface;

		public NotEnoughCornersException(DrawSurface3D surface,
				String message) {
			super(message);
			this.surface = surface;
		}

		public void caught() {
			printStackTrace();
			surface.setNoRoomLeft();
		}
	}

	/**
	 * common constructor
	 * 
	 * @param a_view3d
	 *            view
	 * @param surface
	 *            surface
	 */
	public DrawSurface3D(EuclidianView3D a_view3d, SurfaceEvaluable surface) {
		super(a_view3d, (GeoElement) surface);
		this.surfaceGeo = surface;

		levelOfDetail = null;

		cornerForStillToSplit = new Corner[6];
		cornerToDrawStillToSplit = new Corner[12];
		for (int i = 0; i < 12; i++) {
			cornerToDrawStillToSplit[i] = new Corner(-1);
		}

		splitsStartedNotFinished = false;

	}

	private void setLevelOfDetail() {

		SurfaceEvaluable.LevelOfDetail lod = surfaceGeo.getLevelOfDetail();

		if (levelOfDetail == lod) {
			return;
		}

		levelOfDetail = lod;

		// set sizes
		switch (levelOfDetail) {
		case SPEED:
			maxSplit = MAX_SPLIT_SPEED;
			maxSplitsInOneUpdate = MAX_SPLIT_IN_ONE_UPDATE_SPEED;
			break;
		case QUALITY:
			maxSplit = MAX_SPLIT_QUALITY;
			maxSplitsInOneUpdate = MAX_SPLIT_IN_ONE_UPDATE_QUALITY;
			break;
		}

		maxDraw = maxSplit;
		cornerListSize = maxDraw * 3;

		// create arrays
		currentSplit = new DrawSurface3D.Corner[maxSplit + 4];
		nextSplit = new DrawSurface3D.Corner[maxSplit + 4];
		drawList = new CornerAndCenter[maxDraw + 100];
		cornerList = new DrawSurface3D.Corner[cornerListSize];

	}

	private void setTolerances() {
		maxRWPixelDistance = CurveSegmentPlotter.MAX_PIXEL_DISTANCE;

		// set sizes
		switch (levelOfDetail) {
		case SPEED:
			maxRWDistanceNoAngleCheck = 1 * maxRWPixelDistance;
			maxRWDistance = 5 * maxRWPixelDistance;
			maxBend = getView3D().getMaxBendSpeedSurface();
			break;
		case QUALITY:
			maxRWDistanceNoAngleCheck = 1 * maxRWPixelDistance;
			maxRWDistance = 2 * maxRWPixelDistance;
			maxBend = CurveSegmentPlotter.MAX_BEND;
			break;
		}

	}

	/**
	 * console debug
	 * 
	 * @param s
	 *            message
	 */
	final static protected void debug(String s) {
		if (DEBUG) {
			Log.debug(s);
		}
	}

	@Override
	public void drawGeometry(Renderer renderer) {
		renderer.getRendererImpl().setLayer(getLayer());
		renderer.getGeometryManager().draw(getSurfaceIndex());
		renderer.getRendererImpl().setLayer(Renderer.LAYER_DEFAULT);
	}

	@Override
	protected void drawSurfaceGeometry(Renderer renderer) {
		drawGeometry(renderer);
	}

	@Override
	void drawGeometryHiding(Renderer renderer) {
		drawSurfaceGeometry(renderer);
	}

	@Override
	public void drawGeometryHidden(Renderer renderer) {

		if (wireframeNeeded()) {
			if (!isVisible()) {
				return;
			}

			if (!wireframeVisible) {
				return;
			}

			if (getGeoElement()
					.getLineTypeHidden() == EuclidianStyleConstants.LINE_TYPE_HIDDEN_NONE) {
				return;
			}

			setDrawingColor(GColor.DARK_GRAY);
			setLineTextureHidden(renderer);

			renderer.getGeometryManager().draw(getGeometryIndex());
		}

	}

	@Override
	public void drawOutline(Renderer renderer) {
		if (wireframeNeeded()) {
			if (!isVisible()) {
				return;
			}

			if (!wireframeVisible) {
				return;
			}

			setDrawingColor(GColor.DARK_GRAY);
			renderer.getTextures()
					.setDashFromLineType(getGeoElement().getLineType());

			renderer.getGeometryManager().draw(getGeometryIndex());
		}
	}

	@Override
	protected boolean updateForItSelf() {
		if (!surfaceGeo.isDefined()) {
			return false;
		}

		if (((GeoElement) surfaceGeo).isGeoFunctionNVar()) {
			if (((GeoFunctionNVar) surfaceGeo).getVarNumber() != 2) {
				setSurfaceIndex(-1);
				setGeometryIndex(-1);
				return true;
			}
		}

		boolean drawOccured = false;

		if (drawFromScratch) {
			borders.clear();
			drawUpToDate = false;

			// maybe it was set to null after redefine, so we need to compute it again
			surfaceGeo.setDerivatives();

			if (levelOfDetail == LevelOfDetail.QUALITY
					&& splitsStartedNotFinished) {
				draw();
				drawOccured = true;
			}

			// calc min/max values
			uParam.initBorder(surfaceGeo, getView3D(), 0);
			vParam.initBorder(surfaceGeo, getView3D(), 1);

			if (DoubleUtil.isZero(uParam.delta)
					|| DoubleUtil.isZero(vParam.delta)) {
				setSurfaceIndex(-1);
				setWireframeInvisible();
				return true;
			}

			// max values
			setLevelOfDetail();
			setTolerances();

			updateCullingBox();

			initBounds();

			debug("\nmax distances = " + maxRWDistance + ", "
					+ maxRWDistanceNoAngleCheck);

			// create root mesh
			double uOverVFactor = uParam.delta / vParam.delta;
			if (uOverVFactor > ROOT_MESH_INTERVALS_SPEED) {
				uOverVFactor = ROOT_MESH_INTERVALS_SPEED;
			} else if (uOverVFactor < 1.0 / ROOT_MESH_INTERVALS_SPEED) {
				uOverVFactor = 1.0 / ROOT_MESH_INTERVALS_SPEED;
			}
			uParam.n = (int) (ROOT_MESH_INTERVALS_SPEED * uOverVFactor);
			vParam.n = ROOT_MESH_INTERVALS_SPEED_SQUARE / uParam.n;
			uParam.n += 2;
			vParam.n += 2;

			uParam.init(levelOfDetail);
			vParam.init(levelOfDetail);

			debug("grids: " + uParam.n + ", " + vParam.n);
			cornerListIndex = 0;

			try {
				firstCorner = createRootMesh();

				// split root mesh as start
				currentSplitIndex = 0;
				currentSplitStoppedIndex = 0;
				nextSplitIndex = 0;
				drawListIndex = 0;
				notDrawn = 0;
				splitRootMesh(firstCorner);
				debug("\nnot drawn after split root mesh: " + notDrawn);

				// now splitted root mesh is ready
				drawFromScratch = false;
			} catch (NotEnoughCornersException e) {
				e.caught();
			}
		}

		if (wireframeNeeded()) {
			if (drawUpToDate) {
				// update is called for visual style, i.e. line thickness
				drawWireframe(getView3D().getRenderer());
				return true;
			}
		}

		// start recursive split
		loopSplitIndex = 0;
		// long time = System.currentTimeMillis();
		try {
			stillRoomLeft = split();
		} catch (NotEnoughCornersException e) {
			e.caught();
		}

		// time = System.currentTimeMillis() - time;
		// if (time > 0){
		// debug("split : "+time);
		// }

		debug("\ndraw size : " + drawListIndex + "\nnot drawn : " + notDrawn
				+ "\nstill to split : "
				+ (currentSplitIndex - currentSplitStoppedIndex)
				+ "\nnext to split : " + nextSplitIndex
				+ "\ncorner list size : " + cornerListIndex
				+ "\nstill room left : " + stillRoomLeft);

		splitsStartedNotFinished = (currentSplitIndex
				- currentSplitStoppedIndex) + nextSplitIndex > 0;

		// time = System.currentTimeMillis();

		// set old thickness to force wireframe update
		oldThickness = -1;

		switch (levelOfDetail) {
		case SPEED:
		default:
			draw();
			// still room left and still split to do: still to update
			drawUpToDate = !splitsStartedNotFinished || !stillRoomLeft;
			return drawUpToDate;
		case QUALITY:
			splitsStartedNotFinished = splitsStartedNotFinished
					&& stillRoomLeft;
			if (!splitsStartedNotFinished) {
				if (!drawOccured) {
					// no draw at start: can do the draw now
					draw();
					drawUpToDate = true;
					return true;
				}
				// no room left or no split to do: update is finished, but
				// the
				// object may change
				return false;
			}
			// still room left and still split to do: still to update
			return false;
		}

		// time = System.currentTimeMillis() - time;
		// if (time > 0){
		// debug("draw : "+time);
		// }

	}

	/**
	 * ends geometry
	 * 
	 * @param surface
	 *            surface plotter
	 * 
	 */
	static final private void endGeometry(PlotterSurface surface) {
		surface.endGeometryDirect();
	}

	/**
	 * draw all corners and centers
	 * 
	 * @param surface
	 *            surface plotter
	 * 
	 */
	protected void drawCornersAndCenters(PlotterSurface surface) {
		// used with GL.drawElements()
	}

	protected void startTriangles(PlotterSurface surface) {
		surface.startTriangles(cornerListIndex * 16);
	}

	private void draw() {

		Renderer renderer = getView3D().getRenderer();

		// point were already scaled
		renderer.getGeometryManager().setScalerIdentity();

		// draw splitted, still to split, and next to split
		PlotterSurface surface = renderer.getGeometryManager().getSurface();
		setPackSurface(true);
		surface.start(getReusableSurfaceIndex());

		if (!stillRoomLeft) {
			try {
				for (int i = currentSplitStoppedIndex; i < currentSplitIndex; i++) {
					currentSplit[i].split(true);
				}
				for (int i = 0; i < nextSplitIndex; i++) {
					nextSplit[i].split(true);
				}
			} catch (NotEnoughCornersException e) {
				e.caught();
			}
			debug("\n--- draw size : " + drawListIndex);
			if (drawListIndex > 0) {
				startTriangles(surface);
				for (int i = 0; i < drawListIndex; i++) {
					drawList[i].draw(surface);
				}

				drawCornersAndCenters(surface);
				endGeometry(surface);

			}

		} else {
			if (drawListIndex > 0 || splitsStartedNotFinished) {
				startTriangles(surface);
				for (int i = 0; i < drawListIndex; i++) {
					drawList[i].draw(surface);
				}

				drawCornersAndCenters(surface);

				for (int i = currentSplitStoppedIndex; i < currentSplitIndex; i++) {
					currentSplit[i].drawAsStillToSplit(surface);
				}
				for (int i = 0; i < nextSplitIndex; i++) {
					nextSplit[i].drawAsNextToSplit(surface);
				}

				endGeometry(surface);
			}
		}

		setSurfaceIndex(surface.end());
		endPacking();
		renderer.getGeometryManager().setScalerView();

		drawWireframe(renderer);
	}

	static final private boolean isDefinedForWireframe(Corner corner) {
		if (corner.p.isFinalUndefined()) {
			return false;
		}

		return corner.p.isDefined();
	}

	private void setWireframeInvisible() {
		wireframeVisible = false;
		if (shouldBePackedForManager()) {
			setGeometryIndexNotVisible();
		}
	}

	private void drawWireframe(Renderer renderer) {

		if (!wireframeNeeded()) {
			return;
		}

		int thickness = getGeoElement().getLineThickness();
		if (thickness == 0) {
			setWireframeInvisible();
			return;
		}

		// wireframe is visible
		wireframeVisible = true;

		if (thickness == oldThickness) {
			// surface and thickness have not changed
			return;
		}

		oldThickness = thickness;

		PlotterBrush brush = renderer.getGeometryManager().getBrush();

		// point were already scaled
		renderer.getGeometryManager().setScalerIdentity();

		setPackCurve(true);
		brush.start(getReusableGeometryIndex());
		brush.setThickness(thickness, (float) getView3D().getScale());
		brush.setAffineTexture(0f, 0f);
		brush.setLength(1f);

		for (int i = 0; i < wireframeBottomCornersLength; i++) {
			Corner above = wireframeBottomCorners[i];
			boolean currentPointIsDefined = isDefinedForWireframe(above);
			if (currentPointIsDefined) {
				brush.moveTo(above.p.getXd(), above.p.getYd(), above.p.getZd());
			}
			boolean lastPointIsDefined = currentPointIsDefined;
			above = above.a;
			while (above != null) {
				currentPointIsDefined = isDefinedForWireframe(above);
				if (currentPointIsDefined) {
					if (lastPointIsDefined) {
						brush.drawTo(above.p.getXd(), above.p.getYd(),
								above.p.getZd(), true);
					} else {
						brush.moveTo(above.p.getXd(), above.p.getYd(),
								above.p.getZd());
					}
				}
				lastPointIsDefined = currentPointIsDefined;
				above = above.a;
			}
			brush.endPlot();
		}

		for (int i = 0; i < wireframeRightCornersLength; i++) {
			Corner left = wireframeRightCorners[i];
			boolean currentPointIsDefined = isDefinedForWireframe(left);
			if (currentPointIsDefined) {
				brush.moveTo(left.p.getXd(), left.p.getYd(), left.p.getZd());
			}
			boolean lastPointIsDefined = currentPointIsDefined;
			left = left.l;
			while (left != null) {
				currentPointIsDefined = isDefinedForWireframe(left);
				if (currentPointIsDefined) {
					if (lastPointIsDefined) {
						brush.drawTo(left.p.getXd(), left.p.getYd(),
								left.p.getZd(), true);
					} else {
						brush.moveTo(left.p.getXd(), left.p.getYd(),
								left.p.getZd());
					}
				}
				lastPointIsDefined = currentPointIsDefined;
				left = left.l;
			}
			brush.endPlot();
		}

		setGeometryIndex(brush.end());
		endPacking();

		// point were already scaled
		renderer.getGeometryManager().setScalerView();
	}

	@Override
	protected void updateForView() {
		if (getView3D().viewChangedByZoom()
				|| getView3D().viewChangedByTranslate()) {
			setWaitForUpdate();
		}
	}

	@Override
	public void setWaitForUpdate() {
		drawFromScratch = true;
		super.setWaitForUpdate();
	}

	@Override
	public int getPickOrder() {
		return DRAW_PICK_ORDER_SURFACE;
	}

	private static boolean wireframeNeeded() {
		return true;
	}

	@Override
	public void addToDrawable3DLists(Drawable3DLists lists) {
		addToDrawable3DLists(lists, DRAW_TYPE_CLIPPED_SURFACES);
		if (wireframeNeeded()) {
			addToDrawable3DLists(lists, DRAW_TYPE_CLIPPED_CURVES);
		}
	}

	@Override
	public void removeFromDrawable3DLists(Drawable3DLists lists) {
		removeFromDrawable3DLists(lists, DRAW_TYPE_CLIPPED_SURFACES);
		if (wireframeNeeded()) {
			removeFromDrawable3DLists(lists, DRAW_TYPE_CLIPPED_CURVES);
		}
	}

	private boolean updateCullingBox() {
		EuclidianView3D view = getView3D();
		double off = maxRWPixelDistance * 4;
		double offScaled = off / getView3D().getXscale();
		cullingBox[0] = view.getXmin() - offScaled;
		cullingBox[1] = view.getXmax() + offScaled;
		offScaled = off / getView3D().getYscale();
		cullingBox[2] = view.getYmin() - offScaled;
		cullingBox[3] = view.getYmax() + offScaled;
		offScaled = off / getView3D().getZscale();
		cullingBox[4] = view.getZmin() - offScaled;
		cullingBox[5] = view.getZmax() + offScaled;
		return true;
	}

	private boolean inCullingBox(Coords3 p) {

		// check point is in culling box
		if ((p.getXd() > cullingBox[0]) && (p.getXd() < cullingBox[1])
				&& (p.getYd() > cullingBox[2]) && (p.getYd() < cullingBox[3])
				&& (p.getZd() > cullingBox[4]) && (p.getZd() < cullingBox[5])) {
			return true;
		}

		return false;
	}

	private void initBounds() {
		boundsMin.set(Double.POSITIVE_INFINITY);
		boundsMax.set(Double.NEGATIVE_INFINITY);
	}

	private void updateBounds(Coords3 p) {

		// update bounds
		if (p.getXd() < boundsMin.getX()) {
			boundsMin.setX(p.getXd());
		}
		if (p.getYd() < boundsMin.getY()) {
			boundsMin.setY(p.getYd());
		}
		if (p.getZd() < boundsMin.getZ()) {
			boundsMin.setZ(p.getZd());
		}
		if (p.getXd() > boundsMax.getX()) {
			boundsMax.setX(p.getXd());
		}
		if (p.getYd() > boundsMax.getY()) {
			boundsMax.setY(p.getYd());
		}
		if (p.getZd() > boundsMax.getZ()) {
			boundsMax.setZ(p.getZd());
		}

	}

	@Override
	public void enlargeBounds(Coords min, Coords max, boolean dontExtend) {
		if (!Double.isInfinite(boundsMin.getX())) {
			if (dontExtend) {
				reduceBounds(boundsMin, boundsMax);
			}
			enlargeBounds(min, max, boundsMin, boundsMax);
		}
	}

	private Corner createRootMesh() throws NotEnoughCornersException {
		if (wireframeNeeded()) {
			wireframeBottomCorners = new Corner[uParam.getCornerCount()];
			wireframeRightCorners = new Corner[vParam.getCornerCount()];
		}

		Corner bottomRight = newCorner(uParam.borderMax, vParam.borderMax);
		Corner first = bottomRight;

		wireframeBottomCornersLength = 0;
		wireframeRightCornersLength = 0;
		int wireFrameSetU = uParam.wireFrameStep,
				wireFrameSetV = vParam.wireFrameStep;
		if (wireframeNeeded()) {
			if (uParam.wireframeUnique) {
				wireFrameSetU = 0;
			}
			if (uParam.wireframeBorder == 1) { // draw edges
				wireframeBottomCorners[0] = first;
				wireframeBottomCornersLength = 1;
				wireFrameSetU = 1;
			}
			if (vParam.wireframeUnique) {
				wireFrameSetV = 0;
			}
			if (vParam.wireframeBorder == 1) { // draw edges
				wireframeRightCorners[0] = first;
				wireframeRightCornersLength = 1;
				wireFrameSetV = 1;
			}
		}

		// first row
		Corner right = bottomRight;
		int uN = uParam.n;
		for (int i = 0; i < uN - 1; i++) {
			right = addLeftToMesh(right, uParam.max - (uParam.delta * i) / uN,
					vParam.borderMax);
			if (wireframeNeeded()) {
				if (wireFrameSetU == uParam.wireFrameStep) { // set wireframe
					wireframeBottomCorners[wireframeBottomCornersLength] = right;
					wireframeBottomCornersLength++;
					if (uParam.wireframeUnique) {
						wireFrameSetU++;
					} else {
						wireFrameSetU = 1;
					}
				} else {
					wireFrameSetU++;
				}
			}
		}
		right = addLeftToMesh(right, uParam.borderMin, vParam.borderMax);
		if (wireframeNeeded()) {
			if (uParam.wireframeBorder == 1) {
				wireframeBottomCorners[wireframeBottomCornersLength] = right;
				wireframeBottomCornersLength++;
			}
		}
		int vN = vParam.n;
		// all intermediate rows
		for (int j = 0; j < vN - 1; j++) {
			bottomRight = addRowAboveToMesh(bottomRight,
					vParam.max - (vParam.delta * j) / vN, uParam.borderMin,
					uParam.borderMax, uParam.max, uN);
			if (wireframeNeeded()) {
				if (wireFrameSetV == vParam.wireFrameStep) { // set wireframe
					wireframeRightCorners[wireframeRightCornersLength] = bottomRight;
					wireframeRightCornersLength++;
					if (vParam.wireframeUnique) {
						wireFrameSetV++;
					} else {
						wireFrameSetV = 1;
					}
				} else {
					wireFrameSetV++;
				}
			}
		}

		// last row
		bottomRight = addRowAboveToMesh(bottomRight, vParam.borderMin,
				uParam.borderMin, uParam.borderMax, uParam.max, uN);
		if (wireframeNeeded()) {
			if (vParam.wireframeBorder == 1) {
				wireframeRightCorners[wireframeRightCornersLength] = bottomRight;
				wireframeRightCornersLength++;
			}
		}

		return first;
	}

	final private Corner addLeftToMesh(Corner right, double u, double v)
			throws NotEnoughCornersException {
		Corner left = newCorner(u, v);
		right.l = left;
		return left;
	}

	final private Corner addRowAboveToMesh(Corner bottomRight, double v,
			double uBorderMin, double uBorderMax, double uMax, int uN)
			throws NotEnoughCornersException {
		Corner below = bottomRight;
		Corner right = newCorner(uBorderMax, v);
		below.a = right;
		for (int i = 0; i < uN - 1; i++) {
			right = addLeftToMesh(right, uMax - (uParam.delta * i) / uN, v);
			below = below.l;
			below.a = right;
		}
		right = addLeftToMesh(right, uBorderMin, v);
		below = below.l;
		below.a = right;

		return bottomRight.a;
	}

	private static void splitRootMesh(Corner first)
			throws NotEnoughCornersException {

		Corner nextAbove, nextLeft;

		Corner current = first;
		while (current.a != null) {
			nextAbove = current.a;
			while (current.l != null) {
				nextLeft = current.l;
				if (nextLeft.a == null) { // already splitted by last row
					nextLeft = nextLeft.l;
				}
				// Log.debug(current.u + "," + current.v);
				current.split(false);
				current = nextLeft;
			}
			current = nextAbove;
		}

	}

	private boolean split() throws NotEnoughCornersException {

		if (currentSplitStoppedIndex == currentSplitIndex) {
			// swap stacks
			Corner[] tmp = currentSplit;
			currentSplit = nextSplit;
			nextSplit = tmp;
			currentSplitIndex = nextSplitIndex;
			nextSplitIndex = 0;
			currentSplitStoppedIndex = 0;
		}

		while (currentSplitStoppedIndex < currentSplitIndex
				&& loopSplitIndex < maxSplitsInOneUpdate) {
			currentSplit[currentSplitStoppedIndex].split(false);
			currentSplitStoppedIndex++;

			if (drawListIndex + (currentSplitIndex - currentSplitStoppedIndex)
					+ nextSplitIndex >= maxDraw) { // no room left for new draw
				return false;
			}

			if (nextSplitIndex >= maxSplit) { // no room left for new split
				return false;
			}
		}

		// debug("nextSplitIndex = " + nextSplitIndex + " , drawListIndex = " +
		// drawListIndex);

		if (loopSplitIndex < maxSplitsInOneUpdate && nextSplitIndex > 0) {
			return split();
		}

		return true; // went to end of loop

	}

	/**
	 * 
	 * @return new coords 3
	 */
	final static protected Coords3 newCoords3() {
		return new CoordsDouble3();
	}

	final private void scaleXYZ(Coords3 p) {
		getView3D().scaleXYZ(p);
	}

	final private void scaleAndNormalizeNormalXYZ(Coords3 n) {
		getView3D().scaleAndNormalizeNormalXYZ(n);
	}

	protected Coords3 evaluatePoint(double u, double v) {
		surfaceGeo.evaluatePoint(u, v, evaluatedPoint);

		if (!evaluatedPoint.isDefined()) {
			return Coords3.UNDEFINED;
		}

		updateBounds(evaluatedPoint);

		if (inCullingBox(evaluatedPoint)) {
			scaleXYZ(evaluatedPoint);
			return evaluatedPoint.copyVector();
		}

		return Coords3.UNDEFINED;
	}

	protected Coords3 evaluatePoint(double u, double v, Coords3 p) {

		// p is final value: use evaluatedPoint to compute
		if (p == null || p.isFinalUndefined()) {
			surfaceGeo.evaluatePoint(u, v, evaluatedPoint);

			if (!evaluatedPoint.isDefined()) {
				return Coords3.UNDEFINED;
			}

			updateBounds(evaluatedPoint);

			if (inCullingBox(evaluatedPoint)) {
				scaleXYZ(evaluatedPoint);
				return evaluatedPoint.copyVector();
			}

			return Coords3.UNDEFINED;
		}

		// p is not final value
		surfaceGeo.evaluatePoint(u, v, p);

		if (!p.isDefined()) {
			return Coords3.UNDEFINED;
		}

		updateBounds(p);

		if (inCullingBox(p)) {
			scaleXYZ(p);
			return p;
		}

		return Coords3.UNDEFINED;
	}

	protected Coords3 evaluateNormal(Coords3 p, double u, double v,
			Coords3 normal) {

		boolean defined;
		// normal is final value: use evaluatedNormal to compute
		if (normal == null || normal.isFinalUndefined()) {
			defined = surfaceGeo.evaluateNormal(p, u, v, evaluatedNormal);

			if (!defined) {
				return Coords3.UNDEFINED;
			}

			scaleAndNormalizeNormalXYZ(evaluatedNormal);
			return evaluatedNormal.copyVector();
		}

		// normal is not final value
		defined = surfaceGeo.evaluateNormal(p, u, v, normal);

		if (!defined) {
			return Coords3.UNDEFINED;
		}

		scaleAndNormalizeNormalXYZ(normal);
		return normal;

	}

	class Corner {
		Coords3 p;
		Coords3 normal;
		double u;
		double v;
		boolean isNotEnd;
		Corner a; // above
		Corner l; // left
		int id;

		public Corner(int id) {
			this.id = id;
		}

		public Corner(double u, double v, int id) {
			this.id = id;
			set(u, v);
		}

		public void set(double u, double v) {
			this.u = u;
			this.v = v;
			p = evaluatePoint(u, v, p);
			if (p.isFinalUndefined()) {
				normal = Coords3.UNDEFINED;
			} else {
				normal = evaluateNormal(p, u, v, normal);
			}
			isNotEnd = true;
			a = null;
			l = null;
		}

		public void set(Corner c) {
			u = c.u;
			v = c.v;
			p = c.p;
			normal = c.normal;
			id = c.id;
		}

		public Corner(double u, double v, Coords3 p) {
			set(u, v, p);
		}

		public void set(double u, double v, Coords3 p) {
			this.u = u;
			this.v = v;
			this.p = p;
			normal = evaluateNormal(p, u, v, normal);

			isNotEnd = true;
			a = null;
			l = null;
		}

		/**
		 * draw this corner as part of "next to split" list
		 * 
		 * @param surface
		 *            surface plotter
		 */
		public void drawAsNextToSplit(PlotterSurface surface) {

			// if (this.p.isNotFinalUndefined()){
			// if (a.p.isNotFinalUndefined()){
			// if (l.p.isNotFinalUndefined()){
			// drawTriangle(surface, this, a, l);
			// if (l.a.p.isNotFinalUndefined()){
			// drawTriangle(surface, l, a, l.a);
			// }
			// }else{
			// if (l.a.p.isNotFinalUndefined()){
			// drawTriangle(surface, this, a, l.a);
			// }
			// }
			// }else{
			// if (l.p.isNotFinalUndefined() && l.a.p.isNotFinalUndefined()){
			// drawTriangle(surface, this, l.a, l);
			// }
			// }
			// }else{ // this undefined
			// if (l.p.isNotFinalUndefined() && a.p.isNotFinalUndefined() &&
			// l.a.p.isNotFinalUndefined()){
			// drawTriangle(surface, l, a, l.a);
			// }
			// }

			drawAsStillToSplit(surface);

		}

		/**
		 * draw this corner as part of "still to split" list
		 * 
		 * @param surface
		 *            surface plotter
		 */
		public void drawAsStillToSplit(PlotterSurface surface) {

			// prevent keeping old element id
			for (int i = 0; i < cornerToDrawStillToSplit.length; i++) {
				cornerToDrawStillToSplit[i].id = -1;
			}

			// create ring about corners
			int length;

			cornerForStillToSplit[0] = this;
			cornerForStillToSplit[1] = this.l;

			if (this.l.a == null) { // a split occurred
				cornerForStillToSplit[2] = this.l.l;
				cornerForStillToSplit[3] = this.l.l.a;
				length = 4;
			} else {
				cornerForStillToSplit[2] = this.l.a;
				length = 3;
			}

			if (this.a.l == null) { // a split occurred
				cornerForStillToSplit[length] = this.a.a;
				length++;
				cornerForStillToSplit[length] = this.a;
				length++;
			} else {
				cornerForStillToSplit[length] = this.a;
				length++;
			}

			// check defined and create intermediate corners if needed
			Corner previous = cornerForStillToSplit[length - 1];
			int index = 0;
			for (int i = 0; i < length; i++) {
				Corner current = cornerForStillToSplit[i];

				if (current.p.isNotFinalUndefined()) {
					if (previous.p.isFinalUndefined()) {
						// previous undefined -- current defined : create
						// intermediate
						if (DoubleUtil.isEqual(previous.u, current.u)) {
							findV(current, previous, BOUNDARY_SPLIT,
									cornerToDrawStillToSplit[index]);
						} else {
							findU(current, previous, BOUNDARY_SPLIT,
									cornerToDrawStillToSplit[index]);
						}
						index++;
					}
					// add current for drawing
					cornerToDrawStillToSplit[index].set(current);
					index++;
				} else {
					if (previous.p.isNotFinalUndefined()) {
						// previous defined -- current undefined : create
						// intermediate
						if (DoubleUtil.isEqual(previous.u, current.u)) {
							findV(previous, current, BOUNDARY_SPLIT,
									cornerToDrawStillToSplit[index]);
						} else {
							findU(previous, current, BOUNDARY_SPLIT,
									cornerToDrawStillToSplit[index]);
						}
						index++;
					}
				}

				previous = current;
			}

			if (index < 3) {
				// Log.debug("index = "+index);
				return;
			}

			Coords3 v0 = new CoordsDouble3(), n0 = new CoordsDouble3();
			setBarycenter(v0, n0, index, cornerToDrawStillToSplit);

			for (int i = 0; i < index; i++) {
				drawTriangle(surface, v0, n0,
						cornerToDrawStillToSplit[(i + 1) % index],
						cornerToDrawStillToSplit[i]);
			}

		}

		public void split(boolean draw) throws NotEnoughCornersException {

			Corner left, above, subLeft, subAbove;

			if (l.a == null) {
				left = l.l;
				subLeft = l;
			} else {
				left = l;
				subLeft = null;
			}

			if (a.l == null) {
				above = a.a;
				subAbove = a;
			} else {
				above = a;
				subAbove = null;
			}

			if (p.isFinalUndefined()) {
				if (left.p.isFinalUndefined()) {
					if (above.p.isFinalUndefined()) {
						if (left.a.p.isFinalUndefined()) {
							// all undefined: nothing to draw /0/
							notDrawn++;
						} else {
							// l.a is defined /1/
							// find defined between l.a and a
							Corner n = newCorner();
							findU(left.a, above, BOUNDARY_SPLIT, n);
							// find defined between l.a and l
							Corner w = newCorner();
							findV(left.a, left, BOUNDARY_SPLIT, w);
							boolean split;
							if (draw) { // time to draw
								split = false;
							} else if (n.p.isFinalUndefined()
									|| w.p.isFinalUndefined()) { // some
																	// undefined
																	// point:
																	// force
																	// split
								split = true;
							} else { // check distance
								double d = getDistance(left.a, n, w);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check
																			// angle
									if (isAngleOK(maxBend, left.a, n, w)) { // angle
																			// ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
								}
							}
							if (split) {
								split(subLeft, left, subAbove, above);
							} else {
								// new neighbors
								n.l = left.a;
								above.l = n;
								// new neighbors
								w.a = left.a;
								left.a = w;

								// draw
								addToDrawList(w.a, n, w, w.a);
							}
						}
					} else {
						if (left.a.p.isFinalUndefined()) {
							// a defined /1/
							// find defined between a and l.a
							Corner n = newCorner();
							findU(above, left.a, BOUNDARY_SPLIT, n);
							// find defined between a and this
							Corner e;
							if (subAbove != null) {
								e = subAbove;
							} else {
								e = newCorner();
								findV(above, this, BOUNDARY_SPLIT, e);
							}
							boolean split;
							if (draw) { // time to draw
								split = false;
							} else if (n.p.isFinalUndefined()
									|| e.p.isFinalUndefined()) { // some
																	// undefined
																	// point:
																	// force
																	// split
								split = true;
							} else { // check distance
								double d = getDistance(above, n, e);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check
																			// angle
									if (isAngleOK(maxBend, above, n, e)) { // angle
																			// ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
								}
							}
							if (split) {
								split(subLeft, left, subAbove, above);
							} else {
								// new neighbors
								if (subAbove == null) {
									this.a = e;
									e.a = above;
								}
								n.l = left.a;
								above.l = n;

								// drawing
								addToDrawList(left.a, n, e, above);
							}
						} else {
							// a and l.a defined /2/
							boolean split;
							if (draw) { // time to draw
								split = false;
							} else if (subAbove != null
									&& subAbove.p.isFinalUndefined()) { // some
																		// undefined
																		// point:
																		// force
																		// split
								split = true;
							} else { // check distance
								double d = getDistance(above, left.a);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check
																			// angle
									if (isAngleOK(maxBend, above, left.a)) { // angle
																				// ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
								}
							}
							if (split) {
								split(subLeft, left, subAbove, above);
							} else {
								// find defined between a and this
								Corner e;
								if (subAbove != null) {
									e = subAbove;
								} else {
									e = newCorner();
									findV(above, this, BOUNDARY_SPLIT, e);
								}
								// find defined between l.a and left
								Corner w = newCorner();
								findV(left.a, left, BOUNDARY_SPLIT, w);

								if (!draw) {
									// check distances
									double d = getDistanceNoLoop(above, e, w,
											left.a);
									if (Double.isInfinite(d)) { // d >
																// maxRWDistance
										split = true;
									} else if (d > maxRWDistanceNoAngleCheck) { // check
																				// angle
										if (isAngleOKNoLoop(maxBend, above, e,
												w, left.a)) { // angle ok
											split = false;
										} else { // angle not ok
											split = true;
										}
									} else { // no need to check angle
										split = false;
									}
								}

								if (split) {
									split(subLeft, left, subAbove, above);
								} else {
									if (subAbove == null) {
										// new neighbors
										this.a = e;
										e.a = above;
									}
									// new neighbors
									w.a = left.a;
									left.a = w;

									// drawing
									addToDrawList(w.a, e, above, left.a, w);
								}
							}
						}
					}
				} else {
					if (above.p.isFinalUndefined()) {
						if (left.a.p.isFinalUndefined()) {
							// l defined /1/
							// find defined between l and this
							Corner s;
							if (subLeft != null) {
								s = subLeft;
							} else {
								s = newCorner();
								findU(left, this, BOUNDARY_SPLIT, s);
							}
							// find defined between l and l.a
							Corner w = newCorner();
							findV(left, left.a, BOUNDARY_SPLIT, w);
							boolean split;
							if (draw) { // time to draw
								split = false;
							} else if (s.p.isFinalUndefined()
									|| w.p.isFinalUndefined()) { // some
																	// undefined
																	// point:
																	// force
																	// split
								split = true;
							} else { // check distance
								double d = getDistance(left, s, w);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check
																			// angle
									if (isAngleOK(maxBend, left, s, w)) { // angle
																			// ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
								}
							}
							if (split) {
								split(subLeft, left, subAbove, above);
							} else {
								// new neighbors
								if (subLeft == null) {
									this.l = s;
									s.l = left;
								}
								w.a = left.a;
								left.a = w;

								// drawing
								addToDrawList(w.a, s, w, left);

							}
						} else {
							// l and l.a defined /2/
							boolean split;
							if (draw) { // time to draw
								split = false;
							} else if (subLeft != null
									&& subLeft.p.isFinalUndefined()) { // some
																		// undefined
																		// point:
																		// force
																		// split
								split = true;
							} else { // check distance
								double d = getDistance(left.a, left);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check
																			// angle
									if (isAngleOK(maxBend, left.a, left)) { // angle
																			// ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
								}
							}
							if (split) {
								split(subLeft, left, subAbove, above);
							} else {
								// find defined between l and this
								Corner s;
								if (subLeft != null) {
									s = subLeft;
								} else {
									s = newCorner();
									findU(left, this, BOUNDARY_SPLIT, s);
								}
								// find defined between l.a and a
								Corner n = newCorner();
								findU(left.a, above, BOUNDARY_SPLIT, n);

								if (!draw) {
									// check distances
									double d = getDistanceNoLoop(left.a, n, s,
											left);
									if (Double.isInfinite(d)) { // d >
																// maxRWDistance
										split = true;
									} else if (d > maxRWDistanceNoAngleCheck) { // check
																				// angle
										if (isAngleOKNoLoop(maxBend, left.a, n,
												s, left)) { // angle ok
											split = false;
										} else { // angle not ok
											split = true;
										}
									} else { // no need to check angle
										split = false;
									}
								}

								if (split) {
									split(subLeft, left, subAbove, above);
								} else {
									if (subLeft == null) {
										// new neighbors
										this.l = s;
										s.l = left;
									}
									// new neighbors
									n.l = left.a;
									above.l = n;

									// drawing
									addToDrawList(left.a, s, n, left.a, left);
								}
							}
						}
					} else {
						if (left.a.p.isFinalUndefined()) {
							// l and a not undefined /2/diag/
							boolean split;
							if (draw) { // time to draw
								split = false;
							} else { // check distance
								double d = getDistance(left, above);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check
																			// angle
									if (isAngleOK(maxBend, left, above)) { // angle
																			// ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
								}
							}
							if (split) {
								split(subLeft, left, subAbove, above);
							} else {
								// find defined between l and this
								Corner s;
								if (subLeft != null) {
									s = subLeft;
								} else {
									s = newCorner();
									findU(left, this, BOUNDARY_SPLIT, s);
									// new neighbors
									this.l = s;
									s.l = left;
								}
								// find defined between a and this
								Corner e;
								if (subAbove != null) {
									e = subAbove;
								} else {
									e = newCorner();
									findV(above, this, BOUNDARY_SPLIT, e);
									// new neighbors
									this.a = e;
									e.a = above;
								}
								// find defined between l and l.a
								Corner w = newCorner();
								findV(left, left.a, BOUNDARY_SPLIT, w);
								w.a = left.a;
								left.a = w;
								// find defined between a and l.a
								Corner n = newCorner();
								findU(above, left.a, BOUNDARY_SPLIT, n);
								n.l = above.l;
								above.l = n;

								// drawing
								addToDrawList(w.a, left, above);

							}
						} else {
							// l, a and l.a not undefined /3/
							boolean split;
							if (draw) { // time to draw
								split = false;
							} else if (subLeft != null
									&& subLeft.p.isFinalUndefined()) { // some
																		// undefined
																		// point:
																		// force
																		// split
								split = true;
							} else if (subAbove != null
									&& subAbove.p.isFinalUndefined()) { // some
																		// undefined
																		// point:
																		// force
																		// split
								split = true;
							} else { // check distance
								double d = getDistance(left.a, left, above);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check
																			// angle
									if (isAngleOK(maxBend, left.a, left,
											above)) { // angle ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
								}
							}
							if (split) {
								split(subLeft, left, subAbove, above);
							} else {
								// find defined between l and this
								Corner s;
								if (subLeft != null) {
									s = subLeft;
								} else {
									s = newCorner();
									findU(left, this, BOUNDARY_SPLIT, s);
									// new neighbors
									this.l = s;
									s.l = left;
								}
								// find defined between a and this
								Corner e;
								if (subAbove != null) {
									e = subAbove;
								} else {
									e = newCorner();
									findV(above, this, BOUNDARY_SPLIT, e);
									// new neighbors
									this.a = e;
									e.a = above;
								}

								// drawing
								addToDrawList(left.a, left, above, left.a);
							}
						}
					}
				}
			} else {
				if (left.p.isFinalUndefined()) {
					if (above.p.isFinalUndefined()) {
						if (left.a.p.isFinalUndefined()) {
							// this defined /1/
							// find defined between this and l
							Corner s;
							if (subLeft != null) {
								s = subLeft;
							} else {
								s = newCorner();
								findU(this, left, BOUNDARY_SPLIT, s);
							}
							// find defined between this and a
							Corner e;
							if (subAbove != null) {
								e = subAbove;
							} else {
								e = newCorner();
								findV(this, above, BOUNDARY_SPLIT, e);
							}
							boolean split;
							if (draw) { // time to draw
								split = false;
							} else if (s.p.isFinalUndefined()
									|| e.p.isFinalUndefined()) { // some
																	// undefined
																	// point:
																	// force
																	// split
								split = true;
							} else { // check distance
								double d = getDistance(this, s, e);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check
																			// angle
									if (isAngleOK(maxBend, this, s, e)) { // angle
																			// ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
								}
							}
							if (split) {
								split(subLeft, left, subAbove, above);
							} else {
								// new neighbors
								if (subLeft == null) {
									this.l = s;
									s.l = left;
								}
								if (subAbove == null) {
									this.a = e;
									e.a = above;
								}

								// drawing
								addToDrawList(left.a, s, e, this);
							}
						} else {
							// this and l.a not undefined /2/diag/
							boolean split;
							if (draw) { // time to draw
								split = false;
							} else { // check distance
								double d = getDistance(left.a, this);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check
																			// angle
									if (isAngleOK(maxBend, left.a, this)) { // angle
																			// ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
								}
							}
							if (split) {
								split(subLeft, left, subAbove, above);
							} else {
								// find defined between l and this
								Corner s;
								if (subLeft != null) {
									s = subLeft;
								} else {
									s = newCorner();
									findU(this, left, BOUNDARY_SPLIT, s);
									// new neighbors
									this.l = s;
									s.l = left;
								}
								// find defined between a and this
								Corner e;
								if (subAbove != null) {
									e = subAbove;
								} else {
									e = newCorner();
									findV(this, above, BOUNDARY_SPLIT, e);
									// new neighbors
									this.a = e;
									e.a = above;
								}
								// find defined between l and l.a
								Corner w = newCorner();
								findV(left.a, left, BOUNDARY_SPLIT, w);
								w.a = left.a;
								left.a = w;
								// find defined between a and l.a
								Corner n = newCorner();
								findU(left.a, above, BOUNDARY_SPLIT, n);
								n.l = above.l;
								above.l = n;

								// drawing
								addToDrawList(w.a, this, left.a);
							}
						}
					} else {
						if (left.a.p.isFinalUndefined()) {
							// this and a defined /2/
							boolean split;
							if (draw) { // time to draw
								split = false;
							} else if (subLeft != null
									&& subLeft.p.isFinalUndefined()) { // some
																		// undefined
																		// point:
																		// force
																		// split
								split = true;
							} else { // check distance
								double d = getDistance(this, above);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check
																			// angle
									if (isAngleOK(maxBend, this, above)) { // angle
																			// ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
								}
							}
							if (split) {
								split(subLeft, left, subAbove, above);
							} else {
								// find defined between this and l
								Corner s;
								if (subLeft != null) {
									s = subLeft;
								} else {
									s = newCorner();
									findU(this, left, BOUNDARY_SPLIT, s);
								}
								// find defined between a and l.a
								Corner n = newCorner();
								findU(above, left.a, BOUNDARY_SPLIT, n);

								if (!draw) {
									// check distances
									double d = getDistanceNoLoop(this, s, n,
											above);
									if (Double.isInfinite(d)) { // d >
																// maxRWDistance
										split = true;
									} else if (d > maxRWDistanceNoAngleCheck) { // check
																				// angle
										if (isAngleOKNoLoop(maxBend, this, s, n,
												above)) { // angle ok
											split = false;
										} else { // angle not ok
											split = true;
										}
									} else { // no need to check angle
										split = false;
									}
								}

								if (split) {
									split(subLeft, left, subAbove, above);
								} else {
									if (subLeft == null) {
										// new neighbors
										this.l = s;
										s.l = left;
									}
									// new neighbors
									n.l = left.a;
									above.l = n;

									// drawing
									addToDrawList(left.a, this, above, n, s);

								}
							}
						} else {
							// this, a and l.a defined /3/
							boolean split;
							if (draw) { // time to draw
								split = false;
							} else if (subLeft != null
									&& subLeft.p.isFinalUndefined()) { // some
																		// undefined
																		// point:
																		// force
																		// split
								split = true;
							} else { // check distance
								double d = getDistance(above, left.a, this);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check
																			// angle
									if (isAngleOK(maxBend, above, left.a,
											this)) { // angle ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
								}
							}
							if (split) {
								split(subLeft, left, subAbove, above);
							} else {
								// find defined between this and l
								Corner s;
								if (subLeft != null) {
									s = subLeft;
								} else {
									s = newCorner();
									findU(this, left, BOUNDARY_SPLIT, s);
									// new neighbors
									this.l = s;
									s.l = left;
								}
								// find defined between l.a and l
								Corner w = newCorner();
								findV(left.a, left, BOUNDARY_SPLIT, w);
								// new neighbors
								w.a = left.a;
								left.a = w;

								// drawing
								addToDrawList(w.a, above, left.a, this);
							}
						}
					}
				} else {
					if (above.p.isFinalUndefined()) {
						if (left.a.p.isFinalUndefined()) {
							// this and l defined /2/
							boolean split;
							if (draw) { // time to draw
								split = false;
							} else if (subAbove != null
									&& subAbove.p.isFinalUndefined()) { // some
																		// undefined
																		// point:
																		// force
																		// split
								split = true;
							} else { // check distance
								double d = getDistance(this, left);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check
																			// angle
									if (isAngleOK(maxBend, this, left)) { // angle
																			// ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
								}
							}
							if (split) {
								split(subLeft, left, subAbove, above);
							} else {
								// find defined between this and a
								Corner e;
								if (subAbove != null) {
									e = subAbove;
								} else {
									e = newCorner();
									findV(this, above, BOUNDARY_SPLIT, e);
								}
								// find defined between l and l.a
								Corner w = newCorner();
								findV(left, left.a, BOUNDARY_SPLIT, w);

								if (!draw) {
									// check distances
									double d = getDistanceNoLoop(this, e, w,
											left);
									if (Double.isInfinite(d)) { // d >
																// maxRWDistance
										split = true;
									} else if (d > maxRWDistanceNoAngleCheck) { // check
																				// angle
										if (isAngleOKNoLoop(maxBend, this, e, w,
												left)) { // angle ok
											split = false;
										} else { // angle not ok
											split = true;
										}
									} else { // no need to check angle
										split = false;
									}
								}

								if (split) {
									split(subLeft, left, subAbove, above);
								} else {
									if (subAbove == null) {
										// new neighbors
										this.a = e;
										e.a = above;
									}
									// new neighbors
									w.a = left.a;
									left.a = w;

									// drawing
									addToDrawList(w.a, this, e, w, left);
								}
							}
						} else {
							// this, l and l.a not undefined /3/
							boolean split;
							if (draw) { // time to draw
								split = false;
							} else if (subAbove != null
									&& subAbove.p.isFinalUndefined()) { // some
																		// undefined
																		// point:
																		// force
																		// split
								split = true;
							} else { // check distance
								double d = getDistance(left, left.a, this);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check
																			// angle
									if (isAngleOK(maxBend, left, left.a,
											this)) { // angle ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
								}
							}
							if (split) {
								split(subLeft, left, subAbove, above);
							} else {
								// find defined between l.a and a
								Corner n = newCorner();
								findU(left.a, above, BOUNDARY_SPLIT, n);
								// find defined between this and a
								Corner e;
								if (subAbove != null) {
									e = subAbove;
								} else {
									e = newCorner();
									findV(this, above, BOUNDARY_SPLIT, e);
									// new neighbors
									this.a = e;
									e.a = above;
								}
								// new neighbors
								n.l = left.a;
								above.l = n;

								// drawing
								addToDrawList(left.a, left, left.a, this);
							}
						}
					} else {
						if (left.a.p.isFinalUndefined()) {
							// this, l and a not undefined /3/
							boolean split;
							if (draw) { // time to draw
								split = false;
							} else { // check distance
								double d = getDistance(this, left, above);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check
																			// angle
									if (isAngleOK(maxBend, this, left, above)) { // angle
																					// ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
								}
							}
							if (split) {
								split(subLeft, left, subAbove, above);
							} else {
								// find defined between a and l.a
								Corner n = newCorner();
								findU(above, left.a, BOUNDARY_SPLIT, n);
								// find defined between l and l.a
								Corner w = newCorner();
								findV(left, left.a, BOUNDARY_SPLIT, w);
								// new neighbors
								n.l = left.a;
								above.l = n;
								// new neighbors
								w.a = left.a;
								left.a = w;

								// drawing
								addToDrawList(w.a, this, left, above);

							}
						} else {
							// this, l, a and l.a defined /4/
							if (draw) {
								// drawing
								addToDrawList(left.a, this, left, above,
										left.a);
							} else {
								// check distances
								double d = getDistance(this, left, above,
										left.a);
								if (Double.isInfinite(d)
										|| (d > maxRWDistanceNoAngleCheck
												&& !isAngleOK(maxBend, this,
														left, above, left.a))) {
									split(subLeft, left, subAbove, above);
								} else {
									// drawing
									addToDrawList(left.a, this, left, above,
											left.a);
								}
							}
						}
					}
				}
			}

		}

		private void split(Corner subLeft, Corner left, Corner subAbove,
				Corner above) throws NotEnoughCornersException {
			// new corners
			double um = (u + left.u) / 2;
			double vm = (v + above.v) / 2;
			if (subLeft != null) {
				um = subLeft.u;
			}
			if (subAbove != null) {
				vm = subAbove.v;
			}
			Corner e;
			if (subAbove != null) {
				e = subAbove;
			} else {
				e = newCorner(u, vm);
				// new neighbors
				this.a = e;
				e.a = above;
			}
			Corner s;
			if (subLeft != null) {
				s = subLeft;
			} else {
				s = newCorner(um, v);
				// new neighbors
				this.l = s;
				s.l = left;
			}
			Corner m = newCorner(um, vm);
			s.a = m;
			e.l = m;
			Corner n = newCorner(um, above.v);
			n.l = above.l;
			above.l = n;
			m.a = n;
			Corner w = newCorner(left.u, vm);
			w.a = left.a;
			left.a = w;
			m.l = w;
			// next split
			addToNextSplit(this);
			addToNextSplit(s);
			addToNextSplit(e);
			addToNextSplit(m);

			loopSplitIndex += 4;
		}

		private void addToDrawList(Corner end, Corner... corners) {

			CornerAndCenter cc = drawList[drawListIndex];
			if (cc == null) {
				cc = new CornerAndCenter(this, drawListIndex);
				drawList[drawListIndex] = cc;
			} else {
				cc.setCorner(this);
			}
			drawListIndex++;
			loopSplitIndex++;

			setBarycenter(cc.getCenter(), cc.getCenterNormal(), corners);

			end.isNotEnd = false;
		}

		private void findU(Corner defined, Corner undefined, int depth,
				Corner corner) {
			findU(defined.p, defined.u, defined.u, undefined.u, defined.v,
					depth, corner, true);
		}

		private void findU(Coords3 lastDefined, double uLastDef, double uDef,
				double uUndef, double vRow, int depth, Corner corner,
				boolean lastDefinedIsFirst) {

			double uNew = (uDef + uUndef) / 2;
			Coords3 coords = evaluatePoint(uNew, vRow);

			if (depth == 0) { // no more split
				if (coords.isFinalUndefined()) {
					// return last defined point
					if (lastDefinedIsFirst) {
						corner.set(uLastDef, vRow, lastDefined.copyVector());
					} else {
						corner.set(uLastDef, vRow, lastDefined);
					}
				} else {
					corner.set(uNew, vRow, coords);
				}
			} else {
				if (coords.isFinalUndefined()) {
					findU(lastDefined, uLastDef, uDef, uNew, vRow, depth - 1,
							corner, lastDefinedIsFirst);
				} else {
					findU(coords, uNew, uNew, uUndef, vRow, depth - 1, corner,
							false);
				}
			}

		}

		private void findV(Corner defined, Corner undefined, int depth,
				Corner corner) {
			findV(defined.p, defined.v, defined.v, undefined.v, defined.u,
					depth, corner, true);
		}

		private void findV(Coords3 lastDefined, double vLastDef, double vDef,
				double vUndef, double uRow, int depth, Corner corner,
				boolean lastDefinedIsFirst) {

			double vNew = (vDef + vUndef) / 2;
			Coords3 coords = evaluatePoint(uRow, vNew);

			if (depth == 0) { // no more split
				if (coords.isFinalUndefined()) {
					// return last defined point
					if (lastDefinedIsFirst) {
						corner.set(uRow, vLastDef, lastDefined.copyVector());
					} else {
						corner.set(uRow, vLastDef, lastDefined);
					}
				} else {
					corner.set(uRow, vNew, coords);
				}
			} else {
				if (coords.isFinalUndefined()) {
					findV(lastDefined, vLastDef, vDef, vNew, uRow, depth - 1,
							corner, lastDefinedIsFirst);
				} else {
					findV(coords, vNew, vNew, vUndef, uRow, depth - 1, corner,
							false);
				}
			}

		}

	}

	/**
	 * set center as barycenter for points
	 * 
	 * @param center
	 *            center
	 * @param normal
	 *            normal for center point
	 * @param c
	 *            corners
	 * 
	 */
	static protected void setBarycenter(Coords3 center, Coords3 normal,
			Corner... c) {
		setBarycenter(center, normal, c.length, c);
	}

	/**
	 * set center as barycenter for points
	 * 
	 * @param center
	 *            center
	 * @param normal
	 *            normal for center point
	 * @param length
	 *            length of considered corners
	 * @param c
	 *            corners
	 * 
	 */
	static protected void setBarycenter(Coords3 center, Coords3 normal,
			int length, Corner... c) {
		double f = 1.0 / length;

		// // try first barycenter about parameters
		// double u = 0, v = 0;
		// for (int j = 0; j < length; j++) {
		// u += c[j].u;
		// v += c[j].v;
		// }
		// u *= f;
		// v *= f;
		// Coords3 ret = evaluatePoint(u, v, center);
		// if (ret.isNotFinalUndefined()){ // center is not undefined
		// ret = evaluateNormal(center, u, v, normal);
		// if (ret.isNotFinalUndefined()){ // normal is not undefined
		// return;
		// }
		// }

		// center is undefined : barycenter about coords
		center.set(0, 0, 0);
		normal.set(0, 0, 0);
		// int lengthDefined = 0;
		for (int j = 0; j < length; j++) {
			// if (!center.isFinalUndefined()){
			center.addInside(c[j].p);
			normal.addInside(c[j].normal);
			// lengthDefined ++;
			// }
		}
		// f = 1.0 / lengthDefined;
		center.mulInside(f);
		normal.normalizeIfPossible();

		// if (!center.isDefined()) {
		// App.printStacktrace("!center.isDefined()");
		// }
		// if (!normal.isDefined()) {
		// App.printStacktrace("!normal.isDefined()");
		// }

	}

	/**
	 * 
	 * @param c1
	 *            first corner
	 * @param c2
	 *            second corner
	 * @return distance between c1 and c2, or POSITIVE_INFINITY if distance is
	 *         more than maxRWDistance
	 */
	protected double getDistance(Corner c1, Corner c2) {

		double ret = 0;

		double d = Math.abs(c1.p.getXd() - c2.p.getXd());
		if (d > maxRWDistance) {
			return Double.POSITIVE_INFINITY;
		}
		if (d > ret) {
			ret = d;
		}

		d = Math.abs(c1.p.getYd() - c2.p.getYd());
		if (d > maxRWDistance) {
			return Double.POSITIVE_INFINITY;
		}
		if (d > ret) {
			ret = d;
		}

		d = Math.abs(c1.p.getZd() - c2.p.getZd());
		if (d > maxRWDistance) {
			return Double.POSITIVE_INFINITY;
		}
		if (d > ret) {
			ret = d;
		}

		return ret;
	}

	/**
	 * 
	 * @param c1
	 *            first corner
	 * @param c2
	 *            second corner
	 * @param c3
	 *            third corner
	 * @param c4
	 *            fourth corner
	 * @return max distance between c1-c2 / c2-c3 / c3-c4 / c4-c1, or
	 *         POSITIVE_INFINITY if distance is more than maxRWDistance
	 */
	protected double getDistance(Corner c1, Corner c2, Corner c3, Corner c4) {
		double ret = 0;
		double d;

		d = getDistance(c1, c2);
		if (Double.isInfinite(d)) {
			return d;
		}
		ret = d;

		d = getDistance(c2, c3);
		if (Double.isInfinite(d)) {
			return d;
		}
		if (d > ret) {
			ret = d;
		}

		d = getDistance(c3, c4);
		if (Double.isInfinite(d)) {
			return d;
		}
		if (d > ret) {
			ret = d;
		}

		d = getDistance(c4, c1);
		if (Double.isInfinite(d)) {
			return d;
		}
		if (d > ret) {
			ret = d;
		}

		return ret;
	}

	/**
	 * 
	 * @param c1
	 *            first corner
	 * @param c2
	 *            second corner
	 * @param c3
	 *            third corner
	 * @param c4
	 *            fourth corner
	 * @return max distance between c1-c2 / c2-c3 / c3-c4, or POSITIVE_INFINITY
	 *         if distance is more than maxRWDistance
	 */
	protected double getDistanceNoLoop(Corner c1, Corner c2, Corner c3,
			Corner c4) {
		double ret = 0;
		double d;

		d = getDistance(c1, c2);
		if (Double.isInfinite(d)) {
			return d;
		}
		ret = d;

		d = getDistance(c2, c3);
		if (Double.isInfinite(d)) {
			return d;
		}
		if (d > ret) {
			ret = d;
		}

		d = getDistance(c3, c4);
		if (Double.isInfinite(d)) {
			return d;
		}
		if (d > ret) {
			ret = d;
		}

		return ret;
	}

	/**
	 * 
	 * @param c1
	 *            first corner
	 * @param c2
	 *            second corner
	 * @param c3
	 *            third corner
	 * @return max distance between c1-c2 / c2-c3 / c3-c1, or POSITIVE_INFINITY
	 *         if distance is more than maxRWDistance
	 */
	protected double getDistance(Corner c1, Corner c2, Corner c3) {
		double ret = 0;
		double d;

		d = getDistance(c1, c2);
		if (Double.isInfinite(d)) {
			return d;
		}
		ret = d;

		d = getDistance(c2, c3);
		if (Double.isInfinite(d)) {
			return d;
		}
		if (d > ret) {
			ret = d;
		}

		d = getDistance(c3, c1);
		if (Double.isInfinite(d)) {
			return d;
		}
		if (d > ret) {
			ret = d;
		}

		return ret;
	}

	/**
	 * Returns whether the angle between the vectors (vx, vy) and (wx, wy) is
	 * smaller than MAX_BEND, where MAX_BEND = tan(MAX_ANGLE).
	 */
	private static boolean isAngleOK(Coords3 v, Coords3 w, double bend) {
		// |v| * |w| * sin(alpha) = |det(v, w)|
		// cos(alpha) = v . w / (|v| * |w|)
		// tan(alpha) = sin(alpha) / cos(alpha)
		// tan(alpha) = |det(v, w)| / v . w

		// small angle: tan(alpha) < MAX_BEND
		// |det(v, w)| / v . w < MAX_BEND
		// |det(v, w)| < MAX_BEND * (v . w)

		double innerProduct = v.getXd() * w.getXd() + v.getYd() * w.getYd()
				+ v.getZd() * w.getZd();

		if (innerProduct <= 0) {
			// angle >= 90 degrees
			return false;
		}

		// angle < 90 degrees
		// small angle: |det(v, w)| < MAX_BEND * (v . w)
		double d1 = v.getXd() * w.getYd() - v.getYd() * w.getXd();
		double d2 = v.getYd() * w.getZd() - v.getZd() * w.getYd();
		double d3 = v.getZd() * w.getXd() - v.getXd() * w.getZd();
		double det = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
		return det < bend * innerProduct;
	}

	/**
	 * 
	 * @param bend
	 *            tan of max angle
	 * @param c1
	 *            first corner
	 * @param c2
	 *            second corner
	 * @return true if angle is ok between c1-c2
	 */
	protected static boolean isAngleOK(double bend, Corner c1, Corner c2) {
		return isAngleOK(c1.normal, c2.normal, bend);
	}

	/**
	 * 
	 * @param bend
	 *            tan of max angle
	 * @param c1
	 *            first corner
	 * @param c2
	 *            second corner
	 * @param c3
	 *            third corner
	 * @return true if angle is ok between c1-c2 and c2-c3 and c3-c1
	 */
	protected static boolean isAngleOK(double bend, Corner c1, Corner c2,
			Corner c3) {
		return isAngleOK(c1.normal, c2.normal, bend)
				&& isAngleOK(c2.normal, c3.normal, bend)
				&& isAngleOK(c3.normal, c1.normal, bend);
	}

	/**
	 * 
	 * @param bend
	 *            tan of max angle
	 * @param c1
	 *            first corner
	 * @param c2
	 *            second corner
	 * @param c3
	 *            third corner
	 * @param c4
	 *            fourth corner
	 * @return true if angle is ok between c1-c2 and c2-c3 and c3-c4 and c4-c1
	 */
	protected static boolean isAngleOK(double bend, Corner c1, Corner c2,
			Corner c3, Corner c4) {
		return isAngleOK(c1.normal, c2.normal, bend)
				&& isAngleOK(c2.normal, c3.normal, bend)
				&& isAngleOK(c3.normal, c4.normal, bend)
				&& isAngleOK(c4.normal, c1.normal, bend);
	}

	/**
	 * 
	 * @param bend
	 *            tan of max angle
	 * @param c1
	 *            first corner
	 * @param c2
	 *            second corner
	 * @param c3
	 *            third corner
	 * @param c4
	 *            fourth corner
	 * @return true if angle is ok between c1-c2 and c2-c3 and c3-c4
	 */
	protected static boolean isAngleOKNoLoop(double bend, Corner c1, Corner c2,
			Corner c3, Corner c4) {
		return isAngleOK(c1.normal, c2.normal, bend)
				&& isAngleOK(c2.normal, c3.normal, bend)
				&& isAngleOK(c3.normal, c4.normal, bend);
	}

	class CornerAndCenter {
		private Corner corner;
		Coords3 center;
		Coords3 centerNormal;
		int id;

		public CornerAndCenter(Corner corner, int id) {
			center = newCoords3();
			centerNormal = newCoords3();
			setCorner(corner);
			this.id = id;
		}

		/**
		 * set the corner
		 * 
		 * @param corner
		 *            corner
		 */
		public void setCorner(Corner corner) {
			this.corner = corner;
		}

		/**
		 * 
		 * @return corner
		 */
		public Corner getCorner() {
			return corner;
		}

		/**
		 * 
		 * @return center
		 */
		public Coords3 getCenter() {
			return center;
		}

		/**
		 * 
		 * @return center normal
		 */
		public Coords3 getCenterNormal() {
			return centerNormal;
		}

		public void drawDebug(PlotterSurface surface) {

			surface.startTrianglesWireFrame();
			draw(surface);
			surface.endGeometryDirect();

			surface.startTrianglesWireFrameSurface();
			draw(surface);
			surface.endGeometryDirect();

		}

		public void draw(PlotterSurface surface) {
			Corner p1, p2;

			// go left
			Corner current = corner;
			// get first defined point on south (if exists)
			Corner sw1 = current;
			Corner sw2 = sw1;
			// draw south
			p1 = sw1;
			do {
				p2 = current.l;
				if (p2.p.isNotFinalUndefined()) {
					if (p1.p.isNotFinalUndefined()) {
						if (sw1.p.isFinalUndefined()) {
							sw1 = p1;
						}
						drawTriangle(surface, this, p2, p1);
					}
					p1 = p2;
					sw2 = p2;
				}
				current = current.l;
			} while (current.a == null);

			Corner sw = current;

			// go above
			current = corner;
			// get first defined point on east (if exists)
			Corner ne1 = current;
			Corner ne2 = ne1;
			// draw east
			p1 = ne1;
			do {
				p2 = current.a;
				if (p2.p.isNotFinalUndefined()) {
					if (p1.p.isNotFinalUndefined()) {
						drawTriangle(surface, this, p1, p2);
						if (ne1.p.isFinalUndefined()) {
							ne1 = p1;
						}
					}
					p1 = p2;
					ne2 = p2;
				}
				current = current.a;
			} while (current.l == null);
			Corner ne = current;

			// west side
			current = sw;
			p1 = sw2;
			if (sw1.p.isFinalUndefined()) {
				sw1 = p1;
			}
			do {
				p2 = current.a;
				if (p2.p.isNotFinalUndefined()) {
					if (p1.p.isNotFinalUndefined()) {
						drawTriangle(surface, this, p2, p1);
						if (sw1.p.isFinalUndefined()) {
							sw1 = p1;
						}
					}
					p1 = p2;
					sw2 = p2;
				}
				current = current.a;
			} while (current.isNotEnd);

			// north side
			current = ne;
			p1 = ne2;
			if (ne1.p.isFinalUndefined()) {
				ne1 = p1;
			}
			do {
				p2 = current.l;
				if (p2.p.isNotFinalUndefined()) {
					if (p1.p.isNotFinalUndefined()) {
						drawTriangle(surface, this, p1, p2);
						if (ne1.p.isFinalUndefined()) {
							ne1 = p1;
						}
					}
					p1 = p2;
					ne2 = p2;
				}
				current = current.l;
			} while (current.isNotEnd);

			// closure triangles if needed
			if (sw1 != ne1) {
				drawTriangleCheckCorners(surface, this, sw1, ne1);
			}
			if (sw2 != ne2) {
				drawTriangleCheckCorners(surface, this, ne2, sw2);
			}
			if (ne1.p.isFinalUndefined() && ne2.p.isFinalUndefined()) {
				drawTriangleCheckCorners(surface, this, sw2, sw1);
			}
			if (sw1.p.isFinalUndefined() && sw2.p.isFinalUndefined()) {
				drawTriangleCheckCorners(surface, this, ne1, ne2);
			}
		}

	}

	/**
	 * draw triangle with surface plotter
	 * 
	 * @param surface
	 *            surface plotter
	 * @param p0
	 *            first point
	 * @param n0
	 *            first point normal
	 * 
	 * @param c1
	 *            second point
	 * @param c2
	 *            third point
	 */
	protected void drawTriangle(PlotterSurface surface, Coords3 p0, Coords3 n0,
			Corner c1, Corner c2) {

		surface.normalDirect(n0);
		surface.vertexDirect(p0);
		surface.normalDirect(c2.normal);
		surface.vertexDirect(c2.p);
		surface.normalDirect(c1.normal);
		surface.vertexDirect(c1.p);

	}

	/**
	 * draws triangle between center and two corners
	 * 
	 * @param surface
	 *            surface plotter
	 * @param cc
	 *            center
	 * @param c1
	 *            first corner
	 * @param c2
	 *            second corner
	 */
	protected void drawTriangle(PlotterSurface surface, CornerAndCenter cc,
			Corner c1, Corner c2) {
		drawTriangle(surface, cc.center, cc.centerNormal, c1, c2);
	}

	/**
	 * draw triangle with surface plotter, check if second and third points are
	 * defined
	 * 
	 * @param surface
	 *            surface plotter
	 * @param cc
	 *            first point and normal
	 * 
	 * @param c1
	 *            second point
	 * @param c2
	 *            third point
	 */
	final protected void drawTriangleCheckCorners(PlotterSurface surface,
			CornerAndCenter cc, Corner c1, Corner c2) {
		if (c1.p.isFinalUndefined()) {
			return;
		}
		if (c2.p.isFinalUndefined()) {
			return;
		}

		drawTriangle(surface, cc, c1, c2);
	}

	/**
	 * add the corner to next split array
	 * 
	 * @param corner
	 *            corner
	 */
	protected void addToNextSplit(Corner corner) {
		nextSplit[nextSplitIndex] = corner;
		nextSplitIndex++;
	}

	/**
	 * 
	 * @param u
	 *            first parameter
	 * @param v
	 *            second parameter
	 * @throws NotEnoughCornersException
	 *             if no new corner left in array
	 * @return new corner calculated for parameters u, v
	 */
	protected Corner newCorner(double u, double v)
			throws NotEnoughCornersException {
		if (cornerListIndex >= cornerListSize) {
			throw new NotEnoughCornersException(this, "Index " + cornerListIndex
					+ " is larger than size " + cornerListSize);
		}
		Corner c = cornerList[cornerListIndex];
		if (c == null) {
			c = new Corner(u, v, cornerListIndex);
			cornerList[cornerListIndex] = c;
		} else {
			c.set(u, v);
		}
		cornerListIndex++;
		return c;
	}

	/**
	 * @return new corner
	 * @throws NotEnoughCornersException
	 *             if no new corner left in array
	 */
	protected Corner newCorner() throws NotEnoughCornersException {
		if (cornerListIndex >= cornerListSize) {
			throw new NotEnoughCornersException(this, "Index " + cornerListIndex
					+ " is larger than size " + cornerListSize);
		}
		Corner c = cornerList[cornerListIndex];
		if (c == null) {
			c = new Corner(cornerListIndex);
			cornerList[cornerListIndex] = c;
		}
		cornerListIndex++;
		return c;
	}

	@Override
	public boolean hit(Hitting hitting) {

		if (waitForReset) { // prevent NPE
			return false;
		}

		if (getGeoElement()
				.getAlphaValue() < EuclidianController.MIN_VISIBLE_ALPHA_VALUE) {
			return false;
		}

		if (((GeoElement) surfaceGeo).isGeoFunctionNVar()) {
			return hitFunction2Var(hitting);
		}

		if (!(getGeoElement() instanceof GeoSurfaceCartesian3D)) {
			return false;
		}

		if (curveHitting == null) {
			curveHitting = new CurveHitting(this, getView3D());
		}
		resetZPick();
		boolean isHit = false;
		if (borders.isEmpty()) {
			calculateBorders();
		}
		for (GeoCurveCartesian3D border : borders) {
			isHit = curveHitting.hit(hitting, border,
					Math.max(5, getGeoElement().getLineThickness())) || isHit;

		}
		return isHit;
	}

	private void calculateBorders() {
		for (int axis = 0; axis < 2; axis++) {
			double[] paramValues = new double[] {
					surfaceGeo.getMinParameter(axis),
					surfaceGeo.getMaxParameter(axis) };
			for (int borderIndex = 0; borderIndex < 2; borderIndex++) {
				GeoCurveCartesian3D border = setHitting(axis,
						paramValues[borderIndex]);
				borders.add(border);
			}
		}
	}

	private GeoCurveCartesian3D setHitting(int axis, double paramValue) {
		GeoCurveCartesian3D border = new GeoCurveCartesian3D(
				getGeoElement().getConstruction());
		GeoSurfaceCartesian3D geoSurface3D = (GeoSurfaceCartesian3D) getGeoElement();
		FunctionNVar[] functions = geoSurface3D.getFunctions();
		Function[] borderFunctions = new Function[functions.length];
		for (int i = 0; i < functions.length; i++) {
			Kernel kernel = geoSurface3D.getKernel();
			ExpressionNode expr = functions[i].getFunctionExpression()
					.deepCopy(kernel);
			FunctionVariable fVar = new FunctionVariable(kernel, "u");
			expr = expr.traverse(VariableReplacer.getReplacer(
					functions[i].getVarString(axis,
							StringTemplate.defaultTemplate),
					new MyDouble(kernel, paramValue), kernel)).wrap();
			expr = expr
					.traverse(VariableReplacer.getReplacer(
							functions[i].getVarString(1 - axis,
									StringTemplate.defaultTemplate),
							fVar, kernel))
					.wrap();
			borderFunctions[i] = new Function(expr, fVar);
		}
		border.setFun(borderFunctions);
		border.setInterval(geoSurface3D.getMinParameter(1 - axis),
				geoSurface3D.getMaxParameter(1 - axis));
		return border;

	}

	private boolean hitFunction2Var(Hitting hitting) {
		GeoFunctionNVar geoF = (GeoFunctionNVar) surfaceGeo;

		hitting.calculateClippedValues();
		if (Double.isNaN(hitting.x0)) { // hitting doesn't intersect
										// clipping box
			resetLastHitParameters(geoF);
			return false;
		}

		double[][] xyzf = geoF.getXYZF();

		// compute samples from xyz0 to xyz1, try to find consecutive +/-
		geoF.setXYZ(hitting.x0, hitting.y0, hitting.z0,
				xyzf[GeoFunctionNVar.DICHO_LAST]);
		boolean isLessZ0 = false, isLessZ1;
		isLessZ1 = GeoFunctionNVar.isLessZ(xyzf[GeoFunctionNVar.DICHO_LAST]);
		double t = 0;

		for (int i = 1; i <= HIT_SAMPLES; i++) {
			double[] tmp = xyzf[GeoFunctionNVar.DICHO_FIRST];
			xyzf[GeoFunctionNVar.DICHO_FIRST] = xyzf[GeoFunctionNVar.DICHO_LAST];
			xyzf[GeoFunctionNVar.DICHO_LAST] = tmp;
			t = i * DELTA_SAMPLES;
			geoF.setXYZ(hitting.x0 * (1 - t) + hitting.x1 * t,
					hitting.y0 * (1 - t) + hitting.y1 * t,
					hitting.z0 * (1 - t) + hitting.z1 * t,
					xyzf[GeoFunctionNVar.DICHO_LAST]);
			isLessZ0 = isLessZ1;
			isLessZ1 = GeoFunctionNVar
					.isLessZ(xyzf[GeoFunctionNVar.DICHO_LAST]);
			if (isLessZ0 ^ isLessZ1) {
				break; // found
			}
		}

		// set - as first value, + as second value, or return false
		if (isLessZ0) {
			if (isLessZ1) {
				resetLastHitParameters(geoF);
				return false;
			}
			double dx = xyzf[GeoFunctionNVar.DICHO_FIRST][0]
					- hitting.origin.getX();
			double dy = xyzf[GeoFunctionNVar.DICHO_FIRST][1]
					- hitting.origin.getY();
			double dz = xyzf[GeoFunctionNVar.DICHO_FIRST][2]
					- hitting.origin.getZ();
			double d = Math.sqrt(dx * dx + dy * dy + dz * dz);
			setZPick(-d, -d, hitting.discardPositiveHits(), d);
			setLastHitParameters(geoF, false);
			return true;
		}

		if (isLessZ1) {
			double dx = xyzf[GeoFunctionNVar.DICHO_FIRST][0]
					- hitting.origin.getX();
			double dy = xyzf[GeoFunctionNVar.DICHO_FIRST][1]
					- hitting.origin.getY();
			double dz = xyzf[GeoFunctionNVar.DICHO_FIRST][2]
					- hitting.origin.getZ();
			double d = Math.sqrt(dx * dx + dy * dy + dz * dz);
			setZPick(-d, -d, hitting.discardPositiveHits(), d);
			setLastHitParameters(geoF, true);
			return true;
		}

		resetLastHitParameters(geoF);
		return false;
	}

	private static void setLastHitParameters(GeoFunctionNVar geoF,
			boolean swap) {
		geoF.setLastHitParameters(swap);
	}

	private static void resetLastHitParameters(GeoFunctionNVar geoF) {
		geoF.resetLastHitParameters();
	}

	@Override
	protected void updateForViewVisible() {
		updateGeometriesVisibility();
		updateForView();
	}

	@Override
	public void setWaitForUpdateVisualStyle(GProperty prop) {
		super.setWaitForUpdateVisualStyle(prop);
		if (prop == GProperty.LINE_STYLE) {
			// also update for line width (e.g when translated)
			setWaitForUpdate();
		} else if (prop == GProperty.COLOR) {
			setWaitForUpdateColor();
		} else if (prop == GProperty.HIGHLIGHT) {
			setWaitForUpdateColor();
		} else if (prop == GProperty.VISIBLE) {
			setWaitForUpdateVisibility();
		}
	}

	@Override
	protected GColor getObjectColorForOutline() {
		return GColor.DARK_GRAY;
	}

	/**
	 * set there is no more corner available
	 */
	public void setNoRoomLeft() {
		drawFromScratch = false;
		stillRoomLeft = false;
	}

	@Override
	public void setZPickIfBetter(double zNear, double zFar,
			boolean discardPositive, double positionOnHitting) {
		if (!needsDiscardZPick(discardPositive, zNear, zFar)
				&& (zNear > getZPickNear())) {
			setZPickValue(zNear, zFar);
			setPositionOnHitting(positionOnHitting);
		}
	}

}
