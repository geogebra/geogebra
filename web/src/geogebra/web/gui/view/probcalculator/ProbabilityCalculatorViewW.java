package geogebra.web.gui.view.probcalculator;

import geogebra.common.gui.view.data.PlotSettings;
import geogebra.common.gui.view.probcalculator.ProbabilityCalcualtorView;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * @author gabor
 * 
 * ProbablityCalculatorView for web
 *
 */
public class ProbabilityCalculatorViewW extends ProbabilityCalcualtorView {

	/**
	 * @param app App
	 * creates new probabilitycalculatorView
	 */
	
	private FlowPanel wrappedPanel;
	
	/**
	 * @param app creates new probabilitycalculatorView
	 */
	public ProbabilityCalculatorViewW(AppW app) {
	   super(app);
	   
    }
	
	/**
	 * inits the gui of ProbablityCalculatorView
	 */
	public void initGUI() {
		this.wrappedPanel = new FlowPanel();	
	}
	
	/**
	 * @return the wrapper panel of this view
	 */
	public FlowPanel getWrapperPanel() {
		return wrappedPanel;
	}

	@Override
    public void updateAll() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void plotPanelUpdateSettings(PlotSettings settings) {
	    // TODO Auto-generated method stub
	    
    }

}
