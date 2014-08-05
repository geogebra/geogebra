package geogebra.web.gui.layout.panels;

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
	public static final int DEFAULT_WIDTH = 480;
	private App app;

	/**
	 * @param app App
	 * Creates panel
	 */
	public DataAnalysisViewDockPanelW(App app) {
		super(App.VIEW_DATA_ANALYSIS, // view id
				"DataAnalysis", // view title phrase
				null, // toolbar string
				false, // style bar?
				-1, // menu order
				'D' // menu shortcut
		);

		this.app = app;
		this.setOpenInFrame(true);
		this.setEmbeddedSize(DEFAULT_WIDTH);
		
	    this.app = app;
    }

	@Override
	protected Widget loadComponent() {
		App.debug("[DATA ANALYSIS] loadComponent");
		DataAnalysisViewW da = ((DataAnalysisViewW)((GuiManagerW)app.getGuiManager()).getDataAnalysisView());
		if (da == null) {
			App.debug("[DATA ANALYSIS] VIEW IS NULL");
			
		} else {
			App.debug("[DATA ANALYSIS] VIEW IS NOT NULL");
			
		}
		return da.getDataAnalysisViewComponent();
		
	}

	@Override
	public void showView(boolean b) {
	}

	@Override
	protected Widget loadStyleBar() {
		return null;
	}
	
	@Override
	public boolean isStyleBarEmpty(){
		return true;
	}

}
