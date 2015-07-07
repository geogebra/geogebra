/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop.gui.dialog.options;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.options.model.EuclidianOptionsModel;
import org.geogebra.common.gui.dialog.options.model.EuclidianOptionsModel.IEuclidianOptionsListener;
import org.geogebra.common.gui.dialog.options.model.EuclidianOptionsModel.MinMaxType;
import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolNavigation;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.NumberComboBox;
import org.geogebra.desktop.gui.dialog.AxesStyleListRenderer;
import org.geogebra.desktop.gui.dialog.DashListRenderer;
import org.geogebra.desktop.gui.inputfield.MyTextField;
import org.geogebra.desktop.gui.util.FullWidthLayout;
import org.geogebra.desktop.gui.util.LayoutUtil;
import org.geogebra.desktop.gui.view.consprotocol.ConstructionProtocolNavigationD;
import org.geogebra.desktop.main.AppD;

/**
 * Panel with options for the euclidian view. TODO: optimization: updateGUI()
 * called too often (F.S.)
 * 
 * revised by G.Sturr 2010-8-15
 * 
 */
public class OptionsEuclidianD extends
		org.geogebra.common.gui.dialog.options.OptionsEuclidian implements
		OptionPanelD, ActionListener, FocusListener, ItemListener, SetLabels,
		IEuclidianOptionsListener {

	private static final long serialVersionUID = 1L;

	protected AppD app;
	private Kernel kernel;
	private EuclidianOptionsModel model;
	protected EuclidianView view;

	// GUI containers
	protected AxisPanel xAxisPanel, yAxisPanel;
	protected JTabbedPane tabbedPane;
	private JPanel dimPanel, stylePanel;

	protected JPanel typePanel;

	protected JPanel axesOptionsPanel;

	private JPanel consProtocolPanel;

	protected JPanel miscPanel;

	private JPanel selectViewPanel;

	// GUI elements
	protected JButton btBackgroundColor;

	protected JButton btAxesColor;

	protected JButton btGridColor;
	protected JCheckBox cbShowAxes;

	private JCheckBox cbBoldAxes;

	private JCheckBox cbShowGrid;

	protected JCheckBox cbBoldGrid;

	protected JCheckBox cbGridManualTick;

	protected JCheckBox cbShowMouseCoords;

	private JCheckBox ckShowNavbar;

	private JCheckBox ckNavPlay;

	private JCheckBox ckOpenConsProtocol;
	protected JComboBox cbAxesStyle, cbGridType, cbGridStyle, cbGridTickAngle,
			cbTooltips;

	protected JLabel lblAxisLabelStyle;
	protected JCheckBox cbAxisLabelBold, cbAxisLabelItalic;
	private JTextField tfAxesRatioX, tfAxesRatioY;

	private NumberFormat nfAxesRatio;
	protected NumberComboBox ncbGridTickX, ncbGridTickY;

	protected JTextField tfMinX, tfMaxX, tfMinY, tfMaxY;

	private JLabel[] dimLabel;
	private JLabel axesRatioLabel, gridLabel1, gridLabel2, gridLabel3,
			lblColor, tooltips;

	protected JLabel backgroundColor;

	private JLabel color;

	private JLabel lineStyle;

	// flags
	private boolean isIniting;

	private JToggleButton cbLockRatio;

	private JPanel wrappedPanel;

	// private JButton restoreDefaultsButton;

	/***********************************************
	 * Creates a new dialog for the properties of the Euclidian view.
	 * 
	 * @param app
	 *            parent frame
	 */
	public OptionsEuclidianD(AppD app, EuclidianView view) {

		isIniting = true;
		this.app = app;
		kernel = app.getKernel();
		this.view = view;
		model = new EuclidianOptionsModel(app, view, this);
		view.setOptionPanel(this);

		wrappedPanel = new JPanel();
		// build GUI
		initGUI();
		isIniting = false;
	}

	public void setView(EuclidianView view) {
		this.view = view;
		if (!isIniting) {
			updateGUI();
		}
	}

	/**
	 * update the view (also for model)
	 * 
	 * @param view
	 *            view
	 */
	public void updateView(EuclidianView view) {
		setView(view);
		view.setOptionPanel(this);
		model.setView(view);
		xAxisPanel.updateView(view);
		yAxisPanel.updateView(view);
	}

	/**
	 * inits GUI with labels of current language
	 */
	private void initGUI() {

		// create color buttons
		btGridColor = new JButton("\u2588");
		btGridColor.addActionListener(this);

		// setup axes ratio field
		nfAxesRatio = NumberFormat.getInstance(Locale.ENGLISH);
		nfAxesRatio.setMaximumFractionDigits(5);
		nfAxesRatio.setGroupingUsed(false);

		// init cons protocol panel
		initConsProtocolPanel();

		// create panels for the axes
		initAxisPanels();

		// create tabbed pane for basic, axes, and grid options
		tabbedPane = new JTabbedPane();
		addTabs();

		// put it all together
		wrappedPanel.removeAll();
		wrappedPanel.setLayout(new BorderLayout());
		wrappedPanel.add(tabbedPane, BorderLayout.CENTER);

		/*
		 * //restore default button if (!app.isApplet()) { JPanel panel = new
		 * JPanel(new FlowLayout(FlowLayout.LEFT));
		 * 
		 * restoreDefaultsButton = new JButton();
		 * restoreDefaultsButton.addActionListener(new ActionListener() { public
		 * void actionPerformed(ActionEvent e) {
		 * 
		 * AbstractApplication.error("not implemented yet");
		 * 
		 * } });
		 * 
		 * panel.add(restoreDefaultsButton); add(panel, BorderLayout.SOUTH); }
		 */

	}

	protected void addTabs() {
		tabbedPane.addTab("", new JScrollPane(buildBasicPanel()));
		addAxisTabs();
		tabbedPane.addTab("", new JScrollPane(buildGridPanel()));
	}

	protected void addAxisTabs() {
		tabbedPane.addTab("", new JScrollPane(xAxisPanel));
		tabbedPane.addTab("", new JScrollPane(yAxisPanel));
	}

	protected void initAxisPanels() {

		xAxisPanel = new AxisPanel(app, view, 0);
		yAxisPanel = new AxisPanel(app, view, 1);
	}

	private void initDimensionPanel() {

		dimLabel = new JLabel[4]; // "Xmin", "Xmax" etc.
		for (int i = 0; i < 4; i++)
			dimLabel[i] = new JLabel("");

		tfMinX = new MyTextField(app, 8);
		tfMaxX = new MyTextField(app, 8);
		tfMinX.addActionListener(this);
		tfMaxX.addActionListener(this);
		tfMinX.addFocusListener(this);
		tfMaxX.addFocusListener(this);

		tfMinY = new MyTextField(app, 8);
		tfMaxY = new MyTextField(app, 8);
		tfMinY.addActionListener(this);
		tfMaxY.addActionListener(this);
		tfMinY.addFocusListener(this);
		tfMaxY.addFocusListener(this);

		tfAxesRatioX = new MyTextField(app, 6);
		tfAxesRatioY = new MyTextField(app, 6);
		tfAxesRatioX.setEnabled(view.isZoomable() && !view.isLockedAxesRatio());
		tfAxesRatioY.setEnabled(view.isZoomable() && !view.isLockedAxesRatio());
		tfAxesRatioX.addActionListener(this);
		tfAxesRatioY.addActionListener(this);
		tfAxesRatioX.addFocusListener(this);
		tfAxesRatioY.addFocusListener(this);
		cbLockRatio = new JToggleButton();
		cbLockRatio.setSelected(view.isLockedAxesRatio());
		cbLockRatio.setIcon(app.getScaledIcon("object_unfixed.png"));
		cbLockRatio.setEnabled(view.isZoomable());
		cbLockRatio.addActionListener(this);
		axesRatioLabel = new JLabel("");

		dimPanel = new JPanel();
		dimPanel.setLayout(new BoxLayout(dimPanel, BoxLayout.Y_AXIS));

		dimPanel.add(LayoutUtil.flowPanel(dimLabel[0], tfMinX, dimLabel[1],
				tfMaxX));
		dimPanel.add(LayoutUtil.flowPanel(dimLabel[2], tfMinY, dimLabel[3],
				tfMaxY));

		dimPanel.add(LayoutUtil.flowPanel(axesRatioLabel));
		dimPanel.add(LayoutUtil.flowPanel(Box.createHorizontalStrut(20),
				tfAxesRatioX, new JLabel(" : "), tfAxesRatioY, cbLockRatio));
	}

	/**
	 * init axes options panel
	 */
	protected void initAxesOptionsPanel() {

		// show axes checkbox
		cbShowAxes = new JCheckBox(app.getPlain("ShowAxes"));

		// show bold checkbox
		cbBoldAxes = new JCheckBox(app.getPlain("Bold"));

		// show axis label bold checkbox
		cbAxisLabelBold = new JCheckBox(app.getPlain("Bold"));

		// show axis label bold checkbox
		cbAxisLabelItalic = new JCheckBox(app.getPlain("Italic"));

		cbAxisLabelBold.addActionListener(this);
		cbAxisLabelItalic.addActionListener(this);

		// axes color
		color = new JLabel(app.getMenu("Color") + ":");
		color.setLabelFor(btAxesColor);
		btAxesColor = new JButton("\u2588");
		btAxesColor.addActionListener(this);

		// axes style
		lineStyle = new JLabel(app.getPlain("LineStyle") + ":");
		lineStyle.setLabelFor(cbAxesStyle);

		// axes font style
		lblAxisLabelStyle = new JLabel(app.getPlain("LabelStyle") + ":");

		AxesStyleListRenderer renderer = new AxesStyleListRenderer();
		cbAxesStyle = new JComboBox(EuclidianStyleConstants.lineStyleOptions);
		cbAxesStyle.setRenderer(renderer);
		cbAxesStyle.setMaximumRowCount(AxesStyleListRenderer.MAX_ROW_COUNT);
		// cbAxesStyle.setBackground(getBackground());

		// cbAxesStyle.addItem("\u2014" + " " + app.getPlain("Bold")); // bold
		// line
		// cbAxesStyle.addItem("\u2192" + " " + app.getPlain("Bold")); // bold
		// arrow

		/*
		 * 
		 * PointStyleListRenderer renderer = new PointStyleListRenderer();
		 * renderer.setPreferredSize(new Dimension(18, 18)); cbStyle = new
		 * JComboBox(EuclidianViewD.getPointStyles());
		 * cbStyle.setRenderer(renderer);
		 * cbStyle.setMaximumRowCount(EuclidianStyleConstants.MAX_POINT_STYLE +
		 * 1); cbStyle.setBackground(getBackground());
		 * cbStyle.addActionListener(this); add(cbStyle);
		 */
		cbAxesStyle.setEditable(false);

		// axes options panel
		axesOptionsPanel = new JPanel();
		axesOptionsPanel.setLayout(new BoxLayout(axesOptionsPanel,
				BoxLayout.Y_AXIS));
		fillAxesOptionsPanel();

	}

	protected void fillAxesOptionsPanel() {
		axesOptionsPanel.add(LayoutUtil.flowPanel(cbShowAxes,
				Box.createHorizontalStrut(20), cbBoldAxes));
		axesOptionsPanel.add(LayoutUtil.flowPanel(color, btAxesColor,
				Box.createHorizontalStrut(20), lineStyle, cbAxesStyle));

		axesOptionsPanel.add(LayoutUtil.flowPanel(lblAxisLabelStyle,
				cbAxisLabelBold, cbAxisLabelItalic));
	}

	protected void initMiscPanel() {

		// background color panel
		backgroundColor = new JLabel(app.getPlain("BackgroundColor") + ":");
		backgroundColor.setLabelFor(btBackgroundColor);
		btBackgroundColor = new JButton("\u2588");
		btBackgroundColor.addActionListener(this);

		// show mouse coords
		cbShowMouseCoords = new JCheckBox();
		cbShowMouseCoords.addActionListener(this);

		// show tooltips
		tooltips = new JLabel(app.getPlain("Tooltips") + ":");
		cbTooltips = new JComboBox();
		model.fillTooltipCombo();
		cbTooltips.addActionListener(this);

		miscPanel = new JPanel();
		miscPanel.setLayout(new BoxLayout(miscPanel, BoxLayout.Y_AXIS));
		fillMiscPanel();

	}

	protected void fillMiscPanel() {
		miscPanel.add(LayoutUtil.flowPanel(backgroundColor, btBackgroundColor));
		miscPanel.add(LayoutUtil.flowPanel(tooltips, cbTooltips));
		miscPanel.add(LayoutUtil.flowPanel(cbShowMouseCoords));
	}

	protected JPanel buildBasicPanel() {

		initDimensionPanel();
		initAxesOptionsPanel();
		initMiscPanel();

		JPanel basicPanel = new JPanel();
		basicPanel.setLayout(new FullWidthLayout());
		addDimPanel(basicPanel);
		basicPanel.add(axesOptionsPanel);
		basicPanel.add(consProtocolPanel);
		basicPanel.add(miscPanel);

		return basicPanel;
	}

	/**
	 * add dimension panel
	 * 
	 * @param basicPanel
	 *            basic panel
	 */
	protected void addDimPanel(JPanel basicPanel) {
		basicPanel.add(dimPanel);
	}

	private void initGridTypePanel() {

		// grid type combo box
		cbGridType = new JComboBox();
		model.fillGridTypeCombo();
		cbGridType.addActionListener(this);

		// tick intervals

		cbGridManualTick = new JCheckBox(app.getPlain("TickDistance") + ":");
		ncbGridTickX = new NumberComboBox(app);
		ncbGridTickY = new NumberComboBox(app);

		cbGridManualTick.addActionListener(this);
		ncbGridTickX.addItemListener(this);
		ncbGridTickY.addItemListener(this);

		// checkbox for grid labels
		cbGridTickAngle = new JComboBox();
		model.fillAngleOptions();
		cbGridTickAngle.addItemListener(this);

		// grid labels
		gridLabel1 = new JLabel("x:");
		gridLabel1.setLabelFor(ncbGridTickX);
		gridLabel2 = new JLabel("y:");
		gridLabel2.setLabelFor(ncbGridTickY);
		gridLabel3 = new JLabel("\u03B8" + ":"); // Theta
		gridLabel3.setLabelFor(cbGridTickAngle);

		JPanel tickPanel = LayoutUtil.flowPanel(cbGridManualTick, gridLabel1,
				ncbGridTickX, gridLabel2, ncbGridTickY, gridLabel3,
				cbGridTickAngle);
		typePanel = new JPanel();
		typePanel.setLayout(new BoxLayout(typePanel, BoxLayout.Y_AXIS));
		addComboGridType();
		typePanel.add(LayoutUtil.flowPanel(cbGridManualTick));
		typePanel.add(LayoutUtil.flowPanel(Box.createHorizontalStrut(20),
				gridLabel1, ncbGridTickX, gridLabel2, ncbGridTickY, gridLabel3,
				cbGridTickAngle));

	}

	protected void addComboGridType() {
		typePanel.add(LayoutUtil.flowPanel(cbGridType));
	}

	private void initGridStylePanel() {

		// line style
		DashListRenderer renderer = new DashListRenderer();
		renderer.setPreferredSize(new Dimension(80, app.getGUIFontSize() + 6));
		cbGridStyle = new JComboBox(EuclidianView.getLineTypes());
		cbGridStyle.setRenderer(renderer);
		cbGridStyle.addActionListener(this);

		// color
		lblColor = new JLabel(app.getMenu("Color") + ":");
		lblColor.setLabelFor(btGridColor);

		// bold
		cbBoldGrid = new JCheckBox(app.getMenu("Bold"));
		cbBoldGrid.addActionListener(this);

		// style panel
		stylePanel = new JPanel();
		stylePanel.setLayout(new BoxLayout(stylePanel, BoxLayout.Y_AXIS));

		stylePanel.add(LayoutUtil.flowPanel(cbGridStyle));
		stylePanel.add(LayoutUtil.flowPanel(lblColor, btGridColor, cbBoldGrid));

	}

	private JPanel buildGridPanel() {

		// show grid
		cbShowGrid = new JCheckBox(app.getPlain("ShowGrid"));
		cbShowGrid.addActionListener(this);
		JPanel showGridPanel = LayoutUtil.flowPanel(cbShowGrid);

		initGridTypePanel();
		initGridStylePanel();

		JPanel gridPanel = new JPanel(new FullWidthLayout());
		fillGridPanel(showGridPanel, gridPanel);

		return gridPanel;
	}

	protected void fillGridPanel(JPanel showGridPanel, JPanel gridPanel) {
		gridPanel.add(showGridPanel);
		gridPanel.add(typePanel);
		gridPanel.add(stylePanel);
	}

	final protected void updateMinMax() {

		tfMinX.removeActionListener(this);
		tfMaxX.removeActionListener(this);
		tfMinY.removeActionListener(this);
		tfMaxY.removeActionListener(this);
		view.updateBoundObjects();
		tfMinX.setText(view.getXminObject().getLabel(
				StringTemplate.editTemplate));
		tfMaxX.setText(view.getXmaxObject().getLabel(
				StringTemplate.editTemplate));
		tfMinY.setText(view.getYminObject().getLabel(
				StringTemplate.editTemplate));
		tfMaxY.setText(view.getYmaxObject().getLabel(
				StringTemplate.editTemplate));
		tfMinX.addActionListener(this);
		tfMaxX.addActionListener(this);
		tfMinY.addActionListener(this);
		tfMaxY.addActionListener(this);

	}

	@Override
	final public void updateBounds() {

		if (!isSelected)
			return;

		updateMinMax();

		tfAxesRatioX.removeActionListener(this);
		tfAxesRatioY.removeActionListener(this);
		double xscale = view.getXscale();
		double yscale = view.getYscale();
		if (xscale >= yscale) {
			tfAxesRatioX.setText("1");
			tfAxesRatioY.setText(nfAxesRatio.format(xscale / yscale));
		} else {
			tfAxesRatioX.setText(nfAxesRatio.format(yscale / xscale));
			tfAxesRatioY.setText("1");
		}
		tfAxesRatioX.addActionListener(this);
		tfAxesRatioY.addActionListener(this);

	}

	public void updateAxes(GColor color, boolean isShown, boolean isBold) {
		btAxesColor.setForeground(org.geogebra.desktop.awt.GColorD.getAwtColor(color));
		cbShowAxes.removeActionListener(this);
		cbShowAxes.setSelected(isShown);
		cbShowAxes.addActionListener(this);

		cbBoldAxes.removeActionListener(this);
		cbBoldAxes.setSelected(isBold);
		cbBoldAxes.addActionListener(this);

	}

	public void updateGrid(GColor color, boolean isShown, boolean isBold,
			int gridType) {
		btGridColor.setForeground(org.geogebra.desktop.awt.GColorD.getAwtColor(color));

		cbShowGrid.removeActionListener(this);
		cbShowGrid.setSelected(isShown);
		cbShowGrid.addActionListener(this);

		cbBoldGrid.removeActionListener(this);
		cbBoldGrid.setSelected(isBold);
		cbBoldGrid.addActionListener(this);

		cbGridType.removeActionListener(this);
		cbGridType.setSelectedIndex(gridType);
		cbGridType.addActionListener(this);

	}

	public void updateGUI() {
		btBackgroundColor.setForeground(org.geogebra.desktop.awt.GColorD.getAwtColor(view
				.getBackgroundCommon()));
		cbTooltips.removeActionListener(this);
		cbAxesStyle.removeActionListener(this);
		cbGridStyle.removeActionListener(this);

		model.updateProperties();

		cbGridStyle.addActionListener(this);
		cbAxesStyle.addActionListener(this);
		cbTooltips.addActionListener(this);

		xAxisPanel.updatePanel();
		yAxisPanel.updatePanel();

	}

	public void updateConsProtocolPanel(boolean isVisible) {

		// cons protocol panel
		ckShowNavbar.setSelected(isVisible);
		ckNavPlay.setSelected(((GuiManagerD) app.getGuiManager())
				.isConsProtNavigationPlayButtonVisible());
		ckOpenConsProtocol.setSelected(((GuiManagerD) app.getGuiManager())
				.isConsProtNavigationProtButtonVisible());

		ckNavPlay.setEnabled(isVisible);
		ckOpenConsProtocol.setEnabled(isVisible);

	}

	protected void setTypePanelLabel() {
		typePanel.setBorder(LayoutUtil.titleBorder(app.getPlain("GridType")));
	}

	public void setLabels() {
		setTypePanelLabel();

		int index = cbGridType.getSelectedIndex();
		cbGridType.removeActionListener(this);
		cbGridType.removeAllItems();
		model.fillGridTypeCombo();
		cbGridType.setSelectedIndex(index);
		cbGridType.addActionListener(this);

		cbGridManualTick.setText(app.getPlain("TickDistance") + ":");
		stylePanel.setBorder(LayoutUtil.titleBorder(app.getPlain("LineStyle")));

		// color
		lblColor.setText(app.getMenu("Color") + ":");
		cbBoldGrid.setText(app.getMenu("Bold"));

		// TODO --- finish set labels
		cbShowGrid.setText(app.getPlain("ShowGrid"));

		// tab titles
		setTabLabels();

		// window dimension panel
		dimLabel[0].setText(app.getPlain("xmin") + ":");
		dimLabel[1].setText(app.getPlain("xmax") + ":");
		dimLabel[2].setText(app.getPlain("ymin") + ":");
		dimLabel[3].setText(app.getPlain("ymax") + ":");
		axesRatioLabel.setText(app.getPlain("xAxis") + " : "
				+ app.getPlain("yAxis"));

		setLabelsForCbView();

		cbShowMouseCoords.setText(app.getMenu("ShowMouseCoordinates"));

		// axis
		xAxisPanel.setLabels();
		yAxisPanel.setLabels();

		// construction protocol panel
		consProtocolPanel.setBorder(LayoutUtil.titleBorder(app
				.getPlain("ConstructionProtocolNavigation")));
		ckShowNavbar.setText(app.getPlain("Show"));
		ckNavPlay.setText(app.getPlain("PlayButton"));
		ckOpenConsProtocol.setText(app.getPlain("ConstructionProtocolButton"));

		/*
		 * if (!app.isApplet())
		 * restoreDefaultsButton.setText(app.getMenu("ApplyDefaults"));
		 */
	}

	protected void setTabLabels() {
		tabbedPane.setTitleAt(0, app.getMenu("Properties.Basic"));
		tabbedPane.setTitleAt(1, app.getPlain("xAxis"));
		tabbedPane.setTitleAt(2, app.getPlain("yAxis"));
		tabbedPane.setTitleAt(3, app.getMenu("Grid"));

		app.setComponentOrientation(tabbedPane);
	}

	protected void setLabelsForCbView() {

		backgroundColor.setText(app.getPlain("BackgroundColor") + ":");
		cbShowMouseCoords.setText(app.getMenu("ShowMouseCoordinates"));
		tooltips.setText(app.getPlain("Tooltips") + ":");

		color.setText(app.getMenu("Color") + ":");
		lineStyle.setText(app.getPlain("LineStyle") + ":");

		int index = cbTooltips.getSelectedIndex();
		cbTooltips.removeActionListener(this);
		cbTooltips.removeAllItems();
		model.fillTooltipCombo();
		cbTooltips.setSelectedIndex(index);
		cbTooltips.addActionListener(this);

		dimPanel.setBorder(LayoutUtil.titleBorder(app.getPlain("Dimensions")));
		axesOptionsPanel.setBorder(LayoutUtil.titleBorder(app.getMenu("Axes")));
		miscPanel.setBorder(LayoutUtil.titleBorder(app
				.getPlain("Miscellaneous")));

		cbShowAxes.setText(app.getPlain("ShowAxes"));
		cbBoldAxes.setText(app.getPlain("Bold"));

		lblAxisLabelStyle.setText(app.getPlain("LabelStyle"));
		cbAxisLabelBold.setText(app.getPlain("Bold"));
		cbAxisLabelItalic.setText(app.getPlain("Italic"));
	}

	public void actionPerformed(ActionEvent e) {
		doActionPerformed(e.getSource());
	}

	protected void actionBtBackgroundColor() {
		model.applyBackgroundColor();
	}

	protected void doActionPerformed(Object source) {
		if (source == btBackgroundColor) {
			actionBtBackgroundColor();
		} else if (source == btAxesColor) {
			model.applyAxesColor(new org.geogebra.desktop.awt.GColorD(((GuiManagerD) app
					.getGuiManager()).showColorChooser(view.getAxesColor())));

		} else if (source == btGridColor) {
			model.applyGridColor(new org.geogebra.desktop.awt.GColorD(((GuiManagerD) (app
					.getGuiManager())).showColorChooser(view.getGridColor())));

		} else if (source == cbTooltips) {
			model.applyTooltipMode(cbTooltips.getSelectedIndex());

		} else if (source == cbShowAxes) {
			model.showAxes(cbShowAxes.isSelected());

		} else if (source == cbBoldAxes) {
			model.applyBoldAxes(cbBoldAxes.isSelected(),
					cbShowAxes.isSelected());

		} else if (source == cbShowGrid) {
			model.showGrid(cbShowGrid.isSelected());

		} else if (source == cbBoldGrid) {
			model.applyBoldGrid(cbBoldGrid.isSelected());

		} else if (source == cbShowMouseCoords) {
			model.applyMouseCoords(cbShowMouseCoords.isSelected());

		} else if (source == cbGridType) {
			model.appyGridType(cbGridType.getSelectedIndex());

		} else if (source == cbAxesStyle) {

			model.appyAxesStyle(((Integer) cbAxesStyle.getSelectedItem())
					.intValue()
			// make sure bold checkbox doesn't change
					+ (cbBoldAxes.isSelected() ? EuclidianStyleConstants.AXES_BOLD
							: 0));
		} else if (source == cbAxisLabelBold) {
			model.setAxisFontBold(cbAxisLabelBold.isSelected());

		} else if (source == cbAxisLabelItalic) {
			model.setAxisFontItalic(cbAxisLabelItalic.isSelected());

		}

		else if (source == cbGridStyle) {
			model.appyGridStyle(((Integer) cbGridStyle.getSelectedItem())
					.intValue());

		} else if (source == cbGridManualTick) {
			model.appyGridManualTick(cbGridManualTick.isSelected());

		} else if (source == tfAxesRatioX || source == tfAxesRatioY) {
			double xval = parseDouble(tfAxesRatioX.getText());
			double yval = parseDouble(tfAxesRatioY.getText());
			model.applyAxesRatio(xval, yval);

		} else if (source == cbLockRatio) {
			if (cbLockRatio.isSelected()) {
				model.applyLockRatio(parseDouble(tfAxesRatioX.getText())
						/ parseDouble(tfAxesRatioY.getText()));
			} else {
				model.applyLockRatio(null);
			}

		} else if (source == tfMinX || source == tfMaxX || source == tfMaxY
				|| source == tfMinY) {

			String text = ((JTextField) source).getText();
			MinMaxType type = null;
			if (source == tfMinX) {
				type = MinMaxType.minX;
			} else if (source == tfMaxX) {
				type = MinMaxType.maxX;
			} else if (source == tfMinY) {
				type = MinMaxType.minY;
			} else if (source == tfMaxY) {
				type = MinMaxType.maxY;
			}
			model.applyMinMax(text, type);
		}

		view.updateBackground();
		updateGUI();
	}

	protected void setViewFromIndex(int index) {

		if (index == 0)
			setView(app.getEuclidianView1());
		else
			setView(((GuiManagerD) (app.getGuiManager()))
					.getEuclidianView2(index));

	}

	protected double parseDouble(String text) {
		if (text == null || text.equals(""))
			return Double.NaN;
		return kernel.getAlgebraProcessor().evaluateToDouble(text);
	}

	public void itemStateChanged(ItemEvent e) {
		Object source = e.getSource();
		if (e.getStateChange() != ItemEvent.SELECTED)
			return;

		if (source == ncbGridTickX) {
			model.applyGridTicks(ncbGridTickX.getValue(), 0);
		}

		else if (source == ncbGridTickY) {
			model.applyGridTicks(ncbGridTickY.getValue(), 1);
		}

		else if (source == cbGridTickAngle) {
			model.applyGridTickAngle(cbGridTickAngle.getSelectedIndex());
		}

		view.updateBackground();
		updateGUI();
	}

	public void focusGained(FocusEvent arg0) {
	}

	public void focusLost(FocusEvent e) {
		// handle focus changes in text fields
		doActionPerformed(e.getSource());

	}

	/**
	 * set which tab is visible
	 * 
	 * @param constant
	 *            xAxis, yAxis, ...
	 */
	public void setSelectedTab(Construction.Constants constant) {
		switch (constant) {
		case X_AXIS:
			tabbedPane.setSelectedIndex(1);
			break;
		case Y_AXIS:
			tabbedPane.setSelectedIndex(2);
			break;
		default:
			tabbedPane.setSelectedIndex(0);
			break;
		}
	}

	/**
	 * 
	 * @return selected tab
	 */
	public int getSelectedTab() {
		return tabbedPane.getSelectedIndex();
	}

	/**
	 * select the correct tab
	 * 
	 * @param index
	 *            index
	 */
	public void setSelectedTab(int index) {
		tabbedPane.setSelectedIndex(index);
	}

	public JPanel getWrappedPanel() {
		return this.wrappedPanel;
	}

	public void revalidate() {
		getWrappedPanel().revalidate();

	}

	public void setBorder(Border border) {
		wrappedPanel.setBorder(border);
	}

	public void applyModifications() {
		// override this method to make the properties view apply modifications
		// when panel changes
	}

	public void updateFont() {

		Font font = app.getPlainFont();
		updateFont(font);
		reinit();
		setLabels();
	}

	/**
	 * update font
	 * 
	 * @param font
	 *            font
	 */
	protected void updateFont(Font font) {

		setTypePanelLabel();

		cbGridType.setFont(font);

		cbGridManualTick.setFont(font);
		stylePanel.setFont(font);

		// color
		lblColor.setFont(font);
		cbBoldGrid.setFont(font);

		cbShowGrid.setFont(font);

		// tab titles
		tabbedPane.setFont(font);

		// window dimension panel
		for (int i = 0; i < 4; i++)
			dimLabel[i].setFont(font);
		axesRatioLabel.setFont(font);

		backgroundColor.setFont(font);
		cbShowMouseCoords.setFont(font);
		tooltips.setFont(font);

		color.setFont(font);
		lineStyle.setFont(font);

		cbTooltips.setFont(font);

		dimPanel.setFont(font);
		axesOptionsPanel.setFont(font);
		miscPanel.setFont(font);
		cbShowAxes.setFont(font);
		cbBoldAxes.setFont(font);

		cbShowMouseCoords.setFont(font);

		// axis
		xAxisPanel.updateFont();
		yAxisPanel.updateFont();

		cbAxesStyle.setFont(font);
		cbGridType.setFont(font);
		cbGridStyle.setFont(font);
		cbGridTickAngle.setFont(font);
		cbTooltips.setFont(font);

		tfAxesRatioX.setFont(font);
		tfAxesRatioY.setFont(font);

		tfMinX.setFont(font);
		tfMaxX.setFont(font);
		tfMinY.setFont(font);
		tfMaxY.setFont(font);

		// construction protocol panel
		consProtocolPanel.setFont(font);
		ckShowNavbar.setFont(font);
		ckNavPlay.setFont(font);
		ckOpenConsProtocol.setFont(font);


	}

	private boolean isSelected = false;

	public void setSelected(boolean flag) {
		isSelected = flag;
	}

	/**
	 * Initialize the construction protocol panel.
	 */
	private void initConsProtocolPanel() {

		consProtocolPanel = new JPanel();
		consProtocolPanel.setLayout(new BoxLayout(consProtocolPanel,
				BoxLayout.Y_AXIS));

		ckShowNavbar = new JCheckBox();
		ckShowNavbar.addActionListener(showConsProtNavigationAction);
		consProtocolPanel.add(LayoutUtil.flowPanel(ckShowNavbar));

		int tab = 20;
		ckNavPlay = new JCheckBox();
		ckNavPlay.addActionListener(showConsProtNavigationPlayAction);
		consProtocolPanel.add(LayoutUtil.flowPanel(tab, ckNavPlay));

		ckOpenConsProtocol = new JCheckBox();
		ckOpenConsProtocol
				.addActionListener(showConsProtNavigationOpenProtAction);
		consProtocolPanel.add(LayoutUtil.flowPanel(tab, ckOpenConsProtocol));

	}

	Action showConsProtNavigationAction = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			app.toggleShowConstructionProtocolNavigation(view.getViewID());
		}
	};

	Action showConsProtNavigationPlayAction = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			Collection<ConstructionProtocolNavigation> cpns = app
					.getGuiManager().getAllConstructionProtocolNavigations();
			for (ConstructionProtocolNavigation cpn : cpns) {
				cpn.setPlayButtonVisible(!cpn.isPlayButtonVisible());
				// cpn.initGUI();
				SwingUtilities
						.updateComponentTreeUI(((ConstructionProtocolNavigationD) cpn)
								.getImpl());
			}
			app.setUnsaved();
			updateGUI();
		}
	};

	Action showConsProtNavigationOpenProtAction = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {

			Collection<ConstructionProtocolNavigation> cpns = app
					.getGuiManager().getAllConstructionProtocolNavigations();
			for (ConstructionProtocolNavigation cpn : cpns) {
				cpn.setConsProtButtonVisible(!cpn.isConsProtButtonVisible());
				// cpn.initGUI();
				SwingUtilities
						.updateComponentTreeUI(((ConstructionProtocolNavigationD) cpn)
								.getImpl());
			}
			app.setUnsaved();
			updateGUI();
		}
	};

	public GColor getEuclidianBackground(int viewNumber) {
		return new org.geogebra.desktop.awt.GColorD(
				((GuiManagerD) (app.getGuiManager()))
						.showColorChooser(app.getSettings()
								.getEuclidian(viewNumber).getBackground()));
	}

	public void enableAxesRatio(boolean value) {
		tfAxesRatioX.setEnabled(value);
		tfAxesRatioY.setEnabled(value);
		cbLockRatio.setIcon(app.getScaledIcon(value ? "object_unfixed.png"
				: "object_fixed.png"));

	}

	public void setMinMaxText(String minX, String maxX, String minY, String maxY) {
		tfMinX.setText(minX);
		tfMaxX.setText(maxX);
		tfMinY.setText(minY);
		tfMaxY.setText(maxY);

	}

	public void addTooltipItem(String item) {
		cbTooltips.addItem(item);
	}

	public void selectTooltipType(int index) {
		cbTooltips.setSelectedIndex(index);
	}

	public void showMouseCoords(boolean value) {
		cbShowMouseCoords.removeActionListener(this);
		cbShowMouseCoords.setSelected(value);
		cbShowMouseCoords.addActionListener(this);
	}

	public void selectAxesStyle(int index) {
		cbAxesStyle.removeActionListener(this);
		cbAxesStyle.setSelectedIndex(index);
		cbAxesStyle.addActionListener(this);

	}

	public void updateGridTicks(boolean isAutoGrid, double[] gridTicks,
			int gridType) {
		ncbGridTickX.removeItemListener(this);
		ncbGridTickY.removeItemListener(this);
		cbGridTickAngle.removeItemListener(this);

		if (gridType != EuclidianView.GRID_POLAR) {

			ncbGridTickY.setVisible(true);
			gridLabel2.setVisible(true);
			cbGridTickAngle.setVisible(false);
			gridLabel3.setVisible(false);

			ncbGridTickX.setValue(gridTicks[0]);
			ncbGridTickY.setValue(gridTicks[1]);
			gridLabel1.setText("x:");

		} else {
			ncbGridTickY.setVisible(false);
			gridLabel2.setVisible(false);
			cbGridTickAngle.setVisible(true);
			gridLabel3.setVisible(true);

			ncbGridTickX.setValue(gridTicks[0]);
			int val = (int) (view.getGridDistances(2) * 12 / Math.PI) - 1;
			if (val == 5)
				val = 4; // handle Pi/2 problem
			cbGridTickAngle.setSelectedIndex(val);
			gridLabel1.setText("r:");
		}

		ncbGridTickX.setEnabled(!isAutoGrid);
		ncbGridTickY.setEnabled(!isAutoGrid);
		cbGridTickAngle.setEnabled(!isAutoGrid);
		ncbGridTickX.addItemListener(this);
		ncbGridTickY.addItemListener(this);
		cbGridTickAngle.addItemListener(this);

	}

	public void enableLock(boolean value) {
		cbLockRatio.setEnabled(value);
	}

	public void selectGridStyle(int style) {
		// TODO Auto-generated method stub

	}

	public void addGridTypeItem(String item) {
		cbGridType.addItem(item);
	}

	public void addAngleOptionItem(String item) {
		cbGridTickAngle.addItem(item);
	}

	public void updateBackgroundColor(GColor color) {
		// TODO Auto-generated method stub

	}

	public void reinit() {
		wrappedPanel.removeAll();
		initGUI();
	}

	public void updateAxisFontStyle(boolean isBold, boolean isItalic) {
		cbAxisLabelBold.setSelected(isBold);
		cbAxisLabelItalic.setSelected(isItalic);
	}
}
