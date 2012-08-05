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
import geogebra.common.kernel.Construction;
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
import geogebra.gui.util.FullWidthLayout;
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
import javax.swing.border.Border;

/**
 * Panel with options for the euclidian view. TODO: optimization: updateGUI()
 * called too often (F.S.)
 * 
 * revised by G.Sturr 2010-8-15
 * 
 */
public class OptionsEuclidianD extends geogebra.common.gui.dialog.options.OptionsEuclidian implements OptionPanelD, ActionListener,
		FocusListener, ItemListener, SetLabels {

	private static final long serialVersionUID = 1L;

	public static final String PI_STR = "\u03c0";
	public static final String DEGREE_STR = "\u00b0";

	protected AppD app;
	private Kernel kernel;
	protected EuclidianViewND view;

	// GUI containers
	protected AxisPanel xAxisPanel, yAxisPanel;
	protected JTabbedPane tabbedPane;
	private JPanel dimPanel, stylePanel, typePanel, axesOptionsPanel,
			miscPanel, selectViewPanel;

	// GUI elements
	private JButton btBackgroundColor, btAxesColor, btGridColor;
	private JCheckBox cbShowAxes, cbShowGrid, cbBoldGrid, cbGridManualTick,
			cbShowMouseCoords;
	protected JComboBox cbAxesStyle, cbGridType, cbGridStyle, cbGridTickAngle, cbTooltips;

	private JTextField tfAxesRatioX, tfAxesRatioY;

	private NumberFormat nfAxesRatio;
	protected NumberComboBox ncbGridTickX, ncbGridTickY;

	protected JTextField tfMinX, tfMaxX, tfMinY, tfMaxY;

	private JLabel[] dimLabel;
	private JLabel axesRatioLabel, gridLabel1, gridLabel2, gridLabel3,
			lblColor, tooltips, backgroundColor, color, lineStyle;

	// flags
	private boolean isIniting;

	private JToggleButton cbLockRatio;

	private JPanel wrappedPanel;
	
	
	//private JButton restoreDefaultsButton;

	/***********************************************
	 * Creates a new dialog for the properties of the Euclidian view.
	 * 
	 * @param app
	 *            parent frame
	 */
	public OptionsEuclidianD(AppD app, EuclidianViewND view) {

		isIniting = true;
		this.app = app;
		kernel = app.getKernel();
		this.view = view;

		wrappedPanel = new JPanel();
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
		//restore default button	
		if (!app.isApplet()) {
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			
			restoreDefaultsButton = new JButton();
			restoreDefaultsButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				
					AbstractApplication.error("not implemented yet");

				}
			});

			panel.add(restoreDefaultsButton);
			add(panel, BorderLayout.SOUTH);
		}
		*/
		
	}

	
	


	protected void addTabs() {
		tabbedPane.addTab("", new JScrollPane(buildBasicPanel()));
		tabbedPane.addTab("",  new JScrollPane(xAxisPanel));
		tabbedPane.addTab("",  new JScrollPane(yAxisPanel));
		tabbedPane.addTab("", new JScrollPane(buildGridPanel()));
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
		cbLockRatio.setIcon(app.getImageIcon("lock.png"));
		cbLockRatio.setEnabled(view.isZoomable());
		cbLockRatio.addActionListener(this);
		axesRatioLabel = new JLabel("");

		dimPanel = new JPanel();
		dimPanel.setLayout(new BoxLayout(dimPanel, BoxLayout.Y_AXIS));

		dimPanel.add(OptionsUtil.flowPanel(dimLabel[0], tfMinX, dimLabel[1], tfMaxX));
		dimPanel.add(OptionsUtil.flowPanel(dimLabel[2], tfMinY, dimLabel[3], tfMaxY));

		dimPanel.add(OptionsUtil.flowPanel(axesRatioLabel));
		dimPanel.add(OptionsUtil.flowPanel(Box.createHorizontalStrut(20), tfAxesRatioX,
				new JLabel(" : "), tfAxesRatioY,cbLockRatio));
	}

	private void initAxesOptionsPanel() {

		// show axes checkbox
		cbShowAxes = new JCheckBox(app.getPlain("ShowAxes"));

		// axes color
		color = new JLabel(app.getPlain("Color") + ":");
		color.setLabelFor(btAxesColor);
		btAxesColor = new JButton("\u2588");
		btAxesColor.addActionListener(this);

		// axes style
		lineStyle = new JLabel(app.getPlain("LineStyle") + ":");
		lineStyle.setLabelFor(cbAxesStyle);
		cbAxesStyle = new JComboBox();
		cbAxesStyle.addItem("\u2014"); // line
		cbAxesStyle.addItem("\u2192"); // arrow
		cbAxesStyle.addItem("\u2014" + " " + app.getPlain("Bold")); // bold line
		cbAxesStyle.addItem("\u2192" + " " + app.getPlain("Bold")); // bold
																	// arrow
		cbAxesStyle.setEditable(false);

		// axes options panel
		axesOptionsPanel = new JPanel();
		axesOptionsPanel.setLayout(new BoxLayout(axesOptionsPanel,
				BoxLayout.Y_AXIS));
		axesOptionsPanel.add(OptionsUtil.flowPanel(cbShowAxes));
		axesOptionsPanel.add(OptionsUtil.flowPanel(color, btAxesColor,
				Box.createHorizontalStrut(20), lineStyle, cbAxesStyle));
	}

	private void initMiscPanel() {

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
		cbTooltips = new JComboBox(new String[] { app.getPlain("On"),
				app.getPlain("Automatic"), app.getPlain("Off") });
		cbTooltips.addActionListener(this);

		miscPanel = new JPanel();
		miscPanel.setLayout(new BoxLayout(miscPanel, BoxLayout.Y_AXIS));
		miscPanel.add(OptionsUtil.flowPanel(backgroundColor, btBackgroundColor));
		miscPanel.add(OptionsUtil.flowPanel(tooltips, cbTooltips));
		miscPanel.add(OptionsUtil.flowPanel(cbShowMouseCoords));

	}

	private JPanel buildBasicPanel() {

		initDimensionPanel();
		initAxesOptionsPanel();
		initMiscPanel();

		JPanel basicPanel = new JPanel();
		basicPanel.setLayout(new FullWidthLayout());
		basicPanel.add(dimPanel);
		basicPanel.add(axesOptionsPanel);
		basicPanel.add(miscPanel);

		return basicPanel;
	}

	private void initGridTypePanel() {

		// grid type combo box
		String[] gridTypeLabel = new String[3];
		gridTypeLabel[EuclidianView.GRID_CARTESIAN] = app
				.getMenu("Cartesian");
		gridTypeLabel[EuclidianView.GRID_ISOMETRIC] = app
				.getMenu("Isometric");
		gridTypeLabel[EuclidianView.GRID_POLAR] = app.getMenu("Polar");
		cbGridType = new JComboBox(gridTypeLabel);
		cbGridType.addActionListener(this);

		// tick intervals

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

		// checkbox for grid labels
		cbGridTickAngle = new JComboBox(angleOptions);
		cbGridTickAngle.addItemListener(this);

		// grid labels
		gridLabel1 = new JLabel("x:");
		gridLabel1.setLabelFor(ncbGridTickX);
		gridLabel2 = new JLabel("y:");
		gridLabel2.setLabelFor(ncbGridTickY);
		gridLabel3 = new JLabel("\u03B8" + ":"); // Theta
		gridLabel3.setLabelFor(cbGridTickAngle);

		JPanel tickPanel = OptionsUtil.flowPanel(cbGridManualTick, gridLabel1,
				ncbGridTickX, gridLabel2, ncbGridTickY, gridLabel3,
				cbGridTickAngle);
		typePanel = new JPanel();
		typePanel.setLayout(new BoxLayout(typePanel, BoxLayout.Y_AXIS));
		typePanel.add(OptionsUtil.flowPanel(cbGridType));
		typePanel.add(OptionsUtil.flowPanel(cbGridManualTick));
		typePanel.add(OptionsUtil.flowPanel(Box.createHorizontalStrut(20), gridLabel1,
				ncbGridTickX, gridLabel2, ncbGridTickY, gridLabel3,
				cbGridTickAngle));

	}

	private void initGridStylePanel() {

		// line style
		DashListRenderer renderer = new DashListRenderer();
		renderer.setPreferredSize(new Dimension(80, app.getGUIFontSize() + 6));
		cbGridStyle = new JComboBox(EuclidianViewD.getLineTypes());
		cbGridStyle.setRenderer(renderer);
		cbGridStyle.addActionListener(this);

		// color
		lblColor = new JLabel(app.getPlain("Color") + ":");
		lblColor.setLabelFor(btGridColor);

		// bold
		cbBoldGrid = new JCheckBox(app.getMenu("Bold"));
		cbBoldGrid.addActionListener(this);

		// style panel
		stylePanel = new JPanel();
		stylePanel.setLayout(new BoxLayout(stylePanel, BoxLayout.Y_AXIS));

		stylePanel.add(OptionsUtil.flowPanel(cbGridStyle));
		stylePanel.add(OptionsUtil.flowPanel(lblColor, btGridColor, cbBoldGrid));

	}

	protected JPanel buildBasicNorthPanel() {
		return null;
	}

	private JPanel buildGridPanel() {

		// show grid
		cbShowGrid = new JCheckBox(app.getPlain("ShowGrid"));
		cbShowGrid.addActionListener(this);
		JPanel showGridPanel = OptionsUtil.flowPanel(cbShowGrid);

		initGridTypePanel();
		initGridStylePanel();

		JPanel gridPanel = new JPanel(new FullWidthLayout());
		gridPanel.add(showGridPanel);
		gridPanel.add(typePanel);
		gridPanel.add(stylePanel);

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


		tfAxesRatioX.setEnabled(view.isZoomable() && !view.isLockedAxesRatio());
		tfAxesRatioY.setEnabled(view.isZoomable() && !view.isLockedAxesRatio());
		cbLockRatio.setEnabled(view.isZoomable());

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
		typePanel.setBorder(OptionsUtil.titleBorder(app.getPlain("GridType")));

		int index = cbGridType.getSelectedIndex();
		cbGridType.removeActionListener(this);
		cbGridType.removeAllItems();
		cbGridType.addItem(app.getMenu("Cartesian"));
		cbGridType.addItem(app.getMenu("Isometric"));
		cbGridType.addItem(app.getMenu("Polar"));
		cbGridType.setSelectedIndex(index);
		cbGridType.addActionListener(this);

		cbGridManualTick.setText(app.getPlain("TickDistance") + ":");
		stylePanel.setBorder(OptionsUtil.titleBorder(app.getPlain("LineStyle")));

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
				+ app.getPlain("yAxis"));

		setLabelsForCbView();

		cbShowMouseCoords.setText(app.getMenu("ShowMouseCoordinates"));
		
		//axis
		xAxisPanel.setLabels();
		yAxisPanel.setLabels();
		
		
		/*
		if (!app.isApplet()) 
			restoreDefaultsButton.setText(app.getMenu("ApplyDefaults"));
			*/
	}

	protected void setTabLabels() {
		tabbedPane.setTitleAt(0, app.getMenu("Properties.Basic"));
		tabbedPane.setTitleAt(1, app.getPlain("xAxis"));
		tabbedPane.setTitleAt(2, app.getPlain("yAxis"));
		tabbedPane.setTitleAt(3, app.getMenu("Grid"));
	}

	protected void setLabelsForCbView() {

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

		dimPanel.setBorder(OptionsUtil.titleBorder(app.getPlain("Dimensions")));
		axesOptionsPanel.setBorder(OptionsUtil.titleBorder(app.getMenu("Axes")));
		miscPanel.setBorder(OptionsUtil.titleBorder(app.getPlain("Miscellaneous")));

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
		} else if(source == cbLockRatio){
			if(cbLockRatio.isSelected()){
				view.setLockedAxesRatio(parseDouble(tfAxesRatioX.getText())/parseDouble(tfAxesRatioY.getText()));
			}
			else
				view.setLockedAxesRatio(null);
			tfAxesRatioX.setEnabled(view.isZoomable() && !view.isLockedAxesRatio() );
			tfAxesRatioY.setEnabled(view.isZoomable() && !view.isLockedAxesRatio() );

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

	/**
	 * set which tab is visible
	 * @param constant xAxis, yAxis, ...
	 */
	public void setSelectedTab(Construction.Constants constant){
		switch (constant){
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
	public int getSelectedTab(){
		return tabbedPane.getSelectedIndex();
	}
	
	/**
	 * select the correct tab
	 * @param index index
	 */
	public void setSelectedTab(int index){
		tabbedPane.setSelectedIndex(index);
	}

	public JPanel getWrappedPanel() {
		return this.wrappedPanel;
	}

	public void revalidate() {
		// TODO Auto-generated method stub
		
	}

	public void setBorder(Border border) {
		wrappedPanel.setBorder(border);
	}
	
	public void applyModifications(){
		//override this method to make the properties view apply modifications
		//when panel changes
	}

}
