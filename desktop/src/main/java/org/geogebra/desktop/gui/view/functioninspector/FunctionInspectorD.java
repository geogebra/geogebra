/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop.gui.view.functioninspector;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.gui.view.functioninspector.FunctionInspector;
import org.geogebra.common.gui.view.functioninspector.FunctionInspectorModel.Colors;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.dialog.Dialog;
import org.geogebra.desktop.gui.inputfield.MyTextFieldD;
import org.geogebra.desktop.gui.util.GeoGebraIconD;
import org.geogebra.desktop.gui.util.PopupMenuButtonD;
import org.geogebra.desktop.gui.util.SpecialNumberFormat;
import org.geogebra.desktop.gui.util.SpecialNumberFormatInterface;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.util.GuiResourcesD;

/**
 * View for inspecting selected GeoFunc } else {tions
 * 
 * @author G. Sturr, 2011-2-12
 * 
 */

public class FunctionInspectorD extends FunctionInspector
 implements
		ListSelectionListener, KeyListener,
		SpecialNumberFormatInterface, ActionListener, WindowFocusListener,
		FocusListener {

	// ggb fields
	private JDialog wrappedDialog;

	// color constants
	private static final Color DISPLAY_GEO_COLOR = Color.RED;
	private static final Color DISPLAY_GEO2_COLOR = Color.RED;
	private static final Color EVEN_ROW_COLOR = new Color(241, 245, 250);
	private static final Color TABLE_GRID_COLOR = GColorD
			.getAwtColor(GeoGebraColorConstants.TABLE_GRID_COLOR);
	// table fields
	private InspectorTable tableXY, tableInterval;
	private DefaultTableModel modelXY, modelInterval;
	private static final int minRows = 12;

	// GUI
	private JLabel lblGeoName, lblStep, lblInterval;
	private MyTextFieldD fldStep, fldLow, fldHigh;
	private JButton btnRemoveColumn, btnHelp;
	private JToggleButton btnOscCircle, btnTangent, btnXYSegments, btnTable;
	private PopupMenuButtonD btnAddColumn, btnOptions;
	private JTabbedPane tabPanel;
	private JPanel intervalTabPanel, pointTabPanel, headerPanel, helpPanel;

	private boolean isChangingValue;
	private int pointCount = 9;

	private SpecialNumberFormat nf;

	/***************************************************************
	 * Constructs a FunctionInspecor
	 * 
	 * @param app
	 * @param selectedGeo
	 */
	public FunctionInspectorD(AppD app, GeoFunction selectedGeo) {
		super(app, selectedGeo);
		this.app = app;
	}

	private AppD getAppD() {
		return (AppD) app;
	}

	// ======================================================
	// GUI
	// ======================================================

	@Override
	protected void createGUI() {
		wrappedDialog = new Dialog(getAppD().getFrame(), false) {
			/**
			 * } else {
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void setVisible(boolean isVisible) {
				setInspectorVisible(isVisible);
				super.setVisible(isVisible);
			}
		};

		nf = new SpecialNumberFormat(getAppD(), this);
		super.createGUI();
		// add GUI to contentPane
		wrappedDialog.getContentPane().add(headerPanel, BorderLayout.NORTH);
		wrappedDialog.getContentPane().add(tabPanel, BorderLayout.CENTER);

		// prepare the dialog to be displayed in the center
		wrappedDialog.setResizable(true);
		wrappedDialog.pack();
		wrappedDialog.setLocationRelativeTo(getAppD().getMainComponent());
		updateFonts();
		setLabels();
	}

	@Override
	protected void createTabIntervalPanel() {
		JToolBar intervalTB = new JToolBar(); // JPanel(new
												// FlowLayout(FlowLayout.LEFT));
		intervalTB.setFloatable(false);
		intervalTB.add(fldLow);
		intervalTB.add(lblInterval);
		intervalTB.add(fldHigh);

		intervalTabPanel = new JPanel(new BorderLayout(5, 5));
		intervalTabPanel.add(new JScrollPane(tableInterval),
				BorderLayout.CENTER);
		intervalTabPanel.add(intervalTB, BorderLayout.SOUTH);

	}

	@Override
	protected void createTabPointPanel() {

		// create step toolbar
		JToolBar tb1 = new JToolBar();
		tb1.setFloatable(false);
		tb1.add(lblStep);
		tb1.add(fldStep);

		// create add/remove column toolbar
		JToolBar tb2 = new JToolBar();
		tb2.setFloatable(false);
		tb2.add(btnAddColumn);
		tb2.add(btnRemoveColumn);

		// create toggle graphics panel

		FlowLayout flow = new FlowLayout(FlowLayout.CENTER);
		flow.setHgap(4);
		JPanel tb3 = new JPanel(flow);
		// JToolBar tb3 = new JToolBar();
		// tb3.setFloatable(false);
		tb3.add(btnTable);
		tb3.add(btnXYSegments);
		tb3.add(btnTangent);
		tb3.add(btnOscCircle);
		JPanel toggleGraphicsPanel = new JPanel(new BorderLayout());
		toggleGraphicsPanel.add(tb3, BorderLayout.CENTER);

		// create the panel

		LocalizationD loc = getAppD().getLocalization();
		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.add(tb1, loc.borderWest());
		northPanel.add(tb2, loc.borderEast());

		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add(toggleGraphicsPanel, BorderLayout.CENTER);

		JScrollPane scroller = new JScrollPane(tableXY);

		pointTabPanel = new JPanel(new BorderLayout(2, 2));
		pointTabPanel.add(northPanel, BorderLayout.NORTH);
		pointTabPanel.add(scroller, BorderLayout.CENTER);
		pointTabPanel.add(southPanel, BorderLayout.SOUTH);

	}

	@Override
	protected void createGUIElements() {

		// create XY table
		tableXY = new InspectorTable(getAppD(), this, minRows,
				InspectorTable.TYPE_XY);
		modelXY = new DefaultTableModel();
		modelXY.addColumn("x");
		modelXY.addColumn("y(x)");
		// modelXY.addRow(new String[] { "", "" });
		modelXY.setRowCount(pointCount);
		tableXY.setModel(modelXY);

		tableXY.getSelectionModel().addListSelectionListener(this);
		// tableXY.addKeyListener(this);
		tableXY.setMyCellEditor(0);

		// create interval table
		tableInterval = new InspectorTable(getAppD(), this, minRows,
				InspectorTable.TYPE_INTERVAL);
		modelInterval = new DefaultTableModel();
		modelInterval.setColumnCount(2);
		modelInterval.setRowCount(pointCount);
		tableInterval.setModel(modelInterval);
		tableInterval.getSelectionModel()
				.addListSelectionListener(new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						getModel().updateIntervalGeoVisiblity();
					}
				});

		lblGeoName = new JLabel(getModel().getTitleString());
		lblGeoName.setFont(getAppD().getBoldFont());

		lblStep = new JLabel();
		fldStep = makeTextField(app);
		fldStep.addActionListener(this);
		fldStep.addFocusListener(this);
		fldStep.setColumns(6);

		lblInterval = new JLabel();
		fldLow = makeTextField(app);
		fldLow.addActionListener(this);
		fldLow.addFocusListener(this);
		fldLow.setColumns(6);
		fldHigh = makeTextField(app);
		fldHigh.addActionListener(this);
		fldHigh.addFocusListener(this);
		fldHigh.setColumns(6);

		btnOscCircle = new JToggleButton();
		btnTangent = new JToggleButton();
		btnXYSegments = new JToggleButton();
		btnTable = new JToggleButton();


		btnOscCircle.addActionListener(this);
		btnTangent.addActionListener(this);
		btnXYSegments.addActionListener(this);
		btnTable.addActionListener(this);

		// btnOscCircle.setPreferredSize(new Dimension(24,24));
		// btnTangent.setPreferredSize(new Dimension(24,24));
		// btnXYSegments.setPreferredSize(new Dimension(24,24));
		// btnTable.setPreferredSize(new Dimension(24,24));

		btnXYSegments.setSelected(true);

		btnRemoveColumn = new JButton();
		btnRemoveColumn.addActionListener(this);

		btnHelp = new JButton();
		btnHelp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					@Override
					public void run() {
						((GuiManagerD) app.getGuiManager())
								.openHelp("Function_Inspector_Tool");
					}
				};
				runner.start();
			}
		});
		btnHelp.setFocusable(false);
		updateIcons();
		createBtnAddColumn();
	}

	private void createBtnAddColumn() {

		btnAddColumn = new PopupMenuButtonD(getAppD(),
				getModel().getColumnNames(), -1, 1, new Dimension(0, 18),
				SelectionTable.MODE_TEXT);
		btnAddColumn.setKeepVisible(false);
		btnAddColumn.setStandardButton(true);
		btnAddColumn.setFixedIcon(GeoGebraIconD.createEmptyIcon(1, 1));
		btnAddColumn.setText("\u271A");
		btnAddColumn.addActionListener(this);
	}

	@Override
	public void setLabels() {
		LocalizationD loc = getAppD().getLocalization();
		wrappedDialog.setTitle(loc.getMenu("FunctionInspector"));
		lblStep.setText(loc.getMenu("Step") + ":");
		lblInterval.setText(" \u2264 x \u2264 "); // <= x <=

		// header text

		modelInterval.setColumnIdentifiers(getModel().getIntervalColumnNames());

		tabPanel.setTitleAt(1, loc.getMenu("fncInspector.Points"));
		tabPanel.setTitleAt(0, loc.getMenu("fncInspector.Interval"));
		lblGeoName.setText(getModel().getTitleString());

		// tool tips
		btnHelp.setToolTipText(loc.getMenu("ShowOnlineHelp"));
		btnOscCircle.setToolTipText(
				loc.getPlainTooltip("fncInspector.showOscCircle"));
		btnXYSegments.setToolTipText(
				loc.getPlainTooltip("fncInspector.showXYLines"));
		btnTable.setToolTipText(loc.getPlainTooltip("fncInspector.showTable"));
		btnTangent.setToolTipText(
				loc.getPlainTooltip("fncInspector.showTangent"));
		btnAddColumn
				.setToolTipText(loc.getPlainTooltip("fncInspector.addColumn"));
		btnRemoveColumn.setToolTipText(
				loc.getPlainTooltip("fncInspector.removeColumn"));
		fldStep.setToolTipText(loc.getPlainTooltip("fncInspector.step"));
		lblStep.setToolTipText(loc.getPlainTooltip("fncInspector.step"));

		// add/remove extra column buttons
		btnRemoveColumn.setText("\u2718");
		// btnAddColumn.setText("\u271A");

		Container c = btnAddColumn.getParent();
		c.removeAll();
		createBtnAddColumn();
		c.add(btnAddColumn);
		c.add(btnRemoveColumn);

		createOptionsButton();

	}

	// =====================================
	// Update
	// =====================================

	@Override
	protected void updateIntervalFields() {

		if (tabPanel.getSelectedComponent() == intervalTabPanel) {

			double[] coords = new double[3];
			getModel().getLowPoint().getCoords(coords);
			fldLow.setText(getModel().format(coords[0]));
			getModel().getHighPoint().getCoords(coords);
			fldHigh.setText(getModel().format(coords[0]));
			updateIntervalTable();
		}
	}

	/**
	 * Updates the interval table. The max, min, roots, area etc. for the
	 * current interval are calculated and put into the IntervalTable model.
	 */
	@Override
	protected void updateIntervalTable() {

		isChangingValue = true;
		getModel().updateIntervalTable();
		isChangingValue = false;

	}

	/**
	 * Updates the XYTable with the coordinates of the current sample points and
	 * any related values (e.g. derivative, difference)
	 */
	@Override
	protected void updateXYTable() {

		isChangingValue = true;
		getModel().updateXYTable(modelXY.getRowCount(), btnTable.isSelected());
		isChangingValue = false;
	}

	@Override
	public void addTableColumn(String name) {
		modelXY.addColumn(name);
		tableXY.setMyCellEditor(0);
		updateXYTable();
	}

	@Override
	protected void removeColumn() {
		int count = tableXY.getColumnCount();
		if (count <= 2) {
			return;
		}

		getModel().removeColumn();
		modelXY.setColumnCount(modelXY.getColumnCount() - 1);
		tableXY.setMyCellEditor(0);
		updateXYTable();

	}

	// ========================================================
	// Event Handlers
	// ========================================================

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source instanceof JTextField) {
			doTextFieldActionPerformed((JTextField) source);
		} else if (source == btnAddColumn) {
			getModel().addColumn(btnAddColumn.getSelectedIndex());
		}

		else if (source == btnRemoveColumn) {
			removeColumn();
		}

		else if (source == btnOscCircle || source == btnTangent
				|| source == btnTable || source == btnXYSegments) {
			updateGUI();
		}

	}

	private void doTextFieldActionPerformed(JTextField source) {
		try {

			String inputText = source.getText().trim();

			// allow input such as sqrt(2)
			NumberValue nv;
			nv = getKernel().getAlgebraProcessor().evaluateToNumeric(inputText,
					false);
			double value = nv.getDouble();

			if (source == fldStep) {
				getModel().applyStep(value);
				updateXYTable();
			} else if (source == fldLow) {
				isChangingValue = true;

				getModel().applyLow(value);

				isChangingValue = false;
				updateIntervalTable();
			} else if (source == fldHigh) {
				isChangingValue = true;

				getModel().applyHigh(value);

				isChangingValue = false;
				updateIntervalTable();
			}

		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void focusGained(FocusEvent e) {
		if (e.getSource() instanceof MyTextFieldD) {
			((MyTextFieldD) e.getSource()).selectAll();
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		doTextFieldActionPerformed((JTextField) (e.getSource()));
	}

	public void show() {
		wrappedDialog.setVisible(true);
	}

	public void hide() {
		wrappedDialog.setVisible(false);
	}

	@Override
	public void reset() {
		wrappedDialog.setVisible(false);
	}

	// ====================================================
	// Table Selection Listener
	// ====================================================

	@Override
	public void valueChanged(ListSelectionEvent e) {

		if (e.getValueIsAdjusting() || isChangingValue) {
			return;
		}

		tableXY.getSelectionModel().removeListSelectionListener(this);
		if (e.getSource() == tableXY.getSelectionModel()) {
			// row selection changed
			updateTestPoint();
		}
		tableXY.getSelectionModel().addListSelectionListener(this);
	}

	// ====================================================
	// Key Listeners
	// ====================================================

	@Override
	public void keyPressed(KeyEvent e) {

		int key = e.getKeyCode();

		tableXY.getSelectionModel().removeListSelectionListener(this);
		switch (key) {
		default:
			// do nothing
			break;
		case KeyEvent.VK_UP:
			if (tableXY.getSelectedRow() == 0) {

				getModel().stepStartBackward();
				updateXYTable();
				updateTestPoint();
			}
			break;

		case KeyEvent.VK_DOWN:
			if (tableXY.getSelectedRow() == tableXY.getRowCount() - 1) {
				getModel().stepStartForward();
				updateXYTable();
				tableXY.changeSelection(tableXY.getRowCount() - 1, 0, false,
						false);
				updateTestPoint();
			}
			break;
		}

		tableXY.getSelectionModel().addListSelectionListener(this);

	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// only handle key pressed
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// only handle key pressed
	}

	// Mouse Listeners
	// =========================================



	@Override
	public void updateFonts() {
		Font font = getAppD().getPlainFont();
		wrappedDialog.setFont(font);
		tableXY.setFont(font);
		tableInterval.setFont(font);
		MyTextFieldD dummyField = makeTextField(app);
		tableXY.setRowHeight(dummyField.getPreferredSize().height);
		tableInterval.setRowHeight(dummyField.getPreferredSize().height);
		updateIcons();

		GuiManagerD.setFontRecursive(wrappedDialog, font);
	}

	private static MyTextFieldD makeTextField(App app) {
		return new MyTextFieldD((AppD) app);
	}

	@Override
	public void windowGainedFocus(WindowEvent arg0) {
		//
	}

	@Override
	public void changeStart(double x) {
		tableXY.getSelectionModel().removeListSelectionListener(this);
		setStart(x);
		tableXY.getSelectionModel().addListSelectionListener(this);
	}

	private SpecialNumberFormat getMyNumberFormat() {
		return nf;
	}

	@Override
	protected void createOptionsButton() {
		if (btnOptions == null) {
			btnOptions = new PopupMenuButtonD(getAppD());
			btnOptions.setKeepVisible(true);
			btnOptions.setStandardButton(true);
			btnOptions
					.setFixedIcon(getAppD().getScaledIcon(GuiResourcesD.TOOL));
			btnOptions.setDownwardPopup(true);
		}

		btnOptions.removeAllMenuItems();

		LocalizationD loc = getAppD().getLocalization();

		btnOptions.setToolTipText(loc.getMenu("Options"));

		// copy to spreadsheet
		JMenuItem mi = new JMenuItem(loc.getMenu("CopyToSpreadsheet"));
		mi.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				doCopyToSpreadsheet();
			}
		});
		mi.setEnabled(((GuiManagerD) app.getGuiManager()).hasSpreadsheetView());
		btnOptions.addPopupMenuItem(mi);

		// rounding
		btnOptions.addPopupMenuItem(
				getMyNumberFormat().createMenuDecimalPlaces());

	}

	@Override
	protected void doCopyToSpreadsheet() {

		if (tabPanel.getSelectedComponent() == pointTabPanel) {
			getModel().copyPointsToSpreadsheet(tableXY.getColumnCount(),
					tableXY.getRowCount());
		} else {
			getModel().copyIntervalsToSpreadsheet(
					tableInterval.getColumnCount(),
					tableInterval.getRowCount());
		}
	}

	@Override
	public void updateXYTable(boolean isTable) {
		// reset table model and update the XYtable
		tableXY.setCellEditable(-1, -1);

		if (isTable) {
			modelXY.setRowCount(pointCount);
			tableXY.setCellEditable((pointCount - 1) / 2, 0);
			// tableXY.setRowSelectionAllowed(true);
			tableXY.changeSelection((pointCount - 1) / 2, 0, false, false);

		} else {

			modelXY.setRowCount(1);
			tableXY.setCellEditable(0, 0);
			tableXY.changeSelection(0, 0, false, false);
			// tableXY.setRowSelectionAllowed(false);
		}

		updateXYTable();
		updateTestPoint();

	}

	@Override
	public void updateInterval(ArrayList<String> property,
			ArrayList<String> value) {
		// load the model with these pairs
		modelInterval.setRowCount(property.size());

		for (int i = 0; i < property.size(); i++) {
			modelInterval.setValueAt(property.get(i), i, 0);
			modelInterval.setValueAt(value.get(i), i, 1);
		}
	}

	@Override
	public void setXYValueAt(Double value, int row, int col) {
		if (col < modelXY.getColumnCount() && row < modelXY.getRowCount()) {
			modelXY.setValueAt(value == null ? null : getModel().format(value),
					row, col);
		} else {
			Log.debug("[FI] Outside of range: " + modelXY.getRowCount() + ", "
					+ modelXY.getRowCount());
		}
	}

	@Override
	public Object getXYValueAt(int row, int col) {
		return modelXY.getValueAt(row, col);
	}

	@Override
	public void setGeoName(String name) {
		lblGeoName.setText(name);
	}

	@Override
	public void changeTableSelection() {
		updateXYTable();

		tableXY.getSelectionModel().removeListSelectionListener(this);

		if (btnTable.isSelected() && tableXY.getSelectedRow() != 4) {
			tableXY.changeSelection(4, 0, false, false);
		} else if (!btnTable.isSelected() && tableXY.getSelectedRow() != 0) {
			tableXY.changeSelection(0, 0, false, false);
		}

		tableXY.getSelectionModel().addListSelectionListener(this);
	}

	@Override
	public void updateHighAndLow(boolean isAscending, boolean isLowSelected) {
		if (isAscending) {
			if (isLowSelected) {
				doTextFieldActionPerformed(fldLow);
			} else {
				doTextFieldActionPerformed(fldHigh);
			}

		}

		updateIntervalFields();
	}

	@Override
	public void setStepText(String text) {
		fldStep.removeActionListener(this);
		fldStep.setText(text);
		fldStep.addActionListener(this);
	}

	@Override
	public GColor getColor(Colors id) {
		Color color;
		switch (id) {
		case EVEN_ROW:
			color = EVEN_ROW_COLOR;
			break;
		case GEO:
			color = DISPLAY_GEO_COLOR;
			break;
		case GEO2:
			color = DISPLAY_GEO2_COLOR;
			break;
		case GRID:
			color = TABLE_GRID_COLOR;
			break;
		default:
			color = Color.black;
			break;

		}
		return GColorD.newColor(color);
	}

	@Override
	public int getSelectedXYRow() {
		return tableXY.getSelectedRow();
	}

	@Override
	public void setStepVisible(boolean isVisible) {
		lblStep.setVisible(isVisible);
		fldStep.setVisible(isVisible);
	}

	@Override
	protected void buildTabPanel() {
		// build tab panel
		tabPanel = new JTabbedPane();
		tabPanel.addTab("Interval", intervalTabPanel);
		tabPanel.addTab("Point", pointTabPanel);

		tabPanel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent evt) {
				updateTabPanels();
			}

		});

	}

	@Override
	protected void buildHelpPanel() {
		helpPanel = new JPanel(new FlowLayout());
		helpPanel.add(btnHelp);
		helpPanel.add(btnOptions);
	}

	@Override
	protected void buildHeaderPanel() {
		LocalizationD loc = getAppD().getLocalization();

		headerPanel = new JPanel(new BorderLayout());
		headerPanel.add(lblGeoName, BorderLayout.CENTER);
		headerPanel.add(helpPanel, loc.borderEast());
		headerPanel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 2));
	}

	@Override
	protected void updatePointsTab() {
		tableXY.getSelectionModel().removeListSelectionListener(this);

		getModel().updatePoints(btnTangent.isSelected(),
				btnOscCircle.isSelected(), btnXYSegments.isSelected(),
				btnTable.isSelected());

		tableXY.getSelectionModel().addListSelectionListener(this);
	}

	@Override
	protected boolean isIntervalTabSelected() {
		return tabPanel.getSelectedComponent() == intervalTabPanel;
	}

	@Override
	public boolean hasFocus() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void windowLostFocus(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void changedNumberFormat() {
		getModel().setPrintDecimals(nf.getPrintDecimals());
		getModel().setPrintFigures(nf.getPrintFigures());
		super.changedNumberFormat();
	}

	@Override
	public boolean suggestRepaint() {
		return false;
		// only for web
	}

	public void updateIcons() {
		if (app == null || btnOscCircle == null) {
			return;
		}
		
		getScaledIcon(btnOscCircle, GuiResourcesD.OSCULATING_CIRCLE);
		getScaledIcon(btnTangent, GuiResourcesD.TANGENT_LINE);
		getScaledIcon(btnXYSegments, GuiResourcesD.XY_SEGMENTS);
		getScaledIcon(btnTable, GuiResourcesD.XY_TABLE);
		getScaledIcon(btnHelp, GuiResourcesD.HELP);
		if (btnOptions != null) {
			btnOptions
					.setFixedIcon(getAppD().getScaledIcon(GuiResourcesD.TOOL));
		}

	}

	private void getScaledIcon(AbstractButton target, GuiResourcesD tool) {
		target.setIcon(getAppD().getScaledIcon(tool));
	}
}
