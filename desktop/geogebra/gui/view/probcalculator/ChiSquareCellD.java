package geogebra.gui.view.probcalculator;

import geogebra.common.gui.view.probcalculator.ChiSquareCell;
import geogebra.common.gui.view.probcalculator.StatisticsCollection;
import geogebra.common.main.GeoGebraColorConstants;
import geogebra.gui.inputfield.MyTextField;
import geogebra.gui.util.LayoutUtil;
import geogebra.main.AppD;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/*****************************************************************
 * 
 * Class ChiSquareCell: extended JPanel to hold cell components
 * 
 ***************************************************************** 
 */
public class ChiSquareCellD extends ChiSquareCell implements ActionListener,
		FocusListener {

	private static final long serialVersionUID = 1L;

	
	

	private JPanel wrappedPanel;
	
	private MyTextField fldInput;
	private JLabel[] label;

	/**
	 * Construct ChiSquareCell with given row, column
	 */
	public ChiSquareCellD(StatisticsCollection sc, int row, int column) {
		this(sc);
		this.row = row;
		this.column = column;
	}

	/**
	 * Construct ChiSquareCell
	 */
	public ChiSquareCellD(StatisticsCollection sc) {

		this.sc = sc;
		this.wrappedPanel = new JPanel();
		wrappedPanel.setOpaque(true);
		wrappedPanel.setLayout(new BoxLayout(this.wrappedPanel, BoxLayout.Y_AXIS));

		fldInput = new MyTextField((AppD) app);
		fldInput.addActionListener(this);
		fldInput.addFocusListener(this);
		wrappedPanel.add(LayoutUtil.flowPanelCenter(0, 0, 0, fldInput));

		label = new JLabel[5];
		for (int i = 0; i < label.length; i++) {
			label[i] = new JLabel();
			label[i].setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			wrappedPanel.add(LayoutUtil.flowPanelCenter(0, 0, 0, label[i]));
		}
		setColumns(4);
		setVisualStyle();
		hideAllLabels();

	}

	public void setColumns(int columns) {
		fldInput.setColumns(columns);

		// force a minimum width for margin cells
		wrappedPanel.add(Box.createHorizontalStrut(fldInput.getPreferredSize().width));

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
		wrappedPanel.setBorder(BorderFactory.createEmptyBorder());
	}

	/**
	 * @return input field
	 */
	public MyTextField getInputField() {
		return fldInput;
	}

	/**
	 * @return label array
	 */
	public JLabel[] getLabel() {
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
		wrappedPanel.setBackground(null);
		fldInput.setVisible(false);

		if (isMarginCell) {
			wrappedPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
			setLabelVisible(0, true);

		} else if (isHeaderCell) {
			wrappedPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
			fldInput.setVisible(true);
			fldInput.setBackground(geogebra.awt.GColorD
					.getAwtColor(GeoGebraColorConstants.TABLE_BACKGROUND_COLOR_HEADER));

		} else {
			fldInput.setVisible(true);
			wrappedPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
			fldInput.setBackground(geogebra.awt.GColorD
					.getAwtColor(GeoGebraColorConstants.WHITE));
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
		if (e.getSource() instanceof MyTextField) {
			((MyTextField) e.getSource()).selectAll();
		}
	}

	public void focusLost(FocusEvent e) {
		updateCellData();
		statCalc.updateResult();
	}

	public void actionPerformed(ActionEvent e) {
		updateCellData();
		statCalc.updateResult();

	}
	
	public JPanel getWrappedPanel() {
		return wrappedPanel;
	}
	
	

}
