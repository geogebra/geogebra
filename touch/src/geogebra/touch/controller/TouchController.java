package geogebra.touch.controller;

import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.Hits;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.Test;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;
import geogebra.common.util.debug.GeoGebraProfiler;
import geogebra.html5.euclidian.EuclidianViewWeb;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.euclidian.MobileMouseEvent;
import geogebra.touch.model.TouchModel;
import geogebra.touch.utils.OptionType;
import geogebra.touch.utils.Swipeables;
import geogebra.touch.utils.ToolBarCommand;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import com.google.gwt.user.client.Timer;

/**
 * Receives the events from the canvas and sends the orders to the kernel.
 * 
 * @author Thomas Krismayer
 * @see geogebra.common.euclidian.EuclidianController EuclidianController
 * 
 */
public class TouchController extends EuclidianController {
	/**
	 * Maximum time in ms between touch move was stopped (finger not moving, but
	 * still down) and event is handled
	 */
	private static final int DELAY_UNTIL_MOVE_FINISH = 150;
	private final TouchModel model;
	private GPoint origin;
	private boolean clicked = false, ignoreNextMove = false;
	private int waitingX;
	private int waitingY;
	private long lastMoveEvent;
	private boolean externalHandling;

	private final Timer repaintTimer = new Timer() {
		@Override
		public void run() {
			TouchController.this.touchMoveIfWaiting();
		}
	};

	public TouchController(final TouchModel touchModel, final App app) {
		super(app);
		this.model = touchModel;
		this.mode = -1;
	}

	public void reset() {
		this.collectingRepaints = 0;
	}

	/**
	 * prevent redraw
	 */
	@Override
	protected boolean createNewPoint(final Hits hits,
			final boolean onPathPossible, final boolean inRegionPossible,
			final boolean intersectPossible,
			final boolean doSingleHighlighting, final boolean complex) {
		return super.createNewPoint(hits, onPathPossible, inRegionPossible,
				intersectPossible, false, complex);
	}

	/**
	 * used to get events from the AlgebraView
	 * 
	 * @param hits
	 */
	public void handleEvent(final Hits hits) {
		this.model.getGuiModel().closeOptions();
		if (this.model.getCommand().equals(ToolBarCommand.Slider)) {
			// a slider cannot be placed without coordinates in the
			// EuclicianView
			return;
		}

		this.model.handleEvent(hits, null, null);
	}

	private void handleEvent(final int x, final int y) {
		// make sure undo-information is stored first

		OptionType activeOption = this.model.getGuiModel().getOptionTypeShown();
		this.model.getGuiModel().closeOptions();

		// do not handle event, if an optionPanel of the styleBar was still open
		if (activeOption != OptionType.None
				&& activeOption != OptionType.ToolBar) {

			return;
		}

		this.model.getGuiModel().closeOptions();

		final ToolBarCommand cmd = this.model.getCommand();

		super.mouseLoc = new GPoint(x, y);
		this.mode = this.model.getCommand().getMode();

		calcRWcoords();

		if (cmd == ToolBarCommand.Move_Mobile) {
			this.view.setHits(this.mouseLoc);
			if (this.view.getHits().size() == 0) {
				this.mode = EuclidianConstants.MODE_TRANSLATEVIEW;
				this.model.resetSelection();
			}
		}

		// draw the new point
		switchModeForMousePressed(new MobileMouseEvent(x, y));

		this.view.setHits(this.mouseLoc);
		final Hits hits = this.view.getHits();

		this.model.handleEvent(hits, new GPoint(x, y), new Point2D.Double(
				this.xRW, this.yRW));
	}

