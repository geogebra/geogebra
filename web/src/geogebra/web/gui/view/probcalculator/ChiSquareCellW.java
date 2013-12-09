package geogebra.web.gui.view.probcalculator;


import geogebra.common.euclidian.event.KeyEvent;
import geogebra.common.euclidian.event.KeyHandler;
import geogebra.common.gui.view.probcalculator.ChiSquareCell;
import geogebra.common.gui.view.probcalculator.StatisticsCollection;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;

import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;


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
