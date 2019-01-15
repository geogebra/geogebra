package org.geogebra.common.geogebra3D.euclidian3D;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterCursor;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.main.Feature;

/**
 * target type for visual feedback around cursor
 */
public enum TargetType {
	/** no target */
	NOT_USED {
		@Override
		public void drawTarget(Renderer renderer, EuclidianView3D view3d) {
			// nothing to draw
		}
	},
	/** nothing targeted, no hit */
	NOTHING_NO_HIT {
		@Override
		public void drawTarget(Renderer renderer, EuclidianView3D view3d) {
			// draw nothing (TODO)
		}
	},
	/** nothing targeted, hit can be used for showing */
	NOTHING {
		@Override
		public void drawTarget(Renderer renderer, EuclidianView3D view3d) {
			// draw nothing (TODO)
		}
	},
	/** target new point on region */
	POINT_ON_REGION {
		@Override
		public void drawTarget(Renderer renderer, EuclidianView3D view3d) {
			drawSphere(renderer, view3d);
		}
	},
	/** target new point on path */
	POINT_ON_PATH {
		@Override
		public void drawTarget(Renderer renderer, EuclidianView3D view3d) {
			drawSphere(renderer, view3d);
		}
	},
	/** target new point at intersection */
	POINT_INTERSECTION {
		@Override
		public void drawTarget(Renderer renderer, EuclidianView3D view3d) {
			drawSphere(renderer, view3d);
		}
	},
	/** target existing point for move or select tools */
	POINT_ALREADY_MOVE_OR_SELECT {
		@Override
		public void drawTarget(Renderer renderer, EuclidianView3D view3d) {
			renderer.setMatrix(view3d.getCursorMatrix());
			view3d.drawPointAlready(view3d.getCursor3D());
		}
	},
	/** target existing point with point (or point on object) tool */
	POINT_ALREADY_POINT_TOOL {
		@Override
		public void drawTarget(Renderer renderer, EuclidianView3D view3d) {
			renderer.setMatrix(view3d.getCursorMatrix());
            view3d.drawPointAlready(view3d.getCursor3D());
		}
	},
	/** target existing point with a tool that can NOT move a point */
	POINT_ALREADY_CANNOT_MOVE_TOOL {
		@Override
		public void drawTarget(Renderer renderer, EuclidianView3D view3d) {
			// draw nothing
		}
	},
	/** target free point (3D input devices) */
	POINT_FREE {
		@Override
		public void drawTarget(Renderer renderer, EuclidianView3D view3d) {
			renderer.setMatrix(view3d.getCursorMatrix());
			view3d.drawFreeCursor(renderer);
		}
	},
	/** target path to select */
	SELECT_PATH {
		@Override
		public void drawTarget(Renderer renderer, EuclidianView3D view3d) {
			drawSphere(renderer, view3d);
		}
	},
	/** target region to select */
	SELECT_REGION {
		@Override
		public void drawTarget(Renderer renderer, EuclidianView3D view3d) {
			drawSphere(renderer, view3d);
		}
	},
	/** target path or region with "view in front of" tool */
	VIEW_IN_FRONT_OF {
		@Override
		public void drawTarget(Renderer renderer, EuclidianView3D view3d) {
			renderer.setMatrix(view3d.getCursorMatrix());
			renderer.drawViewInFrontOf();
		}
	};
	
	/**
	 * temp matrix
	 */
	static CoordMatrix4x4 tmpMatrix = CoordMatrix4x4.identity();

