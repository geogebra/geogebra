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

package org.geogebra.web.full.gui.layout.panels;

import org.geogebra.web.full.gui.layout.DockPanelW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Panel;
import org.gwtproject.user.client.ui.RequiresResize;

/**
 * Inner panel for dock panels with navigation.
 */
public class InnerPanel extends FlowPanel implements RequiresResize {

	private Panel content;
		private DockPanelW dock;

	/**
	 * @param dock
	 *            parent dock
	 * @param cpPanel
	 *            content
	 */
	public InnerPanel(DockPanelW dock, Panel cpPanel) {
		this.content = cpPanel;
		this.dock = dock;
		add(cpPanel);
	}

	@Override
	public void onResize() {
		dock.resizeContent(content);
	}

}