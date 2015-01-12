package geogebra.common.geogebra3D.euclidian3D.draw;

import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.euclidian3D.openGL.PlotterSurface;
import geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.SurfaceEvaluable;
import geogebra.common.main.App;

import java.util.ArrayList;

/**
 * Class for drawing a 2-var function
 * 
 * @author matthieu
 * 
 */
public class DrawSurface3D extends Drawable3DSurfaces {

	/** The function being rendered */
	SurfaceEvaluable surfaceGeo;

	private double uDelta, vDelta;

	// number of intervals in root mesh (for each parameters, if parameters
	// delta are equals)
	private static final short ROOT_MESH_INTERVALS = 10;


	// number of split for boundary
	private static final short BOUNDARY_SPLIT = 10;

	/** Current culling box - set to view3d.(x|y|z)(max|min) */
	private double[] cullingBox = new double[6];

	/**
	 * common constructor
	 * 
	 * @param a_view3d
	 * @param function
	 */
	public DrawSurface3D(EuclidianView3D a_view3d, SurfaceEvaluable surface) {
		super(a_view3d, (GeoElement) surface);
		this.surfaceGeo = surface;

	}

	@Override
	public void drawGeometry(Renderer renderer) {
		renderer.getGeometryManager().draw(getSurfaceIndex());
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
		// TODO Auto-generated method stub

	}

	@Override
	public void drawOutline(Renderer renderer) {
		// no outline
	}

	@Override
	protected boolean updateForItSelf() {

		Renderer renderer = getView3D().getRenderer();

		PlotterSurface surface = renderer.getGeometryManager().getSurface();

		double uMin = surfaceGeo.getMinParameter(0);
		double uMax = surfaceGeo.getMaxParameter(0);
		double vMin = surfaceGeo.getMinParameter(1);
		double vMax = surfaceGeo.getMaxParameter(1);

		uDelta = uMax - uMin;
		if (Kernel.isZero(uDelta)) {
			setSurfaceIndex(-1);
			return true;
		}
		vDelta = vMax - vMin;
		if (Kernel.isZero(vDelta)) {
			setSurfaceIndex(-1);
			return true;
		}



		surface.start(getReusableSurfaceIndex());

		maxRWPixelDistance = getView3D().getMaxPixelDistance() / getView3D().getScale();
		maxRWDistanceNoAngleCheck = 3 * maxRWPixelDistance;
		maxRWDistance = 5 * maxRWPixelDistance;
		// maxRWDistanceNoAngleCheck = 1 * maxRWPixelDistance;
		// maxRWDistance = 2 * maxRWPixelDistance;
		maxBend = Math.tan(20 * Kernel.PI_180);// getView3D().getMaxBend();

		updateCullingBox();

		App.debug("\nmax distances = " + maxRWDistance + ", " + maxRWDistanceNoAngleCheck);

		int uN = 1 + (int) (ROOT_MESH_INTERVALS * Math.sqrt(uDelta / vDelta));
		int vN = 1 + ROOT_MESH_INTERVALS * ROOT_MESH_INTERVALS / uN;
		App.debug("grids: " + uN + ", " + vN);
		Corner corner = createRootMesh(uMin, uMax, uN, vMin, vMax, vN);

		currentSplit = new ArrayList<DrawSurface3D.Corner>();
		nextSplit = new ArrayList<DrawSurface3D.Corner>();
		drawList = new ArrayList<DrawSurface3D.CornerAndCenter>();
		// currentSplit.add(corner);
		notDrawn = 0;
		splitRootMesh(corner);
		App.debug("\nnot drawn after split root mesh: " + notDrawn);
		split(5);

		App.debug("\ndraw size : " + drawList.size() + "\nnot drawn : "
				+ notDrawn + "\nstill to split : " + nextSplit.size());

		// for (CornerAndCenter cc : drawList) {
		// cc.drawDebug(surface);
		// }

		surface.startTriangles();
		for (CornerAndCenter cc : drawList) {
			cc.draw(surface);
		}
		surface.endGeometry();

		setSurfaceIndex(surface.end());

		return true;
	}

