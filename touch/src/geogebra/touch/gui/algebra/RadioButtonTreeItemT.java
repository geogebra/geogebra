package geogebra.touch.gui.algebra;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.safehtml.shared.SafeUri;

import geogebra.common.euclidian.Hits;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.html5.gui.view.algebra.RadioButtonTreeItem;
import geogebra.touch.controller.TouchController;

public class RadioButtonTreeItemT extends RadioButtonTreeItem
{

	private TouchController controller;
	
	public RadioButtonTreeItemT(GeoElement ge, SafeUri showUrl, SafeUri hiddenUrl, MouseDownHandler mdh, TouchController controller)
	{
		super(ge, showUrl, hiddenUrl, mdh);
		this.controller = controller;
	}

	@Override
	public void onClick(ClickEvent evt)
	{
		Hits hits = new Hits();
		hits.add(getGeo());
		this.controller.handleEvent(hits);
	}
	
	@Override
	public void onDoubleClick(DoubleClickEvent evt)
	{
	  // don't do anything
	}
	
	@Override
	public void onMouseMove(MouseMoveEvent evt)
	{
	  // don't do anything
	}
}
