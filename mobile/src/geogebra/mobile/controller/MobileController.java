package geogebra.mobile.controller;

import geogebra.common.awt.GPoint;
import geogebra.common.awt.GPoint2D;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.Hits;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.Test;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;
import geogebra.mobile.gui.euclidian.MobileMouseEvent;
import geogebra.mobile.model.MobileModel;
import geogebra.mobile.utils.Swipeables;
import geogebra.mobile.utils.ToolBarCommand;

import java.awt.Point;
import java.util.ArrayList;

/**
 * Receives the events from the canvas and sends the orders to the kernel.
 * 
 * @author Thomas Krismayer
 * @see geogebra.common.euclidian.EuclidianController EuclidianController
 * 
 */
public class MobileController extends EuclidianController
{
	private MobileModel model;
	private GPoint origin;
	private boolean clicked = false;

	public MobileController(MobileModel mobileModel)
	{
		this.model = mobileModel;
		this.mode = -1;
	}

	public void setView(EuclidianView euclidianView)
	{
		this.view = euclidianView;
	}

	/**
	 * sets kernel AND app (kernel.getApplication)
	 */
	@Override
	public void setKernel(Kernel k)
	{
		this.kernel = k;
		this.app = this.kernel.getApplication();
	}

	@Override
	public void setApplication(App application)
	{
		this.app = application;
	}

	@Override
	protected void initToolTipManager()
	{
	}

	@Override
	protected GeoElement[] createCircle2ForPoints3D(GeoPointND p0, GeoPointND p1)
	{
		return null;
	}

	@Override
	protected void resetToolTipManager()
	{
	}

	public void onTouchStart(int x, int y)
	{
		this.origin = new GPoint(x, y);
		this.clicked = true;
		handleEvent(x, y);
	}

	public void onTouchMove(int x, int y)
	{
		if (this.clicked && this.model.getCommand() == ToolBarCommand.Move_Mobile)
		{
			this.mouseLoc = new GPoint(this.origin.getX(), this.origin.getY());
			MobileMouseEvent mEvent = new MobileMouseEvent(x, y);

			this.startPoint = new GPoint2D.Double(this.view.toRealWorldCoordX(this.origin.getX()), this.view.toRealWorldCoordY(this.origin.getY()));
			wrapMouseDragged(mEvent);
			this.origin = new GPoint(x, y);
		}
	}

	public void onTouchEnd(int x, int y)
	{
		this.clicked = false;

		if (Swipeables.isSwipeable(this.model.getCommand()) && this.model.getNumberOf(Test.GEOPOINT) == 1
		    && (Math.abs(this.origin.getX() - x) > 10 || Math.abs(this.origin.getY() - y) > 10))
		{
			handleEvent(x, y);
		}
	}

	public void onPinch(int x, int y, double scaleFactor)
	{
		// TODO Deactivate other events, while zoom is in progress (moving,
		// placing objects etc.)
		super.mouseLoc = new GPoint(x, y);
		// scaleFactor > 1 because scaleFactor is not > 1 when fingers are moved
		// apart
		super.zoomInOut(true, scaleFactor > 1);
	}

	public void handleEvent(Hits hits)
	{
		this.model.handleEvent(hits, null);
	}

	private void handleEvent(int x, int y)
	{
		ToolBarCommand cmd = this.model.getCommand();

		super.mouseLoc = new GPoint(x, y);
		this.mode = this.model.getCommand().getMode();

		if (cmd == ToolBarCommand.Move_Mobile)
		{
			this.view.setHits(this.mouseLoc);
			if (this.view.getHits().size() == 0)
			{
				this.mode = EuclidianConstants.MODE_TRANSLATEVIEW;
				this.model.resetSelection();
			}
		}

		// draw the new point
		switchModeForMousePressed(new MobileMouseEvent(x, y));

		this.view.setHits(this.mouseLoc);
		Hits hits = this.view.getHits();

		this.model.handleEvent(hits, new Point(x, y));
	}

	/**
	 * prevent redraw
	 */
	@Override
	protected boolean createNewPoint(Hits hits, boolean onPathPossible, boolean inRegionPossible, boolean intersectPossible,
	    boolean doSingleHighlighting, boolean complex)
	{
		return super.createNewPoint(hits, onPathPossible, inRegionPossible, intersectPossible, false, complex);
	}

	/**
	 * save the selected elements in MobileModel instead of App; no repaint
	 * anymore!
	 * 
	 * @see EuclidianController#handleMovedElement(GeoElement, boolean)
	 */
	@Override
	protected void handleMousePressedForMoveMode(AbstractEvent e, boolean drag)
	{

		// move label?
		GeoElement geo = this.view.getLabelHit(this.mouseLoc);
		// Application.debug("label("+(System.currentTimeMillis()-t0)+")");
		if (geo != null)
		{
			this.moveMode = MOVE_LABEL;
			this.movedLabelGeoElement = geo;
			this.oldLoc.setLocation(geo.labelOffsetX, geo.labelOffsetY);
			this.startLoc = this.mouseLoc;
			this.view.setDragCursor();
			return;
		}

		// find and set movedGeoElement
		this.view.setHits(this.mouseLoc);
		Hits viewHits = this.view.getHits();

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
		if (drag)
		{
			moveableList = viewHits.getMoveableHits(this.view);
		}
		else
		{
			moveableList = viewHits;
		}

		Hits hits = moveableList.getTopHits();

		ArrayList<GeoElement> selGeos = this.model.getSelectedGeos();

		// if object was chosen before, take it now!
		if ((selGeos.size() == 1) && !hits.isEmpty() && hits.contains(selGeos.get(0)))
		{
			// object was chosen before: take it
			geo = selGeos.get(0);
		}
		else
		{
			// choose out of hits
			geo = chooseGeo(hits, false);

			if (!selGeos.contains(geo))
			{
				this.model.resetSelection();
				this.model.select(geo);
			}
		}

		if ((geo != null) && !geo.isFixed())
		{
			this.moveModeSelectionHandled = true;
		}
		else
		{
			// no geo clicked at
			this.moveMode = MOVE_NONE;
			resetMovedGeoPoint();
			return;
		}

		handleMovedElement(geo, selGeos.size() > 1);
	}

	/**
	 * use the selected Elements from MobileModel instead of the ones from App
	 * 
	 * @see EuclidianController#moveMultipleObjects
	 */
	@Override
	protected void moveMultipleObjects(boolean repaint)
	{
		this.translationVec.setX(this.xRW - this.startPoint.x);
		this.translationVec.setY(this.yRW - this.startPoint.y);
		this.startPoint.setLocation(this.xRW, this.yRW);
		this.startLoc = this.mouseLoc;

		// move all selected geos
		GeoElement.moveObjects(removeParentsOfView(this.model.getSelectedGeos()), this.translationVec, new Coords(this.xRW, this.yRW, 0), null);

		if (repaint)
		{
			this.kernel.notifyRepaint();
		}
	}

}
