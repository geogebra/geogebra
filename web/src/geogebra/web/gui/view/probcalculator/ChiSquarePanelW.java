package geogebra.web.gui.view.probcalculator;

import geogebra.common.euclidian.event.KeyEvent;
import geogebra.common.euclidian.event.KeyHandler;
import geogebra.common.gui.view.probcalculator.ChiSquareCell;
import geogebra.common.gui.view.probcalculator.ChiSquarePanel;
import geogebra.common.gui.view.probcalculator.StatisticsCalculator;
import geogebra.common.gui.view.probcalculator.StatisticsCollection;
import geogebra.common.main.App;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
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
    
    public class ChiSquareCellW extends ChiSquareCell implements KeyHandler, FocusHandler {
    	
    	private FlowPanel wrappedPanel;
    	
    	private AutoCompleteTextFieldW fldInput;
    	private Label[] label;
    	
    	/**
    	 * Construct ChiSquareCell with given row, column
    	 */
    	public ChiSquareCellW(StatisticsCollection sc, int row, int column) {
    		this(sc);
    		this.row = row;
    		this.column = column;
    	}

    	/**
    	 * Construct ChiSquareCell
    	 */
    	public ChiSquareCellW(StatisticsCollection sc) {

    		this.sc = sc;
    		this.wrappedPanel = new FlowPanel();

    		fldInput = new AutoCompleteTextFieldW(app);
    		fldInput.addKeyHandler(this);
    		fldInput.addFocusHandler(this);
    		wrappedPanel.add(fldInput);

    		label = new Label[5];
    		for (int i = 0; i < label.length; i++) {
    			label[i] = new Label();
    			wrappedPanel.add(label[i]);
    		}
    		setColumns(4);
    		setVisualStyle();
    		hideAllLabels();

    	}

    	public void setColumns(int columns) {
    		fldInput.setColumns(columns);

    		// force a minimum width for margin cells
    		wrappedPanel.add(fldInput);

    	}

    	/**
    	 * hide all labels
    	 */
    	public void hideAllLabels() {
    		for (int i = 0; i < label.length; i++) {
    			label[i].setVisible(false);
    		}
    	}

    	/**
    	 * hide all
    	 */
    	public void hideAll() {
    		hideAllLabels();
    		fldInput.setVisible(false);
    	}

    	/**
    	 * @return input field
    	 */
    	public AutoCompleteTextFieldW getInputField() {
    		return fldInput;
    	}

    	/**
    	 * @return label array
    	 */
    	public Label[] getLabel() {
    		return label;
    	}

    	public void setLabelText(int index, String s) {
    		label[index].setText(s);
    	}

    	public void setLabelVisible(int index, boolean isVisible) {
    		label[index].setVisible(isVisible);
    	}

    	public void setMarginCell(boolean isMarginCell) {
    		this.isMarginCell = isMarginCell;
    		setVisualStyle();
    	}

    	public void setHeaderCell(boolean isHeaderCell) {
    		this.isHeaderCell = isHeaderCell;
    		setVisualStyle();
    	}

    	private void setVisualStyle() {
    		fldInput.setVisible(false);

    		if (isMarginCell) {
    			setLabelVisible(0, true);

    		} else if (isHeaderCell) {
    			
    			fldInput.setVisible(true);
    			//TODO CSSfldInput.setBackground(geogebra.awt.GColorD
    					//.getAwtColor(GeoGebraColorConstants.TABLE_BACKGROUND_COLOR_HEADER));

    		} else {
    			fldInput.setVisible(true);
    			//TODO csswrappedPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
    			//TODO cssfldInput.setBackground(geogebra.awt.GColorD
    					//.getAwtColor(GeoGebraColorConstants.WHITE));
    		}

    	}

    	public int getRow() {
    		return row;
    	}

    	public void setRow(int row) {
    		this.row = row;
    	}

    	public int getColumn() {
    		return column;
    	}

    	public void setColumn(int column) {
    		this.column = column;
    	}

    	private void updateCellData() {
    		sc.chiSquareData[row][column] = fldInput.getText();
    	}

    	public void focusGained(FocusEvent e) {
    		
    	}

    	public void focusLost(FocusEvent e) {
    		updateCellData();
    		statCalc.updateResult();
    	}
    	
    	public FlowPanel getWrappedPanel() {
    		return wrappedPanel;
    	}

        public void keyReleased(KeyEvent e) {
    		updateCellData();
    		statCalc.updateResult();
    	    
        }

        public void onFocus(FocusEvent event) {
    		if (event.getSource() instanceof AutoCompleteTextFieldW) { //possibly wont be good
    			((AutoCompleteTextFieldW) event.getSource()).selectAll();
    		}
        }

    }


}
