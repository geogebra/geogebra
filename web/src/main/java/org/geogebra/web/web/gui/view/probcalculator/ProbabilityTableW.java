package org.geogebra.web.web.gui.view.probcalculator;

import org.geogebra.common.gui.view.probcalculator.ProbabilityTable;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings.DIST;
import org.geogebra.web.web.gui.view.data.StatTableW;
import org.geogebra.web.web.gui.view.data.StatTableW.MyTable;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * @author gabor
 * 
 * ProbablityTable for Web
 *
 */
public class ProbabilityTableW extends ProbabilityTable implements ClickHandler {
	
	

	/**
	 * default width of table
	 */
	public static int DEFAULT_WIDTH = 200;
	private FlowPanel wrappedPanel;
	private StatTableW statTable;
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
	   statTable.getTable().addClickHandler(this);
	   
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
		//statTable.getTable().getElement().focus();
		isIniting = false;
	}
	
	public void setLabels() {
		setTable(distType, parms, xMin, xMax);
	}
	
	@Override
	public void setSelectionByRowValue(int lowValue, int highValue) {
		//if(!probManager.isDiscrete(distType)) 
				//	return;

				//try {
					//statTable.getTable().getSelectionModel().removeListSelectionListener(this);

					int lowIndex = lowValue - xMin;
					if(lowIndex < 0) lowIndex = 0;
					int highIndex = highValue - xMin;
					//System.out.println("-------------");
					//System.out.println(lowIndex + " , " + highIndex);
					
					if(isCumulative()){
						statTable.getTable().changeSelection(highIndex,false,false);
					}
					else
					{
						statTable.getTable().changeSelection(lowIndex,false,false);
						statTable.getTable().changeSelection(highIndex,false,true);
					}
					//wrappedPanel.repaint();
					//statTable.getTable().getSelectionModel().addListSelectionListener(this);
				//} catch (Exception e) {
					// TODO Auto-generated catch block
				//	e.printStackTrace();
				//}

	}
	
	public FlowPanel getWrappedPanel() {
		return wrappedPanel;
	}
	
	public StatTableW getStatTable() {
		return statTable;
	}
	
	
	public void onClick(ClickEvent event) {
		MyTable table = statTable.getTable();
		
		table.handleSelection(event);

		int[] selRow = table.getSelectedRows();

		// exit if initing or nothing selected
		if(isIniting || selRow.length == 0) return;

		if(probCalc.getProbMode() == ProbabilityCalculatorViewW.PROB_INTERVAL){	
			//System.out.println(Arrays.toString(selectedRow));
			String lowStr = (String) table.getValueAt(selRow[0], 0);
			String highStr = (String) table.getValueAt(selRow[selRow.length-1], 0);
			int low = Integer.parseInt(lowStr);
			int high = Integer.parseInt(highStr);
			//System.out.println(low + " , " + high);
			((ProbabilityCalculatorViewW) probCalc).setInterval(low,high);
		}
		else if(probCalc.getProbMode() == ProbabilityCalculatorViewW.PROB_LEFT){
			String lowStr = (String) statTable.getTable().getValueAt(1, 0);
			String highStr = (String) statTable.getTable().getValueAt(selRow[selRow.length-1], 0);
			int low = Integer.parseInt(lowStr);
			int high = Integer.parseInt(highStr);
			//System.out.println(low + " , " + high);
			((ProbabilityCalculatorViewW) probCalc).setInterval(low,high);

			// adjust the selection
			//table.getSelectionModel().removeListSelectionListener(this);
			if(isCumulative()){
				// single row selected
				table.changeSelection(selRow[selRow.length-1], false, false);
			}
			else
			{
				// select multiple rows: first up to selected
				table.changeSelection(0, false, false);
				table.changeSelection(selRow[selRow.length-1], false, true);
				//table.scrollRectToVisible(table.getCellRect(selRow[selRow.length-1], 0, true));
			}
			//table.getSelectionModel().addListSelectionListener(this);
		}
		else if(probCalc.getProbMode() == ProbabilityCalculatorViewW.PROB_RIGHT){
			String lowStr = (String) statTable.getTable().getValueAt(selRow[0], 0);
			int maxRow = statTable.getTable().getRowCount()-1;
			String highStr = (String) statTable.getTable().getValueAt(maxRow, 0);
			int low = Integer.parseInt(lowStr);
			int high = Integer.parseInt(highStr);
			//System.out.println(low + " , " + high);
			((ProbabilityCalculatorViewW) probCalc).setInterval(low,high);

			//table.getSelectionModel().removeListSelectionListener(this);
			table.changeSelection(maxRow, false, false);
			table.changeSelection(selRow[0], false, true);
			//table.scrollRectToVisible(table.getCellRect(selRow[0], 0, true));
			//table.getSelectionModel().addListSelectionListener(this);
		}
	}
}
