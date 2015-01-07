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
	SurfaceEvaluable dfxu;

	private static final long MAX_SPLIT = 32768;
	private static final long MIN_SPLIT = 512;
	private static final double MAX_CENTER_QUAD_DISTANCE = 1.e-3;
	private static final double MAX_DIAGONAL_QUAD_LENGTH = 1.e-3;
	private double limit1;
	private double limit2;
	private double uMin;
	private double vMin;

	// number of split for boundary
	private static final short BOUNDARY_SPLIT = 5;

	/** Current culling box - set to view3d.(x|y|z)(max|min) */
	private double[] cullingBox = new double[6];
	private double cullingBoxDelta;

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

		uMin = surfaceGeo.getMinParameter(0);
		double uMax = surfaceGeo.getMaxParameter(0);
		vMin = surfaceGeo.getMinParameter(1);
		double vMax = surfaceGeo.getMaxParameter(1);

		updateCullingBox();
		cullingBoxDelta = (cullingBox[5] - cullingBox[4]);
		limit1 = cullingBoxDelta * MAX_DIAGONAL_QUAD_LENGTH;
		limit2 = cullingBoxDelta * MAX_CENTER_QUAD_DISTANCE;

		surface.start(getReusableSurfaceIndex());

		// Corner al = new Corner(uMin, vMin);
		// Corner a = new Corner(uMax, vMin);
		// Corner l = new Corner(uMin, vMax);
		// Corner corner = new Corner(uMax, vMax, a, l, al);

		maxRWDistance = 5 * getView3D().getMaxPixelDistance() / getView3D().getScale();
		maxBend = Math.tan(20 * Kernel.PI_180);// getView3D().getMaxBend();

		App.debug("\nmaxRWDistance = " + maxRWDistance);

		int n = 10;
		// double uDelta = (uMax - uMin) / n;
		// double vDelta = (vMax - vMin) / n;
		Corner corner = createRootMesh(uMin, uMax, n, vMin, vMax, n);

		currentSplit = new ArrayList<DrawSurface3D.Corner>();
		nextSplit = new ArrayList<DrawSurface3D.Corner>();
		drawList = new ArrayList<DrawSurface3D.CornerAndCenter>();
		drawListBoundary = new ArrayList<DrawSurface3D.CornerAndCenter>();
		// currentSplit.add(corner);
		notDrawn = 0;
		splitRootMesh(corner);
		split(5);

		App.debug("\ndraw size : " + drawList.size() + "\nnot drawn : "
				+ notDrawn + "\nstill to split : " + nextSplit.size());

		for (CornerAndCenter cc : drawList) {
			cc.draw(surface);
		}


		for (CornerAndCenter cc : drawListBoundary) {
			cc.draw(surface);
		}

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
		cullingBox[0] = view.getXmin();
		cullingBox[1] = view.getXmax();
		cullingBox[2] = view.getYmin();
		cullingBox[3] = view.getYmax();
		cullingBox[4] = view.getZmin();
		cullingBox[5] = view.getZmax();
		return true;
	}

	private boolean inCullingBox(Coords p) {

		if ( // (p.isDefined())
				// && (p.isFinite())
				// &&
		(p.getX() > cullingBox[0]) && (p.getX() < cullingBox[1])
				&& (p.getY() > cullingBox[2]) && (p.getY() < cullingBox[3])
				&& (p.getZ() > cullingBox[4]) && (p.getZ() < cullingBox[5])) {
			return true;
		}

		return false;
	}

	private ArrayList<Corner> currentSplit;

	private Corner createRootMesh(double uMin, double uMax, int uN, double vMin, double vMax, int vN) {

		double uDelta = uMin - uMax;
		double vDelta = vMin - vMax;

		Corner bottomRight = new Corner(uMax, vMax);
		Corner first = bottomRight;

		// first row
		Corner right = bottomRight;
		for (int i = 1; i <= uN; i++) {
			Corner left = new Corner(uMax + (uDelta * i) / uN, vMax);
			right.l = left;
			right = left;
		}

		// all rows
		for (int j = 1; j <= vN; j++) {
			double v = vMax + (vDelta * j) / vN;
			Corner below = bottomRight;
			right = new Corner(uMax, v);
			below.a = right;
			for (int i = 1; i <= uN; i++) {
				Corner left = new Corner(uMax + (uDelta * i) / uN, v);
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
			return Coords.UNDEFINED;
		}

		if (inCullingBox(evaluatedPoint)) {
			return evaluatedPoint.copyVector();
		}

		return Coords.UNDEFINED;
	}

	protected Coords evaluateNormal(double u, double v) {
		surfaceGeo.evaluateNormal(u, v, evaluatedNormal);

		if (!evaluatedNormal.isDefined()) {
			return Coords.UNDEFINED;
		}

		return evaluatedNormal.copyVector();
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
							// all undefined: nothing to draw
							notDrawn++;
						} else {
							// l.a is defined
							if (draw) {
								// find defined between l.a and a
								Corner n = findU(left.a, above, BOUNDARY_SPLIT);
								// find defined between l.a and l
								Corner w = findV(left.a, left, BOUNDARY_SPLIT);
								// new neighbors
								n.l = left.a;
								above.l = n;
								// new neighbors
								w.a = left.a;
								left.a = w;

								// draw
								Coords center = new Coords(3);
								setBarycenter(center, n, w, w.a);
								w.a.isNotEnd = false;
								drawListBoundary.add(new CornerAndCenter(this, center));
							} else {
								split(subLeft, left, subAbove, above);
							}
						}
					} else {
						if (left.a.p.isFinalUndefined()) {
							// a defined
							if (draw) {
								// find defined between a and l.a
								Corner n = findU(above, left.a, BOUNDARY_SPLIT);
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
								// new neighbors
								n.l = left.a;
								above.l = n;
								// for drawing
								Coords center = new Coords(3);
								setBarycenter(center, n, e, above);
								left.a.isNotEnd = false;
								drawListBoundary.add(new CornerAndCenter(this, center));
							} else {
								split(subLeft, left, subAbove, above);
							}
						} else {
							// a and l.a not undefined
							if (draw || ((subAbove == null || subAbove.p.isNotFinalUndefined()) && isDistanceOK(above, left.a) && isAngleOK(maxBend, above, left.a))) {
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
								// find defined between l.a and left
								Corner w = findV(left.a, left, BOUNDARY_SPLIT);
								// new neighbors
								w.a = left.a;
								left.a = w;
								// for drawing
								Coords center = new Coords(3);
								setBarycenter(center, e, above, left.a, w);
								w.a.isNotEnd = false;
								drawListBoundary.add(new CornerAndCenter(this, center));
							} else {
								split(subLeft, left, subAbove, above);
							}
						}
					}
				} else {
					if (above.p.isFinalUndefined()) {
						if (left.a.p.isFinalUndefined()) {
							// l defined
							if (draw) {
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
								// find defined between l and l.a
								Corner w = findV(left, left.a, BOUNDARY_SPLIT);
								// new neighbors
								w.a = left.a;
								left.a = w;
								// for drawing
								Coords center = new Coords(3);
								setBarycenter(center, s, w, left);
								w.a.isNotEnd = false;
								drawListBoundary.add(new CornerAndCenter(this, center));
							} else {
								split(subLeft, left, subAbove, above);
							}
						} else {
							// l and l.a not undefined
							if (draw || ((subLeft == null || subLeft.p.isNotFinalUndefined()) && isDistanceOK(left.a, left) && isAngleOK(maxBend, left.a, left))) {
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
								// find defined between l.a and a
								Corner n = findU(left.a, above, BOUNDARY_SPLIT);
								// new neighbors
								n.l = left.a;
								above.l = n;
								// for drawing
								Coords center = new Coords(3);
								setBarycenter(center, s, n, left.a, left);
								left.a.isNotEnd = false;
								drawListBoundary.add(new CornerAndCenter(this, center));
							} else {
								split(subLeft, left, subAbove, above);
							}
						}
					} else {
						if (left.a.p.isFinalUndefined()) {
							// l and a not undefined
						} else {
							// l, a and l.a not undefined
							if (draw || (isDistanceOK(left.a, left, above) && isAngleOK(maxBend, left.a, left, above))) {
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
								// draw
								Coords center = new Coords(3);
								setBarycenter(center, left, above, left.a);

								left.a.isNotEnd = false;
								drawListBoundary.add(new CornerAndCenter(this, center));
							} else {
								split(subLeft, left, subAbove, above);
							}
						}
					}
				}
			} else {
				if (left.p.isFinalUndefined()) {
					if (above.p.isFinalUndefined()) {
						if (left.a.p.isFinalUndefined()) {
							// this defined
							if (draw) {
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

								// draw
								Coords center = new Coords(3);
								setBarycenter(center, s, e, this);
								left.a.isNotEnd = false;
								drawListBoundary.add(new CornerAndCenter(this, center));
							} else {
								split(subLeft, left, subAbove, above);
							}
						} else {
							// this and l.a not undefined
						}
					} else {
						if (left.a.p.isFinalUndefined()) {
							// this and a not undefined
							if (draw || ((subLeft == null || subLeft.p.isNotFinalUndefined()) && isDistanceOK(this, above) && isAngleOK(maxBend, this, above))) {
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
								// find defined between a and l.a
								Corner n = findU(above, left.a, BOUNDARY_SPLIT);
								// new neighbors
								n.l = left.a;
								above.l = n;
								// for drawing
								Coords center = new Coords(3);
								setBarycenter(center, this, above, n, s);
								left.a.isNotEnd = false;
								drawListBoundary.add(new CornerAndCenter(this, center));
							} else {
								split(subLeft, left, subAbove, above);
							}
						} else {
							// this, a and l.a not undefined
							if (draw || (isDistanceOK(above, left.a, this) && isAngleOK(maxBend, above, left.a, this))) {
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
								// for drawing
								Coords center = new Coords(3);
								setBarycenter(center, above, left.a, this);
								w.a.isNotEnd = false;
								drawListBoundary.add(new CornerAndCenter(this, center));
							} else {
								split(subLeft, left, subAbove, above);
							}
						}
					}
				} else {
					if (above.p.isFinalUndefined()) {
						if (left.a.p.isFinalUndefined()) {
							// this and l not undefined
							if (draw || ((subAbove == null || subAbove.p.isNotFinalUndefined()) && isDistanceOK(this, left) && isAngleOK(maxBend, this, left))) {
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
								// find defined between l and l.a
								Corner w = findV(left, left.a, BOUNDARY_SPLIT);
								// new neighbors
								w.a = left.a;
								left.a = w;
								// for drawing
								Coords center = new Coords(3);
								setBarycenter(center, this, e, w, left);
								w.a.isNotEnd = false;
								drawListBoundary.add(new CornerAndCenter(this, center));
							} else {
								split(subLeft, left, subAbove, above);
							}
						} else {
							// this, l and l.a not undefined
							if (draw || (isDistanceOK(left, left.a, this) && isAngleOK(maxBend, left, left.a, this))) {
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
								// for drawing
								Coords center = new Coords(3);
								setBarycenter(center, left, left.a, this);
								left.a.isNotEnd = false;
								drawListBoundary.add(new CornerAndCenter(this, center));
							} else {
								split(subLeft, left, subAbove, above);
							}
						}
					} else {
						if (left.a.p.isFinalUndefined()) {
							// this, l and a not undefined
							if (draw || (isDistanceOK(this, left, above) && isAngleOK(maxBend, this, left, above))) {
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
								// for drawing
								Coords center = new Coords(3);
								setBarycenter(center, this, left, above);
								w.a.isNotEnd = false;
								drawListBoundary.add(new CornerAndCenter(this, center));
							} else {
								split(subLeft, left, subAbove, above);
							}
						} else {
							// this, l, a and l.a not undefined
							// check distances
							if (isDistanceOK(this, left, above, left.a) && isAngleOK(maxBend, this, left, above, left.a)) {
								// drawing
								Coords center = new Coords(3);
								setBarycenter(center, this, left, above, left.a);
								left.a.isNotEnd = false;
								drawList.add(new CornerAndCenter(this, center));
							} else {
								split(subLeft, left, subAbove, above);
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

		private boolean hasUndefinedCorner() {

			// check first corner
			if (p.isFinalUndefined()) {
				return true;
			}

			Corner left;

			// check left
			if (l.a == null) { // already splitted on left
				left = l.l;
			} else {
				left = l;
			}

			if (left.p.isFinalUndefined()) {
				return true;
			}

			// check left - above
			if (left.a.p.isFinalUndefined()) {
				return true;
			}

			// check above
			if (a.l == null) { // already splitted on above
				if (a.a.p.isFinalUndefined()) {
					return true;
				}
			} else {
				if (a.p.isFinalUndefined()) {
					return true;
				}
			}

			return false;
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
	 * @param c
	 *            corners
	 * 
	 */
	static protected void setBarycenter(Coords center, Corner... c) {
		double f = 1.0 / c.length;
		for (int i = 0; i < center.getLength(); i++) {
			center.val[i] = 0;
			for (int j = 0; j < c.length; j++) {
				center.val[i] += c[j].p.val[i];
			}
			center.val[i] *= f;
		}

		// if (!center.isDefined()) {
		// App.printStacktrace("");
		// }

	}

	private double maxRWDistance;
	protected double maxBend;

	/**
	 * 
	 * @param c
	 *            corners
	 * @return true if distance are small enough between points
	 */
	protected boolean isDistanceOK(Corner... c) {

		Coords p0 = c[0].p;
		for (int i = 1; i < c.length; i++) {
			for (int j = 0; j < 3; j++) {
				if (Math.abs(c[i].p.val[j] - p0.val[j]) > maxRWDistance) {
					return false;
				}
			}
		}
		return true;
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

	protected static boolean isAngleOK(double bend, Corner... corners) {

		Corner corner0 = corners[0];
		if (corner0.normal.isFinalUndefined()) {
			return false;
		}

		for (int i = 1; i < corners.length; i++) {
			if (corners[i].normal.isFinalUndefined()) {
				return false;
			}
			if (!isAngleOK(corner0.normal.val, corners[i].normal.val, bend)) {
				return false;
			}
		}
		return true;
	}

	private class CornerAndCenter {
		private Corner corner;
		private Coords center;

		public CornerAndCenter(Corner corner, Coords center) {
			set(corner, center);
		}

		public void set(Corner corner, Coords center) {
			this.corner = corner;
			this.center = center;
		}

		public void draw(PlotterSurface surface) {

			surface.startTrianglesWireFrame();
			drawVertices(surface);
			surface.endGeometry();

			surface.startTrianglesWireFrameSurface();
			drawVertices(surface);
			surface.endGeometry();

		}


		private void drawVertices(PlotterSurface surface) {

			Corner current, sw, ne;

			Coords p1, p2;

			// go left
			current = corner;
			// get first defined point on south (if exists)
			Coords sw1 = current.p;
			Coords sw2 = sw1;
			// draw south
			p1 = sw1;
			do {
				p2 = current.l.p;
				if (p2.isNotFinalUndefined()) {
					if (p1.isNotFinalUndefined()) {
						if (sw1.isFinalUndefined()) {
							sw1 = p1;
						}
						drawTriangle(surface, center, p2, p1);
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
			Coords ne1 = current.p;
			Coords ne2 = ne1;
			// draw east
			p1 = ne1;
			do {
				p2 = current.a.p;
				if (p2.isNotFinalUndefined()) {
					if (p1.isNotFinalUndefined()) {
						drawTriangle(surface, center, p1, p2);
						if (ne1.isFinalUndefined()) {
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
			if (sw1.isFinalUndefined()) {
				sw1 = p1;
			}
			do {
				p2 = current.a.p;
				if (p2.isNotFinalUndefined()) {
					if (p1.isNotFinalUndefined()) {
						drawTriangle(surface, center, p2, p1);
						if (sw1.isFinalUndefined()) {
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
			if (ne1.isFinalUndefined()) {
				ne1 = p1;
			}
			do {
				p2 = current.l.p;
				if (p2.isNotFinalUndefined()) {
					if (p1.isNotFinalUndefined()) {
						drawTriangle(surface, center, p1, p2);
						if (ne1.isFinalUndefined()) {
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
				drawTriangle(surface, center, sw1, ne1);
			}
			if (sw2 != ne2) {
				drawTriangle(surface, center, ne2, sw2);
			}
			if (ne1.isFinalUndefined() && ne2.isFinalUndefined()) {
				drawTriangle(surface, center, sw2, sw1);
			}
			if (sw1.isFinalUndefined() && sw2.isFinalUndefined()) {
				drawTriangle(surface, center, ne1, ne2);
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
	 * @param p1
	 *            second point
	 * @param p2
	 *            third point
	 */
	static final protected void drawTriangle(PlotterSurface surface, Coords p0, Coords p1, Coords p2) {
		surface.vertex(p0);
		surface.vertex(p1);
		surface.vertex(p2);
	}


	/**
	 * list of things to draw
	 */
	ArrayList<CornerAndCenter> drawList, drawListBoundary;

}
