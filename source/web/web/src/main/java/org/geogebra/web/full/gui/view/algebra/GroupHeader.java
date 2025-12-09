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

import org.geogebra.common.main.SelectionManager;
import org.gwtproject.safehtml.shared.SafeUri;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.TreeItem;

/**
 * AV group header
 */
public class GroupHeader extends FlowPanel {
	
	/**
	 * label
	 */
	protected GroupNameLabel il;

	/**
	 * +/- button
	 */
	protected OpenButton open;
	private String label;

	/**
	 * @param selection
	 *            selection manager
	 * @param parent
	 *            parent item
	 * @param label
	 *            localized name
	 * @param key
	 *            english name (for sorting)
	 * @param showUrl
	 *            image when open
	 * @param hiddenUrl
	 *            image when collapsed
	 */
	public GroupHeader(SelectionManager selection, TreeItem parent,
			String label, String key, SafeUri showUrl, SafeUri hiddenUrl) {
		
		this.setStyleName("elemHeading");
		this.label = key;
		
		add(open = new OpenButton(showUrl, hiddenUrl, parent, "algebraOpenButton"));
		add(il = new GroupNameLabel(selection, parent, label));
	}

	/**
	 * @param string
	 *            set group name
	 */
	public void setText(String string) {
		il.setText(string);
	}

	/**
	 * @param value
	 *            whether it's open
	 */
	public void setChecked(boolean value) {
		open.setChecked(value);
	}

	/**
	 * @return sort key
	 */
	public String getLabel() {
		return label;
	}
}
