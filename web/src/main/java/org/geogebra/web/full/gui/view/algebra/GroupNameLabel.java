/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.web.full.gui.view.algebra;

import java.util.ArrayList;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.SelectionManager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * InlineLabelTreeItem for the openable tree nodes of the algebra view
 *
 * File created by Arpad Fekete
 */

public class GroupNameLabel extends Label implements ClickHandler {

	private TreeItem par;
	private SelectionManager selection;

	/**
	 * @param selection
	 *            selection manager
	 * @param parent
	 *            parent
	 * @param strlab
	 *            text
	 */
	public GroupNameLabel(SelectionManager selection, TreeItem parent,
			String strlab) {
		super(strlab);
		this.selection = selection;
		par = parent;
		addClickHandler(this);

		this.setStyleName("elemHeadingName");
	}

	@Override
	public void onClick(ClickEvent evt) {
		Object uo;
		ArrayList<GeoElement> groupedGeos = new ArrayList<>();
		for (int i = 0; i < par.getChildCount(); i++) {
			uo = par.getChild(i).getUserObject();
			if (uo instanceof GeoElement) {
				groupedGeos.add((GeoElement) uo);
			}
		}
		par.setSelected(false);

		if (!evt.isControlKeyDown() && !evt.isShiftKeyDown()) {
			selection.clearSelectedGeos();
		}

		selection.addSelectedGeos(groupedGeos, true);
	}
}
