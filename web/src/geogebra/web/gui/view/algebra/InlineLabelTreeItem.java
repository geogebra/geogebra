/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.web.gui.view.algebra;

import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.web.main.AppW;

import java.util.Iterator;
import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * InlineLabelTreeItem for the openable tree nodes of the algebra view
 *
 * File created by Arpad Fekete
 */

public class InlineLabelTreeItem extends InlineLabel
	implements ClickHandler, MouseOverHandler, MouseOutHandler {

	TreeItem par;
	AppW application;

	public InlineLabelTreeItem(AppW app, TreeItem parent, String strlab) {
		super(strlab);
		this.application = app;
		par = parent;
		addClickHandler(this);
		addMouseOverHandler(this);
		addMouseOutHandler(this);
	}

	public void onClick(ClickEvent evt) {
		Object uo;
		ArrayList<GeoElement> groupedGeos = new ArrayList<GeoElement>();
		for (int i = 0; i < par.getChildCount(); i++) {
			//par.getChild(i).setSelected(true);
			uo = par.getChild(i).getUserObject();
			if (uo instanceof GeoElement)
				groupedGeos.add((GeoElement)uo);
		}
		par.setSelected(false);

		//if (!AppD.isControlDown(e) && !e.isShiftDown())
		if (!evt.isControlKeyDown() && !evt.isShiftKeyDown())
			application.clearSelectedGeos();

		if (groupedGeos!=null)
			application.addSelectedGeos(groupedGeos, true);
	}

	public void onMouseOver(MouseOverEvent evt) {
		for (int i = 0; i < par.getChildCount(); i++)
			if (par.getChild(i).getUserObject() instanceof GeoElement)
				//((GeoElement)par.getChild(i).getUserObject()).setHighlighted(true);
				par.getChild(i).getWidget().addStyleName("gwt-TreeItem-selected");
	}

	public void onMouseOut(MouseOutEvent evt) {
		for (int i = 0; i < par.getChildCount(); i++)
			if (par.getChild(i).getUserObject() instanceof GeoElement)
				//((GeoElement)par.getChild(i).getUserObject()).setHighlighted(false);
				par.getChild(i).getWidget().removeStyleName("gwt-TreeItem-selected");
	}
}
