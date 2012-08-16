package geogebra.mobile.controller;

import geogebra.common.euclidian.EuclidianController;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;

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
		// TODO
		// Test
		this.kernel.clearConstruction(); 
		
		this.xRW = 7.0; 
		this.yRW = 2.0; 
		this.createNewPoint(false, false); 
		
		GeoPoint P = this.kernel.Point(null, 8.5, 4.5);
		GeoPoint Q = this.kernel.Point(null, 5.0, 1.0);
		this.kernel.Point("R", 10.0, -2.0);
		this.kernel.Line("g", P, Q);
	}

}
