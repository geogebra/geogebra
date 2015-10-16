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
import javax.swing.JSlider;
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
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings.DIST;
import org.geogebra.desktop.euclidianND.EuclidianViewInterfaceD;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.inputfield.MyTextField;
import org.geogebra.desktop.gui.util.LayoutUtil;
import org.geogebra.desktop.gui.util.ListSeparatorRenderer;
import org.geogebra.desktop.gui.view.data.PlotPanelEuclidianViewD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;

/**
 * Dialog that displays the graphs of various probability density functions with
 * interactive controls for calculating interval probabilities.
 * 
 * @author G. Sturr
 * 
 */
public class ProbabilityCalculatorViewD extends ProbabilityCalculatorView
		implements ActionListener, FocusListener, ChangeListener {

	private static final long serialVersionUID = 1L;

	private ProbabiltyCalculatorStyleBarD styleBar;

	// GUI elements
	private JComboBox comboDistribution, comboProbType;
	private JTextField[] fldParameterArray;
	private JTextField fldLow, fldHigh, fldResult;
	private JLabel[] lblParameterArray;
	private JLabel lblBetween, lblProbOf, lblEndProbOf, lblProb, lblDist;
	private MyToggleButton btnCumulative, btnIntervalLeft, btnIntervalBetween,
			btnIntervalRight;

	private JSlider[] sliderArray;
	private ListSeparatorRenderer comboRenderer;

	// GUI layout panels
	private JPanel controlPanel, distPanel, probPanel, tablePanel;
	private JSplitPane mainSplitPane, plotSplitPane;

	private JToggleButton btnExport;

	private JPanel wrapperPanel;

	private JLabel lblMeanSigma;

	private JPanel plotPanelPlus;

	private JPanel probCalcPanel;

	private JTabbedPane tabbedPane;

	private StatisticsCalculator statCalculator;

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
			public void stateChanged(ChangeEvent e) {
				// System.out.println("Tab: " + tabbedPane.getSelectedIndex());
				if (styleBar != null) {
					styleBar.updateLayout();
				}
			}
		});

		wrapperPanel.setLayout(new BorderLayout());
		wrapperPanel.add(tabbedPane, BorderLayout.CENTER);

		setLabels();

		attachView();
		settingsChanged(app.getSettings().getProbCalcSettings());

		// TODO for testing only, remove later
		// tabbedPane.setSelectedIndex(1);

	}

	/**************** end constructor ****************/

	/**
	 * @return The style bar for this view.
	 */
	public ProbabiltyCalculatorStyleBarD getStyleBar() {
		if (styleBar == null) {
			styleBar = new ProbabiltyCalculatorStyleBarD((AppD) app, this);
		}

		return styleBar;
	}

	// =================================================
	// Getters/Setters
	// =================================================

	public ProbabilityManager getProbManager() {
		return probManager;
	}

	public void setCumulative(boolean isCumulative) {

		if (this.isCumulative == isCumulative)
			return;

		this.isCumulative = isCumulative;

		// in cumulative mode only left-sided intervals are allowed
		setProbabilityComboBoxMenu();
		if (!isCumulative)
			// make sure left-sided is still selected when reverting to
			// non-cumulative mode
			comboProbType.setSelectedIndex(PROB_LEFT);

		if (isCumulative) {
			graphType = graphTypeCDF;
		} else {
			graphType = graphTypePDF;
		}
		updateAll();

	}

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
			plotPanel = new PlotPanelEuclidianViewD(app.getKernel(),
					exportToEVAction);
			((PlotPanelEuclidianViewD) plotPanel).setMouseEnabled(true, true);
			((PlotPanelEuclidianViewD) plotPanel).setMouseMotionEnabled(true);
			((EuclidianViewInterfaceD) plotPanel).setBorder(BorderFactory
					.createEmptyBorder());

			// plot label panel
			JPanel plotLabelPanel = LayoutUtil.flowPanelRight(0, 0, 0,
					lblMeanSigma, Box.createHorizontalStrut(10));
			plotLabelPanel.setBorder(BorderFactory
					.createEmptyBorder(4, 0, 4, 0));
			plotLabelPanel.setBackground(Color.white);
			// plot panel with label field below
			plotPanelPlus = new JPanel(new BorderLayout());
			plotPanelPlus.add(
					((EuclidianViewInterfaceD) plotPanel).getJPanel(),
					BorderLayout.CENTER);
			plotPanelPlus.add(plotLabelPanel, BorderLayout.SOUTH);

			// table panel
			table = new ProbabilityTableD((AppD) app, this);
			((ProbabilityTableD) table).getWrappedPanel().setBorder(
					BorderFactory.createMatteBorder(0, 1, 0, 0,
							SystemColor.controlShadow));
			tablePanel = new JPanel(new BorderLayout());
			tablePanel.add(((ProbabilityTableD) table).getWrappedPanel(),
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

		mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				plotSplitPane, scroller);
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
		if (comboRenderer == null)
			comboRenderer = new ListSeparatorRenderer();
		return comboRenderer;

	}

	private void createGUIElements() {

		setLabelArrays();
		comboDistribution = new JComboBox();
		comboDistribution.setRenderer(getComboRenderer());
		comboDistribution
				.setMaximumRowCount(ProbabilityCalculatorSettings.distCount + 1);
		// setComboDistribution();
		comboDistribution.addActionListener(this);
		lblDist = new JLabel();

		btnCumulative = new MyToggleButton(
				((AppD) app).getScaledIcon("cumulative_distribution.png"));

		btnIntervalLeft = new MyToggleButton(
				((AppD) app).getScaledIcon("interval-left.png"));
		btnIntervalBetween = new MyToggleButton(
				((AppD) app).getScaledIcon("interval-between.png"));
		btnIntervalRight = new MyToggleButton(
				((AppD) app).getScaledIcon("interval-right.png"));

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
		btnExport.setIcon(((AppD) app).getScaledIcon("export16.png"));
		btnExport.setFocusable(false);
		btnExport.addActionListener(this);

		lblParameterArray = new JLabel[maxParameterCount];
		fldParameterArray = new JTextField[maxParameterCount];

		for (int i = 0; i < maxParameterCount; ++i) {
			lblParameterArray[i] = new JLabel();
			fldParameterArray[i] = new MyTextField((AppD) app);
			fldParameterArray[i].setColumns(5);
			fldParameterArray[i].addActionListener(this);
			fldParameterArray[i].addFocusListener(this);
		}

		// create probability mode JComboBox and put it in a JPanel
		comboProbType = new JComboBox();
		comboProbType.setRenderer(getComboRenderer());
		comboProbType.addActionListener(this);
		lblProb = new JLabel();

		lblProbOf = new JLabel();
		lblBetween = new JLabel(); // <= X <=
		lblEndProbOf = new JLabel();
		fldLow = new MyTextField((AppD) app);
		fldLow.setColumns(5);
		fldLow.addActionListener(this);
		fldLow.addFocusListener(this);

		fldHigh = new MyTextField((AppD) app);
		fldHigh.setColumns(6);
		fldHigh.addActionListener(this);
		fldHigh.addFocusListener(this);

		fldResult = new MyTextField((AppD) app);
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
		lblProb.setFont(((AppD) app).getItalicFont());
		plotPanel.updateFonts();
		((ProbabilityTableD) table).updateFonts(font);
		((StatisticsCalculatorD) statCalculator).updateFonts(font);
		btnCumulative.setIcon(((AppD) app)
				.getScaledIcon("cumulative_distribution.png"));

		btnIntervalLeft
				.setIcon(((AppD) app).getScaledIcon("interval-left.png"));
		btnIntervalBetween.setIcon(((AppD) app)
				.getScaledIcon("interval-between.png"));
		btnIntervalRight.setIcon(((AppD) app)
				.getScaledIcon("interval-right.png"));

		btnExport.setIcon(((AppD) app).getScaledIcon("export16.png"));
		if (styleBar != null) {
			styleBar.updateIcons();
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (isIniting)
			return;
		Object source = e.getSource();

		if (source instanceof JTextField) {
			doTextFieldActionPerformed((JTextField) source);
		}

		if (source == comboDistribution) {

			if (comboDistribution.getSelectedItem() != null)
				if (comboDistribution.getSelectedItem().equals(
						ListSeparatorRenderer.SEPARATOR)) {
					comboDistribution.removeActionListener(this);
					comboDistribution.setSelectedItem(distributionMap
							.get(selectedDist));
					comboDistribution.addActionListener(this);
				} else if (!selectedDist.equals(this.reverseDistributionMap
						.get(comboDistribution.getSelectedItem()))) {

					selectedDist = reverseDistributionMap.get(comboDistribution
							.getSelectedItem());
					parameters = ProbabilityManager
							.getDefaultParameters(selectedDist);
					this.setProbabilityCalculator(selectedDist, parameters,
							isCumulative);
				}
			wrapperPanel.requestFocus();
		}

		else if (source == comboProbType) {
			updateProbabilityType();
		}

		else if (source == btnCumulative) {
			setCumulative(btnCumulative.isSelected());

		} else if (source == btnIntervalLeft || source == btnIntervalBetween
				|| source == btnIntervalRight) {

			btnIntervalLeft.removeActionListener(this);
			btnIntervalBetween.removeActionListener(this);
			btnIntervalRight.removeActionListener(this);

			if (!isCumulative) {
				updateProbabilityType();
			}

			btnIntervalLeft.addActionListener(this);
			btnIntervalBetween.addActionListener(this);
			btnIntervalRight.addActionListener(this);
		}

		else if (source == btnExport) {
			JPopupMenu menu = ((PlotPanelEuclidianViewD) plotPanel)
					.getContextMenu();
			menu.show(btnExport,
					-menu.getPreferredSize().width + btnExport.getWidth(),
					btnExport.getHeight());
		}

	}

	private void doTextFieldActionPerformed(JTextField source) {
		if (isIniting)
			return;
		try {
			String inputText = source.getText().trim();
			// Double value = Double.parseDouble(source.getText());

			// allow input such as sqrt(2)
			NumberValue nv;
			nv = kernel.getAlgebraProcessor().evaluateToNumeric(inputText,
					false);
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
			}

			else
				// handle parameter entry
				for (int i = 0; i < parameters.length; ++i)
					if (source == fldParameterArray[i]) {

						if (isValidParameter(value, i)) {
							parameters[i] = value;
							updateAll();
						}

					}

			updateIntervalProbability();
			updateGUI();

		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

	}

	public void focusGained(FocusEvent e) {
		if (e.getSource() instanceof MyTextField) {
			((MyTextField) e.getSource()).selectAll();
		}
	}

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
		if (styleBar != null)
			styleBar.updateGUI();
		// this.requestFocus();

	}

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
				lblParameterArray[i].setText(parameterLabels[selectedDist
						.ordinal()][i]);
				// set field
				fldParameterArray[i].removeActionListener(this);
				fldParameterArray[i].setText("" + format(parameters[i]));
				fldParameterArray[i].setCaretPosition(0);
				fldParameterArray[i].addActionListener(this);
			}
		}

		// set low/high interval field values
		fldLow.setText("" + format(getLow()));
		fldLow.setCaretPosition(0);
		fldHigh.setText("" + format(getHigh()));
		fldHigh.setCaretPosition(0);
		fldResult.setText("" + format(probability));
		fldResult.setCaretPosition(0);

		// set distribution combo box
		comboDistribution.removeActionListener(this);
		if (comboDistribution.getSelectedItem() != distributionMap
				.get(selectedDist))
			comboDistribution
					.setSelectedItem(distributionMap.get(selectedDist));
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

	private void updateProbabilityType() {

		if (isIniting)
			return;

		boolean isDiscrete = probmanagerIsDiscrete();
		int oldProbMode = probMode;

		if (isCumulative) {
			probMode = PROB_LEFT;
		} else {
			if (btnIntervalLeft.isSelected()) {
				probMode = this.PROB_LEFT;
			} else if (btnIntervalBetween.isSelected()) {
				probMode = this.PROB_INTERVAL;
			} else {
				probMode = this.PROB_RIGHT;
			}
		}
		this.getPlotDimensions();

		if (probMode == PROB_INTERVAL) {
			lowPoint.setEuclidianVisible(showProbGeos);
			highPoint.setEuclidianVisible(showProbGeos);
			fldLow.setVisible(true);
			fldHigh.setVisible(true);
			lblBetween.setText(loc.getMenu("XBetween"));

			setLow(plotSettings.xMin + 0.4
					* (plotSettings.xMax - plotSettings.xMin));
			setHigh(plotSettings.xMin + 0.6
					* (plotSettings.xMax - plotSettings.xMin));

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

			if (isDiscrete)
				setLow(((GeoNumeric) discreteValueList.get(0)).getDouble());
			else
				setLow(plotSettings.xMin - 1); // move offscreen so the integral
												// looks complete

		}

		else if (probMode == PROB_RIGHT) {
			lowPoint.setEuclidianVisible(showProbGeos);
			highPoint.setEuclidianVisible(false);
			fldLow.setVisible(true);
			fldHigh.setVisible(false);
			lblBetween.setText(loc.getMenu("LessThanOrEqualToX"));

			if (oldProbMode == PROB_LEFT) {
				setLow(getHigh());
			}

			if (isDiscrete)
				setHigh(((GeoNumeric) discreteValueList.get(discreteValueList
						.size() - 1)).getDouble());
			else
				setHigh(plotSettings.xMax + 1); // move offscreen so the integral
												// looks complete

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
		updateGUI();
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
			if (hasIntegral)
				integral.update();
		}

		lblMeanSigma.setText(getMeanSigma());
		wrapperPanel.repaint();

	}

	protected void updateDiscreteTable() {
		if (!probmanagerIsDiscrete())
			return;
		int[] firstXLastX = generateFirstXLastXCommon();
		((ProbabilityTableD) table).setTable(selectedDist, parameters,
				firstXLastX[0], firstXLastX[1]);
	}

	protected void updatePrintFormat(int printDecimals, int printFigures) {
		this.printDecimals = printDecimals;
		this.printFigures = printFigures;
		updateGUI();
		updateDiscreteTable();
	}

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

	public void setLabels() {

		tabbedPane.setTitleAt(0, loc.getMenu("Distribution"));

		((SetLabels) statCalculator).setLabels();
		tabbedPane.setTitleAt(1, loc.getMenu("Statistics"));

		setLabelArrays();


		lblDist.setText(loc.getMenu("Distribution") + ": ");
		lblProb.setText(loc.getMenu("Probability") + ": ");

		setProbabilityComboBoxMenu();

		lblBetween.setText(loc.getMenu("XBetween")); // <= X <=
		lblEndProbOf.setText(loc.getMenu("EndProbabilityOf") + " = ");
		lblProbOf.setText(loc.getMenu("ProbabilityOf"));

		setDistributionComboBoxMenu();

		if (table != null)
			((ProbabilityTableD) table).setLabels();
		if (styleBar != null)
			styleBar.setLabels();

		btnCumulative.setToolTipText(loc.getMenu("Cumulative"));

		btnIntervalLeft.setToolTipText(loc.getMenu("LeftProb"));
		btnIntervalRight.setToolTipText(loc.getMenu("RightProb"));
		btnIntervalBetween.setToolTipText(loc.getMenu("IntervalProb"));

		for (int i = 0; i < ProbabilityManager.getParmCount(selectedDist); i++) {
			lblParameterArray[i]
					.setText(parameterLabels[selectedDist.ordinal()][i]);
		}
	}

	private void setProbabilityComboBoxMenu() {

		comboProbType.removeActionListener(this);
		comboProbType.removeAllItems();
		if (isCumulative)
			comboProbType.addItem(loc.getMenu("LeftProb"));
		else {
			comboProbType.addItem(loc.getMenu("IntervalProb"));
			comboProbType.addItem(loc.getMenu("LeftProb"));
			comboProbType.addItem(loc.getMenu("RightProb"));
		}
		comboProbType.addActionListener(this);

	}

	private void setDistributionComboBoxMenu() {

		comboDistribution.removeActionListener(this);
		comboDistribution.removeAllItems();
		comboDistribution.addItem(distributionMap.get(DIST.NORMAL));
		comboDistribution.addItem(distributionMap.get(DIST.STUDENT));
		comboDistribution.addItem(distributionMap.get(DIST.CHISQUARE));
		comboDistribution.addItem(distributionMap.get(DIST.F));
		comboDistribution.addItem(distributionMap.get(DIST.EXPONENTIAL));
		comboDistribution.addItem(distributionMap.get(DIST.CAUCHY));
		comboDistribution.addItem(distributionMap.get(DIST.WEIBULL));
		comboDistribution.addItem(distributionMap.get(DIST.GAMMA));
		comboDistribution.addItem(distributionMap.get(DIST.LOGNORMAL));
		comboDistribution.addItem(distributionMap.get(DIST.LOGISTIC));

		comboDistribution.addItem(ListSeparatorRenderer.SEPARATOR);

		comboDistribution.addItem(distributionMap.get(DIST.BINOMIAL));
		comboDistribution.addItem(distributionMap.get(DIST.PASCAL));
		comboDistribution.addItem(distributionMap.get(DIST.POISSON));
		comboDistribution.addItem(distributionMap.get(DIST.HYPERGEOMETRIC));

		comboDistribution.setSelectedItem(distributionMap.get(selectedDist));
		comboDistribution.addActionListener(this);

	}

	// ============================================================
	// Sliders
	// ============================================================

	private void setSliderDefaults() {
		for (int i = 0; i < ProbabilityManager.getParmCount(selectedDist); i++) {
			// TODO: this is breaking the discrete distributions
			// sliderArray[i].setValue((int)
			// probManager.getDefaultParameterMap().get(selectedDist)[i]);
		}
	}

	public void stateChanged(ChangeEvent e) {
		if (isIniting)
			return;

		JSlider source = (JSlider) e.getSource();
		for (int i = 0; i < maxParameterCount; i++) {
			if (source == sliderArray[i]) {

				fldParameterArray[i].setText("" + sliderArray[i].getValue());
				doTextFieldActionPerformed(fldParameterArray[i]);

			}
		}

	}

	@Override
	protected void plotPanelUpdateSettings(PlotSettings settings) {
		((PlotPanelEuclidianViewD) plotPanel).commonFields.updateSettings(
				((PlotPanelEuclidianViewD) plotPanel), plotSettings);
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

		public void actionPerformed(ActionEvent event) {
			Integer euclidianViewID = (Integer) this
					.getValue("euclidianViewID");

			// if null ID then use EV1 unless shift is down, then use EV2
			if (euclidianViewID == null) {
				euclidianViewID = ((AppD) app).getShiftDown() ? app
						.getEuclidianView2(1).getViewID() : app
						.getEuclidianView1().getViewID();
			}

			// do the export
			exportGeosToEV(euclidianViewID);

			// null out the ID property
			this.putValue("euclidianViewID", null);
		}
	};

	public PlotPanelEuclidianViewD getPlotPanel() {
		return (PlotPanelEuclidianViewD) plotPanel;
	}

	/**
	 * Custom toggle button
	 */
	class MyToggleButton extends JToggleButton {

		private static final long serialVersionUID = 1L;

		/**
		 * @param ic
		 *            button icon
		 */
		public MyToggleButton(Icon ic) {
			super(ic);
			this.setFocusPainted(false);
			// this.setPreferredSize(new Dimension(24, 24));
			this.setMargin(new Insets(0, 0, 0, 0));
		}
	}

	public JPanel getWrapperPanel() {
		return wrapperPanel;
	}

	public boolean suggestRepaint() {
		return false;
		// only for web
	}
}