	/**
	 * 
	 * @param view3D
	 *            3D view
	 * @param ec
	 *            3D controller
	 * @return current target type regarding view 3D cursor and mode
	 */
	static public TargetType getCurrentTargetType(EuclidianView3D view3D,
			EuclidianController3D ec) {
		int mode = ec.getMode();
		switch (view3D.getCursor3DType()) {
		case EuclidianView3D.PREVIEW_POINT_ALREADY:
			switch (mode) {
			// modes in which the result could be a dependent point
			case EuclidianConstants.MODE_MOVE:
			case EuclidianConstants.MODE_SELECT:
				return POINT_ALREADY_MOVE_OR_SELECT;
			case EuclidianConstants.MODE_POINT:
			case EuclidianConstants.MODE_POINT_ON_OBJECT:
				return POINT_ALREADY_POINT_TOOL;
			default:
				return isModeForCreatingPoint(mode)
						? POINT_ALREADY_CANNOT_MOVE_TOOL
						: NOT_USED;
			}

		case EuclidianView3D.PREVIEW_POINT_DEPENDENT:
			if (mode == EuclidianConstants.MODE_POINT_ON_OBJECT) {
				return NOTHING;
			}
			return isModeForCreatingPoint(mode) ? POINT_INTERSECTION : NOT_USED;

		case EuclidianView3D.PREVIEW_POINT_NONE:
			return isModeForCreatingPoint(mode) ? NOTHING_NO_HIT : NOT_USED;
		case EuclidianView3D.PREVIEW_POINT_FREE:
			if (mode == EuclidianConstants.MODE_INTERSECT) {
				return NOTHING;
			}
			return isModeForCreatingPoint(mode) ? POINT_FREE : NOT_USED;

		case EuclidianView3D.PREVIEW_POINT_PATH:
		case EuclidianView3D.PREVIEW_POINT_REGION_AS_PATH:
			return getCurrentTargetTypeForPathOrRegion(view3D, ec, mode,
					POINT_ON_PATH, SELECT_PATH);
		case EuclidianView3D.PREVIEW_POINT_REGION:
			return getCurrentTargetTypeForPathOrRegion(view3D, ec, mode,
					POINT_ON_REGION, SELECT_REGION);
		}

		return NOT_USED;
	}

	/**
	 * 
	 * @param mode
	 *            mode
	 * @return true if this mode can create a point
	 */
	static public boolean isModeForCreatingPoint(int mode) {
		switch (mode) {
		case EuclidianConstants.MODE_POINT:
		case EuclidianConstants.MODE_POINT_ON_OBJECT:
		case EuclidianConstants.MODE_INTERSECT:
		case EuclidianConstants.MODE_SEGMENT:
		case EuclidianConstants.MODE_SEGMENT_FIXED:
		case EuclidianConstants.MODE_JOIN:
		case EuclidianConstants.MODE_RAY:
		case EuclidianConstants.MODE_VECTOR:
		case EuclidianConstants.MODE_POLYGON:
		case EuclidianConstants.MODE_PYRAMID:
		case EuclidianConstants.MODE_PRISM:
		case EuclidianConstants.MODE_TETRAHEDRON:
		case EuclidianConstants.MODE_CUBE:
		case EuclidianConstants.MODE_SPHERE_TWO_POINTS:
		case EuclidianConstants.MODE_SPHERE_POINT_RADIUS:
		case EuclidianConstants.MODE_CONE_TWO_POINTS_RADIUS:
		case EuclidianConstants.MODE_CYLINDER_TWO_POINTS_RADIUS:
		case EuclidianConstants.MODE_PLANE_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCLE_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
		case EuclidianConstants.MODE_ELLIPSE_THREE_POINTS:
		case EuclidianConstants.MODE_CONIC_FIVE_POINTS:
		case EuclidianConstants.MODE_POLYLINE:
			return true;
		default:
			return false;
		}
	}

