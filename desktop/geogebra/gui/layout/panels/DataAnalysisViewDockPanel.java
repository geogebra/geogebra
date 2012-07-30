package geogebra.gui.layout.panels;

import geogebra.common.main.App;
import geogebra.gui.layout.DockPanel;
import geogebra.main.AppD;

import javax.swing.JComponent;

/**
 * Dock panel for the probability calculator.
 */
public class DataAnalysisViewDockPanel extends DockPanel {
	private static final long serialVersionUID = 1L;
	private AppD app;
	
	/**
	 * @param app
	 */
	public DataAnalysisViewDockPanel(AppD app) {
		super(App.VIEW_DATA_ANALYSIS, 	// view id
				"DataAnalysis", 		// view title phrase
				null, 					// toolbar string
				true, 					// style bar?
				-1, 					// menu order
				'D' 					// menu shortcut
		);

		this.app = app;
	}

	
	@Override
	protected JComponent loadComponent() {
		return app.getGuiManager().getDataAnalysisView();
	}
	
	@Override
	protected JComponent loadStyleBar() {
		return app.getGuiManager().getDataAnalysisView().getStyleBar();
	}

}
