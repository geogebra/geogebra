package geogebra.touch.controller;

import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.Hits;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.Test;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;
import geogebra.touch.gui.euclidian.EuclidianViewM;
import geogebra.touch.gui.euclidian.MobileMouseEvent;
import geogebra.touch.model.TouchModel;
import geogebra.touch.utils.Swipeables;
import geogebra.touch.utils.ToolBarCommand;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Receives the events from the canvas and sends the orders to the kernel.
 * 
 * @author Thomas Krismayer
 * @see geogebra.common.euclidian.EuclidianController EuclidianController
 * 
 */
public class TouchController extends EuclidianController
{
	private TouchModel model;
	private GPoint origin;
	private boolean clicked = false;
	private static final int DELAY_BETWEEN_MOVE_EVENTS = 30;
	private int waitingX,waitingY;
	private long lastMoveEvent;

	public TouchController(TouchModel touchModel, App app)
	{
		super(app);
		this.model = touchModel;
		this.mode = -1;
	}

	public void setView(EuclidianView euclidianView)
	{
		this.view = euclidianView;
	}

	@Override
	public void setKernel(Kernel k)
	{
		this.kernel = k;
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
		
		if (this.clicked && (this.clicked = this.model.controlClicked()) && this.model.getCommand() == ToolBarCommand.Move_Mobile)
		{	
			EuclidianViewM.drags++;
			long l = System.currentTimeMillis();
			if(l<this.lastMoveEvent + DELAY_BETWEEN_MOVE_EVENTS){
				waitingX = x;
				waitingY = y;
				EuclidianViewM.moveEventsIgnored++;
				return;
			}
			this.lastMoveEvent = l;
			this.mouseLoc = new GPoint(this.origin.getX(), this.origin.getY());
			MobileMouseEvent mEvent = new MobileMouseEvent(x, y);
			wrapMouseDragged(mEvent);
			this.origin = new GPoint(x, y);
			EuclidianViewM.dragTime+=System.currentTimeMillis()-l;
			
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

		if (this.model.getCommand().equals(ToolBarCommand.Move_Mobile) && this.view.getHits().size() > 0)
		{
			this.kernel.storeUndoInfo();
		}
	}

	public void onPinch(int x, int y, double scaleFactor)
	{
		super.mouseLoc = new GPoint(x, y);
		super.zoomInOut(scaleFactor,scaleFactor<EuclidianView.MOUSE_WHEEL_ZOOM_FACTOR?1:2);
	}

	public void handleEvent(Hits hits)
	{
		this.model.handleEvent(hits, null, null);
	}

	private void handleEvent(int x, int y)
	{
		this.model.getGuiModel().closeOptions(); // make sure undo-information is
																						 // stored first

		ToolBarCommand cmd = this.model.getCommand();

		super.mouseLoc = new GPoint(x, y);
		this.mode = this.model.getCommand().getMode();

		calcRWcoords();

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

		this.model.handleEvent(hits, new Point(x, y), new Point2D.Double(this.xRW, this.yRW));
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
	 * save the selected elements in TouchModel instead of App; no repaint
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
	 * use the selected Elements from TouchModel instead of the ones from App
	 * removes Polygons from the list
	 * 
	 * @see EuclidianController#moveMultipleObjects
	 */
	@Override
	protected void moveMultipleObjects(boolean repaint)
	{
		this.translationVec.setX(this.xRW - getStartPointX());
		this.translationVec.setY(this.yRW - getStartPointY());
		setStartPointLocation(this.xRW, this.yRW);
		this.startLoc = this.mouseLoc;

		// remove Polygons, add their points instead
		ArrayList<GeoElement> polygons = this.model.getAll(Test.GEOPOLYGON);
		for (GeoElement geo : polygons)
		{
			for (GeoPointND p : ((GeoPolygon) geo).getPoints())
			{
				if (p instanceof GeoElement)
				{
					this.model.select((GeoElement) p);
				}
			}
			this.model.deselect(geo);
		}

		// move all selected geos
		GeoElement.moveObjects(removeParentsOfView(this.model.getSelectedGeos()), this.translationVec, new Coords(this.xRW, this.yRW, 0), null);

		if (repaint)
		{
			this.kernel.notifyRepaint();
		}
	}

}
