package geogebra.gui.layout.panels;

import geogebra.common.main.App;
import geogebra.gui.GuiManagerD;
import geogebra.gui.layout.DockPanel;
import geogebra.gui.view.probcalculator.ProbabilityCalculatorViewD;
import geogebra.main.AppD;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

/**
 * Dock panel for the probability calculator.
 */
public class ProbabilityCalculatorDockPanel extends DockPanel {
	private static final long serialVersionUID = 1L;
	private AppD app;

	/**
	 * @param app
	 */
	public ProbabilityCalculatorDockPanel(AppD app) {
		super(App.VIEW_PROBABILITY_CALCULATOR, // view id
				"ProbabilityCalculator", // view title phrase
				"0", // toolbar string - move tool only, force!
				true, // style bar?
				8, // menu order
				'P' // menu shortcut
		);

		this.app = app;
		this.setOpenInFrame(true);

	}

	@Override
	protected JComponent loadComponent() {
		return ((ProbabilityCalculatorViewD) app.getGuiManager()
				.getProbabilityCalculator()).getWrapperPanel();
	}

	@Override
	protected JComponent loadStyleBar() {
		return ((GuiManagerD) app.getGuiManager()).getProbabilityCalculator()
				.getStyleBar().getWrappedToolbar();
	}

	@Override
	public ImageIcon getIcon() {
		return app.getMenuIcon("menu_view_probability.png");
	}

}
