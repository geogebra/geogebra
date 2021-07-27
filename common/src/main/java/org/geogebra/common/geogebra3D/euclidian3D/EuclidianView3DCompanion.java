package org.geogebra.common.geogebra3D.euclidian3D;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianCursor;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewCompanion;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterCursor;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.EuclidianSettings3D;

/**
 * Companion for EuclidianView3D
 * 
 */
public class EuclidianView3DCompanion extends EuclidianViewCompanion {

	private EuclidianView3D view3D;
	private boolean isStereoBuffered = false;

	protected double zNearest;

	/**
	 * @param view
	 *            view
	 */
	public EuclidianView3DCompanion(EuclidianView view) {
		super(view);
	}

	@Override
	protected void setView(EuclidianView view) {
		super.setView(view);
		view3D = (EuclidianView3D) view;
	}

	@Override
	public EuclidianView3D getView() {
		return view3D;
	}

	/**
	 * draws the mouse cursor (for glasses)
	 *
	 * @param renderer1
	 *            renderer
	 */
	public void drawMouseCursor(Renderer renderer1) {
		if (!getView().hasMouse()) {
			return;
		}

		if (getView()
				.getProjection() != EuclidianView3DInterface.PROJECTION_GLASSES) {
			return;
		}

		GPoint mouseLoc = getView().getEuclidianController().getMouseLoc();
		if (mouseLoc == null) {
			return;
		}

		Coords v;

		if (getView().getCursor3DType() == EuclidianView3D.CURSOR_DEFAULT) {
			// if mouse is over nothing, use mouse coords and screen for depth
			v = new Coords(mouseLoc.x + renderer1.getLeft(),
					-mouseLoc.y + renderer1.getTop(), 0, 1);
		} else {
			// if mouse is over an object, use its depth and mouse coords
			Coords eye = renderer1.getPerspEye();
			double z = getView().getToScreenMatrix()
					.mul(getView().getCursor3D().getCoords()).getZ() + 20; // to
																			// be
																			// over
			double eyeSep = renderer1.getEyeSep(); // TODO eye lateralization

			double x = mouseLoc.x + renderer1.getLeft() + eyeSep - eye.getX();
			double y = -mouseLoc.y + renderer1.getTop() - eye.getY();
			double dz = eye.getZ() - z;
			double coeff = dz / eye.getZ();

			v = new Coords(x * coeff - eyeSep + eye.getX(),
					y * coeff + eye.getY(), z, 1);
		}

		getView().drawMouseCursor(renderer1, v);

	}

	/**
	 * Draw 2D cross cursor
	 * 
	 * @param renderer1
	 *            renderer
	 */
	public void drawFreeCursor(Renderer renderer1) {
		// free point on xOy plane
		renderer1.drawCursor(PlotterCursor.Type.CROSS2D);
	}

	/**
	 * @param p
	 *            event location
	 * @param type
	 *            event type
	 * @return whether label was hit
	 */
	public GeoElement getLabelHit(GPoint p, PointerEventType type) {
		if (type == PointerEventType.TOUCH) {
			return null;
		}
		return getView().getRenderer().getLabelHit(p);
	}

	/**
	 * @return mouse pick width for openGL picking
	 */
	public int getMousePickWidth() {
		return 3;
	}

	@Override
	public boolean isMoveable(GeoElement geo) {
		return geo.isMoveable();
	}

	/**
	 * @param type
	 *            event type
	 * @return point capturing threshold
	 */
	public int getCapturingThreshold(PointerEventType type) {
		return getView().getApplication().getCapturingThreshold(type);
	}

	/**
	 * @param zNearest
	 *            near z-coord
	 */
	public void setZNearest(double zNearest) {
		this.zNearest = zNearest;
	}

	/**
	 *
	 * @return current z nearest hit
	 */
	public double getZNearest() {
		return zNearest;
	}

	/**
	 * Reset styles: ignore unless 3D input is used
	 */
	public void resetAllVisualStyles() {
		// used for some input3D
	}

	/**
	 * Reset drawables: ignore unless 3D input is used
	 */
	public void resetOwnDrawables() {
		// used for some input3D
	}

	/**
	 * ignore unless 3D input is used
	 */
	public void update() {
		// used for some input3D
	}

	public void drawPointAlready(GeoPoint3D point) {
		getView().drawPointAlready(point.getMoveMode());
	}

	/**
	 * rotate to default
	 */
	public void setDefaultRotAnimation() {
		getView().setRotAnimation(EuclidianView3DInterface.ANGLE_ROT_OZ,
				EuclidianView3DInterface.ANGLE_ROT_XOY, false);
	}

	protected void getXMLForStereo(StringBuilder sb, int eyeDistance, int sep) {
		if (eyeDistance != EuclidianSettings3D.PROJECTION_PERSPECTIVE_EYE_DISTANCE_DEFAULT) {
			sb.append("\" distance=\"");
			sb.append(eyeDistance);
		}
		if (sep != EuclidianSettings3D.EYE_SEP_DEFAULT) {
			sb.append("\" separation=\"");
			sb.append(sep);
		}
	}

	protected void setBackground(GColor color) {
		if (color != null) {
			getView().setBackground(color, color);
		}
	}

	/**
	 *
	 * @return true if consumes space key hitted
	 */
	public boolean handleSpaceKey() {
		return false;
	}

