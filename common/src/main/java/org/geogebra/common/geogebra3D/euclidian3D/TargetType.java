package org.geogebra.common.geogebra3D.euclidian3D;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * target type for visual feedback around cursor
 */
public enum TargetType {
	/** no target */
	NOT_USED {
		@Override
		public void drawTarget(Renderer renderer, EuclidianView3D view3d, Target target) {
			// nothing to draw
		}
	},
	/** nothing targeted, no hit */
	NOTHING_NO_HIT {
		@Override
		public void drawTarget(Renderer renderer, EuclidianView3D view3d, Target target) {
			// draw nothing (TODO)
		}
	},
	/** nothing targeted, hit can be used for showing */
	NOTHING {
		@Override
		public void drawTarget(Renderer renderer, EuclidianView3D view3d, Target target) {
			// draw nothing (TODO)
		}
	},
	/** target new point on region */
	POINT_ON_REGION {
		@Override
		public void drawTarget(Renderer renderer, EuclidianView3D view3d, Target target) {
			doDrawTarget(renderer, target);
		}
	},
	/** target new point on path */
	POINT_ON_PATH {
		@Override
		public void drawTarget(Renderer renderer, EuclidianView3D view3d, Target target) {
			doDrawTarget(renderer, target);
		}
	},
	/** target new point at intersection */
	POINT_INTERSECTION {
		@Override
		public void drawTarget(Renderer renderer, EuclidianView3D view3d, Target target) {
			doDrawTarget(renderer, target);
		}
	},
	/** target existing point for move or select tools */
	POINT_ALREADY_MOVE_OR_SELECT {
		@Override
		public void drawTarget(Renderer renderer, EuclidianView3D view3d, Target target) {
			renderer.setMatrix(view3d.getCursorMatrix());
			view3d.drawPointAlready(view3d.getCursor3D());
		}
	},
	/** target existing point where arrows are shown to move it */
	POINT_ALREADY_SHOW_ARROWS {
		@Override
		public void drawTarget(Renderer renderer, EuclidianView3D view3d, Target target) {
			renderer.setMatrix(view3d.getCursorMatrix());
            view3d.drawPointAlready(view3d.getCursor3D());
		}
	},
	/** target existing point where no arrow are shown */
	POINT_ALREADY_NO_ARROW {
		@Override
		public void drawTarget(Renderer renderer, EuclidianView3D view3d, Target target) {
		    // avoid z-fighting
			renderer.getRendererImpl()
					.setLayer(view3d.getCursor3D().getLayer() + 1);
			doDrawTarget(renderer, target);
			renderer.getRendererImpl().setLayer(Renderer.LAYER_DEFAULT);
		}
	},
	/** target free point (3D input devices) */
	POINT_FREE {
		@Override
		public void drawTarget(Renderer renderer, EuclidianView3D view3d, Target target) {
			doDrawTarget(renderer, target);
		}
	},
	/** target path to select */
	SELECT_PATH {
		@Override
		public void drawTarget(Renderer renderer, EuclidianView3D view3d, Target target) {
			doDrawTarget(renderer, target);
		}
	},
	/** target region to select */
	SELECT_REGION {
		@Override
		public void drawTarget(Renderer renderer, EuclidianView3D view3d, Target target) {
			doDrawTarget(renderer, target);
		}
	},
	/** target path or region with "view in front of" tool */
	VIEW_IN_FRONT_OF {
		@Override
		public void drawTarget(Renderer renderer, EuclidianView3D view3d, Target target) {
			renderer.setMatrix(view3d.getCursorMatrix());
			renderer.drawViewInFrontOf();
		}
	};

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
			if (isModePointAlreadyMoveOrSelect(mode)) {
				return POINT_ALREADY_MOVE_OR_SELECT;
			}
			if (isModePointAlreadyAsPointTool(mode)) {
				return view3D.getCursor3D()
						.getMoveMode() == GeoPointND.MOVE_MODE_NONE
								? POINT_ALREADY_NO_ARROW
								: POINT_ALREADY_SHOW_ARROWS;
			}
			if (isModeForCreatingPoint(mode)) {
				return POINT_ALREADY_NO_ARROW;
			}
			return NOT_USED;

		case EuclidianView3D.PREVIEW_POINT_DEPENDENT:
			if (mode == EuclidianConstants.MODE_POINT_ON_OBJECT) {
				return NOTHING;
			}
			if (isModeForCreatingPoint(mode)) {
				if (mode == EuclidianConstants.MODE_REGULAR_POLYGON) {
					return (ec.selPoints() == 0 || ec.selCS2D() == 1)
							? POINT_INTERSECTION
							: NOTHING;
				}
				return POINT_INTERSECTION;
			}
			return NOT_USED;

		case EuclidianView3D.PREVIEW_POINT_NONE:
			return isModeForCreatingPoint(mode) ? NOTHING_NO_HIT : NOT_USED;
		case EuclidianView3D.PREVIEW_POINT_FREE:
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
	 *            controller mode
	 * @return true if mode moves/select already created points
	 */
	static public boolean isModePointAlreadyMoveOrSelect(int mode) {
		return mode == EuclidianConstants.MODE_MOVE
				|| mode == EuclidianConstants.MODE_SELECT;
	}

	/**
	 * 
	 * @param mode
	 *            controller mode
	 * @return true if mode acts as point tool when over already created point
	 */
	static public boolean isModePointAlreadyAsPointTool(int mode) {
		return mode == EuclidianConstants.MODE_POINT
				|| mode == EuclidianConstants.MODE_POINT_ON_OBJECT;
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
		case EuclidianConstants.MODE_REGULAR_POLYGON:
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
			
		case EuclidianConstants.MODE_REGULAR_POLYGON:
			// one point or one region: can create a point
			if (ec.selPoints() == 0 || ec.selCS2D() == 1) {
				return onSuccess;
			}
			// on xOy plane or path: can create a point
			GeoPoint3D point = view3D.getCursor3D();
			if (point.hasRegion()) {
				if (point.getRegion() == view3D.getxOyPlane()) {
					return onSuccess;
				}
				if (point.getRegion() instanceof GeoCoordSys2D) {
					return onFail;
				}
			}
            if (point.isPointOnPath()) {
                return onSuccess;
            }
			return NOTHING;
		default:
			return NOT_USED;
		}
	}

	static private TargetType getCurrentTargetTypeForPathOrRegionWithArchimedeanMode(
			EuclidianView3D view3D, EuclidianController3D ec,
			TargetType onSuccess, TargetType onFail, int vertexCount) {
		Hits hits;
		// no point: can select a regular polygon
		if (ec.selPoints() == 0) {
			GeoPoint3D point = view3D.getCursor3D();
			if (point.hasRegion()) {
				GeoElement geo = (GeoElement) point.getRegion();
				if (!(geo instanceof GeoCoordSys2D)) {
					return onSuccess;
				}
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
            if (!(point.getRegion() instanceof GeoCoordSys2D)
                    || point.getRegion() == ec.getKernel().getXOYPlane()) {
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
	 * @param target
	 *            target
	 */
	abstract public void drawTarget(Renderer renderer, EuclidianView3D view3d, Target target);

	/**
	 * draw sphere at current cursor position
	 * 
	 * @param renderer
	 *            renderer
	 * @param target
	 *            target
	 */
	static protected void doDrawTarget(Renderer renderer,
			Target target) {
		renderer.drawTarget(target.getDotMatrix(), target.getCircleMatrix());
	}
}