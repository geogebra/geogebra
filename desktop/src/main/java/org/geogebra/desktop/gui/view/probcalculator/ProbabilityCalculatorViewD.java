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
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetViewInterface;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist;
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
	private JTextField fldLow, fldHigh, fldResult;
	private JLabel[] lblParameterArray;
	private JLabel lblBetween, lblProbOf, lblEndProbOf, lblDist;
	private JToggleButton btnCumulative, btnIntervalLeft, btnIntervalBetween,
			btnIntervalRight;

	// private JSlider[] sliderArray;
	private ListSeparatorRenderer comboRenderer;

	// GUI layout panels
	private JPanel controlPanel, tablePanel;
	private JSplitPane mainSplitPane, plotSplitPane;

	private JToggleButton btnExport;

	private JPanel wrapperPanel;

	private JLabel lblMeanSigma;

	private JPanel plotPanelPlus;

	private JPanel probCalcPanel;

	private JTabbedPane tabbedPane;

	private StatisticsCalculator statCalculator;
	private int defaultDividerSize;

	/*************************************************
	 * 
	 * Construct ProbabilityCalculator
	 * 
	 * @param app
	 */
	public ProbabilityCalculatorViewD(AppD app) {
		super(app);

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
		tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				updateStylebar();
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

	/**************** end constructor ****************/

	protected void updateStylebar() {
		if (styleBar != null) {
			styleBar.updateLayout();
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

	private void addRemoveTable(boolean showTable) {
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
		btnIntervalRight = makeButton(
				((AppD) app).getScaledIcon(GuiResourcesD.INTERVAL_RIGHT));

		btnCumulative.addActionListener(this);
		btnIntervalLeft.addActionListener(this);
		btnIntervalBetween.addActionListener(this);
		btnIntervalRight.addActionListener(this);

		ButtonGroup gp = new ButtonGroup();
		gp.add(btnIntervalLeft);
		gp.add(btnIntervalBetween);
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

		// create probability mode JComboBox and put it in a JPanel
		lblProbOf = new JLabel();
		lblBetween = new JLabel(); // <= X <=
		lblEndProbOf = new JLabel();
		fldLow = new MyTextFieldD((AppD) app);
		fldLow.setColumns(5);
		fldLow.addActionListener(this);
		fldLow.addFocusListener(this);

		fldHigh = new MyTextFieldD((AppD) app);
		fldHigh.setColumns(6);
		fldHigh.addActionListener(this);
		fldHigh.addFocusListener(this);

		fldResult = new MyTextFieldD((AppD) app);
		fldResult.setColumns(6);
		fldResult.addActionListener(this);
		fldResult.addFocusListener(this);

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
				btnIntervalBetween, btnIntervalRight);
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
		controlPanel.add(LayoutUtil.flowPanel(4, 5, 20, lblProbOf, fldLow,
				lblBetween, fldHigh, lblEndProbOf, fldResult));

	}

	// =================================================
	// Event Handlers
	// =================================================

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
							.setSelectedItem(getDistributionMap().get(selectedDist));
					comboDistribution.addActionListener(this);
				} else if (!selectedDist.equals(this.getReverseDistributionMap()
						.get(comboDistribution.getSelectedItem()))) {

					selectedDist = getReverseDistributionMap()
							.get(comboDistribution.getSelectedItem());
					parameters = ProbabilityManager
							.getDefaultParameters(selectedDist, cons);
					this.setProbabilityCalculator(selectedDist, parameters,
							isCumulative);
				}
			}
			wrapperPanel.requestFocus();
		}

		else if (source == btnCumulative) {
			setCumulative(btnCumulative.isSelected());

		} else if (source == btnIntervalLeft || source == btnIntervalBetween
				|| source == btnIntervalRight) {

			btnIntervalLeft.removeActionListener(this);
			btnIntervalBetween.removeActionListener(this);
			btnIntervalRight.removeActionListener(this);

			if (!isCumulative) {
				changeProbabilityType();
				updateProbabilityType();
				updateGUI();
			}

			btnIntervalLeft.addActionListener(this);
			btnIntervalBetween.addActionListener(this);
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

			if (source == fldLow) {
				if (isValidInterval(probMode, value, getHigh())) {
					setLow(value);
					setXAxisPoints();
				} else {
					updateGUI();
				}

			}

			else if (source == fldHigh) {
				if (isValidInterval(probMode, getLow(), value)) {
					setHigh(value);
					setXAxisPoints();
				} else {
					updateGUI();
				}
			}

			// handle inverse probability
			else if (source == fldResult) {
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
							updateAll();
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
	public void updateAll() {
		updateFonts();
		updateDistribution();
		updatePlotSettings();
		updateIntervalProbability();
		updateDiscreteTable();
		setXAxisPoints();
		updateProbabilityType();
		updateGUI();

		if (styleBar != null) {
			styleBar.updateGUI();
		}
	}

	@Override
	protected void updateGUI() {

		// set visibility and text of the parameter labels and fields
		for (int i = 0; i < maxParameterCount; ++i) {

			boolean hasParm = i < ProbabilityManager.getParmCount(selectedDist);

			lblParameterArray[i].setVisible(hasParm);
			fldParameterArray[i].setVisible(hasParm);

			// hide sliders for now ... need to work out slider range for each
			// parm (tricky)
			// sliderArray[i].setVisible(false);

			if (hasParm) {
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

		// set low/high interval field values
		fldLow.setText("" + format(getLow()));
		fldLow.setCaretPosition(0);
		fldHigh.setText("" + format(getHigh()));
		fldHigh.setCaretPosition(0);
		fldResult.setText(getProbabilityText());
		fldResult.setCaretPosition(0);

		// set distribution combo box
		comboDistribution.removeActionListener(this);
		if (!comboDistribution.getSelectedItem()
				.equals(getDistributionMap().get(selectedDist))) {
			comboDistribution
					.setSelectedItem(getDistributionMap().get(selectedDist));
		}

		comboDistribution.addActionListener(this);

		btnIntervalLeft.removeActionListener(this);
		btnIntervalBetween.removeActionListener(this);
		btnIntervalRight.removeActionListener(this);

		btnCumulative.setSelected(isCumulative);
		btnIntervalLeft.setSelected(probMode == PROB_LEFT);
		btnIntervalBetween.setSelected(probMode == PROB_INTERVAL);
		btnIntervalRight.setSelected(probMode == PROB_RIGHT);

		btnIntervalLeft.addActionListener(this);
		btnIntervalBetween.addActionListener(this);
		btnIntervalRight.addActionListener(this);

	}

	@Override
	protected void changeProbabilityType() {
		if (isCumulative) {
			probMode = PROB_LEFT;
		} else {
			if (btnIntervalLeft.isSelected()) {
				probMode = ProbabilityCalculatorView.PROB_LEFT;
			} else if (btnIntervalBetween.isSelected()) {
				probMode = ProbabilityCalculatorView.PROB_INTERVAL;
			} else {
				probMode = ProbabilityCalculatorView.PROB_RIGHT;
			}
		}
	}

	private void updateProbabilityType() {
		if (isIniting) {
			return;
		}

		boolean isDiscrete = probmanagerIsDiscrete();
		int oldProbMode = probMode;

		this.getPlotDimensions();

		if (probMode == PROB_INTERVAL) {
			lowPoint.setEuclidianVisible(showProbGeos);
			highPoint.setEuclidianVisible(showProbGeos);
			fldLow.setVisible(true);
			fldHigh.setVisible(true);
			lblBetween.setText(SpreadsheetViewInterface.X_BETWEEN);

			setLow(plotSettings.xMin
					+ 0.4 * (plotSettings.xMax - plotSettings.xMin));
			setHigh(plotSettings.xMin
					+ 0.6 * (plotSettings.xMax - plotSettings.xMin));

		}

		else if (probMode == PROB_LEFT) {
			lowPoint.setEuclidianVisible(false);
			highPoint.setEuclidianVisible(showProbGeos);
			fldLow.setVisible(false);
			fldHigh.setVisible(true);
			lblBetween.setText(loc.getMenu("XLessThanOrEqual"));

			if (oldProbMode == PROB_RIGHT) {
				setHigh(getLow());
			}

			if (isDiscrete) {
				setLow(((GeoNumeric) discreteValueList.get(0)).getDouble());
			}
			else {
				setLow(plotSettings.xMin - 1); // move offscreen so the integral
												// looks complete
			}

		}

		else if (probMode == PROB_RIGHT) {
			lowPoint.setEuclidianVisible(showProbGeos);
			highPoint.setEuclidianVisible(false);
			fldLow.setVisible(true);
			fldHigh.setVisible(false);
			lblBetween
					.setText(SpreadsheetViewInterface.LESS_THAN_OR_EQUAL_TO_X);

			if (oldProbMode == PROB_LEFT) {
				setLow(getHigh());
			}

			if (isDiscrete) {
				setHigh(((GeoNumeric) discreteValueList
						.get(discreteValueList.size() - 1)).getDouble());
			}
			else {
				setHigh(plotSettings.xMax + 1); // move offscreen so the
												// integral
												// looks complete
			}

		}

		// make result field editable for inverse probability calculation
		if (probMode != PROB_INTERVAL) {
			fldResult.setBackground(fldLow.getBackground());
			fldResult.setBorder(fldLow.getBorder());
			fldResult.setEditable(true);
			fldResult.setFocusable(true);

		} else {

			fldResult.setBackground(wrapperPanel.getBackground());
			fldResult.setBorder(BorderFactory.createEmptyBorder());
			fldResult.setEditable(false);
			fldResult.setFocusable(false);

		}

		if (isDiscrete) {
			setHigh(Math.round(getHigh()));
			setLow(Math.round(getLow()));

			// make sure arrow keys move points in 1s
			lowPoint.setAnimationStep(1);
			highPoint.setAnimationStep(1);
		} else {
			lowPoint.setAnimationStep(0.1);
			highPoint.setAnimationStep(0.1);
		}
		setXAxisPoints();
		updateIntervalProbability();
	}

	/**
	 * Sets the distribution type. This will destroy all GeoElements and create
	 * new ones.
	 */
	protected void updateDistribution() {

		hasIntegral = !isCumulative;
		createGeoElements();
		// setSliderDefaults();

		// update
		if (probmanagerIsDiscrete()) {
			discreteGraph.update();
			discreteIntervalGraph.update();
			// updateDiscreteTable();
			addRemoveTable(true);
			// this.fldParameterArray[0].requestFocus();

		} else {
			addRemoveTable(false);
			densityCurve.update();
			if (pdfCurve != null) {
				pdfCurve.update();
			}
			if (hasIntegral) {
				integral.update();
			}
		}

		lblMeanSigma.setText(getMeanSigma());
		wrapperPanel.repaint();

	}

	@Override
	protected void updateDiscreteTable() {
		if (!probmanagerIsDiscrete()) {
			return;
		}
		int[] firstXLastX = generateFirstXLastXCommon();
		getTable().setTable(selectedDist, parameters,
				firstXLastX[0], firstXLastX[1]);
	}

	protected void updatePrintFormat(int printDecimals1, int printFigures1) {
		this.printDecimals = printDecimals1;
		this.printFigures = printFigures1;
		updateGUI();
		updateDiscreteTable();
	}

	@Override
	public void setInterval(double low, double high) {
		fldHigh.removeActionListener(this);
		fldLow.removeActionListener(this);
		this.setLow(low);
		this.setHigh(high);
		fldLow.setText("" + low);
		fldHigh.setText("" + high);
		setXAxisPoints();
		updateIntervalProbability();
		updateGUI();
		fldHigh.addActionListener(this);
		fldLow.addActionListener(this);
	}

	@Override
	public void setLabels() {
		tabbedPane.setTitleAt(0, loc.getMenu("Distribution"));

		((SetLabels) statCalculator).setLabels();
		tabbedPane.setTitleAt(1, loc.getMenu("Statistics"));

		setLabelArrays();

		lblDist.setText(loc.getMenu("Distribution") + ": ");

		lblEndProbOf.setText(loc.getMenu("EndProbabilityOf") + " = ");
		lblProbOf.setText(loc.getMenu("ProbabilityOf"));

		setDistributionComboBoxMenu();

		if (getTable() != null) {
			getTable().setLabels();
		}
		if (styleBar != null) {
			styleBar.setLabels();
		}

		btnCumulative.setToolTipText(loc.getMenu("Cumulative"));

		btnIntervalLeft.setToolTipText(loc.getMenu("LeftProb"));
		btnIntervalRight.setToolTipText(loc.getMenu("RightProb"));
		btnIntervalBetween.setToolTipText(loc.getMenu("IntervalProb"));

		for (int i = 0; i < ProbabilityManager
				.getParmCount(selectedDist); i++) {
			lblParameterArray[i]
					.setText(getParameterLabels()[selectedDist.ordinal()][i]);
		}
	}

	private void setDistributionComboBoxMenu() {

		comboDistribution.removeActionListener(this);
		comboDistribution.removeAllItems();
		comboDistribution.addItem(getDistributionMap().get(Dist.NORMAL));
		comboDistribution.addItem(getDistributionMap().get(Dist.STUDENT));
		comboDistribution.addItem(getDistributionMap().get(Dist.CHISQUARE));
		comboDistribution.addItem(getDistributionMap().get(Dist.F));
		comboDistribution.addItem(getDistributionMap().get(Dist.EXPONENTIAL));
		comboDistribution.addItem(getDistributionMap().get(Dist.CAUCHY));
		comboDistribution.addItem(getDistributionMap().get(Dist.WEIBULL));
		comboDistribution.addItem(getDistributionMap().get(Dist.GAMMA));
		comboDistribution.addItem(getDistributionMap().get(Dist.LOGNORMAL));
		comboDistribution.addItem(getDistributionMap().get(Dist.LOGISTIC));

		comboDistribution.addItem(ListSeparatorRenderer.SEPARATOR);

		comboDistribution.addItem(getDistributionMap().get(Dist.BINOMIAL));
		comboDistribution.addItem(getDistributionMap().get(Dist.PASCAL));
		comboDistribution.addItem(getDistributionMap().get(Dist.POISSON));
		comboDistribution.addItem(getDistributionMap().get(Dist.HYPERGEOMETRIC));

		comboDistribution.setSelectedItem(getDistributionMap().get(selectedDist));
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
		getPlotPanel().commonFields.updateSettings((getPlotPanel()),
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
}
