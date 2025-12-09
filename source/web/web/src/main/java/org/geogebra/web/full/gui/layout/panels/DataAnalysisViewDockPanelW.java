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

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.view.data.DataAnalysisViewW;
import org.geogebra.web.full.main.AppWFull;
import org.gwtproject.resources.client.ResourcePrototype;
import org.gwtproject.user.client.ui.Widget;

/**
 * @author Laszlo
 * 
 * DataAnalysis dockpanel for Web
 *
 */
public class DataAnalysisViewDockPanelW extends DockPanelW {

	/**
	 * @param app App
	 * Creates panel
	 *
	 */

	public DataAnalysisViewDockPanelW(AppWFull app) {
		super(App.VIEW_DATA_ANALYSIS, getDefaultToolbar(), true);
		this.app = app;
		setShowStyleBar(true);
		this.setEmbeddedSize(900);
	}

	@Override
	protected Widget loadComponent() {
		DataAnalysisViewW da = (DataAnalysisViewW) app
				.getGuiManager().getDataAnalysisView();
		return da.getDataAnalysisViewComponent();
	}

	@Override
	protected Widget loadStyleBar() {
		DataAnalysisViewW da = (DataAnalysisViewW) app
				.getGuiManager().getDataAnalysisView();
		return da.getStyleBar();
	}

	@Override
	public boolean isStyleBarEmpty() {
		return false;
	}

	private static String getDefaultToolbar() {
		StringBuilder sb = new StringBuilder();

		sb.append(EuclidianConstants.MODE_MOVE);
		sb.append(" || ");
		sb.append(EuclidianConstants.MODE_SPREADSHEET_ONEVARSTATS);
		sb.append(" || ");
		sb.append(EuclidianConstants.MODE_SPREADSHEET_TWOVARSTATS);
		sb.append(" || ");
		sb.append(EuclidianConstants.MODE_SPREADSHEET_MULTIVARSTATS);

		return sb.toString();
	}

	@Override
	protected ResourcePrototype getViewIcon() {
		return null;
	}
}
