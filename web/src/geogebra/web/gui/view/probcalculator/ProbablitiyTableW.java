package geogebra.web.gui.view.probcalculator;

import geogebra.common.gui.view.probcalculator.ProbabilityTable;
import geogebra.common.main.App;
import geogebra.common.main.settings.ProbabilityCalculatorSettings.DIST;
import geogebra.web.gui.view.data.StatTableW;

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * @author gabor
 * 
 * ProbablityTable for Web
 *
 */
public class ProbablitiyTableW extends ProbabilityTable {

	
	private FlowPanel wrappedPanel;
	private StatTableW statTable;
	private boolean isIniting;
	private DIST distType;
	private int xMin;
	private int xMax;
	private double[] parms;
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
	   
	   //blank table
	   setTable(null, null, 0, 10);
    }
	
	public void setTable(DIST distType, double[] parms, int xMin, int xMax){

		isIniting = true;

		this.distType = distType;
		this.xMin = xMin;
		this.xMax = xMax;
		this.parms = parms;
		setColumnNames();
		
		statTable.setStatTable(xMax - xMin + 1, null, 2, columnNames);

		//DefaultTableModel model = statTable.getModel();
		int x = xMin;
		int row = 0;

		// set the table model with the prob. values for this distribution
		double prob;
		while(x<=xMax){

			statTable.setValueAt("" + x, row, 0);
			if(distType != null ){
				prob = probManager.probability(x, parms, distType, isCumulative());
				statTable.setValueAt("" + probCalc.format(prob), row, 1);
			}
			x++;
			row++;
		}

		//updateFonts(((AppD) app).getPlainFont());
		
		// need to get focus so that the table will finish resizing columns (not sure why)
		statTable.getTable().getElement().focus();
		isIniting = false;
	}
	
	@Override
	public void setSelectionByRowValue(int lowValue, int highValue) {
		// TODO Auto-generated method stub

	}

}
