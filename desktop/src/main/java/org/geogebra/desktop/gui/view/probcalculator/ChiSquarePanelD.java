package org.geogebra.desktop.gui.view.probcalculator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.view.probcalculator.ChiSquareCell;
import org.geogebra.common.gui.view.probcalculator.ChiSquarePanel;
import org.geogebra.common.gui.view.probcalculator.Procedure;
import org.geogebra.common.gui.view.probcalculator.StatisticsCalculator;
import org.geogebra.common.gui.view.probcalculator.StatisticsCollection;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.main.Localization;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.gui.inputfield.MyTextFieldD;
import org.geogebra.desktop.gui.util.LayoutUtil;
import org.geogebra.desktop.main.AppD;

/**
 * Panel for Chi Square and Goodness of Fit Tests
 * 
 * @author G. Sturr
 * 
 */
public class ChiSquarePanelD extends ChiSquarePanel
		implements ActionListener {

	// ======================================
	// GUI components
	// ======================================
	private JPanel pnlCount, pnlControl;
	private JComboBox<String> cbRows, cbColumns;
	private ChiSquareCellD[][] cell;
	private JCheckBox ckExpected, ckChiDiff, ckRowPercent, ckColPercent;
	private JLabel lblRows, lblColumns;

	private JPanel wrappedPanel;

	/**
	 * @param loc
	 * @param statCalc
	 */
	public ChiSquarePanelD(Localization loc, StatisticsCalculator statCalc) {
		super(loc, statCalc);
		createGUI();
		setLabels();
	}

	public void setLabels() {
		lblRows.setText(getMenu("Rows"));
		lblColumns.setText(getMenu("Columns"));
		ckExpected.setText(getMenu("ExpectedCount"));
		ckChiDiff.setText(getMenu("ChiSquaredContribution"));
		ckRowPercent.setText(getMenu("RowPercent"));
		ckColPercent.setText(getMenu("ColumnPercent"));

		if (getStatCalc().getSelectedProcedure() == Procedure.GOF_TEST) {
			cell[0][1].setLabelText(0, getMenu("ObservedCount"));
			cell[0][2].setLabelText(0, getMenu("ExpectedCount"));
		}

	}

	private void createGUI() {

		this.wrappedPanel = new JPanel();

		createGUIElements();
		createCountPanel();
		createControlPanel();

		JPanel p = new JPanel(new BorderLayout());
		p.add(pnlCount, BorderLayout.NORTH);
		p.setBackground(null);

		wrappedPanel.setLayout(new BorderLayout());
		wrappedPanel.add(pnlControl, BorderLayout.NORTH);
		wrappedPanel.add(p, BorderLayout.CENTER);
		wrappedPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
	}

	private void createGUIElements() {

		lblRows = new JLabel();
		lblColumns = new JLabel();

		ckExpected = new JCheckBox();
		ckChiDiff = new JCheckBox();
		ckRowPercent = new JCheckBox();
		ckColPercent = new JCheckBox();

		ckExpected.addActionListener(this);
		ckChiDiff.addActionListener(this);
		ckRowPercent.addActionListener(this);
		ckColPercent.addActionListener(this);

		// drop down menu for rows/columns 2-12
		String[] num = new String[11];
		for (int i = 0; i < num.length; i++) {
			num[i] = "" + (i + 2);
		}
		cbRows = new JComboBox<>(num);
		cbRows.setSelectedItem("" + getSc().rows);
		cbRows.addActionListener(this);
		cbRows.setMaximumRowCount(12);

		cbColumns = new JComboBox<>(num);
		cbColumns.setSelectedItem("" + getSc().columns);
		cbColumns.addActionListener(this);
		cbColumns.setMaximumRowCount(12);

	}

	private void createControlPanel() {

		pnlControl = new JPanel();
		pnlControl.setLayout(new BoxLayout(pnlControl, BoxLayout.Y_AXIS));
		pnlControl.add(wrappedPanel.add(
				LayoutUtil.flowPanel(lblRows, cbRows, lblColumns, cbColumns)));
		pnlControl.add(wrappedPanel.add(LayoutUtil.flowPanel(ckRowPercent,
				ckColPercent, ckExpected, ckChiDiff)));
	}

	private void createCountPanel() {

		if (pnlCount == null) {
			pnlCount = new JPanel();
		}
		pnlCount.removeAll();
		pnlCount.setLayout(new BoxLayout(pnlCount, BoxLayout.Y_AXIS));

		cell = new ChiSquareCellD[getSc().rows + 2][getSc().columns + 2];

		// create grid of cells
		for (int r = 0; r < getSc().rows + 2; r++) {
			JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));
			for (int c = 0; c < getSc().columns + 2; c++) {
				cell[r][c] = new ChiSquareCellD(getSc(), r, c);

				// wider fields for the GOF test
				if (getStatCalc().getSelectedProcedure() == Procedure.GOF_TEST) {
					cell[r][c].setColumns(10);
				}

				row.add(cell[r][c].getWrappedPanel());
			}
			pnlCount.add(row);
		}

		// upper-right corner cell
		cell[0][0].setMarginCell(true);

		// column headers and margins
		for (int c = 1; c < getSc().columns + 2; c++) {
			cell[0][c].setHeaderCell(true);
			cell[getSc().rows + 1][c].setMarginCell(true);
		}

		// row headers and margins
		for (int r = 1; r < getSc().rows + 1; r++) {
			cell[r][0].setHeaderCell(true);
			cell[r][getSc().columns + 1].setMarginCell(true);
		}

		// clear other corners
		cell[getSc().rows + 1][0].hideAll();
		cell[0][getSc().columns + 1].hideAll();

		if (getStatCalc().getSelectedProcedure() == Procedure.GOF_TEST) {
			cell[0][1].setMarginCell(true);
			cell[0][2].setMarginCell(true);
		}

	}

	// ==========================================
	// Event handlers
	// ==========================================

	public void updateGUI() {

		if (getStatCalc().getSelectedProcedure() == Procedure.CHISQ_TEST) {
			cbColumns.setVisible(true);
			lblColumns.setVisible(true);
			ckRowPercent.setVisible(true);
			ckExpected.setVisible(true);
			ckChiDiff.setVisible(true);

		} else if (getStatCalc().getSelectedProcedure() == Procedure.GOF_TEST) {
			cbColumns.setVisible(false);
			lblColumns.setVisible(false);
			ckRowPercent.setVisible(false);
			ckExpected.setVisible(false);
			ckChiDiff.setVisible(false);

			// only two columns for GOF
			cbColumns.removeActionListener(this);
			cbColumns.setSelectedItem("2");
			cbColumns.addActionListener(this);

		}

		createCountPanel();
		setLabels();
		wrappedPanel.revalidate();
		wrappedPanel.repaint();
	}

	public void updateCollection() {
		getSc().setChiSqData(
				Integer.parseInt((String) cbRows.getSelectedItem()),
				getSc().getSelectedProcedure() == Procedure.GOF_TEST ? 2
						: Integer.parseInt(
								(String) cbColumns.getSelectedItem()));
	}

	public void updateShowFlags() {
		getSc().showExpected = ckExpected.isSelected();
		getSc().showDiff = ckChiDiff.isSelected();
		getSc().showRowPercent = ckRowPercent.isSelected();
		getSc().showColPercent = ckColPercent.isSelected();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source == cbRows || source == cbColumns) {
			updateCollection();
			updateGUI();
		}

		if (source == ckExpected || source == ckChiDiff
				|| source == ckRowPercent || source == ckColPercent) {
			updateShowFlags();
			updateVisibility();
		}

	}

	/*****************************************************************
	 * 
	 * Class ChiSquareCell: extended JPanel to hold cell components
	 * 
	 ***************************************************************** 
	 */
	public class ChiSquareCellD extends ChiSquareCell
			implements ActionListener, FocusListener {


		private JPanel wrappedCellPanel;

		private MyTextFieldD fldInput;
		private JLabel[] label;

		/**
		 * Construct ChiSquareCell with given row, column
		 */
		public ChiSquareCellD(StatisticsCollection sc, int row, int column) {
			this(sc);
			init(row, column);
		}

		@Override
		public void setValue(String string) {
			fldInput.setText(string);
		}

		/**
		 * Construct ChiSquareCell
		 */
		public ChiSquareCellD(StatisticsCollection sc) {

			super(sc);
			this.wrappedCellPanel = new JPanel();
			wrappedCellPanel.setOpaque(true);
			wrappedCellPanel.setLayout(
					new BoxLayout(this.wrappedCellPanel, BoxLayout.Y_AXIS));

			fldInput = new MyTextFieldD((AppD) statCalc.getApp());
			fldInput.addActionListener(this);
			fldInput.addFocusListener(this);
			wrappedCellPanel.add(LayoutUtil.flowPanelCenter(0, 0, 0, fldInput));

			label = new JLabel[5];
			for (int i = 0; i < label.length; i++) {
				label[i] = new JLabel();
				label[i].setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
				wrappedCellPanel.add(LayoutUtil.flowPanelCenter(0, 0, 0, label[i]));
			}
			setColumns(4);
			setVisualStyle();
			hideAllLabels();
		}

		public void setColumns(int columns) {
			fldInput.setColumns(columns);

			// force a minimum width for margin cells
			wrappedCellPanel.add(Box
					.createHorizontalStrut(fldInput.getPreferredSize().width));

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
			wrappedCellPanel.setBorder(BorderFactory.createEmptyBorder());
		}

		/**
		 * @return input field
		 */
		public MyTextFieldD getInputField() {
			return fldInput;
		}

		/**
		 * @return label array
		 */
		public JLabel[] getLabel() {
			return label;
		}

		@Override
		public void setLabelText(int index, String s) {
			label[index].setText(s);
		}

		@Override
		public void setLabelVisible(int index, boolean isVisible) {
			label[index].setVisible(isVisible);
		}

		@Override
		protected void setVisualStyle() {
			wrappedCellPanel.setBackground(null);
			fldInput.setVisible(false);

			if (isMarginCell()) {
				wrappedCellPanel
						.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
				setLabelVisible(0, true);

			} else if (isHeaderCell()) {
				wrappedCellPanel
						.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
				fldInput.setVisible(true);
				fldInput.setBackground(GColorD.getAwtColor(
						GeoGebraColorConstants.TABLE_BACKGROUND_COLOR_HEADER));

			} else {
				fldInput.setVisible(true);
				wrappedCellPanel.setBorder(
						BorderFactory.createLineBorder(Color.GRAY, 1));
				fldInput.setBackground(GColorD.getAwtColor(GColor.WHITE));
			}

		}

		private void updateCellData() {
			updateCellData(fldInput.getText());
		}

		@Override
		public void focusGained(FocusEvent e) {
			if (e.getSource() instanceof MyTextFieldD) {
				((MyTextFieldD) e.getSource()).selectAll();
			}
		}

		@Override
		public void focusLost(FocusEvent e) {
			updateCellData();
			getStatCalc().updateResult(true);
			updateCellContent();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			updateCellData();
			getStatCalc().updateResult(true);
			updateCellContent();
		}

		public JPanel getWrappedPanel() {
			return wrappedCellPanel;
		}

	}

	/**
	 * @return the GUI wrapper panel
	 */
	public JPanel getWrappedPanel() {
		return wrappedPanel;
	}

	@Override
	protected ChiSquareCell getCell(int i, int j) {
		return cell[i][j];
	}

}