	static private TargetType getCurrentTargetTypeForPathOrRegion(
			EuclidianView3D view3D, EuclidianController3D ec, int mode,
			TargetType onSuccess, TargetType onFail) {
		switch (mode) {
		case EuclidianConstants.MODE_POINT:
		case EuclidianConstants.MODE_POINT_ON_OBJECT:
		case EuclidianConstants.MODE_SEGMENT:
		case EuclidianConstants.MODE_SEGMENT_FIXED:
		case EuclidianConstants.MODE_JOIN:
		case EuclidianConstants.MODE_RAY:
		case EuclidianConstants.MODE_VECTOR:
		case EuclidianConstants.MODE_POLYGON:
		case EuclidianConstants.MODE_SPHERE_TWO_POINTS:
		case EuclidianConstants.MODE_SPHERE_POINT_RADIUS:
		case EuclidianConstants.MODE_CONE_TWO_POINTS_RADIUS:
		case EuclidianConstants.MODE_CYLINDER_TWO_POINTS_RADIUS:
		case EuclidianConstants.MODE_PLANE_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCLE_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
		case EuclidianConstants.MODE_ELLIPSE_THREE_POINTS:
		case EuclidianConstants.MODE_CONIC_FIVE_POINTS:
		case EuclidianConstants.MODE_POLYLINE:
			return onSuccess;

		case EuclidianConstants.MODE_INTERSECT:
			return NOTHING;

		case EuclidianConstants.MODE_PYRAMID:
		case EuclidianConstants.MODE_PRISM:
			if (ec.selPolygons() == 1) {
				return onSuccess;
			}
			Hits hits = view3D.getHits();
			if (hits.isEmpty()) {
				return onSuccess;
			}
			return hits.getPolyCount() > 0 ? onFail : onSuccess;

		case EuclidianConstants.MODE_TETRAHEDRON:
			return getCurrentTargetTypeForPathOrRegionWithArchimedeanMode(
					view3D, ec, onSuccess, onFail, 3);
		case EuclidianConstants.MODE_CUBE:
			return getCurrentTargetTypeForPathOrRegionWithArchimedeanMode(
					view3D, ec, onSuccess, onFail, 4);

		case EuclidianConstants.MODE_VIEW_IN_FRONT_OF:
			return VIEW_IN_FRONT_OF;
			
		default:
			return NOT_USED;
		}
	}

	static private TargetType getCurrentTargetTypeForPathOrRegionWithArchimedeanMode(
			EuclidianView3D view3D, EuclidianController3D ec,
			TargetType onSuccess, TargetType onFail, int vertexCount) {
		Hits hits;
		if (view3D.getApplication().has(Feature.G3D_IMPROVE_SOLID_TOOLS)) {
			// no point: can select a regular polygon
			if (ec.selPoints() == 0) {
				GeoPoint3D point = view3D.getCursor3D();
				if (point.hasRegion()) {
					GeoElement geo = (GeoElement) point.getRegion();
					if (!geo.isGeoPolygon()) {
						return geo.isGeoPlane() ? onSuccess : NOTHING;
					}
					GeoPolygon polygon = (GeoPolygon) geo;
					if (polygon.getPointsLength() == vertexCount
							&& polygon.isRegular()) {
						return onFail;
					}
					return NOTHING;
				}
				// must be a path
				return onSuccess;
			}
			// one point, one region: can create a point
			if (ec.selCS2D() == 1) {
				return onSuccess;
			}
		} else {
			// show cursor when direction has been selected
			if (ec.selCS2D() == 1 || (ec.selPoints() != 0)) {
				return onSuccess;
			}
		}
		// no region: can create a point on edge or xOy plane
		hits = view3D.getHits();
		if (hits.isEmpty()) {
			return onSuccess;
		}
		GeoPoint3D point = view3D.getCursor3D();
		if (point.isPointOnPath()) {
			return onSuccess;
		}
		if (point.hasRegion()) {
			if (point.getRegion() == ec.getKernel().getXOYPlane()) {
				return onSuccess;
			}
		}
		return onFail;
	}

	/**
	 * 
	 * @param renderer
	 *            GL renderer
	 * @param view3d
	 *            3D view
	 */
	abstract public void drawTarget(Renderer renderer, EuclidianView3D view3d);

	/**
	 * draw sphere at current cursor position
	 * 
	 * @param renderer
	 *            renderer
	 * @param view3d
	 *            3D view
	 */
	static protected void drawSphere(Renderer renderer,
			EuclidianView3D view3d) {
		tmpMatrix.setOrigin(view3d.getCursorMatrix().getOrigin());
		renderer.setMatrix(tmpMatrix);
		renderer.drawCursorDisableLighting(PlotterCursor.TYPE_SPHERE);
	}
}