	@Override
	protected void updateForView() {
		if (getView3D().viewChangedByZoom()
				|| getView3D().viewChangedByTranslate()) {
			updateForItSelf();
		}
	}

	@Override
	public int getPickOrder() {
		return DRAW_PICK_ORDER_SURFACE;
	}

	@Override
	public void addToDrawable3DLists(Drawable3DLists lists) {
		addToDrawable3DLists(lists, DRAW_TYPE_CLIPPED_SURFACES);
	}

	@Override
	public void removeFromDrawable3DLists(Drawable3DLists lists) {
		removeFromDrawable3DLists(lists, DRAW_TYPE_CLIPPED_SURFACES);
	}

	private boolean updateCullingBox() {
		EuclidianView3D view = getView3D();
		double off = maxRWPixelDistance * 2;
		cullingBox[0] = view.getXmin() - off;
		cullingBox[1] = view.getXmax() + off;
		cullingBox[2] = view.getYmin() - off;
		cullingBox[3] = view.getYmax() + off;
		cullingBox[4] = view.getZmin() - off;
		cullingBox[5] = view.getZmax() + off;
		return true;
	}

	private boolean inCullingBox(Coords p) {

		if ((p.getX() > cullingBox[0]) && (p.getX() < cullingBox[1])
				&& (p.getY() > cullingBox[2]) && (p.getY() < cullingBox[3])
				&& (p.getZ() > cullingBox[4]) && (p.getZ() < cullingBox[5])) {
			return true;
		}

		return false;
	}

	private ArrayList<Corner> currentSplit;

	private Corner createRootMesh(double uMin, double uMax, int uN, double vMin, double vMax, int vN) {


		Corner bottomRight = new Corner(uMax, vMax);
		Corner first = bottomRight;

		// first row
		Corner right = bottomRight;
		for (int i = 1; i <= uN; i++) {
			Corner left = new Corner(uMax - (uDelta * i) / uN, vMax);
			right.l = left;
			right = left;
		}

		// all rows
		for (int j = 1; j <= vN; j++) {
			double v = vMax - (vDelta * j) / vN;
			Corner below = bottomRight;
			right = new Corner(uMax, v);
			below.a = right;
			for (int i = 1; i <= uN; i++) {
				Corner left = new Corner(uMax - (uDelta * i) / uN, v);
				right.l = left;
				right = left;
				below = below.l;
				below.a = right;
			}
			bottomRight = bottomRight.a;
		}

		return first;

	}

	/**
	 * list of next corners to split
	 */
	ArrayList<Corner> nextSplit;

	protected int notDrawn;

	private void splitRootMesh(Corner first) {

		Corner nextAbove, nextLeft;

		Corner current = first;
		while (current.a != null) {
			nextAbove = current.a;
			while (current.l != null) {
				nextLeft = current.l;
				if (nextLeft.a == null) { // already splitted by last row
					nextLeft = nextLeft.l;
				}
				// App.debug(current.u + "," + current.v);
				current.split(false);
				current = nextLeft;
			}
			current = nextAbove;
		}

	}

	private void split(int depth) {

		// swap stacks
		ArrayList<Corner> tmp = currentSplit;
		currentSplit = nextSplit;
		nextSplit = tmp;
		nextSplit.clear();

		boolean recordNext = depth > 1;
		// int test = 1;
		for (Corner corner : currentSplit) {
			corner.split(!recordNext);
			// corner.split(recordNext && (test % 3 != 0));
			// test++;
		}

		if (recordNext) {
			// split again
			split(depth - 1);
		}

	}

	private Coords evaluatedPoint = new Coords(3);
	private Coords evaluatedNormal = new Coords(3);

	protected Coords evaluatePoint(double u, double v) {
		surfaceGeo.evaluatePoint(u, v, evaluatedPoint);

		if (!evaluatedPoint.isDefined()) {
			return Coords.UNDEFINED3;
		}

		if (inCullingBox(evaluatedPoint)) {
			return evaluatedPoint.copyVector();
		}

		return Coords.UNDEFINED3;
	}

