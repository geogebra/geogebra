package geogebra.web.gui.view.probcalculator;

import geogebra.common.gui.view.probcalculator.ChiSquarePanel;
import geogebra.common.gui.view.probcalculator.StatisticsCalculator;
import geogebra.common.main.App;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;

/**
 * @author gabor
 * 
 * ChiSquarePanel for Web
 *
 */
public class ChiSquarePanelW extends ChiSquarePanel implements ValueChangeHandler<Boolean>, ChangeHandler {

	private FlowPanel wrappedPanel;
	private FlowPanel lblRows;
	private FlowPanel lblColumns;
	private CheckBox ckExpected;
	private CheckBox ckChiDiff;
	private CheckBox ckRowPercent;
	private CheckBox ckColPercent;
	private ListBox cbRows;
	private ListBox cbColumns;
	private FlowPanel pnlCount;
	private ChiSquareCellW[][] cell;

	/**
	 * @param app
	 * @param statisticsCalculatorW
	 * 
	 * Constructs chisquarepanel for web
	 * 
	 */
	public ChiSquarePanelW(App app, StatisticsCalculator statcalc) {
	    super(app, statcalc);
	    createGUI();
	    
	    
    }

	private void createGUI() {
	    this.wrappedPanel = new FlowPanel();
	    
	    createGUIElements();
	    createCountPanel();
    }

	private void createCountPanel() {
	    if (pnlCount == null) {
	    	pnlCount = new FlowPanel();
	    }
	    
	    pnlCount.clear();
	    cell = new ChiSquareCellW[sc.rows + 2][sc.columns + 2];
	    //TODO: continue here
	}

	private void createGUIElements() {
	    lblRows = new FlowPanel();
	    lblColumns = new FlowPanel();
	    
	    ckExpected = new CheckBox();
	    ckChiDiff = new CheckBox();
	    ckRowPercent = new CheckBox();
	    ckColPercent = new CheckBox();
	    
	    ckExpected.addValueChangeHandler(this);
	    ckChiDiff.addValueChangeHandler(this);
	    ckRowPercent.addValueChangeHandler(this);
	    ckColPercent.addValueChangeHandler(this);
	    
	    
	    //drop down menu for rows/columns 2-12
	    
	    ArrayList<String> num = new ArrayList<String>();
	    
	    cbRows = new ListBox();
	    cbColumns = new ListBox();
	    
	    for (int i = 0; i < num.size(); i++) {
	    	num.add("" + (i + 2));
	    	cbRows.addItem(num.get(i));
	    	cbColumns.addItem(num.get(i));
	    }
	    
	    cbRows.setSelectedIndex(num.indexOf("" + sc.rows));
	    cbRows.addChangeHandler(this);
	    
	    cbColumns.setSelectedIndex(num.indexOf("" + sc.columns));
	    cbColumns.addChangeHandler(this);
	        
    }

	//@Override
    public void onValueChange(ValueChangeEvent<Boolean> event) {
	    // TODO Auto-generated method stub
	    
    }

	//@Override
    public void onChange(ChangeEvent event) {
	    // TODO Auto-generated method stub
	    
    }

}
