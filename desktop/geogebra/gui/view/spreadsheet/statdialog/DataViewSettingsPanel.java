package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.common.gui.view.spreadsheet.CellRange;
import geogebra.gui.dialog.options.OptionsUtil;
import geogebra.gui.inputfield.AutoCompleteTextFieldD;
import geogebra.gui.view.spreadsheet.CellRangeProcessor;
import geogebra.gui.view.spreadsheet.MyTableD;
import geogebra.main.AppD;
import geogebra.util.Validation;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class DataViewSettingsPanel extends JPanel implements ActionListener,
		FocusListener {

	private AppD app;
	private StatDialog statDialog;

	private JRadioButton btnSourceFrequencyValue, btnSourceValue,
			btnSourceFrequencyClass, btnNumeric, btnCategorical;
	private JButton btnOK, btnCancel;

	private JPanel dataTypePanel, sourceTypePanel;
	private boolean isIniting;
	private StatTable sourceTable, sourceTable2;

	private JLabel lblStart;
	private JLabel lblWidth;
	private AutoCompleteTextFieldD fldStart;
	private AutoCompleteTextFieldD fldWidth;
	private JPanel classesPanel;
	private JPanel sourcePanel;

	double classStart = 0, classWidth = 1;
	int mode;
	int sourceType = StatDialog.SOURCE_VALUE;
	private boolean isNumericData = true;
	private boolean isFrequencyClassPossible = true;
	private boolean isFrequencyValuePossible = true;
	private JPanel sourceTablePanel;
	private int rowCount;
	private String[][] freqValueArray;

	public DataViewSettingsPanel(AppD app, StatDialog statDialog, int mode) {

		this.app = app;
		this.statDialog = statDialog;
		sourceTable = new StatTable(app);
		sourceTable2 = new StatTable(app);
		this.mode = mode;

		createGUI();
		initFields();
		setSourceTables();
		updateGUI();

		Dimension d = sourceTable.getPreferredSize();
		d.height = 8 * sourceTable.getTable().getRowHeight();
		sourceTable.setPreferredSize(d);
		this.addFocusListener(this);

	}

	private void createGUI() {

		isIniting = true;
		createGUIElements();

		buildDataSourcePanel();

		dataTypePanel = OptionsUtil.flowPanel(btnNumeric, btnCategorical);

		sourceTablePanel = new JPanel(new CardLayout());
		sourceTablePanel.add(sourceTable, "source1");
		sourceTablePanel.add(sourceTable2, "source2");

		sourcePanel = new JPanel(new BorderLayout());
		sourcePanel.add(sourceTypePanel, BorderLayout.WEST);
		sourcePanel.add(sourceTablePanel, BorderLayout.CENTER);

		setLayout(new BorderLayout());
		add(dataTypePanel, BorderLayout.NORTH);
		add(sourcePanel, BorderLayout.CENTER);

		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		isIniting = false;
		setLabels();


	}

	private void showSourceTable() {
		CardLayout c = (CardLayout) sourceTablePanel.getLayout();
		if (sourceType == StatDialog.SOURCE_VALUE) {
			c.show(sourceTablePanel, "source1");
		} else {
			c.show(sourceTablePanel, "source2");
		}
	}

	private void createGUIElements() {

		btnSourceFrequencyValue = new JRadioButton();
		btnSourceFrequencyValue.addActionListener(this);

		btnSourceValue = new JRadioButton();
		btnSourceValue.addActionListener(this);

		btnSourceFrequencyClass = new JRadioButton();
		btnSourceFrequencyClass.addActionListener(this);

		ButtonGroup group1 = new ButtonGroup();
		group1.add(btnSourceFrequencyClass);
		group1.add(btnSourceFrequencyValue);
		group1.add(btnSourceValue);

		btnNumeric = new JRadioButton("Numeric");
		btnNumeric.addActionListener(this);

		btnCategorical = new JRadioButton("Categorical");
		btnCategorical.addActionListener(this);

		ButtonGroup group2 = new ButtonGroup();
		group2.add(btnNumeric);
		group2.add(btnCategorical);

		lblStart = new JLabel();
		lblWidth = new JLabel();

		fldStart = new AutoCompleteTextFieldD(4, app);
		Dimension d = fldStart.getMaximumSize();
		d.height = fldStart.getPreferredSize().height;
		fldStart.setMaximumSize(d);
		fldStart.addActionListener(this);
		fldStart.setText("");
		fldStart.addFocusListener(this);

		fldWidth = new AutoCompleteTextFieldD(4, app);
		fldWidth.setMaximumSize(d);
		fldStart.setColumns(4);
		fldWidth.setColumns(4);
		fldWidth.addActionListener(this);
		fldWidth.setText("");
		fldWidth.addFocusListener(this);

		btnOK = new JButton();
		btnOK.addActionListener(this);

		btnCancel = new JButton();
		btnCancel.addActionListener(this);

	}

	private void buildDataSourcePanel() {

		sourceTypePanel = new JPanel();
		sourceTypePanel.setLayout(new BoxLayout(sourceTypePanel,
				BoxLayout.Y_AXIS));

		sourceTypePanel.add(OptionsUtil.flowPanel(btnSourceValue));
		sourceTypePanel.add(OptionsUtil.flowPanel(btnSourceFrequencyValue));
		sourceTypePanel.add(OptionsUtil.flowPanel(btnSourceFrequencyClass));

		buildClassesPanel();

		sourceTypePanel.add(OptionsUtil.flowPanel(classesPanel));
	}

	private void buildClassesPanel() {

		classesPanel = new JPanel();
		classesPanel.setLayout(new BoxLayout(classesPanel, BoxLayout.Y_AXIS));

		classesPanel.add(OptionsUtil.flowPanelRight(0, 0, 20, lblStart,
				fldStart));
		classesPanel.add(OptionsUtil.flowPanelRight(0, 0, 20, lblWidth,
				fldWidth));
	}

	private int getRowCount() {

		int n = app.getSelectedGeos().size();

		return n;
	}

	private void setSourceTables() {
app.error("set source table rowCount: " + rowCount);
		String[] columnNames = { app.getMenu("Values") };
		sourceTable.setStatTable(rowCount, null, 1, columnNames);

		String[] columnNames2 = { app.getMenu("Values"),
				app.getMenu("Frequency") };
		sourceTable2.setStatTable(rowCount, null, 2, columnNames2);

	}

	private void updateSourceTable() {

		DefaultTableModel model;

		if (sourceType == StatDialog.SOURCE_VALUE) {

			sourceTable.clear();
			model = sourceTable.getModel();
			for (int i = 0; i < model.getRowCount(); i++) {

				model.setValueAt(app.getSelectedGeos().get(i)
						.getValueForInputBar(), i, 0);
			}

		} else if (sourceType == StatDialog.SOURCE_FREQUENCY_VALUE) {

			sourceTable2.clear();
			model = sourceTable2.getModel();
			for (int col = 0; col < 2; col++) {
				for (int row = 0; row < freqValueArray[0].length; row++) {
					model.setValueAt(freqValueArray[col][row], row, col);
				}
			}
		} else if (sourceType == StatDialog.SOURCE_FREQUENCY_CLASS) {
			sourceTable2.clear();
			model = sourceTable2.getModel();
			for (int i = 0; i < model.getRowCount(); i++) {

				model.setValueAt(app.getSelectedGeos().get(i)
						.getValueForInputBar(), i, 0);
			}
		}
		sourceTable.revalidate();
	}

	public void setLabels() {

		if (isIniting) {
			return;
		}

		btnSourceValue.setText(app.getMenu("Values"));
		btnSourceFrequencyValue.setText(app.getMenu("Values and Frequency"));
		btnSourceFrequencyClass.setText(app.getMenu("Frequencies"));

		btnNumeric.setText(app.getMenu("Number"));
		btnCategorical.setText(app.getMenu("Name"));

		sourcePanel.setBorder(BorderFactory.createTitledBorder(app
				.getMenu("Source")));

		dataTypePanel.setBorder(BorderFactory.createTitledBorder(app
				.getMenu("DataType")));

		btnCancel.setText(app.getMenu("Cancel"));
		btnOK.setText(app.getMenu("OK"));

		// classesPanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Classes")));
		lblStart.setText(app.getMenu(" Start") + ": ");
		lblWidth.setText(app.getMenu(" Width") + ": ");

	}

	private void updateGUI() {

		btnSourceValue.removeActionListener(this);
		btnSourceFrequencyValue.removeActionListener(this);
		btnSourceFrequencyClass.removeActionListener(this);

		if (sourceType == StatDialog.SOURCE_VALUE) {
			btnSourceValue.setSelected(true);
		} else if (sourceType == StatDialog.SOURCE_FREQUENCY_VALUE) {
			btnSourceFrequencyValue.setSelected(true);
		} else {
			btnSourceFrequencyClass.setSelected(true);
		}

		btnSourceValue.addActionListener(this);
		btnSourceFrequencyValue.addActionListener(this);
		btnSourceFrequencyClass.addActionListener(this);

		btnNumeric.setSelected(isNumericData);

		// set visibility
		btnSourceFrequencyClass.setVisible(isFrequencyClassPossible);
		classesPanel.setVisible(isFrequencyClassPossible);
		btnSourceFrequencyValue.setVisible(isFrequencyValuePossible);

		updateSourceTable();
		showSourceTable();

	}

	private void initFields() {

		// exit if empty source
		if (app.getSelectedGeos() == null || app.getSelectedGeos().size() == 0) {
			setVisible(false);
			return;
		}

		// source is GeoList
		if (app.getSelectedGeos().size() == 1) {
			if (!app.getSelectedGeos().get(0).isGeoList()) {
				setVisible(false);
				return;
			}
			// TODO handle GeoList
			setVisible(false);
			return;
		}

		// source is spreadsheet range
		MyTableD spreadsheetTable = (MyTableD) app.getGuiManagerD()
				.getSpreadsheetView().getTable();
		ArrayList<CellRange> rangeList = spreadsheetTable.selectedCellRanges;

		isFrequencyClassPossible = spreadsheetTable.getCellRangeProcessor()
				.is1DRangeList(rangeList);

		isFrequencyValuePossible = spreadsheetTable.getCellRangeProcessor()
				.isCreatePointListPossible(rangeList);

		rowCount = app.getSelectedGeos().size();
		if (isFrequencyValuePossible) {
			freqValueArray = this.getFreqValueArray();
			rowCount = Math.max(rowCount, freqValueArray[0].length);
		}

		sourceType = statDialog.getSourceType();
		classWidth = statDialog.getDefaults().classWidth;
		classStart = statDialog.getDefaults().classStart;
		isNumericData = statDialog.isNumeric();

	}

	private String[][] getFreqValueArray() {

		String[][] s = new String[2][];

		MyTableD spreadsheetTable = (MyTableD) app.getGuiManagerD()
				.getSpreadsheetView().getTable();
		ArrayList<CellRange> rangeList = spreadsheetTable.selectedCellRanges;
		CellRangeProcessor cr = spreadsheetTable.getCellRangeProcessor();
		ArrayList<String> list0 = null;
		ArrayList<String> list1 = null;
		ArrayList<CellRange> r;

		boolean scanByColumn = rangeList.get(0).getActualDimensions()[1] <=2;
		
		System.out.println("scanby column = " + scanByColumn + "  " + rangeList.get(0).getActualDimensions()[0]
				+ "," + rangeList.get(0).getActualDimensions()[1]);

		// =================
		// step 1: get lists of values and frequencies from spreadsheet cells

		if (rangeList.size() == 1) { // single cell range

			if (scanByColumn) {
				r = rangeList.get(0).toPartialColumnList();
			} else {
				r = rangeList.get(0).toPartialRowList();
			}
			r.get(0).debug();
			r.get(1).debug();
			list0 = r.get(0).toGeoValueList(scanByColumn);
			list1 = r.get(1).toGeoValueList(scanByColumn);

		} else if (rangeList.size() == 2) { // two separate cell ranges

			if (scanByColumn) {
				// extract column values
				list0 = rangeList.get(0).toPartialColumnList().get(0)
						.toGeoValueList(true);
				list1 = rangeList.get(1).toPartialColumnList().get(0)
						.toGeoValueList(true);
			} else {
				// extract row values
				list0 = rangeList.get(0).toPartialRowList().get(0)
						.toGeoValueList(false);
				list1 = rangeList.get(1).toPartialRowList().get(0)
						.toGeoValueList(false);
			}

		}

		// =================
		// step 2: convert lists to arrays

		s[0] = new String[list0.size()];
		list0.toArray(s[0]);
		s[1] = new String[list1.size()];
		list1.toArray(s[1]);

		return s;

	}

	/**
	 * Handles button clicks
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source instanceof JTextField) {
			doTextFieldActionPerformed((JTextField) source);
		}

		else if (source == btnSourceFrequencyValue) {
			sourceType = StatDialog.SOURCE_FREQUENCY_VALUE;

		} else if (source == btnSourceValue) {
			sourceType = StatDialog.SOURCE_VALUE;

		} else if (source == this.btnSourceFrequencyClass) {
			sourceType = StatDialog.SOURCE_FREQUENCY_CLASS;

		} else if (source == btnNumeric || source == btnCategorical) {
			isNumericData = btnNumeric.isSelected();
		}
		updateGUI();

	}

	private void doTextFieldActionPerformed(Object source) {

		if (!(source instanceof JTextField)) {
			return;
		}
		String inputText = ((JTextField) source).getText().trim();

		if (source == fldStart) {
			classStart = Validation.validateDouble(fldStart, classStart);
			// updateStatTable();

		} else if (source == fldWidth) {
			classWidth = Validation
					.validateDoublePositive(fldWidth, classWidth);
		}
	}

	public void focusGained(FocusEvent e) {
		// do nothing

	}

	public void focusLost(FocusEvent e) {
		doTextFieldActionPerformed(e.getSource());

	}

	public void updateFonts(Font font) {
		// TODO Auto-generated method stub

	}

	public void setPanel() {
		updateGUI();
		setSourceTables();
		btnOK.requestFocus();
		revalidate();

	}

	public void applySettings() {

		statDialog.setNumeric(isNumericData);
		statDialog.setSourceType(sourceType);
		statDialog.getDefaults().classStart = classStart;
		statDialog.getDefaults().classWidth = classWidth;

		statDialog.setView(mode);
	}

}
