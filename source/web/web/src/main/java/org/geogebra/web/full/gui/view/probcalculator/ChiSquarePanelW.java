package org.geogebra.web.full.gui.view.probcalculator;

import org.geogebra.common.gui.view.probcalculator.ChiSquareCell;
import org.geogebra.common.gui.view.probcalculator.ChiSquarePanel;
import org.geogebra.common.gui.view.probcalculator.Procedure;
import org.geogebra.common.gui.view.probcalculator.StatisticsCalculator;
import org.geogebra.common.gui.view.probcalculator.StatisticsCollection;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.gui.components.ComponentCheckbox;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.ListBoxApi;
import org.gwtproject.dom.client.Document;
import org.gwtproject.event.dom.client.ChangeEvent;
import org.gwtproject.event.dom.client.ChangeHandler;
import org.gwtproject.event.dom.client.FocusEvent;
import org.gwtproject.event.dom.client.FocusHandler;
import org.gwtproject.event.dom.client.KeyCodes;
import org.gwtproject.event.dom.client.KeyUpEvent;
import org.gwtproject.event.dom.client.KeyUpHandler;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.ListBox;
import org.gwtproject.user.client.ui.TextBox;

/**
 * ChiSquarePanel for Web
 */
public class ChiSquarePanelW extends ChiSquarePanel
		implements ChangeHandler {

	private FlowPanel wrappedPanel;
	private Label lblRows;
	private Label lblColumns;
	private ComponentCheckbox ckExpected;
	private ComponentCheckbox ckChiDiff;
	private ComponentCheckbox ckRowPercent;
	private ComponentCheckbox ckColPercent;
	private ListBox cbRows;
	private ListBox cbColumns;
	private FlowPanel pnlCount;
	private ChiSquareCellW[][] cell;
	private FlowPanel pnlControl;

	/**
	 * Constructs chisquarepanel for web
	 * @param loc - application
	 * @param statcalc - calculator
	 */
	public ChiSquarePanelW(Localization loc, StatisticsCalculator statcalc) {
		super(loc, statcalc);
		createGUI();
		setLabels();
	}

	private void createGUI() {
		this.wrappedPanel = new FlowPanel();

		createGUIElements();
		createCountPanel();
		createControlPanel();
		updateCheckboxes();
		FlowPanel p = new FlowPanel();
		p.add(pnlCount);
		wrappedPanel.add(pnlControl);
		wrappedPanel.add(p);
	}

	private void createControlPanel() {
		pnlControl = new FlowPanel();
		pnlControl.setStyleName("pnlControl");
		pnlControl.add(lblRows);
		pnlControl.add(cbRows);
		pnlControl.getElement().appendChild(Document.get().createBRElement());
		pnlControl.add(lblColumns);
		pnlControl.add(cbColumns);
		FlowPanel lineBreak = new FlowPanel();
		lineBreak.setStyleName("lineBreak");
		pnlControl.add(lineBreak);
		pnlControl.add(ckRowPercent);
		pnlControl.add(ckColPercent);
		pnlControl.add(ckExpected);
		pnlControl.add(ckChiDiff);

	}

	private void createCountPanel() {
		if (pnlCount == null) {
			pnlCount = new FlowPanel();
			pnlCount.addStyleName("pnlCount");
		}

		pnlCount.clear();
		cell = new ChiSquareCellW[getSc().rows + 2][getSc().columns + 2];

		for (int r = 0; r < getSc().rows + 2; r++) {
			FlowPanel row = new FlowPanel();
			row.addStyleName("chirow");
			for (int c = 0; c < getSc().columns + 2; c++) {
				cell[r][c] = new ChiSquareCellW(getSc(), r, c);
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
		for (int r = 0; r < getSc().rows + 1; r++) {
			cell[r][0].setHeaderCell(true);
			cell[r][getSc().columns + 1].setMarginCell(true);
		}

		// set input cells
		for (int r = 1; r < getSc().rows + 1; r++) {
			for (int c = 1; c < getSc().columns + 1; c++) {
				cell[r][c].setInputCell(true);
			}
		}

		// clear other corners
		cell[getSc().rows + 1][0].hideAll();
		cell[0][getSc().columns + 1].hideAll();

		if (getStatCalc().getSelectedProcedure() == Procedure.GOF_TEST) {
			cell[0][1].setMarginCell(true);
			cell[0][2].setMarginCell(true);
		}
	}

	/**
	 * Update the UI
	 */
	public void updateGUI() {
		if (getStatCalc().getSelectedProcedure() == Procedure.CHISQ_TEST) {
			setChiSquaredControlsVisible(true);

		} else if (getStatCalc().getSelectedProcedure() == Procedure.GOF_TEST) {
			setChiSquaredControlsVisible(false);

			cbColumns.setSelectedIndex(0);
		}

		createCountPanel();
		setLabels();
	}

	private void setChiSquaredControlsVisible(boolean visible) {
		cbColumns.setVisible(visible);
		lblColumns.setVisible(visible);
		ckRowPercent.setVisible(visible);
		ckExpected.setVisible(visible);
		ckChiDiff.setVisible(visible);
	}

	/**
	 * Reset chi-squared data
	 */
	public void updateCollection() {
		getSc().setChiSqData(
				Integer.parseInt(cbRows.getValue(cbRows.getSelectedIndex())),
				getSc().getSelectedProcedure() == Procedure.GOF_TEST ? 2
						: Integer.parseInt(cbColumns
								.getValue(cbColumns.getSelectedIndex())));

	}

	/**
	 * Copy visibility flags
	 */
	public void updateShowFlags() {
		getSc().showExpected = ckExpected.isSelected();
		getSc().showDiff = ckChiDiff.isSelected();
		getSc().showRowPercent = ckRowPercent.isSelected();
		getSc().showColPercent = ckColPercent.isSelected();
	}

	private void updateCheckboxes() {
		ckExpected.setSelected(getSc().showExpected);
		ckChiDiff.setSelected(getSc().showDiff);
		ckRowPercent.setSelected(getSc().showRowPercent);
		ckColPercent.setSelected(getSc().showColPercent);
	}

	/**
	 * Update translation
	 */
	public void setLabels() {
		lblRows.setText(getMenu("Rows"));
		lblColumns.setText(getMenu("Columns"));
		ckExpected.setLabels();
		ckChiDiff.setLabels();
		ckRowPercent.setLabels();
		ckColPercent.setLabels();

		if (getStatCalc().getSelectedProcedure() == Procedure.GOF_TEST) {
			cell[0][1].setLabelText(0, getMenu("ObservedCount"));
			cell[0][2].setLabelText(0, getMenu("ExpectedCount"));
		}

	}

	private void createGUIElements() {
		lblRows = new Label();
		lblColumns = new Label();

		ckExpected = createCheckbox("ExpectedCount");
		ckChiDiff = createCheckbox("ChiSquaredContribution");
		ckRowPercent = createCheckbox("RowPercent");
		ckColPercent = createCheckbox("ColumnPercent");

		// drop down menu for rows/columns 2-12
		String[] num = new String[11];
		for (int i = 0; i < num.length; i++) {
			num[i] = "" + (i + 2);
		}

		cbRows = new ListBox();
		cbColumns = new ListBox();

		for (int i = 0; i < num.length; i++) {
			cbRows.addItem(num[i]);
			cbColumns.addItem(num[i]);
		}

		ListBoxApi.select(String.valueOf(getSc().rows), cbRows);
		cbRows.addChangeHandler(this);

		ListBoxApi.select(String.valueOf(getSc().columns), cbColumns);
		cbColumns.addChangeHandler(this);
	}

	private ComponentCheckbox createCheckbox(String ggbtrans) {
		return new ComponentCheckbox(loc, false, ggbtrans, ignore -> {
			updateShowFlags();
			updateVisibility();
		});
	}

	@Override
	protected ChiSquareCell getCell(int i, int j) {
		return cell[i][j];
	}

	@Override
	public void onChange(ChangeEvent event) {
		updateCollection();
		updateGUI();
	}

	/**
	 * Cell (input+output) of the table
	 *
	 */
	public class ChiSquareCellW extends ChiSquareCell
			implements FocusHandler, KeyUpHandler {

		private FlowPanel wrappedCellPanel;
		private AutoCompleteTextFieldW fldInput;
		private Label[] label;

		private Boolean isInputCell = false;

		/**
		 * Construct ChiSquareCell with given row, column
		 * 
		 * @param sc
		 *            data
		 * @param row
		 *            row
		 * @param column
		 *            column
		 */
		public ChiSquareCellW(StatisticsCollection sc, int row, int column) {
			this(sc);
			init(row, column);
		}

		@Override
		public void setValue(String string) {
			fldInput.setText(string);
		}

		/**
		 * Construct ChiSquareCell
		 * 
		 * @param sc
		 *            data
		 */
		public ChiSquareCellW(StatisticsCollection sc) {
			super(sc);
			this.wrappedCellPanel = new FlowPanel();
			this.wrappedCellPanel.addStyleName("ChiSquarePanelW");
			fldInput = new AutoCompleteTextFieldW(getStatCalc().getApp());
			fldInput.addKeyUpHandler(this);
			fldInput.addFocusHandler(this);
			StatisticsCalculatorW.addInsertHandler(fldInput, this::handleInput);
			wrappedCellPanel.add(fldInput);

			label = new Label[5];
			wrappedCellPanel.add(fldInput);
			for (int i = 0; i < label.length; i++) {
				label[i] = new Label();
				wrappedCellPanel.add(label[i]);
			}

			setVisualStyle();
			hideAllLabels();
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

		@Override
		public void setLabelText(int index, String s) {
			label[index].setText(s);
		}

		@Override
		public void setLabelVisible(int index, boolean isVisible) {
			label[index].setVisible(isVisible);
		}

		/**
		 * @param isInputCell
		 *            whether this contains input
		 */
		public void setInputCell(boolean isInputCell) {
			this.isInputCell = isInputCell;
			setVisualStyle();
		}

		@Override
		protected void setVisualStyle() {
			fldInput.setVisible(false);

			if (isMarginCell()) {
				setLabelVisible(0, true);

			} else if (isHeaderCell()) {
				fldInput.setVisible(true);
				wrappedCellPanel.addStyleName("headercell");
			} else if (isInputCell) {
				fldInput.setVisible(true);
				wrappedCellPanel.addStyleName("inputcell");
			} else {
				fldInput.setVisible(true);
				wrappedCellPanel.removeStyleName("headercell");
			}
		}

		private void updateCellData() {
			updateCellData(fldInput.getText());
		}

		/**
		 * @return UI component
		 */
		public FlowPanel getWrappedPanel() {
			return wrappedCellPanel;
		}

		@Override
		public void onKeyUp(KeyUpEvent e) {
			handleInput(e.getNativeKeyCode() == KeyCodes.KEY_ENTER);
		}

		private void handleInput(boolean isEnter) {
			updateCellData();
			getStatCalc().updateResult(isEnter);
			updateCellContent();
		}

		@Override
		public void onFocus(FocusEvent event) {
			if (event.getSource() instanceof TextBox) {
				((TextBox) event.getSource()).selectAll();
			}
		}
	}

	/**
	 * @return the wrapped panel
	 */
	public FlowPanel getWrappedPanel() {
		return wrappedPanel;
	}

}
