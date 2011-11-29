package geogebra.gui.layout.panels;

import geogebra.common.main.AbstractApplication;
import geogebra.gui.layout.DockPanel;
import geogebra.main.Application;

import javax.swing.JComponent;

/**
 * Dock panel for the probability calculator.
 */
public class ProbabilityCalculatorDockPanel extends DockPanel {
	private static final long serialVersionUID = 1L;
	private Application app;
	
	/**
	 * @param app
	 */
	public ProbabilityCalculatorDockPanel(Application app) {
		super(
			AbstractApplication.VIEW_PROBABILITY_CALCULATOR, 		// view id
			"ProbabilityCalculator", 			// view title phrase
			null,								// toolbar string
			true,								// style bar?
			-1, 									// menu order
			'P'									// menu shortcut
		);
		
		this.app = app;
	}

	@Override
	protected JComponent loadComponent() {
		return app.getGuiManager().getProbabilityCalculator();
	}
	
	@Override
	protected JComponent loadStyleBar() {
		return app.getGuiManager().getProbabilityCalculator().getStyleBar();
	}
	
}
