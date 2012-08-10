/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.gui.dialog.options;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.gui.SetLabels;
import geogebra.common.gui.util.TableSymbols;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.common.util.Unicode;
import geogebra.euclidian.EuclidianViewD;
import geogebra.euclidianND.EuclidianViewND;
import geogebra.gui.NumberComboBox;
import geogebra.gui.dialog.DashListRenderer;
import geogebra.gui.inputfield.MyTextField;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 * Panel with options for the euclidian view. TODO: optimization: updateGUI()
 * called too often (F.S.)
 * 
 * revised by G.Sturr 2010-8-15
 * 
 */
public class OptionsEuclidian2 extends JPanel implements OptionPanelD,
		ActionListener, FocusListener, ItemListener, SetLabels {

	private static final long serialVersionUID = 1L;

	private static final String PI_STR = "\u03c0";
	private static final String DEGREE_STR = "\u00b0";

	protected AppD app;
	private Kernel kernel;
	protected EuclidianViewND view;

	// GUI
	protected JTabbedPane tabbedPane;
	private JLabel[] dimLabel;
	private JLabel axesRatioLabel;
	private JPanel dimPanel;

	protected JButton btBackgroundColor;

	protected JButton btAxesColor;

	protected JButton btGridColor;
	protected JCheckBox cbShowAxes, cbShowGrid, cbBoldGrid, cbGridManualTick;

	protected JCheckBox cbShowMouseCoords;
	protected JComboBox cbAxesStyle;

	protected JComboBox cbGridType, cbGridStyle, cbGridTickAngle;

	protected JComboBox cbView;

	protected JComboBox cbTooltips;
	private JTextField tfAxesRatioX, tfAxesRatioY;
	private NumberFormat nfAxesRatio;
	protected NumberComboBox ncbGridTickX, ncbGridTickY;
	protected JTextField tfMinX, tfMaxX, tfMinY, tfMaxY;
	protected AxisPanel xAxisPanel, yAxisPanel;
	private JLabel gridLabel1, gridLabel2, gridLabel3;

	private boolean isIniting;

	private JPanel stylePanel;

	private JLabel lblColor;

	private JPanel typePanel;

	private JLabel tooltips;

	private JLabel backgroundColor;

	private JLabel color;

	private JLabel lineStyle;

	private JPanel axesOptionsPanel;

	/**
	 * Creates a new dialog for the properties of the Euclidian view.
	 * 
	 * @param app
	 *            parent frame
	 */
	public OptionsEuclidian2(AppD app, EuclidianViewND view) {

		isIniting = true;
		this.app = app;
		kernel = app.getKernel();
		this.view = view;

		// build GUI
		initGUI();
		isIniting = false;
	}

	public void setView(EuclidianViewND view) {
		this.view = view;
		if (!isIniting) {
			updateGUI();
		}
	}

	protected void initAxisPanels() {

		xAxisPanel = new AxisPanel(0);
		yAxisPanel = new AxisPanel(1);
	}

	/**
	 * inits GUI with labels of current language
	 */
	private void initGUI() {

		// create color buttons
		btBackgroundColor = new JButton("\u2588");
		btAxesColor = new JButton("\u2588");
		btGridColor = new JButton("\u2588");
		btBackgroundColor.addActionListener(this);
		btAxesColor.addActionListener(this);
		btGridColor.addActionListener(this);

		// setup axes ratio field
		nfAxesRatio = NumberFormat.getInstance(Locale.ENGLISH);
		nfAxesRatio.setMaximumFractionDigits(5);
		nfAxesRatio.setGroupingUsed(false);

		// create panels for the axes
		initAxisPanels();

		// create panel with comboBox to switch between Euclidian views
		createCbView();

		// create tabbed pane for basic, axes, and grid options
		tabbedPane = new JTabbedPane();

		/*
		 * single panel for both axes --- too wide? JPanel axesPanel = new
		 * JPanel(new FlowLayout(FlowLayout.LEFT)); axesPanel.add(xAxisPanel);
		 * axesPanel.add(Box.createRigidArea(new Dimension(16,0)));
		 */
		addTabs();

		// put it all together
		removeAll();
		setLayout(new BorderLayout());
		addCbView();
		add(tabbedPane, BorderLayout.CENTER);
	}

	private JPanel selectViewPanel;

	protected void createCbView() {
		cbView = new JComboBox();
		cbView.addItem(""); // ev
		cbView.addItem(""); // ev2
		cbView = new JComboBox();

		cbView.addActionListener(this);

		selectViewPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		selectViewPanel.add(cbView);
	}

	protected void addCbView() {
		add(selectViewPanel, BorderLayout.NORTH);
	}

	protected void addTabs() {
		tabbedPane.addTab("", buildBasicPanel());
		addAxisTabs();
		tabbedPane.addTab("", buildGridPanel());
	}

	protected void addAxisTabs() {
		tabbedPane.addTab("", xAxisPanel);
		tabbedPane.addTab("", yAxisPanel);

	}

	protected JPanel buildBasicNorthPanel() {

		// ===================================
		// create sub panels

		// -------------------------------------
		// window dimensions panel

		dimLabel = new JLabel[4]; // "Xmin", "Xmax" etc.
		for (int i = 0; i < 4; i++)
			dimLabel[i] = new JLabel("");

		JPanel xDimPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		tfMinX = new MyTextField(app, 8);
		tfMaxX = new MyTextField(app, 8);
		tfMinX.addActionListener(this);
		tfMaxX.addActionListener(this);
		tfMinX.addFocusListener(this);
		tfMaxX.addFocusListener(this);

		xDimPanel.add(dimLabel[0]);
		xDimPanel.add(tfMinX);
		xDimPanel.add(dimLabel[1]);
		xDimPanel.add(tfMaxX);

		JPanel yDimPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		tfMinY = new MyTextField(app, 8);
		tfMaxY = new MyTextField(app, 8);
		tfMinY.addActionListener(this);
		tfMaxY.addActionListener(this);
		tfMinY.addFocusListener(this);
		tfMaxY.addFocusListener(this);

		yDimPanel.add(dimLabel[2]);
		yDimPanel.add(tfMinY);
		yDimPanel.add(dimLabel[3]);
		yDimPanel.add(tfMaxY);

		JPanel axesRatioPanel = new JPanel(
				new FlowLayout(FlowLayout.LEFT, 5, 5));
		tfAxesRatioX = new MyTextField(app, 6);
		tfAxesRatioY = new MyTextField(app, 6);
		tfAxesRatioX.setEnabled(view.isZoomable());
		tfAxesRatioY.setEnabled(view.isZoomable());
		tfAxesRatioX.addActionListener(this);
		tfAxesRatioY.addActionListener(this);
		tfAxesRatioX.addFocusListener(this);
		tfAxesRatioY.addFocusListener(this);
		axesRatioLabel = new JLabel("");
		axesRatioPanel.add(axesRatioLabel);
		axesRatioPanel.add(tfAxesRatioX);
		axesRatioPanel.add(new JLabel(" : "));
		axesRatioPanel.add(tfAxesRatioY);

		dimPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		dimPanel.setLayout(new BoxLayout(dimPanel, BoxLayout.Y_AXIS));
		dimPanel.add(xDimPanel);
		dimPanel.add(yDimPanel);
		dimPanel.add(axesRatioPanel);
		dimPanel.setBorder(BorderFactory.createTitledBorder(app
				.getPlain("Dimensions")));

		// -------------------------------------
		// axes options panel
		axesOptionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		axesOptionsPanel.setBorder(BorderFactory.createTitledBorder(app
				.getMenu("Axes")));

		// show axes
		cbShowAxes = new JCheckBox(app.getPlain("ShowAxes"));
		axesOptionsPanel.add(cbShowAxes);
		axesOptionsPanel.add(Box.createRigidArea(new Dimension(10, 0)));

		// color
		color = new JLabel(app.getPlain("Color") + ":");
		color.setLabelFor(btAxesColor);
		axesOptionsPanel.add(color);
		axesOptionsPanel.add(btAxesColor);
		axesOptionsPanel.add(Box.createRigidArea(new Dimension(10, 0)));

		// axes style
		cbAxesStyle = new JComboBox();
		lineStyle = new JLabel(app.getPlain("LineStyle") + ":");
		lineStyle.setLabelFor(cbAxesStyle);
		cbAxesStyle.addItem("\u2014"); // line
		cbAxesStyle.addItem("\u2192"); // arrow
		cbAxesStyle.addItem("\u2014" + " " + app.getPlain("Bold")); // bold line
		cbAxesStyle.addItem("\u2192" + " " + app.getPlain("Bold")); // bold
																	// arrow
		cbAxesStyle.setEditable(false);
		axesOptionsPanel.add(lineStyle);
		axesOptionsPanel.add(cbAxesStyle);

		// -------------------------------------
		// background color panel
		JPanel bgPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		backgroundColor = new JLabel(app.getPlain("BackgroundColor") + ":");
		bgPanel.add(backgroundColor);
		bgPanel.add(btBackgroundColor);
		backgroundColor.setLabelFor(btBackgroundColor);

		bgPanel.add(Box.createHorizontalStrut(5));

		cbShowMouseCoords = new JCheckBox();
		cbShowMouseCoords.addActionListener(this);
		bgPanel.add(cbShowMouseCoords);

		bgPanel.add(Box.createHorizontalStrut(5));

		tooltips = new JLabel(app.getPlain("Tooltips") + ":");
		bgPanel.add(tooltips);

		cbTooltips = new JComboBox(new String[] { app.getPlain("On"),
				app.getPlain("Automatic"), app.getPlain("Off") });
		bgPanel.add(cbTooltips);
		cbTooltips.addActionListener(this);

		// ==========================================
		// create basic panel and add all sub panels

		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
		northPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 2, 5));

		northPanel.add(dimPanel);
		northPanel.add(Box.createRigidArea(new Dimension(0, 16)));
		northPanel.add(axesOptionsPanel);
		northPanel.add(Box.createRigidArea(new Dimension(0, 16)));
		northPanel.add(bgPanel);

		return northPanel;
	}

	private JPanel buildBasicPanel() {
		// use a BorderLayout to keep sub panels together
		JPanel basicPanel = new JPanel(new BorderLayout());
		basicPanel.add(buildBasicNorthPanel(), BorderLayout.NORTH);

		return basicPanel;
	}

	private JPanel buildGridPanel() {

		int hgap = 5;
		int vgap = 5;

		// ==================================================
		// create sub panels

		// -------------------------------------
		// show grid panel
		JPanel showGridPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, hgap,
				vgap));
		cbShowGrid = new JCheckBox(app.getPlain("ShowGrid"));
		cbShowGrid.addActionListener(this);
		showGridPanel.add(cbShowGrid, BorderLayout.NORTH);

		// -------------------------------------
		// grid type panel

		typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, hgap, vgap));
		typePanel.setBorder(BorderFactory.createTitledBorder((app
				.getPlain("GridType"))));

		// type
		String[] gridTypeLabel = new String[3];
		gridTypeLabel[EuclidianView.GRID_CARTESIAN] = app.getMenu("Cartesian");
		gridTypeLabel[EuclidianView.GRID_ISOMETRIC] = app.getMenu("Isometric");
		gridTypeLabel[EuclidianView.GRID_POLAR] = app.getMenu("Polar");
		cbGridType = new JComboBox(gridTypeLabel);
		cbGridType.addActionListener(this);
		typePanel.add(cbGridType);

		// tick intervals
		JPanel tickPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, hgap,
				vgap));
		cbGridManualTick = new JCheckBox(app.getPlain("TickDistance") + ":");
		ncbGridTickX = new NumberComboBox(app);
		ncbGridTickY = new NumberComboBox(app);

		cbGridManualTick.addActionListener(this);
		ncbGridTickX.addItemListener(this);
		ncbGridTickY.addItemListener(this);

		// angleStep intervals for polar grid lines
		String[] angleOptions = { Unicode.PI_STRING + "/12",
				Unicode.PI_STRING + "/6", Unicode.PI_STRING + "/4",
				Unicode.PI_STRING + "/3", Unicode.PI_STRING + "/2", };

		cbGridTickAngle = new JComboBox(angleOptions);
		cbGridTickAngle.addItemListener(this);
		tickPanel.add(cbGridManualTick);

		gridLabel1 = new JLabel("x:");
		gridLabel1.setLabelFor(ncbGridTickX);
		tickPanel.add(gridLabel1);
		tickPanel.add(ncbGridTickX);

		gridLabel2 = new JLabel("y:");
		gridLabel2.setLabelFor(ncbGridTickY);
		tickPanel.add(gridLabel2);
		tickPanel.add(ncbGridTickY);

		gridLabel3 = new JLabel("\u03B8" + ":"); // Theta
		gridLabel3.setLabelFor(cbGridTickAngle);
		tickPanel.add(gridLabel3);
		tickPanel.add(cbGridTickAngle);

		typePanel.add(tickPanel);

		// -------------------------------------
		// style panel
		stylePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, hgap, vgap));
		stylePanel.setBorder(BorderFactory.createTitledBorder((app
				.getPlain("LineStyle"))));

		// line style
		DashListRenderer renderer = new DashListRenderer();
		renderer.setPreferredSize(new Dimension(130, app.getGUIFontSize() + 6));
		cbGridStyle = new JComboBox(EuclidianView.getLineTypes());
		cbGridStyle.setRenderer(renderer);
		cbGridStyle.addActionListener(this);
		stylePanel.add(cbGridStyle);

		// color
		lblColor = new JLabel(app.getPlain("Color") + ":");
		lblColor.setLabelFor(btGridColor);
		// bold
		cbBoldGrid = new JCheckBox(app.getMenu("Bold"));
		cbBoldGrid.addActionListener(this);

		stylePanel.add(lblColor);
		stylePanel.add(btGridColor);
		stylePanel.add(cbBoldGrid);

		// ==================================================
		// create grid panel and add all the sub panels

		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
		northPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		northPanel.add(showGridPanel);
		northPanel.add(Box.createRigidArea(new Dimension(0, 16)));
		northPanel.add(typePanel);
		northPanel.add(Box.createRigidArea(new Dimension(0, 16)));
		northPanel.add(stylePanel);

		JPanel gridPanel = new JPanel(new BorderLayout());
		gridPanel.add(northPanel, BorderLayout.NORTH);

		return gridPanel;
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

	protected void setCbViewSelectedIndex() {
		if (view == app.getEuclidianView1())
			cbView.setSelectedIndex(0);
		else
			cbView.setSelectedIndex(1);
	}

	protected void updateGUIforCbView() {
		cbView.removeActionListener(this);
		setCbViewSelectedIndex();
		cbView.addActionListener(this);
	}

	public void updateGUI() {

		btBackgroundColor.setForeground(geogebra.awt.GColorD.getAwtColor(view
				.getBackgroundCommon()));
		btAxesColor.setForeground(geogebra.awt.GColorD.getAwtColor(view
				.getAxesColor()));
		btGridColor.setForeground(geogebra.awt.GColorD.getAwtColor(view
				.getGridColor()));

		cbShowAxes.removeActionListener(this);
		cbShowAxes.setSelected(view.getShowXaxis() && view.getShowYaxis());
		cbShowAxes.addActionListener(this);

		cbShowGrid.removeActionListener(this);
		cbShowGrid.setSelected(view.getShowGrid());
		cbShowGrid.addActionListener(this);

		if (view instanceof EuclidianViewD) {
			cbTooltips.removeActionListener(this);
			int ind = ((EuclidianView) view).getAllowToolTips();
			if (ind == EuclidianStyleConstants.TOOLTIPS_ON)
				cbTooltips.setSelectedIndex(0);
			else if (ind == EuclidianStyleConstants.TOOLTIPS_AUTOMATIC)
				cbTooltips.setSelectedIndex(1);
			else if (ind == EuclidianStyleConstants.TOOLTIPS_OFF)
				cbTooltips.setSelectedIndex(2);
			cbTooltips.addActionListener(this);
		}

		// Michael Borcherds 2008-04-11
		cbBoldGrid.removeActionListener(this);
		cbBoldGrid.setSelected(view.getGridIsBold());
		cbBoldGrid.addActionListener(this);

		cbShowMouseCoords.removeActionListener(this);
		cbShowMouseCoords.setSelected(view.getAllowShowMouseCoords());
		cbShowMouseCoords.addActionListener(this);

		updateGUIforCbView();

		tfAxesRatioX.setEnabled(view.isZoomable());
		tfAxesRatioY.setEnabled(view.isZoomable());

		updateMinMax();

		cbGridType.removeActionListener(this);
		cbGridType.setSelectedIndex(view.getGridType());
		cbGridType.addActionListener(this);

		cbAxesStyle.removeActionListener(this);
		cbAxesStyle.setSelectedIndex(view.getAxesLineStyle());
		cbAxesStyle.addActionListener(this);

		cbGridStyle.removeActionListener(this);
		int type = view.getGridLineStyle();
		for (int i = 0; i < cbGridStyle.getItemCount(); i++) {
			if (type == ((Integer) cbGridStyle.getItemAt(i)).intValue()) {
				cbGridStyle.setSelectedIndex(i);
				break;
			}
		}
		cbGridStyle.addActionListener(this);

		cbGridManualTick.removeActionListener(this);
		boolean autoGrid = view.isAutomaticGridDistance();
		cbGridManualTick.setSelected(!autoGrid);
		cbGridManualTick.addActionListener(this);

		ncbGridTickX.removeItemListener(this);
		ncbGridTickY.removeItemListener(this);
		cbGridTickAngle.removeItemListener(this);
		double[] gridTicks = view.getGridDistances();

		if (view.getGridType() != EuclidianView.GRID_POLAR) {

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

		ncbGridTickX.setEnabled(!autoGrid);
		ncbGridTickY.setEnabled(!autoGrid);
		cbGridTickAngle.setEnabled(!autoGrid);
		ncbGridTickX.addItemListener(this);
		ncbGridTickY.addItemListener(this);
		cbGridTickAngle.addItemListener(this);

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

		xAxisPanel.updatePanel();
		yAxisPanel.updatePanel();
	}

	public void setLabels() {
		typePanel.setBorder(BorderFactory.createTitledBorder((app
				.getPlain("GridType"))));

		int index = cbGridType.getSelectedIndex();
		cbGridType.removeActionListener(this);
		cbGridType.removeAllItems();
		cbGridType.addItem(app.getMenu("Cartesian"));
		cbGridType.addItem(app.getMenu("Isometric"));
		cbGridType.addItem(app.getMenu("Polar"));
		cbGridType.setSelectedIndex(index);
		cbGridType.addActionListener(this);

		cbGridManualTick.setText(app.getPlain("TickDistance") + ":");
		stylePanel.setBorder(BorderFactory.createTitledBorder((app
				.getPlain("LineStyle"))));

		// color
		lblColor.setText(app.getPlain("Color") + ":");
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
				+ app.getPlain("yAxis") + " = ");
		// dimPanelTitle = "ttt";

		setLabelsForCbView();

		cbShowMouseCoords.setText(app.getMenu("ShowMouseCoordinates"));
	}

	protected void setTabLabels() {
		tabbedPane.setTitleAt(0, app.getMenu("Properties.Basic"));
		tabbedPane.setTitleAt(1, app.getPlain("xAxis"));
		tabbedPane.setTitleAt(2, app.getPlain("yAxis"));
		tabbedPane.setTitleAt(3, app.getMenu("Grid"));
	}

	protected void setLabelsForCbView() {
		cbView.removeActionListener(this);
		cbView.removeAllItems();
		cbView.addItem(app.getPlain("DrawingPad"));
		cbView.addItem(app.getPlain("DrawingPad2"));
		cbView.removeActionListener(this);

		backgroundColor.setText(app.getPlain("BackgroundColor") + ":");
		cbShowMouseCoords.setText(app.getMenu("ShowMouseCoordinates"));
		tooltips.setText(app.getPlain("Tooltips") + ":");

		color.setText(app.getPlain("Color") + ":");
		lineStyle.setText(app.getPlain("LineStyle") + ":");

		int index = cbTooltips.getSelectedIndex();
		cbTooltips.removeActionListener(this);
		cbTooltips.removeAllItems();// = new JComboBox(new String[] {
									// app.getPlain("On"),
									// app.getPlain("Automatic"),
									// app.getPlain("Off") });
		cbTooltips.addItem(app.getPlain("On"));
		cbTooltips.addItem(app.getPlain("Automatic"));
		cbTooltips.addItem(app.getPlain("Off"));
		cbTooltips.setSelectedIndex(index);
		cbTooltips.addActionListener(this);

		dimPanel.setBorder(BorderFactory.createTitledBorder(app
				.getPlain("Dimensions")));
		axesOptionsPanel.setBorder(BorderFactory.createTitledBorder(app
				.getMenu("Axes")));
		cbShowAxes.setText(app.getPlain("ShowAxes"));
	}

	public void actionPerformed(ActionEvent e) {
		doActionPerformed(e.getSource());
	}

	protected void doActionPerformed(Object source) {
		if (source == btBackgroundColor) {
			if (view == app.getEuclidianView1()) {
				app.getSettings()
						.getEuclidian(1)
						.setBackground(
								new geogebra.awt.GColorD(app.getGuiManager()
										.showColorChooser(
												app.getSettings()
														.getEuclidian(1)
														.getBackground())));
			} else if (!app.hasEuclidianView2EitherShowingOrNot()) {
				view.setBackground(view.getBackgroundCommon());
			} else if (view == app.getEuclidianView2()) {
				app.getSettings()
						.getEuclidian(2)
						.setBackground(
								new geogebra.awt.GColorD(app.getGuiManager()
										.showColorChooser(
												app.getSettings()
														.getEuclidian(2)
														.getBackground())));
			} else {
				view.setBackground(view.getBackgroundCommon());
			}
		} else if (source == btAxesColor) {
			geogebra.common.awt.GColor col = new geogebra.awt.GColorD(app
					.getGuiManager().showColorChooser(view.getAxesColor()));
			if (view == app.getEuclidianView1()) {
				app.getSettings().getEuclidian(1).setAxesColor(col);
			} else if (!app.hasEuclidianView2EitherShowingOrNot()) {
				view.setAxesColor(col);
			} else if (view == app.getEuclidianView2()) {
				app.getSettings().getEuclidian(2).setAxesColor(col);
			} else {
				view.setAxesColor(col);
			}
		} else if (source == btGridColor) {
			geogebra.common.awt.GColor col = new geogebra.awt.GColorD(app
					.getGuiManager().showColorChooser(view.getGridColor()));
			if (view == app.getEuclidianView1()) {
				app.getSettings().getEuclidian(1).setGridColor(col);
			} else if (!app.hasEuclidianView2EitherShowingOrNot()) {
				view.setGridColor(col);
			} else if (view == app.getEuclidianView2()) {
				app.getSettings().getEuclidian(2).setGridColor(col);
			} else {
				view.setGridColor(col);
			}
		} else if (source == cbTooltips) {
			int ind = cbTooltips.getSelectedIndex();
			if (ind == 0) {
				ind = EuclidianStyleConstants.TOOLTIPS_ON;
			} else if (ind == 1) {
				ind = EuclidianStyleConstants.TOOLTIPS_AUTOMATIC;
			} else if (ind == 2) {
				ind = EuclidianStyleConstants.TOOLTIPS_OFF;
			}
			if (view instanceof EuclidianViewD) {
				if (view == app.getEuclidianView1()) {
					app.getSettings().getEuclidian(1).setAllowToolTips(ind);
				} else if (!app.hasEuclidianView2EitherShowingOrNot()) {
					((EuclidianView) view).setAllowToolTips(ind);
				} else if (view == app.getEuclidianView2()) {
					app.getSettings().getEuclidian(2).setAllowToolTips(ind);
				} else {
					((EuclidianView) view).setAllowToolTips(ind);
				}
			}
		} else if (source == cbShowAxes) {
			if (app.getEuclidianView1() == view) {
				app.getSettings()
						.getEuclidian(1)
						.setShowAxes(cbShowAxes.isSelected(),
								cbShowAxes.isSelected());
			} else if (!app.hasEuclidianView2EitherShowingOrNot()) {
				view.setShowAxes(cbShowAxes.isSelected(), true);
			} else if (app.getEuclidianView2() == view) {
				app.getSettings()
						.getEuclidian(2)
						.setShowAxes(cbShowAxes.isSelected(),
								cbShowAxes.isSelected());
			} else {
				view.setShowAxes(cbShowAxes.isSelected(), true);
			}
		} else if (source == cbShowGrid) {
			if (app.getEuclidianView1() == view) {
				app.getSettings().getEuclidian(1)
						.showGrid(cbShowGrid.isSelected());
			} else if (!app.hasEuclidianView2EitherShowingOrNot()) {
				view.showGrid(cbShowGrid.isSelected());
			} else if (app.getEuclidianView2() == view) {
				app.getSettings().getEuclidian(2)
						.showGrid(cbShowGrid.isSelected());
			} else {
				view.showGrid(cbShowGrid.isSelected());
			}
		} else if (source == cbBoldGrid) {
			if (app.getEuclidianView1() == view) {
				app.getSettings().getEuclidian(1)
						.setGridIsBold(cbBoldGrid.isSelected());
			} else if (!app.hasEuclidianView2EitherShowingOrNot()) {
				view.setGridIsBold(cbBoldGrid.isSelected());
			} else if (app.getEuclidianView2() == view) {
				app.getSettings().getEuclidian(2)
						.setGridIsBold(cbBoldGrid.isSelected());
			} else {
				view.setGridIsBold(cbBoldGrid.isSelected());
			}
		} else if (source == cbShowMouseCoords) {
			if (view == app.getEuclidianView1()) {
				app.getSettings()
						.getEuclidian(1)
						.setAllowShowMouseCoords(cbShowMouseCoords.isSelected());
			} else if (!app.hasEuclidianView2EitherShowingOrNot()) {
				view.setAllowShowMouseCoords(cbShowMouseCoords.isSelected());
			} else if (view == app.getEuclidianView2()) {
				app.getSettings()
						.getEuclidian(2)
						.setAllowShowMouseCoords(cbShowMouseCoords.isSelected());
			} else {
				view.setAllowShowMouseCoords(cbShowMouseCoords.isSelected());
			}
		} else if (source == cbGridType) {
			if (app.getEuclidianView1() == view) {
				app.getSettings().getEuclidian(1)
						.setGridType(cbGridType.getSelectedIndex());
			} else if (!app.hasEuclidianView2EitherShowingOrNot()) {
				view.setGridType(cbGridType.getSelectedIndex());
			} else if (app.getEuclidianView2() == view) {
				app.getSettings().getEuclidian(2)
						.setGridType(cbGridType.getSelectedIndex());
			} else {
				view.setGridType(cbGridType.getSelectedIndex());
			}
		} else if (source == cbAxesStyle) {
			if (view == app.getEuclidianView1()) {
				app.getSettings().getEuclidian(1)
						.setAxesLineStyle(cbAxesStyle.getSelectedIndex());
			} else if (!app.hasEuclidianView2EitherShowingOrNot()) {
				view.setAxesLineStyle(cbAxesStyle.getSelectedIndex());
			} else if (view == app.getEuclidianView2()) {
				app.getSettings().getEuclidian(2)
						.setAxesLineStyle(cbAxesStyle.getSelectedIndex());
			} else {
				view.setAxesLineStyle(cbAxesStyle.getSelectedIndex());
			}
		} else if (source == cbGridStyle) {
			int type = ((Integer) cbGridStyle.getSelectedItem()).intValue();

			if (app.getEuclidianView1() == view) {
				app.getSettings().getEuclidian(1).setGridLineStyle(type);
			} else if (!app.hasEuclidianView2EitherShowingOrNot()) {
				view.setGridLineStyle(type);
			} else if (app.getEuclidianView2() == view) {
				app.getSettings().getEuclidian(2).setGridLineStyle(type);
			} else {
				view.setGridLineStyle(type);
			}
		} else if (source == cbGridManualTick) {
			if (app.getEuclidianView1() == view) {
				app.getSettings()
						.getEuclidian(1)
						.setAutomaticGridDistance(
								!cbGridManualTick.isSelected(), true);
			} else if (!app.hasEuclidianView2EitherShowingOrNot()) {
				view.setAutomaticGridDistance(!cbGridManualTick.isSelected());
			} else if (app.getEuclidianView2() == view) {
				app.getSettings()
						.getEuclidian(2)
						.setAutomaticGridDistance(
								!cbGridManualTick.isSelected(), true);
			} else {
				view.setAutomaticGridDistance(!cbGridManualTick.isSelected());
			}
		} else if (source == tfAxesRatioX || source == tfAxesRatioY) {
			double xval = parseDouble(tfAxesRatioX.getText());
			double yval = parseDouble(tfAxesRatioY.getText());
			if (!(Double.isInfinite(xval) || Double.isNaN(xval)
					|| Double.isInfinite(yval) || Double.isNaN(yval))) {
				// ratio = xval / yval
				// xscale / yscale = ratio
				// => yscale = xscale * xval/yval
				view.setCoordSystem(view.getXZero(), view.getYZero(),
						view.getXscale(), view.getXscale() * xval / yval);
			}
		} else if (source == cbView) {

			setViewFromIndex(cbView.getSelectedIndex());

		} else if (source == tfMinX || source == tfMaxX || source == tfMaxY
				|| source == tfMinY) {

			NumberValue minMax = kernel.getAlgebraProcessor()
					.evaluateToNumeric(((JTextField) source).getText(), false);
			// not parsed to number => return all
			if (minMax == null) {
				tfMinX.setText(view.getXminObject().getLabel(
						StringTemplate.editTemplate));
				tfMaxX.setText(view.getXmaxObject().getLabel(
						StringTemplate.editTemplate));
				tfMinY.setText(view.getYminObject().getLabel(
						StringTemplate.editTemplate));
				tfMaxY.setText(view.getYmaxObject().getLabel(
						StringTemplate.editTemplate));
			} else {
				if (source == tfMinX) {
					if (view == app.getEuclidianView1()) {
						app.getSettings().getEuclidian(1)
								.setXminObject(minMax, true);
					} else if (!app.hasEuclidianView2EitherShowingOrNot()) {
						view.setXminObject(minMax);
					} else if (view == app.getEuclidianView2()) {
						app.getSettings().getEuclidian(2)
								.setXminObject(minMax, true);
					} else {
						view.setXminObject(minMax);
					}
				} else if (source == tfMaxX) {
					if (view == app.getEuclidianView1()) {
						app.getSettings().getEuclidian(1)
								.setXmaxObject(minMax, true);
					} else if (!app.hasEuclidianView2EitherShowingOrNot()) {
						view.setXmaxObject(minMax);
					} else if (view == app.getEuclidianView2()) {
						app.getSettings().getEuclidian(2)
								.setXmaxObject(minMax, true);
					} else {
						view.setXmaxObject(minMax);
					}
				} else if (source == tfMinY) {
					if (view == app.getEuclidianView1()) {
						app.getSettings().getEuclidian(1)
								.setYminObject(minMax, true);
					} else if (!app.hasEuclidianView2EitherShowingOrNot()) {
						view.setYminObject(minMax);
					} else if (view == app.getEuclidianView2()) {
						app.getSettings().getEuclidian(2)
								.setYminObject(minMax, true);
					} else {
						view.setYminObject(minMax);
					}
				} else if (source == tfMaxY) {
					if (view == app.getEuclidianView1()) {
						app.getSettings().getEuclidian(1)
								.setYmaxObject(minMax, true);
					} else if (!app.hasEuclidianView2EitherShowingOrNot()) {
						view.setYmaxObject(minMax);
					} else if (view == app.getEuclidianView2()) {
						app.getSettings().getEuclidian(2)
								.setYmaxObject(minMax, true);
					} else {
						view.setYmaxObject(minMax);
					}
				}
				view.setXminObject(view.getXminObject());
				tfAxesRatioX.setEnabled(view.isZoomable()
						&& !view.isLockedAxesRatio());
				tfAxesRatioY.setEnabled(view.isZoomable()
						&& !view.isLockedAxesRatio());
				view.updateBounds();
			}
		}

		view.updateBackground();
		updateGUI();
	}

	protected void setViewFromIndex(int index) {

		if (index == 0)
			setView(app.getEuclidianView1());
		else
			setView(app.getGuiManager().getEuclidianView2());

	}

	private double parseDouble(String text) {
		if (text == null || text.equals(""))
			return Double.NaN;
		else
			return kernel.getAlgebraProcessor().evaluateToDouble(text);
	}

	public void itemStateChanged(ItemEvent e) {
		Object source = e.getSource();
		if (e.getStateChange() != ItemEvent.SELECTED)
			return;

		if (source == ncbGridTickX) {
			double val = ncbGridTickX.getValue();
			if (val > 0) {
				double[] ticks = view.getGridDistances();
				ticks[0] = val;
				view.setGridDistances(ticks);
			}
		}

		else if (source == ncbGridTickY) {
			double val = ncbGridTickY.getValue();
			if (val > 0) {
				double[] ticks = view.getGridDistances();
				ticks[1] = val;
				view.setGridDistances(ticks);
			}
		}

		else if (source == cbGridTickAngle) {
			double val = cbGridTickAngle.getSelectedIndex();
			if (val >= 0) {
				double[] ticks = view.getGridDistances();
				// val = 4 gives 5*PI/12, skip this and go to 6*Pi/2 = Pi/2
				if (val == 4)
					val = 5;
				ticks[2] = (val + 1) * Math.PI / 12;
				view.setGridDistances(ticks);
			}
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

	// =======================================================
	// AxisPanel Class
	// =======================================================

	protected class AxisPanel extends JPanel implements ActionListener,
			ItemListener, FocusListener, SetLabels {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		protected int axis;
		protected JCheckBox cbShowAxis, cbAxisNumber, cbManualTicks,
				cbPositiveAxis, cbDrawAtBorder;
		protected NumberComboBox ncbTickDist;
		protected JComboBox cbTickStyle, cbAxisLabel, cbUnitLabel;
		protected JTextField tfCross;

		private JLabel crossAt;

		private JLabel axisTicks;

		private TitledBorder title;

		private JLabel axisLabel;

		private JLabel axisUnitLabel;

		private JLabel stickToEdge;

		final static protected int AXIS_X = 0;
		final static protected int AXIS_Y = 1;

		public AxisPanel(int axis) {

			this.axis = axis;

			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

			String strAxisEn = getString();
			title = BorderFactory.createTitledBorder(app.getPlain(strAxisEn));
			this.setBorder(title);

			cbShowAxis = new JCheckBox(app.getPlain("Show" + strAxisEn));
			cbAxisNumber = new JCheckBox(app.getPlain("ShowAxisNumbers"));
			ncbTickDist = new NumberComboBox(app);
			cbManualTicks = new JCheckBox(app.getPlain("TickDistance") + ":");

			cbShowAxis.addActionListener(this);
			cbAxisNumber.addActionListener(this);
			ncbTickDist.addItemListener(this);
			cbManualTicks.addActionListener(this);

			cbAxisLabel = new JComboBox();
			cbUnitLabel = new JComboBox();
			cbTickStyle = new JComboBox();
			cbAxisLabel.setEditable(true);
			cbUnitLabel.setEditable(true);
			cbTickStyle.setEditable(false);

			cbUnitLabel.addItem(null);
			cbUnitLabel.addItem(DEGREE_STR); // degrees
			cbUnitLabel.addItem(PI_STR); // pi
			cbUnitLabel.addItem("mm");
			cbUnitLabel.addItem("cm");
			cbUnitLabel.addItem("m");
			cbUnitLabel.addItem("km");

			cbAxisLabel.addItem(null);
			cbAxisLabel.addItem(axis == 0 ? "x" : "y");
			String[] greeks = TableSymbols.greekLowerCase;
			for (int i = 0; i < greeks.length; i++) {
				cbAxisLabel.addItem(greeks[i]);
			}

			cbTickStyle = new JComboBox();
			char big = '|';
			char small = '\'';
			cbTickStyle.addItem(" " + big + "  " + small + "  " + big + "  "
					+ small + "  " + big); // major and minor ticks
			cbTickStyle.addItem(" " + big + "     " + big + "     " + big); // major
																			// ticks
																			// only
			cbTickStyle.addItem(""); // no ticks

			cbAxisLabel.addActionListener(this);
			cbUnitLabel.addActionListener(this);
			cbTickStyle.addActionListener(this);

			JPanel showAxisPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,
					3, 5));
			showAxisPanel.add(cbShowAxis);

			JPanel showTicksPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,
					3, 5));
			axisTicks = new JLabel(app.getPlain("AxisTicks") + ":");
			showTicksPanel.add(axisTicks);
			showTicksPanel.add(cbTickStyle);

			// check box for positive axis
			JPanel showPosPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3,
					5));
			cbPositiveAxis = new JCheckBox(
					app.getPlain("PositiveDirectionOnly"));
			cbPositiveAxis.addActionListener(this);
			showPosPanel.add(cbPositiveAxis);

			JPanel numberPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3,
					5));
			numberPanel.add(cbAxisNumber);

			JPanel distancePanel = new JPanel(new FlowLayout(FlowLayout.LEFT,
					3, 5));
			distancePanel.add(cbManualTicks);
			distancePanel.add(ncbTickDist);

			JPanel labelPanel = new JPanel(
					new FlowLayout(FlowLayout.LEFT, 5, 5));
			axisLabel = new JLabel(app.getPlain("AxisLabel") + ":");
			labelPanel.add(axisLabel);
			labelPanel.add(cbAxisLabel);
			labelPanel.add(Box.createRigidArea(new Dimension(10, 0)));
			axisUnitLabel = new JLabel(app.getPlain("AxisUnitLabel") + ":");
			labelPanel.add(axisUnitLabel);
			labelPanel.add(cbUnitLabel);

			JPanel crossPanel = new JPanel(
					new FlowLayout(FlowLayout.LEFT, 5, 5));
			tfCross = new MyTextField(app, 6);
			tfCross.addActionListener(this);
			crossAt = new JLabel(app.getPlain("CrossAt") + ":");
			crossPanel.add(crossAt);
			crossPanel.add(tfCross);

			cbDrawAtBorder = new JCheckBox();
			cbDrawAtBorder.addActionListener(this);
			crossPanel.add(cbDrawAtBorder);
			stickToEdge = new JLabel(app.getPlain("StickToEdge"));
			crossPanel.add(stickToEdge);

			// add all panels
			add(showAxisPanel);
			add(numberPanel);
			add(showPosPanel);
			add(distancePanel);
			add(showTicksPanel);
			add(labelPanel);
			add(crossPanel);

			updatePanel();
		}

		protected String getString() {
			return (axis == AXIS_X) ? "xAxis" : "yAxis";
		}

		public void actionPerformed(ActionEvent e) {
			doActionPerformed(e.getSource());
		}

		private void doActionPerformed(Object source) {

			if (source == cbShowAxis) {
				if (app.getEuclidianView1() == view)
					app.getSettings().getEuclidian(1)
							.setShowAxis(axis, cbShowAxis.isSelected());
				else if (!app.hasEuclidianView2EitherShowingOrNot())
					view.setShowAxis(axis, cbShowAxis.isSelected(), true);
				else if (app.getEuclidianView2() == view)
					app.getSettings().getEuclidian(2)
							.setShowAxis(axis, cbShowAxis.isSelected());
				else
					view.setShowAxis(axis, cbShowAxis.isSelected(), true);
			}

			else if (source == cbAxisNumber) {
				boolean[] show = view.getShowAxesNumbers();
				show[axis] = cbAxisNumber.isSelected();
				view.setShowAxesNumbers(show);
			}

			else if (source == cbManualTicks) {

				if (app.getEuclidianView1() == view)
					app.getSettings()
							.getEuclidian(1)
							.setAutomaticAxesNumberingDistance(
									!cbManualTicks.isSelected(), axis, true);
				else if (!app.hasEuclidianView2EitherShowingOrNot())
					view.setAutomaticAxesNumberingDistance(
							!cbManualTicks.isSelected(), axis);
				else if (app.getEuclidianView2() == view)
					app.getSettings()
							.getEuclidian(2)
							.setAutomaticAxesNumberingDistance(
									!cbManualTicks.isSelected(), axis, true);
				else
					view.setAutomaticAxesNumberingDistance(
							!cbManualTicks.isSelected(), axis);
			}

			else if (source == cbUnitLabel) {
				Object ob = cbUnitLabel.getSelectedItem();
				String text = (ob == null) ? null : ob.toString().trim();
				String[] labels = view.getAxesUnitLabels();
				labels[axis] = text;
				view.setAxesUnitLabels(labels);
			}

			else if (source == cbAxisLabel) {
				Object ob = cbAxisLabel.getSelectedItem();
				String text = (ob == null) ? null : ob.toString().trim();
				view.setAxisLabel(axis, text);
			}

			else if (source == cbTickStyle) {
				int type = cbTickStyle.getSelectedIndex();
				int[] styles = view.getAxesTickStyles();
				styles[axis] = type;

				if (app.getEuclidianView1() == view)
					app.getSettings().getEuclidian(1)
							.setAxisTickStyle(axis, type);
				else if (!app.hasEuclidianView2EitherShowingOrNot())
					view.setAxesTickStyles(styles);
				else if (app.getEuclidianView2() == view)
					app.getSettings().getEuclidian(2)
							.setAxisTickStyle(axis, type);
				else
					view.setAxesTickStyles(styles);
			}

			else if (source == tfCross) {
				String str = tfCross.getText();
				if ("".equals(str))
					str = "0";
				double cross = parseDouble(str);
				if (!(Double.isInfinite(cross) || Double.isNaN(cross))) {
					double[] ac = view.getAxesCross();
					ac[axis] = cross;

					if (app.getEuclidianView1() == view)
						app.getSettings().getEuclidian(1)
								.setAxisCross(axis, cross);
					else if (!app.hasEuclidianView2EitherShowingOrNot())
						view.setAxesCross(ac);
					else if (app.getEuclidianView2() == view)
						app.getSettings().getEuclidian(2)
								.setAxisCross(axis, cross);
					else
						view.setAxesCross(ac);
				}

				tfCross.setText("" + view.getAxesCross()[axis]);
			}

			else if (source == cbPositiveAxis) {
				if (view == app.getEuclidianView1())
					app.getSettings().getEuclidian(1)
							.setPositiveAxis(axis, cbPositiveAxis.isSelected());
				else if (!app.hasEuclidianView2EitherShowingOrNot())
					view.setPositiveAxis(axis, cbPositiveAxis.isSelected());
				else if (view == app.getEuclidianView2())
					app.getSettings().getEuclidian(2)
							.setPositiveAxis(axis, cbPositiveAxis.isSelected());
				else
					view.setPositiveAxis(axis, cbPositiveAxis.isSelected());
			} else if (source == cbDrawAtBorder) {
				boolean[] border = view.getDrawBorderAxes();
				border[axis] = cbDrawAtBorder.isSelected();
				view.setDrawBorderAxes(border);
				if (!cbDrawAtBorder.isSelected())
					view.setAxisCross(axis, 0.0);
			}

			view.updateBackground();
			updateGUI();
		}

		public void itemStateChanged(ItemEvent e) {

			if (e.getStateChange() != ItemEvent.SELECTED)
				return;
			Object source = e.getSource();
			if (source == ncbTickDist) {
				double val = ncbTickDist.getValue();
				if (val > 0) {
					if (app.getEuclidianView1() == view)
						app.getSettings().getEuclidian(1)
								.setAxesNumberingDistance(val, axis);
					else if (!app.hasEuclidianView2EitherShowingOrNot())
						view.setAxesNumberingDistance(val, axis);
					else if (app.getEuclidianView2() == view)
						app.getSettings().getEuclidian(2)
								.setAxesNumberingDistance(val, axis);
					else
						view.setAxesNumberingDistance(val, axis);
				}
			}

			view.updateBackground();
			updateGUI();
		}

		public void updatePanel() {
			cbAxisNumber.removeActionListener(this);
			cbAxisNumber.setSelected(view.getShowAxesNumbers()[axis]);
			cbAxisNumber.addActionListener(this);

			cbManualTicks.removeActionListener(this);
			ncbTickDist.removeItemListener(this);

			cbManualTicks
					.setSelected(!view.isAutomaticAxesNumberingDistance()[axis]);
			ncbTickDist.setValue(view.getAxesNumberingDistances()[axis]);
			ncbTickDist.setEnabled(cbManualTicks.isSelected());

			cbManualTicks.addActionListener(this);
			ncbTickDist.addItemListener(this);

			cbAxisLabel.removeActionListener(this);
			cbAxisLabel.setSelectedItem(view.getAxesLabels()[axis]);
			cbAxisLabel.addActionListener(this);

			cbUnitLabel.removeActionListener(this);
			cbUnitLabel.setSelectedItem(view.getAxesUnitLabels()[axis]);
			cbUnitLabel.addActionListener(this);

			/*
			 * cbShowAxis.removeActionListener(this);
			 * cbShowAxis.setSelected(view.getShowXaxis());
			 * cbShowAxis.addActionListener(this);
			 */

			cbTickStyle.removeActionListener(this);
			int type = view.getAxesTickStyles()[axis];
			cbTickStyle.setSelectedIndex(type);
			cbTickStyle.addActionListener(this);

			cbShowAxis.removeActionListener(this);
			// cbShowAxis.setSelected(axis == 0 ? view.getShowXaxis() :
			// view.getShowYaxis());
			cbShowAxis.setSelected(view.getShowAxis(axis));
			cbShowAxis.addActionListener(this);

			tfCross.removeActionListener(this);
			if (view.getDrawBorderAxes()[axis])
				tfCross.setText("");
			else
				tfCross.setText("" + view.getAxesCross()[axis]);
			tfCross.setEnabled(!view.getDrawBorderAxes()[axis]);
			tfCross.addActionListener(this);
			tfCross.addFocusListener(this);

			cbPositiveAxis.removeActionListener(this);
			cbPositiveAxis.setSelected(view.getPositiveAxes()[axis]);
			cbPositiveAxis.addActionListener(this);

			cbDrawAtBorder.removeActionListener(this);
			cbDrawAtBorder.setSelected(view.getDrawBorderAxes()[axis]);
			cbDrawAtBorder.addActionListener(this);

		}

		public void focusGained(FocusEvent e) {
		}

		public void focusLost(FocusEvent e) {
			// (needed for textfields)
			doActionPerformed(e.getSource());
		}

		public void setLabels() {
			String strAxisEn = getString();
			title.setTitle(app.getPlain(strAxisEn));

			cbShowAxis.setText(app.getPlain("Show" + strAxisEn));
			cbAxisNumber.setText(app.getPlain("ShowAxisNumbers"));
			cbManualTicks.setText(app.getPlain("TickDistance") + ":");
			axisTicks.setText(app.getPlain("AxisTicks") + ":");
			cbPositiveAxis.setText(app.getPlain("PositiveDirectionOnly"));
			axisLabel.setText(app.getPlain("AxisLabel") + ":");
			axisUnitLabel.setText(app.getPlain("AxisUnitLabel") + ":");
			crossAt.setText(app.getPlain("CrossAt") + ":");
			stickToEdge.setText(app.getPlain("StickToEdge"));

		}

	} // end AxisPanel class

	public JPanel getWrappedPanel() {
		return this;
	}

	public void applyModifications() {
		// override this method to make the properties view apply modifications
		// when panel changes
	}
	
	

	public void updateFont() {
		// TODO Auto-generated method stub
		
	}
	
	

	public void setSelected(boolean flag){
		//see OptionsEuclidianD for possible implementation
	}

}
