package geogebra.mobile.controller;

import java.util.ArrayList;

import geogebra.common.euclidian.EuclidianController;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;
import geogebra.mobile.gui.elements.GuiModel;
import geogebra.mobile.utils.ToolBarCommand;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;

public class MobileEuclidianController extends EuclidianController implements TouchStartHandler, TouchEndHandler, TouchMoveHandler, ClickHandler
{

	private GuiModel guiModel;
	ArrayList<GeoPointND> oldPoints = new ArrayList<GeoPointND>();
	private ToolBarCommand lastCmd;

	@Override
	public void setApplication(App app)
	{
		this.app = app;
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
	public void onClick(ClickEvent event)
	{
		this.guiModel.closeOptions();

		ToolBarCommand cmd = this.guiModel.getCommand();
		if (this.lastCmd != cmd)
		{
			this.oldPoints = new ArrayList<GeoPointND>();
			this.lastCmd = cmd;
		}

		boolean draw = true;

		switch (cmd)
		{

		// commands that need one point

		case NewPoint:
			createNewPoint(false, false);
			break;

		// commands that need two points

		case LineThroughTwoPoints:
		case SegmentBetweenTwoPoints:
			recordPoint();
			draw = this.oldPoints.size() == 2;
			break;
		default:
		}

		if (draw)
		{
			switch (cmd)
			{
			case LineThroughTwoPoints:
				this.kernel.Line(null, (GeoPoint) this.oldPoints.get(0), (GeoPoint) this.oldPoints.get(1));
				break;
			case SegmentBetweenTwoPoints:
				this.kernel.Segment(null, (GeoPoint) this.oldPoints.get(0), (GeoPoint) this.oldPoints.get(1));
				break;
			default:
			}//switch
			
			this.oldPoints = new ArrayList<GeoPointND>(); 
		}

		// TODO
		// Test
		// this.kernel.clearConstruction();
		//
		// this.xRW = 4.0;
		// this.yRW = 2.0;
		// createNewPoint(false, false);
		//
		// GeoPoint P = this.kernel.Point(null, 6.0, 4.5);
		// GeoPoint Q = this.kernel.Point(null, 11.0, 1.0);
		// this.kernel.Point("R", 7.0, -2.0);
		// this.kernel.Line("g", P, Q);
	}

	public void setGuiModel(GuiModel model)
	{
		this.guiModel = model;
	}

	protected void recordPoint()
	{
		this.oldPoints.add(createNewPoint(false, false));
	}
}
