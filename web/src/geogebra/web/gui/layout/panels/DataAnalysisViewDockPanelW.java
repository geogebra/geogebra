package geogebra.web.gui.layout.panels;

import geogebra.common.GeoGebraConstants;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.main.App;
import geogebra.html5.main.AppW;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.layout.DockPanelW;
import geogebra.web.gui.view.data.DataAnalysisViewW;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author Laszlo
 * 
 * DataAnalysis dockpanel for Web
 *
 */
public class DataAnalysisViewDockPanelW extends DockPanelW {
	
//	/**
//	 * default width of this panel
//	 */
//	public static final int DEFAULT_WIDTH = 480;
	private AppW app;

	/**
	 * @param app App
	 * Creates panel
	
	 *
	 */
	
	public DataAnalysisViewDockPanelW(AppW app) {
		super(App.VIEW_DATA_ANALYSIS, // view id
				"DataAnalysis", // view title phrase
				null,//getDefaultToolbar(), // toolbar string
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
		DataAnalysisViewW da = ((DataAnalysisViewW)((GuiManagerW)app.getGuiManager()).getDataAnalysisView());
		return da.getDataAnalysisViewComponent();
	}

	@Override
	public void showView(boolean b) {
	}

	@Override
	protected Widget loadStyleBar() {
		DataAnalysisViewW da = ((DataAnalysisViewW)((GuiManagerW)app.getGuiManager()).getDataAnalysisView());
		return  da.getStyleBar();
	}
	
	@Override
	public boolean isStyleBarEmpty(){
		return false;
	}

	private static String getDefaultToolbar() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(EuclidianConstants.MODE_MOVE);

		if (GeoGebraConstants.IS_PRE_RELEASE) {
			sb.append(" || ");
			sb.append(EuclidianConstants.MODE_SPREADSHEET_ONEVARSTATS);
			sb.append(" || ");
			sb.append(EuclidianConstants.MODE_SPREADSHEET_TWOVARSTATS);
			sb.append(" || ");
			sb.append(EuclidianConstants.MODE_SPREADSHEET_MULTIVARSTATS);
			

	}
		return sb.toString();
 }
} 
