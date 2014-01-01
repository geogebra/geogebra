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
public class ProbabilityTableW extends ProbabilityTable {
	
	

	/**
	 * default width of table
	 */
	public static int DEFAULT_WIDTH = 200;
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
	public ProbabilityTableW(App app,
            ProbabilityCalculatorViewW probCalc) {
	   this.app = app;
	   this.probCalc = probCalc;
	   this.probManager = probCalc.getProbManager();
	   
	   this.wrappedPanel = new FlowPanel();
	   this.wrappedPanel.addStyleName("ProbabilityTableW");
	   
	   statTable = new StatTableW(app);
	   
	   wrappedPanel.add(statTable);
	   
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
	
	public void setLabels() {
		setTable(distType, parms, xMin, xMax);
	}
	
	@Override
	public void setSelectionByRowValue(int lowValue, int highValue) {
		// TODO Auto-generated method stub

	}
	
	public FlowPanel getWrappedPanel() {
		return wrappedPanel;
	}
	
	public StatTableW getStatTable() {
		return statTable;
	}
}
