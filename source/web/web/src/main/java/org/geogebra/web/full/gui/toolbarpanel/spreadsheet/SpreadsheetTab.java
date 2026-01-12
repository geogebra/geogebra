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

package org.geogebra.web.full.gui.toolbarpanel.spreadsheet;

import javax.annotation.CheckForNull;

import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarPanel;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarTab;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.user.client.ui.FlowPanel;

/**
 * Tab of Spreadsheet View.
 */
public class SpreadsheetTab extends ToolbarTab {

	private final ToolbarPanel toolbarPanel;
	private @CheckForNull FlowPanel tabPanel;
	private @CheckForNull SpreadsheetPanel spreadsheetPanel;

	/**
	 * Constructor
	 * @param toolbarPanel - toolbar panel
	 */
	public SpreadsheetTab(ToolbarPanel toolbarPanel) {
		super(toolbarPanel);
		this.toolbarPanel = toolbarPanel;
	}

	private void createContent() {
		SpreadsheetPanel panel = new SpreadsheetPanel(toolbarPanel.getApp());
		FlowPanel wrappingPanel = new FlowPanel();
		wrappingPanel.addStyleName("spreadsheetTabPanel");

		if (isStyleBarAllowed()) {
			wrappingPanel.addStyleName("withStyleBar");
		}

		wrappingPanel.add(panel);
		add(wrappingPanel);

		wrappingPanel.getElement().getParentElement().getStyle().setHeight(100, Unit.PCT);
		spreadsheetPanel = panel;
		tabPanel = wrappingPanel;
	}

	private boolean isStyleBarAllowed() {
		return toolbarPanel.spreadsheetStyleBarAllowed();
	}

	@Override
	public void setLabels() {
		// fill
	}

	@Override
	public void onResize() {
		if (tabPanel != null) {
			Dom.toggleClass(tabPanel, "withStyleBar",
					!toolbarPanel.isHeadingVisible() && isStyleBarAllowed());
		}
		if (spreadsheetPanel != null) {
			spreadsheetPanel.onResize();
		}
	}

	@Override
	public void open() {
		toolbarPanel.openSpreadsheetView(true);
	}

	@Override
	public void close() {
		toolbarPanel.close(false);
	}

	@Override
	protected void onActive() {
		if (spreadsheetPanel == null) {
			createContent();
		}
		if (spreadsheetPanel != null) {
			spreadsheetPanel.getSpreadsheet().getController().handleOnViewAppear();
			spreadsheetPanel.requestFocus();
		}
	}

	@Override
	public DockPanelData.TabIds getID() {
		return DockPanelData.TabIds.SPREADSHEET;
	}

	@Override
	public MathKeyboardListener getKeyboardListener() {
		return spreadsheetPanel == null ? null : spreadsheetPanel.getKeyboardListener();
	}

	/**
	 * @return {@link SpreadsheetPanel}
	 */
	public SpreadsheetPanel getSpreadsheetPanel() {
		return spreadsheetPanel;
	}

}