/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.web.gui.view.algebra;

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

	public InlineLabelTreeItem(TreeItem parent, String strlab) {
		super(strlab);
		par = parent;
		addClickHandler(this);
		addMouseOverHandler(this);
		addMouseOutHandler(this);
	}

	public void onClick(ClickEvent evt) {
		for (int i = 0; i < par.getChildCount(); i++)
			par.getChild(i).setSelected(true);
		par.setSelected(false);
	}

	public void onMouseOver(MouseOverEvent evt) {
		for (int i = 0; i < par.getChildCount(); i++)
			par.getChild(i).addStyleName("gwt-TreeItem-selected");
	}

	public void onMouseOut(MouseOutEvent evt) {
		for (int i = 0; i < par.getChildCount(); i++)
			//if (!par.getChild(i).isSelected())
				par.getChild(i).removeStyleName("gwt-TreeItem-selected");
	}
}
