package geogebra.mobile.controller;

import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.Hits;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;
import geogebra.mobile.gui.CommonResources;
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
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;

/**
 * 
 * @author Thomas Krismayer
 * 
 */
public class MobileEuclidianController extends EuclidianController implements
		TouchStartHandler, TouchEndHandler, TouchMoveHandler, MouseDownHandler,
		MouseMoveHandler, ClickHandler
{

	private GuiModel guiModel;
	ArrayList<GeoPointND> oldPoints = new ArrayList<GeoPointND>();
	private ToolBarCommand lastCmd;

	@Override
	public void setKernel(Kernel k)
	{
		// TODO
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
		this.mouseLoc = new GPoint(event.getX(), event.getY());		
		Hits hits = this.view.getHits();
		
		if(hits.getImageCount() == 0){
			this.mode = EuclidianConstants.MODE_TRANSLATEVIEW; 
		}
		else{
			this.mode = EuclidianConstants.MODE_MOVE; 
		}		
	}

	@Override
	public void onMouseMove(MouseMoveEvent event)
	{
		
	}

	
	@Override
	public void onClick(ClickEvent event)
	{
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
		this.view.setShowMouseCoords(false);

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
			}// switch

			this.oldPoints = new ArrayList<GeoPointND>();
		}
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

	public void setView(EuclidianView euclidianView)
	{
		this.view = euclidianView;
	}

	
	
}