	/**
	 * save the selected elements in TouchModel instead of App; no repaint
	 * anymore!
	 * 
	 * @see EuclidianController#handleMovedElement(GeoElement, boolean)
	 */
	@Override
	protected void handleMousePressedForMoveMode(final AbstractEvent e,
			final boolean drag) {

		// move label?
		GeoElement geo = this.view.getLabelHit(this.mouseLoc);
		// Application.debug("label("+(System.currentTimeMillis()-t0)+")");
		if (geo != null) {
			this.moveMode = MOVE_LABEL;
			this.movedLabelGeoElement = geo;
			this.oldLoc.setLocation(geo.labelOffsetX, geo.labelOffsetY);
			this.startLoc = this.mouseLoc;
			this.view.setDragCursor();
			return;
		}

		// find and set movedGeoElement
		this.view.setHits(this.mouseLoc);
		final Hits viewHits = this.view.getHits();

		// make sure that eg slider takes precedence over a polygon (in the same
		// layer)
		viewHits.removePolygons();

		Hits moveableList;

		// if we just click (no drag) on eg an intersection, we want it selected
		// not a popup with just the lines in

		// now we want this behaviour always as
		// * there is no popup
		// * user might do eg click then arrow keys
		// * want drag with left button to work (eg tessellation)

		// consider intersection of 2 circles.
		// On drag, we want to be able to drag a circle
		// on click, we want to be able to select the intersection point
		if (drag) {
			moveableList = viewHits.getMoveableHits(this.view);
		} else {
			moveableList = viewHits;
		}

		final Hits hits = moveableList.getTopHits();

		final ArrayList<GeoElement> selGeos = this.model.getSelectedGeos();

		// if object was chosen before, take it now!
		if (selGeos.size() == 1 && !hits.isEmpty()
				&& hits.contains(selGeos.get(0))) {
			// object was chosen before: take it
			geo = selGeos.get(0);
		} else {
			// choose out of hits
			geo = chooseGeo(hits, false);

			if (!selGeos.contains(geo)) {
				this.model.resetSelection();
				this.model.select(geo);
			}
		}

		if (geo != null && !geo.isFixed()) {
			this.moveModeSelectionHandled = true;
		} else {
			// no geo clicked at
			this.moveMode = MOVE_NONE;
			resetMovedGeoPoint();
			return;
		}

		handleMovedElement(geo, selGeos.size() > 1);
	}

	@Override
	protected void initToolTipManager() {
	}

	/**
	 * use the selected Elements from TouchModel instead of the ones from App
	 * removes Polygons from the list
	 * 
	 * @see EuclidianController#moveMultipleObjects
	 */
	@Override
	protected void moveMultipleObjects(final boolean repaint) {
		this.translationVec.setX(this.xRW - getStartPointX());
		this.translationVec.setY(this.yRW - getStartPointY());
		setStartPointLocation(this.xRW, this.yRW);
		this.startLoc = this.mouseLoc;

		// remove Polygons, add their points instead
		final ArrayList<GeoElement> polygons = this.model
				.getAll(Test.GEOPOLYGON);
		for (final GeoElement geo : polygons) {
			for (final GeoPointND p : ((GeoPolygon) geo).getPoints()) {
				if (p instanceof GeoElement) {
					this.model.select(p);
				}
			}
			this.model.deselect(geo);
		}

		// move all selected geos
		GeoElement.moveObjects(
				removeParentsOfView(this.model.getSelectedGeos()),
				this.translationVec, new Coords(this.xRW, this.yRW, 0), null);

		if (repaint) {
			this.kernel.notifyRepaint();
		}
	}

	public void onTouchEnd(final int x, final int y) {
		touchMoveIfWaiting();

		this.clicked = false;
		if (Swipeables.isSwipeable(this.model.getCommand())
				&& this.model.getNumberOf(Test.GEOPOINT) == 1
				&& (Math.abs(this.origin.getX() - x) > 10 || Math
						.abs(this.origin.getY() - y) > 10)) {
			handleEvent(x, y);
			this.selectedPoints.clear();

			if (this.view.getPreviewDrawable() != null) {
				this.view.getPreviewDrawable().updatePreview();
			}
		}

		if ((this.model.getCommand() == ToolBarCommand.Move_Mobile || this.model
				.getCommand() == ToolBarCommand.Slider)
				&& this.view.getHits().size() > 0 && this.draggingOccured) {
			// just call storeUndoInfo() if an object was moved
			this.app.storeUndoInfo();
		}

		if (this.model.getCommand() == ToolBarCommand.RotateAroundPoint
				&& Math.abs(this.rotationLastAngle) > 0.001
				&& this.model.getTotalNumber() > 1) {
			this.app.storeUndoInfo();
			// deselect all elements except for the rotation-center
			while (this.model.getTotalNumber() > 1) {
				this.model.deselect(this.model.getSelectedGeos().get(1));
			}
			this.kernel.notifyRepaint();
		}

		this.draggingOccured = false;
		this.temporaryMode = false;

		if (this.model.getCommand() == ToolBarCommand.Pen
				|| this.model.getCommand() == ToolBarCommand.FreehandShape
				|| this.model.getCommand() == ToolBarCommand.DeleteObject
				|| this.model.getCommand() == ToolBarCommand.TranslateObjectByVector) {
			wrapMouseReleased(new MobileMouseEvent(x, y));
		}
	}

