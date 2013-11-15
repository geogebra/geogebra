package geogebra.web.gui.layout.panels;

import geogebra.common.main.App;
import geogebra.web.gui.layout.DockPanelW;
import geogebra.web.gui.view.probcalculator.ProbabilityCalculatorViewW;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author gabor
 * 
 * ProabilityCalculator dockpanel for Web
 *
 */
public class ProbabilityCalculatorDockPanelW extends DockPanelW {
	
	private App app;

	/**
	 * @param app App
	 * Creates panel
	 */
	public ProbabilityCalculatorDockPanelW(App app) {
		super(App.VIEW_PROBABILITY_CALCULATOR, // view id
				"ProbabilityCalculator", // view title phrase
				null, // toolbar string
				true, // style bar?
				-1, // menu order
				'P' // menu shortcut
		);

		this.app = app;
		this.setOpenInFrame(true);
		
	    this.app = app;
    }

	@Override
	protected Widget loadComponent() {
		return ((ProbabilityCalculatorViewW) app.getGuiManager().getProbabilityCalculator()).getWrapperPanel();
	}

	@Override
	public void showView(boolean b) {
		
	}

}
