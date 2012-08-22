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
import geogebra.mobile.euclidian.MouseEvent;
import geogebra.mobile.gui.elements.GuiModel;
import geogebra.mobile.utils.ToolBarCommand;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;

/**
 * receives the events from the canvas and sends the orders to the kernel
 * 
 * @author Thomas Krismayer
 * 
 */
public class MobileEuclidianController extends EuclidianController implements
		TouchStartHandler, TouchEndHandler, TouchMoveHandler, MouseDownHandler,
		MouseMoveHandler, MouseUpHandler, ClickHandler
{

	private GuiModel guiModel;
	ArrayList<GeoPointND> oldPoints = new ArrayList<GeoPointND>();
	private ToolBarCommand lastCmd;
	private GPoint origin;
	private boolean moving;

	public MobileEuclidianController()
	{
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

	@Override
	public void onTouchMove(TouchMoveEvent event)
	{
	}

	@Override
	public void onTouchEnd(TouchEndEvent event)
	{
	}

	@Override
	public void onTouchStart(TouchStartEvent event)
	{
	}

	@Override
	public void onMouseDown(MouseDownEvent event)
	{
		this.guiModel.closeOptions();

		this.origin = new GPoint(event.getX(), event.getY());

		this.mouseLoc = new GPoint(event.getX(), event.getY());
		this.view.setHits(this.mouseLoc);
		Hits hits = this.view.getHits();

		if (hits.isEmpty())
		{
			this.mode = EuclidianConstants.MODE_TRANSLATEVIEW;
		} else
		{
			this.mode = EuclidianConstants.MODE_MOVE;
		}
	}

	@Override
	public void onMouseMove(MouseMoveEvent event)
	{
		if (this.moving
				|| ((this.mode == EuclidianConstants.MODE_TRANSLATEVIEW || this.mode == EuclidianConstants.MODE_MOVE) && (Math
						.abs(event.getX() - this.origin.getX()) > 10 || Math
						.abs(event.getY() - this.origin.getY()) > 10)))
		{
			this.mouseLoc = new GPoint(this.origin.getX(), this.origin.getY());

			MouseEvent mEvent = new MouseEvent(event.getX(), event.getY());

			if (!this.moving)
			{
				this.moving = true;

				// get the mode and the object to move
				handleMousePressedForMoveMode(mEvent, false);
			}

			this.startPoint = new GPoint2D.Double(
					this.view.toRealWorldCoordX(this.origin.getX()),
					this.view.toRealWorldCoordY(this.origin.getY()));

			wrapMouseDragged(mEvent);

			this.origin = new GPoint(event.getX(), event.getY());

			removeSelection();
		}
	}

	@Override
	public void onMouseUp(MouseUpEvent event)
	{
		if (this.moving)
		{
			this.moving = false;
			this.mode = this.guiModel.getCommand().getMode();

			return;
		}

		this.guiModel.closeOptions();

		ToolBarCommand cmd = this.guiModel.getCommand();

		if (this.lastCmd != cmd)
		{
			this.oldPoints = new ArrayList<GeoPointND>();
			this.lastCmd = cmd;
		}

		boolean draw = false;

		this.mouseLoc = new GPoint(event.getX(), event.getY());
		this.mode = this.guiModel.getCommand().getMode();

		// draw the new point
		switchModeForMousePressed(null);

		removeSelection();

		Hits hits = this.view.getHits();

		switch (cmd)
		{

		// commands that need one point - nothing to do anymore

		case NewPoint:
			break;

		// commands that need two points

		case LineThroughTwoPoints:
		case SegmentBetweenTwoPoints:
			recordPoint(hits);
			draw = this.oldPoints.size() == 2;
			break;
		default:
		}

		if (draw)
		{
			switch (cmd)
			{
			case LineThroughTwoPoints:
				this.kernel.Line(null, (GeoPoint) this.oldPoints.get(0),
						(GeoPoint) this.oldPoints.get(1));
				break;
			case SegmentBetweenTwoPoints:
				this.kernel.Segment(null, (GeoPoint) this.oldPoints.get(0),
						(GeoPoint) this.oldPoints.get(1));
				break;
			default:
			}

			this.oldPoints = new ArrayList<GeoPointND>();
		}
	}

	@Override
	public void onClick(ClickEvent event)
	{
	}

	public void setGuiModel(GuiModel model)
	{
		this.guiModel = model;
	}

	protected void recordPoint(Hits hits)
	{
		hits.removePolygons();
		if (hits.containsGeoPoint())
		{
			GeoPoint point = getNearestPoint(hits);
			this.oldPoints.add(point);
		} else
		{
			this.oldPoints.add((GeoPointND) this.movedGeoElement);
		}
	}

	private GeoPoint getNearestPoint(Hits hits)
	{
		GeoPoint nearest = null;
		double distNearestSquare = 0.0;

		Iterator<GeoElement> iterator = hits.iterator();
		GeoElement e = iterator.next();

		while (e != null)
		{
			if (e instanceof GeoPointND)
			{
				double distanceSquare = Math.pow(
						(((GeoPoint) e).getX() - this.xRW), 2)
						+ Math.pow((((GeoPoint) e).getY() - this.yRW), 2);
				if (nearest == null || distanceSquare < distNearestSquare)
				{
					nearest = (GeoPoint) e;
					distNearestSquare = distanceSquare;
				}
			}
			e = iterator.hasNext() ? iterator.next() : null;
		}
		return nearest;
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
			this.app.setSelectedGeos(new ArrayList<GeoElement>());
			this.kernel.notifyRepaint();
		}
	}
}
