package org.geogebra.web.full.gui.layout.panels;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.view.data.DataAnalysisViewW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.Widget;

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

	public DataAnalysisViewDockPanelW(AppW app) {
		super(App.VIEW_DATA_ANALYSIS, // view id
				"DataAnalysis", // view title phrase
				getDefaultToolbar(), // toolbar string
				true, // style bar?
				-1, // menu order
				'-' // menu shortcut
				);
		this.app = app;
		setShowStyleBar(true);
		this.setEmbeddedSize(900);
	}

	@Override
	protected Widget loadComponent() {
		DataAnalysisViewW da = ((DataAnalysisViewW) ((GuiManagerW) app
				.getGuiManager()).getDataAnalysisView());
		return da.getDataAnalysisViewComponent();
	}

	@Override
	protected Widget loadStyleBar() {
		DataAnalysisViewW da = ((DataAnalysisViewW) ((GuiManagerW) app
				.getGuiManager()).getDataAnalysisView());
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
