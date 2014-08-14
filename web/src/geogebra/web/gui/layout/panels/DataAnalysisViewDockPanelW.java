package geogebra.web.gui.layout.panels;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.main.App;
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
	
	/**
	 * default width of this panel
	 */
	public static final int DEFAULT_WIDTH = 880;
	private App app;

	/**
	 * @param app App
	 * Creates panel
	 */
	public DataAnalysisViewDockPanelW(App app) {
		super(App.VIEW_DATA_ANALYSIS, // view id
				"DataAnalysis", // view title phrase
				getDefaultToolbar(), // toolbar string
				true, // style bar?
				-1, // menu order
				'-' // menu shortcut
		);

		this.app = app;
		this.setOpenInFrame(true);
		this.setEmbeddedSize(DEFAULT_WIDTH);
		
	    this.app = app;
    }

	@Override
	protected Widget loadComponent() {
		DataAnalysisViewW da = ((DataAnalysisViewW)((GuiManagerW)app.getGuiManager()).getDataAnalysisView());
//		return da.getDummy();
		return da.getDataAnalysisViewComponent();		
	}

	@Override
	public void showView(boolean b) {
	}

	@Override
	protected Widget loadStyleBar() {
		DataAnalysisViewW da = ((DataAnalysisViewW)((GuiManagerW)app.getGuiManager()).getDataAnalysisView());
		return da.getStyleBar();
	}
	
	@Override
	public boolean isStyleBarEmpty(){
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
}
