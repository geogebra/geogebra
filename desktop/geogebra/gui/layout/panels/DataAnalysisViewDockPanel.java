package geogebra.gui.layout.panels;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.main.App;
import geogebra.gui.GuiManagerD;
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
				getDefaultToolbar(), 	// toolbar string
				true, 					// style bar?
				-1, 					// menu order
				'D' 					// menu shortcut
		);

		this.app = app;
		this.setOpenInFrame(true);
		this.setDialog(true);
	}

	
	@Override
	protected JComponent loadComponent() {
		return (JComponent) app.getGuiManager().getDataAnalysisView();
	}
	
	@Override
	protected JComponent loadStyleBar() {
		return ((GuiManagerD) app.getGuiManager()).getDataAnalysisView().getStyleBar();
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
