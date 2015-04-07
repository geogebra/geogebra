package org.geogebra.desktop.gui.layout.panels;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.geogebra.common.main.App;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.layout.DockPanel;
import org.geogebra.desktop.gui.view.probcalculator.ProbabilityCalculatorViewD;
import org.geogebra.desktop.main.AppD;

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
