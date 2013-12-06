package geogebra.web.gui.view.probcalculator;

import geogebra.common.gui.view.probcalculator.ProbabilityTable;
import geogebra.common.main.App;
import geogebra.web.gui.view.data.StatTableW;

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * @author gabor
 * 
 * ProbablityTable for Web
 *
 */
public class ProbablitiyTableW extends ProbabilityTable {

	private ProbabilityCalculatorViewW probCalc;
	private FlowPanel wrappedPanel;
	private StatTableW statTable;

	/**
	 * @param app Application
	 * @param probCalc ProbablityCalculator
	 */
	public ProbablitiyTableW(App app,
            ProbabilityCalculatorViewW probCalc) {
	   this.app = app;
	   this.probCalc = probCalc;
	   
	   this.wrappedPanel = new FlowPanel();
	   
	   statTable = new StatTableW(app);
    }

	@Override
	public void setSelectionByRowValue(int lowValue, int highValue) {
		// TODO Auto-generated method stub

	}

}
