package geogebra.common.geogebra3D.euclidian3D.draw;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.euclidian3D.openGL.PlotterSurface;
import geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
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

	private static final long MAX_SPLIT = 32768;
	private static final long MIN_SPLIT = 512;
	private static final double MAX_CENTER_QUAD_DISTANCE = 1.e-3;
	private static final double MAX_DIAGONAL_QUAD_LENGTH = 1.e-3;
	private double uDelta;
	private double vDelta;
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
		uDelta = (uMax - uMin) / MAX_SPLIT;
		vDelta = (vMax - vMin) / MAX_SPLIT;

		updateCullingBox();
		cullingBoxDelta = (cullingBox[5] - cullingBox[4]);
		limit1 = cullingBoxDelta * MAX_DIAGONAL_QUAD_LENGTH;
		limit2 = cullingBoxDelta * MAX_CENTER_QUAD_DISTANCE;

		surface.start(getReusableSurfaceIndex());

		// Corner al = new Corner(uMin, vMin);
		// Corner a = new Corner(uMax, vMin);
		// Corner l = new Corner(uMin, vMax);
		// Corner corner = new Corner(uMax, vMax, a, l, al);

		double uDelta = (uMax - uMin) / 10;
		double vDelta = (vMax - vMin) / 10;
		Corner corner = createRootMesh(uMin, uMax, uDelta, vMin, vMax, vDelta);

		currentSplit = new ArrayList<DrawSurface3D.Corner>();
		nextSplit = new ArrayList<DrawSurface3D.Corner>();
		drawList = new ArrayList<DrawSurface3D.CornerAndCenter>();
		// currentSplit.add(corner);
		notDrawn = 0;
		splitRootMesh(corner);
		split(1);

		App.debug("\ndraw size : " + drawList.size() + "\nnot drawn : "
				+ notDrawn + "\nstill to split : " + nextSplit.size());

		for (CornerAndCenter cc : drawList) {
			cc.draw(surface);
		}

		for (Corner c : nextSplit) {
			c.draw(surface);
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

	private Corner createRootMesh(double uMin, double uMax, double uDelta,
			double vMin, double vMax, double vDelta) {

		Corner bottomRight = new Corner(uMax, vMax);
		Corner first = bottomRight;


		// first row
		Corner right = bottomRight;
		for (double u = uMax - uDelta; u >= uMin; u = u - uDelta) {
			Corner left = new Corner(u, vMax);
			right.l = left;
			right = left;
			// App.debug("" + u);
		}

		// all rows
		for (double v = vMax - vDelta; v >= vMin; v = v - vDelta) {
			Corner below = bottomRight;
			right = new Corner(uMax, v);
			below.a = right;
			for (double u = uMax - uDelta; u >= uMin; u = u - uDelta) {
				Corner left = new Corner(u, v);
				right.l = left;
				right = left;
				below = below.l;
				below.a = right;
				// App.debug("" + u + "," + v);
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
				current.split();
				current = nextLeft;
			}
			current = nextAbove;
		}

	}

	private void split(int depth) {

		boolean recordNext = depth > 1;
		// int test = 1;
		for (Corner corner : currentSplit) {
			corner.split();
			// corner.split(recordNext && (test % 3 != 0));
			// test++;
		}

		if (recordNext) {
			// swap stacks
			ArrayList<Corner> tmp = currentSplit;
			currentSplit = nextSplit;
			nextSplit = tmp;
			nextSplit.clear();
			// split again
			split(depth - 1);
		}

	}

	private Coords evaluatedPoint = new Coords(3);

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
		double u, v;

		Corner a, l; // above, left

		public Corner(double u, double v) {
			this.u = u;
			this.v = v;
			p = evaluatePoint(u, v);
		}

		public Corner(double u, double v, Coords p) {
			this.u = u;
			this.v = v;
			this.p = p;
		}

		public Corner(double u, double v, Corner a, Corner l, Corner al) {
			this(u, v);

			// neighbors
			this.a = a;
			this.l = l;

			// neighbors of neighbors
			l.a = al;
			a.l = al;

		}



		public void split() {

			// middle parameters
			double um, vm;
			if (l.a == null) { // already splitted on left
				um = l.u;
			} else { // split on left
				um = (u + l.u) / 2;
			}
			if (a.l == null) { // already splitted on above
				vm = a.v;
			} else { // split on above
				vm = (v + a.v) / 2;
			}

			// middle point
			Coords pm = evaluatePoint(um, vm);


			// how many undefined corners?
			int undefinedCorners = 0;
			if (p.isFinalUndefined()) {
				undefinedCorners++;
			}
			Corner left = l;
			if (left.a == null) { // already splitted on left
				left = left.l;
			}
			if (left.p.isFinalUndefined()) {
				undefinedCorners++;
			}
			Corner above = a;
			if (above.l == null) { // already splitted on above
				above = above.a;
			}
			if (above.p.isFinalUndefined()) {
				undefinedCorners++;
			}
			Corner aboveLeft = above.l;
			if (aboveLeft.p.isFinalUndefined()) {
				undefinedCorners++;
			}

			switch (undefinedCorners) {
			case 0: // all corners are defined
				if (pm.isNotFinalUndefined()) {
					drawList.add(new CornerAndCenter(this, pm));
				} else {
					notDrawn++;
				}
				break;

			case 4: // all corners are undefined
				notDrawn++;
				break;

			default: // we are on boundary: split
				split(um, vm, pm);
				break;

			}

			// if (splitNext &&
			// // middle-point or corner undefined
			// (pm.isFinalUndefined() || hasUndefinedCorner())
			// ) {
			// split(um, vm, pm);
			// } else {
			// if (pm.isNotFinalUndefined()) {
			// drawList.add(new CornerAndCenter(this, pm));
			// } else {
			// notDrawn++;
			// }
			// }

		}

		private void split(double um, double vm, Coords pm) {
			// south
			Corner s;
			if (l.a == null) { // already splitted on left
				s = l;
			} else { // split on left
				s = new Corner(um, v);
				s.l = l;
				l = s;
			}

			// east
			Corner e;
			if (a.l == null) { // already splitted on above
				e = a;
			} else { // split on above
				e = new Corner(u, vm);
				e.a = a;
				a = e;
			}

			// middle
			Corner m = new Corner(um, vm, pm);
			s.a = m;
			e.l = m;

			// north
			Corner n = new Corner(um, e.a.v);
			n.l = e.a.l;
			e.a.l = n;
			m.a = n;

			// west
			Corner w = new Corner(s.l.u, vm);
			w.a = s.l.a;
			s.l.a = w;
			m.l = w;

			// split again
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

		// private Corner getLeftCheckSplitted() {
		// if (l.a == null) {
		// return l.l;
		// }
		// return l;
		// }
		//
		// private Corner getAboveCheckSplitted() {
		// if (a.l == null) {
		// return a.a;
		// }
		// return a;
		// }

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

		public void draw(PlotterSurface surface) {

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
						} else {
							// l.a is defined
							// find defined between l.a and a
							Corner n = findU(left.a, above, BOUNDARY_SPLIT);
							// find defined between l.a and l
							Corner w = findV(left.a, left, BOUNDARY_SPLIT);
							drawTriangles(surface, n.p, w.p, left.a.p);
						}
					} else {
						if (left.a.p.isFinalUndefined()) {
							// a defined
							// find defined between a and l.a
							Corner n = findU(above, left.a, BOUNDARY_SPLIT);
							// find defined between a and this
							Corner e = findV(above, this, BOUNDARY_SPLIT);
							drawTriangles(surface, n.p, e.p, above.p);
						} else {
							// a and l.a not undefined
							// find defined between a and this
							Corner e = findV(above, this, BOUNDARY_SPLIT);
							// find defined between l.a and left
							Corner w = findV(left.a, left, BOUNDARY_SPLIT);
							drawTriangles(surface, e.p, above.p, left.a.p);
							drawTriangles(surface, e.p, left.a.p, w.p);
						}
					}
				} else {
					if (above.p.isFinalUndefined()) {
						if (left.a.p.isFinalUndefined()) {
							// l defined
							// find defined between l and this
							Corner s = findU(left, this, BOUNDARY_SPLIT);
							// find defined between l and l.a
							Corner w = findV(left, left.a, BOUNDARY_SPLIT);
							drawTriangles(surface, s.p, w.p, left.p);
						} else {
							// l and l.a not undefined
							// find defined between l and this
							Corner s = findU(left, this, BOUNDARY_SPLIT);
							// find defined between l.a and a
							Corner n = findU(left.a, above, BOUNDARY_SPLIT);
							drawTriangles(surface, s.p, n.p, left.a.p);
							drawTriangles(surface, s.p, left.a.p, left.p);
						}
					} else {
						if (left.a.p.isFinalUndefined()) {
							// l and a not undefined
						} else {
							// l, a and l.a not undefined
							// find defined between l and this
							Corner s = findU(left, this, BOUNDARY_SPLIT);
							// find defined between a and this
							Corner e = findV(above, this, BOUNDARY_SPLIT);
							drawTriangles(surface, left.a.p, left.p, s.p);
							drawTriangles(surface, left.a.p, s.p, e.p);
							drawTriangles(surface, left.a.p, e.p, above.p);
						}
					}
				}
			} else {
				if (left.p.isFinalUndefined()) {
					if (above.p.isFinalUndefined()) {
						if (left.a.p.isFinalUndefined()) {
							// this defined
							// find defined between this and l
							Corner s = findU(this, left, BOUNDARY_SPLIT);
							// find defined between this and a
							Corner e = findV(this, above, BOUNDARY_SPLIT);
							drawTriangles(surface, s.p, e.p, p);
						} else {
							// this and l.a not undefined
						}
					} else {
						if (left.a.p.isFinalUndefined()) {
							// this and a not undefined
							// find defined between this and l
							Corner s = findU(this, left, BOUNDARY_SPLIT);
							// find defined between a and l.a
							Corner n = findU(above, left.a, BOUNDARY_SPLIT);
							drawTriangles(surface, p, above.p, n.p);
							drawTriangles(surface, p, n.p, s.p);
						} else {
							// this, a and l.a not undefined
							// find defined between this and l
							Corner s = findU(this, left, BOUNDARY_SPLIT);
							// find defined between l.a and l
							Corner w = findV(left.a, left, BOUNDARY_SPLIT);
							drawTriangles(surface, above.p, left.a.p, w.p);
							drawTriangles(surface, above.p, w.p, s.p);
							drawTriangles(surface, above.p, s.p, this.p);
						}
					}
				} else {
					if (above.p.isFinalUndefined()) {
						if (left.a.p.isFinalUndefined()) {
							// this and l not undefined
							// find defined between this and a
							Corner e = findV(this, above, BOUNDARY_SPLIT);
							// find defined between l and l.a
							Corner w = findV(left, left.a, BOUNDARY_SPLIT);
							drawTriangles(surface, p, e.p, w.p);
							drawTriangles(surface, p, w.p, left.p);
						} else {
							// this, l and l.a not undefined
							// find defined between l.a and a
							Corner n = findU(left.a, above, BOUNDARY_SPLIT);
							// find defined between this and a
							Corner e = findV(this, above, BOUNDARY_SPLIT);
							drawTriangles(surface, left.p, p, e.p);
							drawTriangles(surface, left.p, e.p, n.p);
							drawTriangles(surface, left.p, n.p, left.a.p);
						}
					} else {
						if (left.a.p.isFinalUndefined()) {
							// this, l and a not undefined
							// find defined between a and l.a
							Corner n = findU(above, left.a, BOUNDARY_SPLIT);
							// find defined between l and l.a
							Corner w = findV(left, left.a, BOUNDARY_SPLIT);
							drawTriangles(surface, this.p, above.p, n.p);
							drawTriangles(surface, this.p, n.p, w.p);
							drawTriangles(surface, this.p, w.p, left.p);
						} else {
							// this, l, a and l.a not undefined
							drawTriangles(surface, this.p, left.p, left.a.p);
							drawTriangles(surface, this.p, above.p, left.a.p);
						}
					}
				}
			}

		}

		private void drawTriangles(PlotterSurface surface, Coords p0,
				Coords p1, Coords p2) {

			surface.startTrianglesWireFrame();
			surface.vertex(p0);
			surface.vertex(p1);
			surface.vertex(p2);

			surface.endGeometry();

			surface.startTrianglesWireFrameSurfaceBoundary();
			surface.vertex(p0);
			surface.vertex(p1);
			surface.vertex(p2);

			surface.endGeometry();
		}

	}

	protected static boolean isDistanceOK(double[] diff, EuclidianView view) {
		for (double d : diff) {
			if (Math.abs(d) > view.getMaxPixelDistance()) {
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

			// go left
			current = corner;
			p2 = current.p;
			do {
				p1 = current.l.p;
				if (p1.isNotFinalUndefined()) {
					if (p2.isNotFinalUndefined()) {
						drawTriangle(surface);
					}
					p2 = p1;
				}
				current = current.l;
			} while (current.a == null);
			double u = current.u;
			sw = current;

			// go above
			current = corner;
			p1 = current.p;
			do {
				p2 = current.a.p;
				if (p2.isNotFinalUndefined()) {
					if (p1.isNotFinalUndefined()) {
						drawTriangle(surface);
					}
					p1 = p2;
				}
				current = current.a;
			} while (current.l == null);
			double v = current.v;
			ne = current;

			// west side
			current = sw;
			p2 = current.p;
			do {
				p1 = current.a.p;
				if (p1.isNotFinalUndefined()) {
					if (p2.isNotFinalUndefined()) {
						drawTriangle(surface);
					}
					p2 = p1;
				}
				current = current.a;
			} while (current.v > v);

			// north side
			current = ne;
			p1 = current.p;
			do {
				p2 = current.l.p;
				if (p2.isNotFinalUndefined()) {
					if (p1.isNotFinalUndefined()) {
						drawTriangle(surface);
					}
					p1 = p2;
				}
				current = current.l;
			} while (current.u > u);

		}

		private Coords p1, p2;

		private void drawTriangle(PlotterSurface surface) {
			surface.vertex(center);
			surface.vertex(p1);
			surface.vertex(p2);
		}

	}

	/**
	 * list of things to draw
	 */
	ArrayList<CornerAndCenter> drawList;

}