	public void onTouchMove(final int x, final int y) {
		if (this.externalHandling) {
			return;
		}
		if (this.ignoreNextMove) {
			this.ignoreNextMove = false;
			return;
		}

		if (this.mouseLoc != null && this.mouseLoc.getX() == x
				&& this.mouseLoc.getY() == y) {
			// no change of position
			return;
		}

		if (this.clicked
				&& (this.clicked = this.model.controlClicked())
				&& (this.model.getCommand() == ToolBarCommand.Move_Mobile
						|| this.model.getCommand() == ToolBarCommand.RotateAroundPoint
						|| this.model.getCommand() == ToolBarCommand.TranslateObjectByVector
						|| this.model.getCommand() == ToolBarCommand.Pen
						|| this.model.getCommand() == ToolBarCommand.FreehandShape
						|| this.model.getCommand() == ToolBarCommand.DeleteObject
						|| Swipeables.isSwipeable(this.model.getCommand()) || (this.model
						.getCommand() == ToolBarCommand.Slider && this.model
						.getTotalNumber() > 0))) {
			GeoGebraProfiler.drags++;
			final long time = System.currentTimeMillis();
			if (time < this.lastMoveEvent
					+ EuclidianViewWeb.DELAY_BETWEEN_MOVE_EVENTS) {
				final boolean wasWaiting = this.waitingX >= 0;
				this.waitingX = x;
				this.waitingY = y;
				GeoGebraProfiler.moveEventsIgnored++;
				if (!wasWaiting) {
					this.repaintTimer
							.schedule(TouchController.DELAY_UNTIL_MOVE_FINISH);
				}
				return;
			}

			touchMoveNow(x, y, time);
		}
	}

	public void onTouchStart(final int x, final int y) {
		reset();

		if (this.mode != this.model.getCommand().getMode()) {
			setMode(this.model.getCommand().getMode());
			switchPreviewableForInitNewMode(this.model.getCommand().getMode());
		}

		if (this.mode == ToolBarCommand.Move_Mobile.getMode()) {
			this.model.resetSelection();
		}

		this.origin = new GPoint(x, y);
		this.clicked = true;
		handleEvent(x, y);

		if (this.model.getCommand() == ToolBarCommand.RotateAroundPoint
				&& this.model.getTotalNumber() >= 2) {
			this.rotationCenter = (GeoPoint) this.model
					.getElement(Test.GEOPOINT);
			this.rotGeoElement = this.model.lastSelected();
			this.moveMode = EuclidianController.MOVE_ROTATE;
			this.rotationLastAngle = Math.atan2(this.yRW
					- this.rotationCenter.inhomY, this.xRW
					- this.rotationCenter.inhomX);
		}

		this.ignoreNextMove = true;
	}

	public void redefine(final GeoElement geo) {
		this.model.redefine(geo);
	}

	@Override
	protected void resetToolTipManager() {
	}

	@Override
	public void setKernel(final Kernel k) {
		this.kernel = k;
		this.tempNum = new MyDouble(this.kernel);
	}

	public void setView(final EuclidianView euclidianView) {
		this.view = euclidianView;
	}

	void touchMoveIfWaiting() {
		if (this.waitingX > 0) {
			GeoGebraProfiler.moveEventsIgnored--;
			touchMoveNow(this.waitingX, this.waitingY,
					System.currentTimeMillis());
		}
	}

	private void touchMoveNow(final int x, final int y, final long time) {
		this.waitingX = -1;
		this.waitingY = -1;
		this.lastMoveEvent = time;
		this.mouseLoc = new GPoint(this.origin.getX(), this.origin.getY());
		final MobileMouseEvent mEvent = new MobileMouseEvent(x, y);
		if (Swipeables.isSwipeable(this.model.getCommand())) {
			final GeoElement geo = this.model.getElement(Test.GEOPOINT);

			if (this.selectedPoints.isEmpty() && geo instanceof GeoPoint) {
				this.selectedPoints.add((GeoPoint) geo);
			}
			wrapMouseMoved(mEvent);
		} else {
			wrapMouseDragged(mEvent);
			this.origin = new GPoint(x, y);
		}

		GeoGebraProfiler.dragTime += System.currentTimeMillis() - time;
	}

	@Override
	protected boolean isMoveSliderExpected() {
		return this.model.getCommand() == ToolBarCommand.Slider;
	}

	@Override
	public void setZoomCenter(final double x, final double y) {
		if (x >= 0) {
			this.mouseLoc = new GPoint((int) x, (int) y);
			this.externalHandling = true;
		} else {
			this.externalHandling = false;
		}

	}

	public void onMouseExited() {
		this.clicked = false;
		TouchEntryPoint.getLookAndFeel().resetNativeHandlers();
	}
}
