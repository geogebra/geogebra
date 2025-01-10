package org.geogebra.desktop.gui.view.probcalculator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.data.PlotSettings;
import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.gui.view.probcalculator.ProbabilityManager;
import org.geogebra.common.gui.view.probcalculator.StatisticsCalculator;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings;
import org.geogebra.common.properties.impl.distribution.DistributionTypeProperty;
import org.geogebra.desktop.euclidianND.EuclidianViewInterfaceD;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.inputfield.MyTextFieldD;
import org.geogebra.desktop.gui.util.LayoutUtil;
import org.geogebra.desktop.gui.util.ListSeparatorRenderer;
import org.geogebra.desktop.gui.view.data.PlotPanelEuclidianViewD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.util.GuiResourcesD;

/**
 * Dialog that displays the graphs of various probability density functions with
 * interactive controls for calculating interval probabilities.
 * 
 * @author G. Sturr
 * 
 */
@SuppressWarnings("javadoc")
public class ProbabilityCalculatorViewD extends ProbabilityCalculatorView
		implements ActionListener, FocusListener, ChangeListener {

	private ProbabilityCalculatorStyleBarD styleBar;

	// GUI elements
	private JComboBox<String> comboDistribution;
	private JTextField[] fldParameterArray;
	private JLabel[] lblParameterArray;
	private JLabel lblDist;
	private JToggleButton btnCumulative;
	private JToggleButton btnIntervalLeft;
	private JToggleButton btnIntervalBetween;
	private JToggleButton btnIntervalTwoTailed;
	private JToggleButton btnIntervalRight;

	private ListSeparatorRenderer comboRenderer;

	// GUI layout panels
	private JPanel controlPanel;
	private JPanel tablePanel;
	private JSplitPane mainSplitPane;
	private JSplitPane plotSplitPane;

	private JToggleButton btnExport;

	private final JPanel wrapperPanel;

	private JLabel lblMeanSigma;

	private JPanel plotPanelPlus;

	private JPanel probCalcPanel;

	private final JTabbedPane tabbedPane;

	private final StatisticsCalculator statCalculator;
	private int defaultDividerSize;
	private ResultPanelD resultPanel;

	private final DistributionTypeProperty distributionType;

	/*************************************************
	 * Construct ProbabilityCalculator
	 * 
	 * @param app application
	 */
	public ProbabilityCalculatorViewD(AppD app) {
		super(app);
		distributionType = new DistributionTypeProperty(loc, this);
		wrapperPanel = new JPanel();

		createGUIElements();
		createLayoutPanels();
		buildProbCalcPanel();
		isIniting = false;

		statCalculator = new StatisticsCalculatorD(app);

		tabbedPane = new JTabbedPane();
		tabbedPane.addTab(loc.getMenu("Distribution"), probCalcPanel);
		tabbedPane.addTab(loc.getMenu("Statistics"),
				((StatisticsCalculatorD) statCalculator).getWrappedPanel());
		tabbedPane.addChangeListener(e -> {
			if (styleBar != null) {
				styleBar.updateLayout();
			}
		});

		wrapperPanel.setLayout(new BorderLayout());
		wrapperPanel.add(tabbedPane, BorderLayout.CENTER);

		setLabels();

		attachView();

		settingsChanged(app.getSettings().getProbCalcSettings());

		tabbedPane.setSelectedIndex(app.getSettings().getProbCalcSettings()
				.getCollection().isActive() ? 1 : 0);
	}

	@Override
	protected void updateStylebar() {
		if (styleBar != null) {
			styleBar.updateGUI();
		}
	}

	/**
	 * @return The style bar for this view.
	 */
	public ProbabilityCalculatorStyleBarD getStyleBar() {
		if (styleBar == null) {
			styleBar = new ProbabilityCalculatorStyleBarD((AppD) app, this);
		}

		return styleBar;
	}

	// =================================================
	// Getters/Setters
	// =================================================

	@Override
	public ProbabilityManager getProbManager() {
		return probManager;
	}

	@Override
	public boolean isDistributionTabOpen() {
		return tabbedPane.getSelectedIndex() == 0;
	}

	// =================================================
	// GUI
	// =================================================

	private void createLayoutPanels() {
		try {
			// control panel
			createControlPanel();
			controlPanel.setBorder(BorderFactory.createEmptyBorder());
			controlPanel.setMinimumSize(controlPanel.getPreferredSize());

			// plot panel (extension of EuclidianView)
			setPlotPanel(new PlotPanelEuclidianViewD(app.getKernel(),
					exportToEVAction));
			getPlotPanel().setMouseEnabled(true, true);
			getPlotPanel().setMouseMotionEnabled(true);
			((EuclidianViewInterfaceD) getPlotPanel())
					.setBorder(BorderFactory.createEmptyBorder());

			// plot label panel
			JPanel plotLabelPanel = LayoutUtil.flowPanelRight(0, 0, 0,
					lblMeanSigma, Box.createHorizontalStrut(10));
			plotLabelPanel
					.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
			plotLabelPanel.setBackground(Color.white);
			// plot panel with label field below
			plotPanelPlus = new JPanel(new BorderLayout());
			plotPanelPlus.add(((EuclidianViewInterfaceD) getPlotPanel()).getJPanel(),
					BorderLayout.CENTER);
			plotPanelPlus.add(plotLabelPanel, BorderLayout.SOUTH);

			// table panel
			setTable(new ProbabilityTableD((AppD) app, this));
			((ProbabilityTableD) getTable()).getWrappedPanel()
					.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0,
							SystemColor.controlShadow));
			tablePanel = new JPanel(new BorderLayout());
			tablePanel.add(((ProbabilityTableD) getTable()).getWrappedPanel(),
					BorderLayout.CENTER);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void buildProbCalcPanel() {

		wrapperPanel.removeAll();

		plotSplitPane = new JSplitPane();
		plotSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		plotSplitPane.setLeftComponent(plotPanelPlus);
		plotSplitPane.setResizeWeight(1);
		plotSplitPane.setBorder(BorderFactory.createEmptyBorder());
		defaultDividerSize = plotSplitPane.getDividerSize();

		JScrollPane scroller = new JScrollPane(controlPanel);
		scroller.setBorder(BorderFactory.createEmptyBorder());

		mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, plotSplitPane,
				scroller);
		mainSplitPane.setResizeWeight(1);
		mainSplitPane.setBorder(BorderFactory.createEmptyBorder());

		probCalcPanel = new JPanel(new BorderLayout());
		probCalcPanel.add(mainSplitPane, BorderLayout.CENTER);
		probCalcPanel.setBorder(BorderFactory.createEmptyBorder(0, 2, 2, 2));

	}

	@Override
	protected void addRemoveTable(boolean showTable) {
		if (showTable) {
			plotSplitPane.setRightComponent(tablePanel);
			plotSplitPane.setDividerSize(defaultDividerSize);
		} else {
			plotSplitPane.setRightComponent(null);
			plotSplitPane.setDividerSize(0);
		}
	}

	private ListSeparatorRenderer getComboRenderer() {
		if (comboRenderer == null) {
			comboRenderer = new ListSeparatorRenderer();
		}
		return comboRenderer;

	}

	private void createGUIElements() {

		setLabelArrays();
		comboDistribution = new JComboBox<>();
		comboDistribution.setRenderer(getComboRenderer());
		comboDistribution.setMaximumRowCount(
				ProbabilityCalculatorSettings.distCount + 1);
		// setComboDistribution();
		comboDistribution.addActionListener(this);
		lblDist = new JLabel();

		btnCumulative = makeButton(((AppD) app)
				.getScaledIcon(GuiResourcesD.CUMULATIVE_DISTRIBUTION));

		btnIntervalLeft = makeButton(
				((AppD) app).getScaledIcon(GuiResourcesD.INTERVAL_LEFT));
		btnIntervalBetween = makeButton(
				((AppD) app).getScaledIcon(GuiResourcesD.INTERVAL_BETWEEN));
		btnIntervalTwoTailed = makeButton(
				((AppD) app).getScaledIcon(GuiResourcesD.INTERVAL_TWO_TAILED));
		btnIntervalRight = makeButton(
				((AppD) app).getScaledIcon(GuiResourcesD.INTERVAL_RIGHT));

		btnCumulative.addActionListener(this);
		btnIntervalLeft.addActionListener(this);
		btnIntervalBetween.addActionListener(this);
		btnIntervalTwoTailed.addActionListener(this);
		btnIntervalRight.addActionListener(this);

		ButtonGroup gp = new ButtonGroup();
		gp.add(btnIntervalLeft);
		gp.add(btnIntervalBetween);
		gp.add(btnIntervalTwoTailed);
		gp.add(btnIntervalRight);

		// create export button
		btnExport = new JToggleButton();
		btnExport.setIcon(((AppD) app).getScaledIcon(GuiResourcesD.EXPORT16));
		btnExport.setFocusable(false);
		btnExport.addActionListener(this);

		lblParameterArray = new JLabel[maxParameterCount];
		fldParameterArray = new JTextField[maxParameterCount];

		for (int i = 0; i < maxParameterCount; ++i) {
			lblParameterArray[i] = new JLabel();
			fldParameterArray[i] = new MyTextFieldD((AppD) app);
			fldParameterArray[i].setColumns(5);
			fldParameterArray[i].addActionListener(this);
			fldParameterArray[i].addFocusListener(this);
		}

		lblMeanSigma = new JLabel();
	}

	private void createControlPanel() {

		// distribution combo box panel
		JPanel cbPanel = new JPanel(new BorderLayout());
		cbPanel.add(comboDistribution, ((LocalizationD) loc).borderWest());

		// parameter panel
		JPanel parameterPanel = new JPanel(
				new FlowLayout(FlowLayout.LEFT, 8, 0));

		for (int i = 0; i < maxParameterCount; ++i) {
			parameterPanel.add(lblParameterArray[i]);
			parameterPanel.add(fldParameterArray[i]);
		}

		// interval panel

		JPanel tb = LayoutUtil.flowPanel(0, 0, 0, btnIntervalLeft,
				btnIntervalBetween, btnIntervalTwoTailed, btnIntervalRight);
		// tb.setFloatable(false);
		// tb.add(btnIntervalLeft);
		// tb.add(btnIntervalBetween);
		// tb.add(btnIntervalRight);
		// tb.addSeparator();

		JPanel p = new JPanel(new BorderLayout(0, 0));
		p.add(LayoutUtil.flowPanel(2, 0, 0, btnCumulative, cbPanel),
				((LocalizationD) loc).borderWest());
		p.add(LayoutUtil.flowPanelRight(0, 0, 0, lblMeanSigma,
				Box.createHorizontalStrut(10)),
				((LocalizationD) loc).borderEast());
		controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
		controlPanel.add(LayoutUtil.flowPanel(0, 0, 0, btnCumulative, cbPanel));
		controlPanel.add(LayoutUtil.flowPanel(4, 5, 20, parameterPanel));
		controlPanel.add(LayoutUtil.flowPanel(2, 5, 0, tb));
		resultPanel = new ResultPanelD((AppD) app, 4, 5, 20);
		resultPanel.addActionListener(this);
		resultPanel.addFocusListener(this);
		controlPanel.add(resultPanel);

	}

	// =================================================
	// Event Handlers
	// =================================================

	/**
	 * Update fonts
	 */
	public void updateFonts() {
		Font font = ((AppD) app).getPlainFont();
		wrapperPanel.setFont(font);
		GuiManagerD.setFontRecursive(this.wrapperPanel, font);
		lblDist.setFont(((AppD) app).getItalicFont());
		getPlotPanel().updateFonts();
		((ProbabilityTableD) getTable()).updateFonts(font);
		((StatisticsCalculatorD) statCalculator).updateFonts(font);
		btnCumulative.setIcon(((AppD) app)
				.getScaledIcon(GuiResourcesD.CUMULATIVE_DISTRIBUTION));

		btnIntervalLeft.setIcon(
				((AppD) app).getScaledIcon(GuiResourcesD.INTERVAL_LEFT));
		btnIntervalBetween.setIcon(
				((AppD) app).getScaledIcon(GuiResourcesD.INTERVAL_BETWEEN));
		btnIntervalTwoTailed.setIcon(
				((AppD) app).getScaledIcon(GuiResourcesD.INTERVAL_TWO_TAILED));
		btnIntervalRight.setIcon(
				((AppD) app).getScaledIcon(GuiResourcesD.INTERVAL_RIGHT));

		btnExport.setIcon(((AppD) app).getScaledIcon(GuiResourcesD.EXPORT16));
		if (styleBar != null) {
			styleBar.updateIcons();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (isIniting) {
			return;
		}
		Object source = e.getSource();

		if (source instanceof JTextField) {
			doTextFieldActionPerformed((JTextField) source);
		}

		if (source == comboDistribution) {

			if (comboDistribution.getSelectedItem() != null) {
				if (comboDistribution.getSelectedItem()
						.equals(ListSeparatorRenderer.SEPARATOR)) {
					comboDistribution.removeActionListener(this);
					comboDistribution
							.setSelectedIndex(distributionType.getIndex());
					comboDistribution.addActionListener(this);
				} else {
					distributionType.setIndex(comboDistribution.getSelectedIndex());
				}
			}
			wrapperPanel.requestFocus();
		}

		else if (source == btnCumulative) {
			setCumulative(btnCumulative.isSelected());
			disableInterval(btnCumulative.isSelected());
		} else if (source == btnIntervalLeft || source == btnIntervalBetween
				|| source == btnIntervalTwoTailed || source == btnIntervalRight) {

			btnIntervalLeft.removeActionListener(this);
			btnIntervalBetween.removeActionListener(this);
			btnIntervalTwoTailed.removeActionListener(this);
			btnIntervalRight.removeActionListener(this);

			if (!isCumulative) {
				changeProbabilityType();
				updateProbabilityType(resultPanel);
				updateGUI();
			}

			btnIntervalLeft.addActionListener(this);
			btnIntervalBetween.addActionListener(this);
			btnIntervalTwoTailed.addActionListener(this);
			btnIntervalRight.addActionListener(this);
		}

		else if (source == btnExport) {
			JPopupMenu menu = getPlotPanel()
					.getContextMenu();
			menu.show(btnExport,
					-menu.getPreferredSize().width + btnExport.getWidth(),
					btnExport.getHeight());
		}

	}

	private void doTextFieldActionPerformed(JTextField source) {
		if (isIniting) {
			return;
		}
		try {
			String inputText = source.getText().trim();
			// Double value = Double.parseDouble(source.getText());

			// allow input such as sqrt(2)
			NumberValue nv;
			nv = kernel.getAlgebraProcessor().evaluateToNumeric(inputText,
					false);
			GeoNumeric numericValue =
					nv instanceof GeoNumeric ? (GeoNumeric) nv : new GeoNumeric(cons, Double.NaN);
			double value = nv.getDouble();

			if (resultPanel.isFieldLow(source)) {
				if (isValidInterval(value, getHigh())) {
					setLow(value);
					setXAxisPoints();
				} else {
					updateGUI();
				}

			}

			else if (resultPanel.isFieldHigh(source)) {
				if (isValidInterval(getLow(), value)) {
					setHigh(value);
					setXAxisPoints();
				} else {
					updateGUI();
				}
			}

			// handle inverse probability
			else if (resultPanel.isFieldResult(source)) {
				if (value < 0 || value > 1) {
					updateGUI();
				} else {
					if (probMode == PROB_LEFT) {
						setHigh(inverseProbability(value));
					}
					if (probMode == PROB_RIGHT) {
						setLow(inverseProbability(1 - value));
					}
					setXAxisPoints();
				}
			} else {
				// handle parameter entry
				for (int i = 0; i < parameters.length; ++i) {
					if (source == fldParameterArray[i]) {
						if (isValidParameterChange(value, i)) {
							parameters[i] = numericValue;
							updateAll(true);
						}
					}
				}
			}

			updateIntervalProbability();
			updateGUI();

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
		updateGUI();
	}

	// =================================================
	// Update Methods
	// =================================================

	@Override
	public ResultPanelD getResultPanel() {
		return resultPanel;
	}

	@Override
	public void updateOutput(boolean updateDistributionView) {
		updateFonts();
		updateDistribution();
		updatePlotSettings();
		updateIntervalProbability();
		updateDiscreteTable();
		setXAxisPoints();
	}

	@Override
	protected void updateGUI() {

		// set visibility and text of the parameter labels and fields
		for (int i = 0; i < maxParameterCount; ++i) {

			boolean hasParam = i < ProbabilityManager.getParamCount(selectedDist);

			lblParameterArray[i].setVisible(hasParam);
			fldParameterArray[i].setVisible(hasParam);

			// hide sliders for now ... need to work out slider range for each
			// param (tricky)
			// sliderArray[i].setVisible(false);

			if (hasParam) {
				// set label
				lblParameterArray[i].setVisible(true);
				lblParameterArray[i]
						.setText(getParameterLabels()[selectedDist.ordinal()][i]);
				// set field
				fldParameterArray[i].removeActionListener(this);
				fldParameterArray[i].setText("" + format(parameters[i].getDouble()));
				fldParameterArray[i].setCaretPosition(0);
				fldParameterArray[i].addActionListener(this);
			}
		}
		updateLowHighResult();
		// set distribution combo box
		comboDistribution.removeActionListener(this);
		if (comboDistribution.getSelectedIndex()
				!= distributionType.getIndex()) {
			comboDistribution
					.setSelectedIndex(distributionType.getIndex());
		}

		comboDistribution.addActionListener(this);

		btnIntervalLeft.removeActionListener(this);
		btnIntervalBetween.removeActionListener(this);
		btnIntervalTwoTailed.removeActionListener(this);
		btnIntervalRight.removeActionListener(this);

		btnCumulative.setSelected(isCumulative);
		btnIntervalLeft.setSelected(probMode == PROB_LEFT);
		btnIntervalBetween.setSelected(probMode == PROB_INTERVAL);
		btnIntervalTwoTailed.setSelected(probMode == PROB_TWO_TAILED);
		btnIntervalRight.setSelected(probMode == PROB_RIGHT);

		btnIntervalLeft.addActionListener(this);
		btnIntervalBetween.addActionListener(this);
		btnIntervalTwoTailed.addActionListener(this);
		btnIntervalRight.addActionListener(this);

	}

	private void updateLowHighResult() {
		updateResult(resultPanel);
	}

	@Override
	protected void changeProbabilityType() {
		if (isCumulative) {
			probMode = PROB_LEFT;
		} else {
			int oldProbMode = probMode;
			if (btnIntervalLeft.isSelected()) {
				probMode = ProbabilityCalculatorView.PROB_LEFT;
			} else if (btnIntervalBetween.isSelected()) {
				probMode = ProbabilityCalculatorView.PROB_INTERVAL;
			} else if (btnIntervalTwoTailed.isSelected()) {
				probMode = ProbabilityCalculatorView.PROB_TWO_TAILED;
			} else {
				probMode = ProbabilityCalculatorView.PROB_RIGHT;
			}
			validateLowHigh(oldProbMode);
		}
	}

	@Override
	protected void onDistributionUpdate() {
		lblMeanSigma.setText(getMeanSigma());
		wrapperPanel.repaint();
	}

	@Override
	protected void updateDiscreteTable() {
		if (!isDiscreteProbability()) {
			return;
		}
		int[] firstXLastX = generateFirstXLastXCommon();
		getTable().setTable(selectedDist, parameters,
				firstXLastX[0], firstXLastX[1]);
	}

	@Override
	public void setInterval(double low, double high) {
		resultPanel.removeActionListener(this);
		this.setLow(low);
		this.setHigh(high);
		resultPanel.updateLowHigh("" + low, "" + high);
		setXAxisPoints();
		updateIntervalProbability();
		updateGUI();
		resultPanel.addActionListener(this);
	}

	@Override
	public void setLabels() {
		tabbedPane.setTitleAt(0, loc.getMenu("Distribution"));

		((SetLabels) statCalculator).setLabels();
		tabbedPane.setTitleAt(1, loc.getMenu("Statistics"));

		setLabelArrays();

		lblDist.setText(loc.getMenu("Distribution") + ": ");
		resultPanel.setLabels();
		setDistributionComboBoxMenu();

		if (getTable() != null) {
			getTable().setLabels();
			selectProbabilityTableRows(); // update highlighting after table rebuild
		}
		if (styleBar != null) {
			styleBar.setLabels();
		}

		btnCumulative.setToolTipText(loc.getMenu("Cumulative"));

		btnIntervalLeft.setToolTipText(loc.getMenu("LeftProb"));
		btnIntervalRight.setToolTipText(loc.getMenu("RightProb"));
		btnIntervalBetween.setToolTipText(loc.getMenu("IntervalProb"));
		btnIntervalTwoTailed.setToolTipText(loc.getMenu("TwoTailedProb"));

		for (int i = 0; i < ProbabilityManager
				.getParamCount(selectedDist); i++) {
			lblParameterArray[i]
					.setText(getParameterLabels()[selectedDist.ordinal()][i]);
		}
	}

	private void setDistributionComboBoxMenu() {

		comboDistribution.removeActionListener(this);
		comboDistribution.removeAllItems();
		for (String distName: distributionType.getValueNames()) {
			comboDistribution.addItem(distName);
		}

		comboDistribution.setSelectedIndex(distributionType.getIndex());
		comboDistribution.addActionListener(this);
	}

	// ============================================================
	// Sliders
	// ============================================================

	@Override
	public void stateChanged(ChangeEvent e) {
		// nothing to do
	}

	@Override
	protected void plotPanelUpdateSettings(PlotSettings settings) {
		getPlotPanel().commonFields.updateSettings(getPlotPanel(),
				plotSettings);
	}

	// ============================================================
	// Export
	// ============================================================

	/**
	 * Action to export all GeoElements that are currently displayed in this
	 * panel to a EuclidianView. The viewID for the target EuclidianView is
	 * stored as a property with key "euclidianViewID".
	 * 
	 * This action is passed as a parameter to plotPanel where it is used in the
	 * plotPanel context menu and the EuclidianView transfer handler when the
	 * plot panel is dragged into an EV.
	 */
	AbstractAction exportToEVAction = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent event) {
			Integer euclidianViewID = (Integer) this
					.getValue("euclidianViewID");

			// if null ID then use EV1 unless shift is down, then use EV2
			if (euclidianViewID == null) {
				euclidianViewID = idForEvent();
			}

			// do the export
			exportGeosToEV(euclidianViewID);

			// null out the ID property
			this.putValue("euclidianViewID", null);
		}
	};

	@Override
	public PlotPanelEuclidianViewD getPlotPanel() {
		return (PlotPanelEuclidianViewD) super.getPlotPanel();
	}

	protected Integer idForEvent() {
		return ((AppD) app).getShiftDown()
				? app.getEuclidianView2(1).getViewID()
				: app.getEuclidianView1().getViewID();
	}

	/**
	 * Custom toggle button
	 */
	private static JToggleButton makeButton(Icon ic) {
		JToggleButton btn = new JToggleButton(ic);
		btn.setFocusPainted(false);
		btn.setMargin(new Insets(0, 0, 0, 0));
		return btn;
	}

	public JPanel getWrapperPanel() {
		return wrapperPanel;
	}

	@Override
	public boolean suggestRepaint() {
		return false;
		// only for web
	}

	@Override
	protected StatisticsCalculator getStatCalculator() {
		return statCalculator;
	}

	@Override
	public void disableInterval(boolean disable) {
		btnIntervalLeft.setEnabled(!disable);
		btnIntervalBetween.setEnabled(!disable);
		btnIntervalTwoTailed.setEnabled(!disable);
		btnIntervalRight.setEnabled(!disable);
	}
}