	protected Coords evaluateNormal(double u, double v) {
		surfaceGeo.evaluateNormal(u, v, evaluatedNormal);

		if (!evaluatedNormal.isDefined()) {
			return Coords.UNDEFINED3;
		}

		return evaluatedNormal.normalized();
	}

	protected boolean evaluatePoint(double u, double v, Coords result) {
		surfaceGeo.evaluatePoint(u, v, result);

		if (!result.isDefined()) {
			return false;
		}

		if (inCullingBox(result)) {
			return true;
		}

		return false;
	}

	private class Corner {
		Coords p;
		Coords normal;
		double u, v;
		boolean isNotEnd;
		Corner a, l; // above, left

		public Corner(double u, double v) {
			this.u = u;
			this.v = v;
			p = evaluatePoint(u, v);
			if (p.isFinalUndefined()) {
				normal = Coords.UNDEFINED;
			} else {
				normal = evaluateNormal(u, v);
			}
			isNotEnd = true;
		}

		public Corner(double u, double v, Coords p) {
			this.u = u;
			this.v = v;
			this.p = p;
			normal = evaluateNormal(u, v);
			isNotEnd = true;
		}

		public void split(boolean draw) {


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
							Corner n = findU(left.a, above, BOUNDARY_SPLIT);
							// find defined between l.a and l
							Corner w = findV(left.a, left, BOUNDARY_SPLIT);
							boolean split;
							if (draw) { // time to draw
								split = false;
							} else if (n.p.isFinalUndefined() || w.p.isFinalUndefined()) { // some undefined point: force split
								split = true;
							}else{ // check distance
								double d = getDistance(left.a, n, w);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check angle
									if (isAngleOK(maxBend, left.a, n, w)) { // angle ok
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
							Corner n = findU(above, left.a, BOUNDARY_SPLIT);
							// find defined between a and this
							Corner e;
							if (subAbove != null) {
								e = subAbove;
							} else {
								e = findV(above, this, BOUNDARY_SPLIT);
							}
							boolean split;
							if (draw) { // time to draw
								split = false;
							} else if (n.p.isFinalUndefined() || e.p.isFinalUndefined()) { // some undefined point: force split
								split = true;
							}else{ // check distance
								double d = getDistance(above, n, e);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check angle
									if (isAngleOK(maxBend, above, n, e)) { // angle ok
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
							} else if (subAbove != null && subAbove.p.isFinalUndefined()) { // some undefined point: force split
								split = true;
							}else{ // check distance
								double d = getDistance(above, left.a);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check angle
									if (isAngleOK(maxBend, above, left.a)) { // angle ok
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
									e = findV(above, this, BOUNDARY_SPLIT);
								}
								// find defined between l.a and left
								Corner w = findV(left.a, left, BOUNDARY_SPLIT);
								
								//check distances
								double d = getDistanceNoLoop(above, e, w, left.a);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check angle
									if (isAngleOKNoLoop(maxBend, above, e, w, left.a)) { // angle ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
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
								s = findU(left, this, BOUNDARY_SPLIT);
							}
							// find defined between l and l.a
							Corner w = findV(left, left.a, BOUNDARY_SPLIT);
							boolean split;
							if (draw) { // time to draw
								split = false;
							} else if (s.p.isFinalUndefined() || w.p.isFinalUndefined()) { // some undefined point: force split
								split = true;
							}else{ // check distance
								double d = getDistance(left, s, w);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check angle
									if (isAngleOK(maxBend, left, s, w)) { // angle ok
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
							} else if (subLeft != null && subLeft.p.isFinalUndefined()) { // some undefined point: force split
								split = true;
							}else{ // check distance
								double d = getDistance(left.a, left);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check angle
									if (isAngleOK(maxBend, left.a, left)) { // angle ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
								}
							}
							if (split){
								split(subLeft, left, subAbove, above);
							} else {
								// find defined between l and this
								Corner s;
								if (subLeft != null) {
									s = subLeft;
								} else {
									s = findU(left, this, BOUNDARY_SPLIT);
								}
								// find defined between l.a and a
								Corner n = findU(left.a, above, BOUNDARY_SPLIT);

								// check distances
								double d = getDistanceNoLoop(left.a, n, s, left);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check angle
									if (isAngleOKNoLoop(maxBend, left.a, n, s, left)) { // angle ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
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
							App.debug("==== 2/diag/");
						} else {
							// l, a and l.a not undefined /3/
							boolean split;
							if (draw) { // time to draw
								split = false;
							} else if (subLeft != null && subLeft.p.isFinalUndefined()) { // some undefined point: force split
								split = true;
							} else if (subAbove != null && subAbove.p.isFinalUndefined()) { // some undefined point: force split
								split = true;
							}else{ // check distance
								double d = getDistance(left.a, left, above);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check angle
									if (isAngleOK(maxBend, left.a, left, above)) { // angle ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
								}
							}
							if (split){
								split(subLeft, left, subAbove, above);
							} else {
								// find defined between l and this
								Corner s;
								if (subLeft != null) {
									s = subLeft;
								} else {
									s = findU(left, this, BOUNDARY_SPLIT);
									// new neighbors
									this.l = s;
									s.l = left;
								}
								// find defined between a and this
								Corner e;
								if (subAbove != null) {
									e = subAbove;
								} else {
									e = findV(above, this, BOUNDARY_SPLIT);
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
								s = findU(this, left, BOUNDARY_SPLIT);
								// new neighbors
								this.l = s;
								s.l = left;
							}
							// find defined between this and a
							Corner e;
							if (subAbove != null) {
								e = subAbove;
							} else {
								e = findV(this, above, BOUNDARY_SPLIT);
								// new neighbors
								this.a = e;
								e.a = above;
							}
							boolean split;
							if (draw) { // time to draw
								split = false;
							} else if (s.p.isFinalUndefined() || e.p.isFinalUndefined()) { // some undefined point: force split
								split = true;
							}else{ // check distance
								double d = getDistance(this, s, e);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check angle
									if (isAngleOK(maxBend, this, s, e)) { // angle ok
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
							App.debug("==== 2/diag/");
						}
					} else {
						if (left.a.p.isFinalUndefined()) {
							// this and a defined /2/
							boolean split;
							if (draw) { // time to draw
								split = false;
							} else if (subLeft != null && subLeft.p.isFinalUndefined()) { // some undefined point: force split
								split = true;
							}else{ // check distance
								double d = getDistance(this, above);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check angle
									if (isAngleOK(maxBend, this, above)) { // angle ok
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
									s = findU(this, left, BOUNDARY_SPLIT);
								}
								// find defined between a and l.a
								Corner n = findU(above, left.a, BOUNDARY_SPLIT);
								
								// check distances
								double d = getDistanceNoLoop(this, s, n, above);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check angle
									if (isAngleOKNoLoop(maxBend, this, s, n, above)) { // angle ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
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
							} else if (subLeft != null && subLeft.p.isFinalUndefined()) { // some undefined point: force split
								split = true;
							}else{ // check distance
								double d = getDistance(above, left.a, this);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check angle
									if (isAngleOK(maxBend, above, left.a, this)) { // angle ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
								}
							}
							if (split){
								split(subLeft, left, subAbove, above);
							} else {
								// find defined between this and l
								Corner s;
								if (subLeft != null) {
									s = subLeft;
								} else {
									s = findU(this, left, BOUNDARY_SPLIT);
									// new neighbors
									this.l = s;
									s.l = left;
								}
								// find defined between l.a and l
								Corner w = findV(left.a, left, BOUNDARY_SPLIT);
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
							} else if (subAbove != null && subAbove.p.isFinalUndefined()) { // some undefined point: force split
								split = true;
							}else{ // check distance
								double d = getDistance(this, left);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check angle
									if (isAngleOK(maxBend, this, left)) { // angle ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
								}
							}
							if (split){
								split(subLeft, left, subAbove, above);
							} else {
								// find defined between this and a
								Corner e;
								if (subAbove != null) {
									e = subAbove;
								} else {
									e = findV(this, above, BOUNDARY_SPLIT);
								}
								// find defined between l and l.a
								Corner w = findV(left, left.a, BOUNDARY_SPLIT);
								//check distances
								double d = getDistanceNoLoop(this, e, w, left);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check angle
									if (isAngleOKNoLoop(maxBend, this, e, w, left)) { // angle ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
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
							} else if (subAbove != null && subAbove.p.isFinalUndefined()) { // some undefined point: force split
								split = true;
							}else{ // check distance
								double d = getDistance(left, left.a, this);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check angle
									if (isAngleOK(maxBend, left, left.a, this)) { // angle ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
								}
							}
							if (split){
								split(subLeft, left, subAbove, above);
							} else {
								// find defined between l.a and a
								Corner n = findU(left.a, above, BOUNDARY_SPLIT);
								// find defined between this and a
								Corner e;
								if (subAbove != null) {
									e = subAbove;
								} else {
									e = findV(this, above, BOUNDARY_SPLIT);
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
							}else{ // check distance
								double d = getDistance(this, left, above);
								if (Double.isInfinite(d)) { // d > maxRWDistance
									split = true;
								} else if (d > maxRWDistanceNoAngleCheck) { // check angle
									if (isAngleOK(maxBend, this, left, above)) { // angle ok
										split = false;
									} else { // angle not ok
										split = true;
									}
								} else { // no need to check angle
									split = false;
								}
							}
							if (split){
								split(subLeft, left, subAbove, above);
							} else {
								// find defined between a and l.a
								Corner n = findU(above, left.a, BOUNDARY_SPLIT);
								// find defined between l and l.a
								Corner w = findV(left, left.a, BOUNDARY_SPLIT);
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
							// check distances
							double d = getDistance(this, left, above, left.a);
							if (Double.isInfinite(d) || (d > maxRWDistanceNoAngleCheck && !isAngleOK(maxBend, this, left, above, left.a))) {
								split(subLeft, left, subAbove, above);
							} else {
								// drawing
								addToDrawList(left.a, this, left, above, left.a);
							}
						}
					}
				}
			}

		}

		private void split(Corner subLeft, Corner left, Corner subAbove, Corner above) {
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
				e = new Corner(u, vm);
				// new neighbors
				this.a = e;
				e.a = above;
			}
			Corner s;
			if (subLeft != null) {
				s = subLeft;
			} else {
				s = new Corner(um, v);
				// new neighbors
				this.l = s;
				s.l = left;
			}
			Corner m = new Corner(um, vm);
			s.a = m;
			e.l = m;
			Corner n = new Corner(um, above.v);
			n.l = above.l;
			above.l = n;
			m.a = n;
			Corner w = new Corner(left.u, vm);
			w.a = left.a;
			left.a = w;
			m.l = w;
			// next split
			nextSplit.add(this);
			nextSplit.add(s);
			nextSplit.add(e);
			nextSplit.add(m);
		}

		private void addToDrawList(Corner end, Corner... corners) {
			Coords center = new Coords(3);
			Coords centerNormal = new Coords(3);
			setBarycenter(center, centerNormal, corners);
			end.isNotEnd = false;
			drawList.add(new CornerAndCenter(this, center, centerNormal));
		}

		private Corner findU(Corner defined, Corner undefined, int depth) {
			return findU(defined.p, defined.u, defined.u, undefined.u,
					defined.v, depth);
		}

		private Corner findU(Coords lastDefined, double uLastDef, double uDef,
				double uUndef, double vRow, int depth) {

			double uNew = (uDef + uUndef) / 2;
			Coords coords = evaluatePoint(uNew, vRow);

			if (depth == 0) { // no more split
				if (coords.isFinalUndefined()) {
					// return last defined point
					return new Corner(uLastDef, vRow, lastDefined);
				}
				return new Corner(uNew, vRow, coords);
			}

			if (coords.isFinalUndefined()) {
				return findU(lastDefined, uLastDef, uDef, uNew, vRow, depth - 1);
			}
			return findU(coords, uNew, uNew, uUndef, vRow, depth - 1);


		}

		private Corner findV(Corner defined, Corner undefined, int depth) {
			return findV(defined.p, defined.v, defined.v, undefined.v,
					defined.u, depth);
		}

		private Corner findV(Coords lastDefined, double vLastDef, double vDef,
				double vUndef, double uRow, int depth) {

			double vNew = (vDef + vUndef) / 2;
			Coords coords = evaluatePoint(uRow, vNew);

			if (depth == 0) { // no more split
				if (coords.isFinalUndefined()) {
					// return last defined point
					return new Corner(uRow, vLastDef, lastDefined);
				}
				return new Corner(uRow, vNew, coords);
			}

			if (coords.isFinalUndefined()) {
				return findV(lastDefined, vLastDef, vDef, vNew, uRow, depth - 1);
			}
			return findV(coords, vNew, vNew, vUndef, uRow, depth - 1);

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
	static protected void setBarycenter(Coords center, Coords normal, Corner... c) {
		double f = 1.0 / c.length;
		for (int i = 0; i < center.getLength(); i++) {
			center.val[i] = 0;
			normal.val[i] = 0;
			for (int j = 0; j < c.length; j++) {
				center.val[i] += c[j].p.val[i];
				normal.val[i] += c[j].normal.val[i];
			}
			center.val[i] *= f;
			normal.val[i] *= f;
		}

		// if (!center.isDefined()) {
		// App.printStacktrace("");
		// }

	}

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
		for (int j = 0; j < 3; j++) {
			double d = Math.abs(c1.p.val[j] - c2.p.val[j]);
			if (d > maxRWDistance) {
				return Double.POSITIVE_INFINITY;
			}
			if (d > ret) {
				ret = d;
			}
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
	protected double getDistanceNoLoop(Corner c1, Corner c2, Corner c3, Corner c4) {
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
	private static boolean isAngleOK(double[] v, double[] w, double bend) {
		// |v| * |w| * sin(alpha) = |det(v, w)|
		// cos(alpha) = v . w / (|v| * |w|)
		// tan(alpha) = sin(alpha) / cos(alpha)
		// tan(alpha) = |det(v, w)| / v . w

		// small angle: tan(alpha) < MAX_BEND
		// |det(v, w)| / v . w < MAX_BEND
		// |det(v, w)| < MAX_BEND * (v . w)

		double innerProduct = 0;
		for (int i = 0; i < v.length; i++) {
			innerProduct += v[i] * w[i];
		}

		if (innerProduct <= 0) {
			// angle >= 90 degrees
			return false;
		}

		// angle < 90 degrees
		// small angle: |det(v, w)| < MAX_BEND * (v . w)
		double d1 = v[0] * w[1] - v[1] * w[0];
		double d2 = v[1] * w[2] - v[2] * w[1];
		double d3 = v[2] * w[0] - v[0] * w[2];
		double det = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
		return det < bend * innerProduct;
	}

	/**
	 * 
	 * @param bend
	 * @param c1
	 * @param c2
	 * @param c3
	 * @param c4
	 * @return true if angle is ok between c1-c2
	 */
	protected static boolean isAngleOK(double bend, Corner c1, Corner c2) {

		if (!isAngleOK(c1.normal.val, c2.normal.val, bend)) {
			return false;
		}

		return true;
	}

	/**
	 * 
	 * @param bend
	 * @param c1
	 * @param c2
	 * @param c3
	 * @param c4
	 * @return true if angle is ok between c1-c2 and c2-c3 and c3-c1
	 */
	protected static boolean isAngleOK(double bend, Corner c1, Corner c2, Corner c3) {

		if (!isAngleOK(c1.normal.val, c2.normal.val, bend)) {
			return false;
		}

		if (!isAngleOK(c2.normal.val, c3.normal.val, bend)) {
			return false;
		}

		if (!isAngleOK(c3.normal.val, c1.normal.val, bend)) {
			return false;
		}

		return true;
	}


	/**
	 * 
	 * @param bend
	 * @param c1
	 * @param c2
	 * @param c3
	 * @param c4
	 * @return true if angle is ok between c1-c2 and c2-c3 and c3-c4 and c4-c1
	 */
	protected static boolean isAngleOK(double bend, Corner c1, Corner c2, Corner c3, Corner c4) {

		if (!isAngleOK(c1.normal.val, c2.normal.val, bend)) {
			return false;
		}

		if (!isAngleOK(c2.normal.val, c3.normal.val, bend)) {
			return false;
		}

		if (!isAngleOK(c3.normal.val, c4.normal.val, bend)) {
			return false;
		}

		if (!isAngleOK(c4.normal.val, c1.normal.val, bend)) {
			return false;
		}

		return true;
	}

	/**
	 * 
	 * @param bend
	 * @param c1
	 * @param c2
	 * @param c3
	 * @param c4
	 * @return true if angle is ok between c1-c2 and c2-c3 and c3-c4
	 */
	protected static boolean isAngleOKNoLoop(double bend, Corner c1, Corner c2, Corner c3, Corner c4) {

		if (!isAngleOK(c1.normal.val, c2.normal.val, bend)) {
			return false;
		}

		if (!isAngleOK(c2.normal.val, c3.normal.val, bend)) {
			return false;
		}

		if (!isAngleOK(c3.normal.val, c4.normal.val, bend)) {
			return false;
		}

		return true;
	}

	private class CornerAndCenter {
		private Corner corner;
		private Coords center;
		private Coords centerNormal;

		public CornerAndCenter(Corner corner, Coords center, Coords centerNormal) {
			set(corner, center, centerNormal);
		}

		public void set(Corner corner, Coords center, Coords centerNormal) {
			this.corner = corner;
			this.center = center;
			this.centerNormal = centerNormal;
		}

		public void drawDebug(PlotterSurface surface) {

			surface.startTrianglesWireFrame();
			draw(surface);
			surface.endGeometry();

			surface.startTrianglesWireFrameSurface();
			draw(surface);
			surface.endGeometry();

		}

		public void draw(PlotterSurface surface) {

			Corner current, sw, ne;

			Corner p1, p2;

			// go left
			current = corner;
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
						drawTriangle(surface, center, centerNormal, p2, p1);
					}
					p1 = p2;
					sw2 = p2;
				}
				current = current.l;
			} while (current.a == null);

			sw = current;

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
						drawTriangle(surface, center, centerNormal, p1, p2);
						if (ne1.p.isFinalUndefined()) {
							ne1 = p1;
						}
					}
					p1 = p2;
					ne2 = p2;
				}
				current = current.a;
			} while (current.l == null);
			ne = current;

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
						drawTriangle(surface, center, centerNormal, p2, p1);
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
						drawTriangle(surface, center, centerNormal, p1, p2);
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
				drawTriangle(surface, center, centerNormal, sw1, ne1);
			}
			if (sw2 != ne2) {
				drawTriangle(surface, center, centerNormal, ne2, sw2);
			}
			if (ne1.p.isFinalUndefined() && ne2.p.isFinalUndefined()) {
				drawTriangle(surface, center, centerNormal, sw2, sw1);
			}
			if (sw1.p.isFinalUndefined() && sw2.p.isFinalUndefined()) {
				drawTriangle(surface, center, centerNormal, ne1, ne2);
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
	static final protected void drawTriangle(PlotterSurface surface, Coords p0, Coords n0, Corner c1, Corner c2) {
		surface.normal(n0);
		surface.vertex(p0);
		surface.normal(c2.normal);
		surface.vertex(c2.p);
		surface.normal(c1.normal);
		surface.vertex(c1.p);

	}


	/**
	 * list of things to draw
	 */
	ArrayList<CornerAndCenter> drawList;

}
