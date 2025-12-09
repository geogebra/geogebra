/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.view.algebra;

import java.util.ArrayList;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.SelectionManager;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.event.dom.client.ClickHandler;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.TreeItem;

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
	 * @param label
	 *            text
	 */
	public GroupNameLabel(SelectionManager selection, TreeItem parent,
			String label) {
		super(label);
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
