package geogebra.mobile.controller;

import geogebra.common.awt.GPoint;
import geogebra.common.awt.GPoint2D;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.Hits;
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
			// this.mode = this.guiModel.getCommand().getMode();

			// object that was moved loses selection
			removeSelection();

			return;
		}

		if (Swipeables.isSwipeable(this.guiModel.getCommand())
				&& this.mobileModel.getNumberOf(GeoPoint.class) == 1
				&& (Math.abs(this.origin.getX() - x) > 10 || Math
						.abs(this.origin.getY() - y) > 10))
		{
			handleEvent(x, y);
		}
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

	private void removeSelection()
	{
		boolean repaint = this.app.getSelectedGeos().size() > 0
				|| this.movedGeoPoint != null;

		for (GeoElement g : this.app.getSelectedGeos())
		{
			g.setSelected(false);
			g.setHighlighted(false);
		}

		if (this.movedGeoPoint != null)
		{
			((GeoElement) this.movedGeoPoint).setSelected(false);
			((GeoElement) this.movedGeoPoint).setHighlighted(false);
		}

		if (repaint)
		{
			// includes repaint
			this.app.setSelectedGeos(new ArrayList<GeoElement>());
		}
	}

	public void onPinch(int x, int y, double scaleFactor)
	{
		super.mouseLoc = new GPoint(x, y);
		super.zoomInOut(false, scaleFactor < 1);
	}
}
