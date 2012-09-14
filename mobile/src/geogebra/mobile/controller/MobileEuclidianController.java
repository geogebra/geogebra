package geogebra.mobile.controller;

import geogebra.common.awt.GPoint;
import geogebra.common.awt.GPoint2D;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.Hits;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;
import geogebra.mobile.gui.euclidian.MobileMouseEvent;
import geogebra.mobile.model.GuiModel;
import geogebra.mobile.model.MobileModel;
import geogebra.mobile.utils.Swipeables;
import geogebra.mobile.utils.ToolBarCommand;

import java.util.ArrayList;

/**
 * Receives the events from the canvas and sends the orders to the kernel.
 * 
 * @author Thomas Krismayer
 * @see geogebra.common.euclidian.EuclidianController EuclidianController
 * 
 */
public class MobileEuclidianController extends EuclidianController
{
	private GuiModel guiModel;
	private MobileModel mobileModel;
	private GPoint origin;
	private boolean clicked = false;

	public MobileEuclidianController(MobileModel model, GuiModel guiModel)
	{
		this.mobileModel = model;
		this.guiModel = guiModel;
		this.mode = -1;
	}

	/**
	 * prevent redraw
	 */
	@Override
	protected boolean createNewPoint(Hits hits, boolean onPathPossible,
			boolean inRegionPossible, boolean intersectPossible,
			boolean doSingleHighlighting, boolean complex)
	{
		return super.createNewPoint(hits, onPathPossible, inRegionPossible,
				intersectPossible, false, complex);
	}

	/**
	 * save the selected elements in MobileModel instead of App no repaint
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
		} else
		{
			moveableList = viewHits;
		}

		Hits hits = moveableList.getTopHits();

		ArrayList<GeoElement> selGeos = this.mobileModel.getSelectedGeos();

		// if object was chosen before, take it now!
		if ((selGeos.size() == 1) && !hits.isEmpty()
				&& hits.contains(selGeos.get(0)))
		{
			// object was chosen before: take it
			geo = selGeos.get(0);
		} else
		{
			// choose out of hits
			geo = chooseGeo(hits, false);

			if (!selGeos.contains(geo))
			{
				this.mobileModel.resetSelection();
				this.mobileModel.select(geo);
			}
		}

		if ((geo != null) && !geo.isFixed())
		{
			this.moveModeSelectionHandled = true;
		} else
		{
			// no geo clicked at
			this.moveMode = MOVE_NONE;
			resetMovedGeoPoint();
			return;
		}

		handleMovedElement(geo, selGeos.size() > 1);
	}

	public void setView(EuclidianView euclidianView)
	{
		this.view = euclidianView;
	}

	@Override
	public void setKernel(Kernel k)
	{
		this.kernel = k;
		this.app = this.kernel.getApplication();
	}

	@Override
	public void setApplication(App app)
	{
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

	public void setGuiModel(GuiModel model)
	{
		this.guiModel = model;
	}

	public void onTouchStart(int x, int y)
	{
		this.guiModel.closeOptions();
		this.origin = new GPoint(x, y);
		this.clicked = true;
		handleEvent(x, y);
	}

	public void onTouchMove(int x, int y)
	{
		if (this.clicked
				&& this.guiModel.getCommand() == ToolBarCommand.Move_Mobile)
		{
			this.mouseLoc = new GPoint(this.origin.getX(), this.origin.getY());
			MobileMouseEvent mEvent = new MobileMouseEvent(x, y);

			this.startPoint = new GPoint2D.Double(
					this.view.toRealWorldCoordX(this.origin.getX()),
					this.view.toRealWorldCoordY(this.origin.getY()));
			wrapMouseDragged(mEvent);
			this.origin = new GPoint(x, y);
		}
	}

	public void onTouchEnd(int x, int y)
	{
		this.clicked = false;

		if (this.guiModel.getCommand() == ToolBarCommand.Move_Mobile)
		{
			System.out.println("\n" + (this.app.getSelectedGeos().size() > 0)
					+ "\n");
			if (this.movedGeoPoint != null)
			{
				this.mobileModel.select((GeoElement) this.movedGeoPoint);
			}
		}

		if (Swipeables.isSwipeable(this.guiModel.getCommand())
				&& this.mobileModel.getNumberOf(GeoPoint.class) == 1
				&& (Math.abs(this.origin.getX() - x) > 10 || Math
						.abs(this.origin.getY() - y) > 10))
		{
			handleEvent(x, y);
		}

		this.guiModel.updateStylingBar(this.mobileModel);
	}

	private void handleEvent(int x, int y)
	{
		ToolBarCommand cmd = this.guiModel.getCommand();

		this.mobileModel.checkCommand();

		super.mouseLoc = new GPoint(x, y);
		this.mode = this.guiModel.getCommand().getMode();

		if (cmd == ToolBarCommand.Move_Mobile)
		{
			this.view.setHits(this.mouseLoc);
			if (this.view.getHits().size() == 0)
			{
				this.mode = EuclidianConstants.MODE_TRANSLATEVIEW;
			}
		}

		// draw the new point
		switchModeForMousePressed(new MobileMouseEvent(x, y));

		this.view.setHits(this.mouseLoc);
		Hits hits = this.view.getHits();

		switch (cmd)
		{
		// other commands
		case DeleteObject:
			for (int i = 0; i < hits.size(); i++)
			{
				hits.get(i).remove();
			}
			break;
		case Select:
			for (GeoElement geo : hits)
			{
				this.mobileModel.select(geo);
			}
			break;

		// commands that only draw one point
		case NewPoint:
			this.mobileModel.select((GeoElement) super.movedGeoPoint);
			//$FALL-THROUGH$
		default:
			this.mobileModel.handleEvent(hits);
		}

		this.kernel.notifyRepaint();
	}

	public void onPinch(int x, int y, double scaleFactor)
	{
		super.mouseLoc = new GPoint(x, y);
		super.zoomInOut(false, scaleFactor < 1);
	}
}