	protected boolean moveCursorIsVisible() {
		if (getView().getEuclidianController()
				.getMoveMode() != EuclidianController.MOVE_NONE
				&& getView().getEuclidianController()
						.getMoveMode() != EuclidianController.MOVE_VIEW) {
			return false;
		}
		return getView().cursorIsTranslateViewCursor()
				|| getView().getEuclidianController()
						.getMode() == EuclidianConstants.MODE_TRANSLATEVIEW;
	}

	protected void drawTranslateViewCursor(Renderer renderer1,
			EuclidianCursor cursor, GeoPoint3D cursorOnXOYPlane,
			CoordMatrix4x4 cursorMatrix) {
		switch (cursor) {
		default:
		case MOVE:
			renderer1.setMatrix(cursorOnXOYPlane.getDrawingMatrix());
			getView().drawPointAlready(cursorOnXOYPlane.getRealMoveMode());
			renderer1.drawCursor(PlotterCursor.Type.CUBE);
			break;
		case RESIZE_X:
		case RESIZE_Y:
		case RESIZE_Z:
			renderer1.setMatrix(cursorMatrix);
			getView().getRenderer().drawCursor(PlotterCursor.Type.ALREADY_Z);
			renderer1.drawCursor(PlotterCursor.Type.CUBE);
			break;
		}
	}

	public void updateStylusBeamForMovedGeo() {
		// used for some 3D inputs
	}

	public void setIsStereoBuffered(boolean flag) {
		isStereoBuffered = flag;
	}

	public boolean isStereoBuffered() {
		return isStereoBuffered;
	}

	public boolean wantsStereo() {
		return isStereoBuffered();
	}

	/**
	 *
	 * @return true if currently uses hand grabbing (3D input)
	 */
	public boolean useHandGrabbing() {
		return false;
	}

	protected void getHittingOrigin(GPoint mouse, Coords ret) {
		getView().getPickPoint(mouse, ret);
		if (getView()
				.getProjection() == EuclidianView3DInterface.PROJECTION_PERSPECTIVE
				|| getView()
						.getProjection() == EuclidianView3DInterface.PROJECTION_GLASSES) {
			ret.set4(getView().getRenderer().getPerspEye());
		}
		getView().toSceneCoords3D(ret);
	}

	/**
	 *
	 * @param ret
	 *            direction for hitting
	 */
	public void getHittingDirection(Coords ret) {
		ret.set4(getView().getViewDirection());
	}

	protected void setPickPointFromMouse(GPoint mouse, Coords pickPoint) {
		Renderer renderer = getView().getRenderer();
		int projection = getView().getProjection();
		pickPoint.setX(mouse.getX() + renderer.getLeft());
		pickPoint.setY(-mouse.getY() + renderer.getTop());
		if (projection == EuclidianView3DInterface.PROJECTION_PERSPECTIVE
				|| projection == EuclidianView3DInterface.PROJECTION_GLASSES) {
			pickPoint.setZ(0);
		} else {
			pickPoint.setZ(renderer.getVisibleDepth());
			if (projection == EuclidianView3DInterface.PROJECTION_OBLIQUE) {
				pickPoint.setX(pickPoint.getX()
						- pickPoint.getZ() * renderer.getObliqueX());
				pickPoint.setY(pickPoint.getY()
						- pickPoint.getZ() * renderer.getObliqueY());
			}
		}
	}

	protected boolean decorationVisible() {
		return getView().getPointDecorations().shouldBeDrawn();
	}

	protected void setPointDecorations(GeoPointND point) {
		getView().getPointDecorations().setPoint(point);
	}

	/**
	 *
	 * @return true if it has to draw 2D/1D arrows to move free point
	 */
	protected boolean drawCrossForFreePoint() {
		return true;
	}

	/**
	 * Initialiye stylus beam
	 */
	public void initAxisAndPlane() {
		// needed for input3D
	}

	/**
	 * 
	 * @return true for some 3D stereo devices
	 */
	public boolean useOnlyProjectionGlasses() {
		return false;
	}

    @Override
    public boolean hasMouse() {
        if (view3D.isXREnabled() && ((EuclidianController3D) view3D.getEuclidianController())
                .isCurrentModeForCreatingPoint()) {
            return true;
        }
        return super.hasMouse();
    }

	/**
	 * 
	 * @return true if view should draw cursor
	 */
	public boolean shouldDrawCursor() {
		return view3D.isCursor3DVisible();
	}

	@Override
	protected void setMinMaxObjectsInView(EuclidianSettings evs) {
		super.setMinMaxObjectsInView(evs);
		if (evs instanceof EuclidianSettings3D) {
			((EuclidianView3D) view).setZminObject(((EuclidianSettings3D) evs).getZminObject());
			((EuclidianView3D) view).setZmaxObject(((EuclidianSettings3D) evs).getZmaxObject());
		}
	}

	@Override
	protected void setMinMaxObjectsInSettings(EuclidianSettings evs) {
		super.setMinMaxObjectsInSettings(evs);
		if (evs instanceof EuclidianSettings3D) {
			((EuclidianSettings3D) evs)
					.setZminObject(((EuclidianView3D) view).getZminObject(), false);
			((EuclidianSettings3D) evs)
					.setZmaxObject(((EuclidianView3D) view).getZmaxObject(), false);
		}
	}
}
