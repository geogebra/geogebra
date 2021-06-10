package org.geogebra.web.full.gui.view.probcalculator;

import java.util.HashMap;

import org.geogebra.common.gui.view.data.PlotSettings;
import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.gui.view.probcalculator.ProbabilityManager;
import org.geogebra.common.gui.view.probcalculator.StatisticsCalculator;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetViewInterface;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist;
import org.geogebra.ggbjdk.java.awt.geom.Dimension;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.util.MyToggleButtonW;
import org.geogebra.web.full.gui.view.data.PlotPanelEuclidianViewW;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.gui.util.AriaMenuBar;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.gui.util.ListBoxApi;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GlobalKeyDispatcherW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabLayoutPanel;

/**
 * @author gabor
 * 
 * ProbablityCalculatorView for web
 *
 */
public class ProbabilityCalculatorViewW extends ProbabilityCalculatorView
		implements ChangeHandler, ValueChangeHandler<Boolean> {

	/**
	 * separator for list boxes
	 */
	public static final String SEPARATOR = "--------------------";
	private static final int CONTROL_PANEL_HEIGHT = 150;
	private static final int TABLE_PADDING_AND_SCROLLBAR = 32;

	private Label lblDist;
	private MyToggleButtonW btnCumulative;
	private MyToggleButtonW btnIntervalLeft;
	private MyToggleButtonW btnIntervalBetween;
	private MyToggleButtonW btnIntervalRight;
	private Label[] lblParameterArray;
	private MathTextFieldW[] fldParameterArray;
	private ListBox comboDistribution;
	private Label lblProb;
	private Label lblProbOf;
	private Label lblBetween;
	private Label lblEndProbOf;
	private MathTextFieldW fldLow;
	private MathTextFieldW fldHigh;
	private MathTextFieldW fldResult;
	private Label lblMeanSigma;
	/** control panel */
	FlowPanel controlPanel;
	/** export action */
	ScheduledCommand exportToEVAction;
	/** plot panel */
	FlowPanel plotPanelPlus;
	private FlowPanel plotSplitPane;
	private FlowPanel mainSplitPane;
	private FlowPanel probCalcPanel;
	private final StatisticsCalculatorW statCalculator;
	private MyTabLayoutPanel tabbedPane;
	private ProbabilityCalculatorStyleBarW styleBar;
	private HandlerRegistration comboDistributionHandler;
	private GPopupMenuW btnExport;
	private MyToggleButtonW btnNormalOverlay;
	
	/**
	 * @param app creates new probabilitycalculatorView
	 */
	public ProbabilityCalculatorViewW(AppW app) {
		super(app);

		tabbedPane = new MyTabLayoutPanel(30, Unit.PX);
		tabbedPane.addStyleName("PropabilityCalculatorViewW");

		createGUIElements();
		createExportToEvAction();
		createLayoutPanels();
		buildProbCalcPanel();
		isIniting = false;

		statCalculator = new StatisticsCalculatorW(app);

		tabbedPane = new MyTabLayoutPanel(30, Unit.PX);
		tabbedPane.add(probCalcPanel, loc.getMenu("Distribution"));
		tabbedPane.add(statCalculator.getWrappedPanel(),
				loc.getMenu("Statistics"));

		tabbedPane.addSelectionHandler(event -> updateStylebarLayout());

		tabbedPane.onResize();

		setLabels();

		attachView();
		settingsChanged(getApp().getSettings().getProbCalcSettings());
		updateAll();

		tabbedPane.selectTab(getApp().getSettings().getProbCalcSettings()
				.getCollection().isActive() ? 1 : 0);
	}

	/**
	 * Updates stylebar layout
	 */
	protected void updateStylebarLayout() {
		if (styleBar != null) {
			styleBar.updateLayout();
		}
	}

	@Override
	public void setLabels() {
		tabbedPane.setTabText(0, loc.getMenu("Distribution"));

		statCalculator.setLabels();
		tabbedPane.setTabText(1, loc.getMenu("Statistics"));

		setLabelArrays();

		lblDist.setText(loc.getMenu("Distribution") + ": ");
		lblProb.setText(loc.getMenu("Probability") + ": ");

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
		// TODO btnExport.setTitle(loc.getMenu("Export"));
		btnNormalOverlay.setTitle(loc.getMenu("OverlayNormalCurve"));
		for (int i = 0; i < ProbabilityManager.getParmCount(selectedDist); i++) {
			lblParameterArray[i]
					.setText(getParameterLabels()[selectedDist.ordinal()][i]);
		}
	}
	
	/**
	 * @return The style bar for this view.
	 */
	public ProbabilityCalculatorStyleBarW getStyleBar() {
		if (styleBar == null) {
			styleBar = new ProbabilityCalculatorStyleBarW(app, this);
		}

		return styleBar;
	}

	/**
	 * Action to export all GeoElements that are currently displayed in this
	 * panel to a EuclidianView. The viewID for the target EuclidianView is
	 * stored as a property with key "euclidianViewID".
	 *
	 * This action is passed as a parameter to plotPanel where it is used in the
	 * plotPanel context menu and the EuclidianView transfer handler when the
	 * plot panel is dragged into an EV.
	 */
	private void createExportToEvAction() {
		exportToEVAction = new ScheduledCommand() {
			
			private final HashMap<String, Object> value = new HashMap<>();
			
			public Object getValue(String key) {
				return value.get(key);
			}
			
			public void putValue(String key, Object val) {
				this.value.put(key, val);
			}
			
			@Override
			public void execute() {
				Integer euclidianViewID = (Integer) this
						.getValue("euclidianViewID");

				// if null ID then use EV1 unless shift is down, then use EV2
				if (euclidianViewID == null) {
					euclidianViewID = GlobalKeyDispatcherW.getShiftDown()
							? getApp().getEuclidianView2(1).getViewID()
							: getApp().getEuclidianView1().getViewID();
				}

				// do the export
				exportGeosToEV(euclidianViewID);

				// null out the ID property
				this.putValue("euclidianViewID", null);
			}
		};
	}

	private void buildProbCalcPanel() {
		tabbedPane.clear();
		plotSplitPane = new FlowPanel();
		plotSplitPane.add(plotPanelPlus);
		plotSplitPane.add(controlPanel);
		plotSplitPane.addStyleName("plotSplitPane");
		mainSplitPane = new FlowPanel();
		mainSplitPane.addStyleName("mainSplitPanel");
		mainSplitPane.add(plotSplitPane);

		probCalcPanel = new FlowPanel();
		probCalcPanel.addStyleName("ProbCalcPanel");
		
		probCalcPanel.add(mainSplitPane);
	}

	private void createLayoutPanels() {
		//control panel
		createControlPanel();
		setPlotPanel(new PlotPanelEuclidianViewW(kernel));

		FlowPanel plotPanelOptions = new FlowPanel();
		plotPanelOptions.setStyleName("plotPanelOptions");
		plotPanelOptions.add(lblMeanSigma);
		if (!getApp().isExam()) {
			plotPanelOptions.add(btnExport.getPopupMenu());
		}
		plotPanelOptions.add(btnNormalOverlay);
		plotPanelOptions.add(new ClearPanel());
		
		plotPanelPlus = new FlowPanel();
		plotPanelPlus.addStyleName("PlotPanelPlus");
		plotPanelPlus.add(plotPanelOptions);
		plotPanelPlus.add(getPlotPanel().getComponent());
		
		//table panel
		setTable(new ProbabilityTableW(app, this));
		//tablePanel = new FlowPanel();
		//tablePanel.add(((ProbabilityTableW)table).getWrappedPanel());	
	}

	private void createControlPanel() {
		//distribution combobox panel
		FlowPanel cbPanel = new FlowPanel();
		cbPanel.addStyleName("cbPanel");
		cbPanel.add(btnCumulative);
		cbPanel.add(comboDistribution);
		FlowPanel parameterPanel = new FlowPanel();
		parameterPanel.addStyleName("parameterPanel");
		comboDistribution.addStyleName("groupEnd");
		//parameter panel
		for (int i = 0; i < maxParameterCount; i++) {
			parameterPanel.add(lblParameterArray[i]);
			parameterPanel.add(fldParameterArray[i]);
		}
		cbPanel.add(parameterPanel);

		// interval panel
		// continue here.....
		FlowPanel tb = new FlowPanel();
		tb.addStyleName("intervalPanel");
		tb.add(btnIntervalLeft);
		tb.add(btnIntervalBetween);
		tb.add(btnIntervalRight);
		btnIntervalRight.addStyleName("groupEnd");

		FlowPanel resultPanel = new FlowPanel();
		resultPanel.addStyleName("resultPanel");
		resultPanel.add(lblProbOf);
		resultPanel.add(fldLow);
		resultPanel.add(lblBetween);
		resultPanel.add(fldHigh);
		resultPanel.add(lblEndProbOf);
		resultPanel.add(fldResult);
		tb.add(resultPanel);
		
		controlPanel = new FlowPanel();
		controlPanel.addStyleName("controlPanel");
		controlPanel.add(cbPanel);
		controlPanel.add(new ClearPanel());
		controlPanel.add(tb);
		controlPanel.add(new ClearPanel());
	}
	
	private static class ClearPanel extends FlowPanel {
		public ClearPanel() {
			super();
			this.setStyleName("clear");
		}
	}

	private void createGUIElements() {
		setLabelArrays();
		comboDistribution = new ListBox();
		comboDistribution.addStyleName("comboDistribution");
		comboDistributionHandler = comboDistribution.addChangeHandler(this);
		
		lblDist = new Label();
		
		btnCumulative = new MyToggleButtonW(GuiResources.INSTANCE.cumulative_distribution());
		
		btnIntervalLeft = new MyToggleButtonW(GuiResources.INSTANCE.interval_left());
		
		btnIntervalBetween = new MyToggleButtonW(GuiResources.INSTANCE.interval_between());
		
		btnIntervalRight = new MyToggleButtonW(GuiResources.INSTANCE.interval_right());
		
		btnCumulative.addValueChangeHandler(this);
		btnIntervalLeft.addValueChangeHandler(this);
		btnIntervalBetween.addValueChangeHandler(this);
		btnIntervalRight.addValueChangeHandler(this);
		
		//buttonGroup
		FlowPanel gp = new FlowPanel();
		gp.add(btnIntervalLeft);
		gp.add(btnIntervalBetween);
		gp.add(btnIntervalRight);
		
		lblParameterArray = new Label[maxParameterCount];
		fldParameterArray = new MathTextFieldW[maxParameterCount];
		
		for (int i = 0; i < maxParameterCount; i++) {
			lblParameterArray[i] = new Label();
			fldParameterArray[i] = new MathTextFieldW(app);
			fldParameterArray[i].setPxWidth(64);
			addInsertHandler(fldParameterArray[i]);
			//TODO fldParameterArray[i].getTextBox().setTabIndex(i + 1);
		}

		lblProb = new Label();
		
		lblProbOf = new Label(); // <= X <=
		lblBetween = new Label();
		lblEndProbOf = new Label();

		fldLow = new MathTextFieldW(app);
		fldLow.setPxWidth(80);
		addInsertHandler(fldLow);
		//fldLow.getTextBox().setTabIndex(maxParameterCount);

		fldHigh = new MathTextFieldW(app);
		fldHigh.setPxWidth(80);
		addInsertHandler(fldHigh);
		//fldHigh.getTextBox().setTabIndex(maxParameterCount + 1);

		fldResult = new MathTextFieldW(app);
		fldResult.setPxWidth(96);
		addInsertHandler(fldResult);
		//fldResult.getTextBox().setTabIndex(maxParameterCount + 2);

		lblMeanSigma = new Label();
		lblMeanSigma.addStyleName("lblMeanSigma");

		createExportMenu();

		btnNormalOverlay = new MyToggleButtonW(
				GuiResources.INSTANCE.normal_overlay());
		btnNormalOverlay.addStyleName("btnNormalOverlay");
		btnNormalOverlay.addClickHandler(event -> onOverlayClicked());
	}
	
	/**
	 * Overlay button action
	 */
	protected void onOverlayClicked() {
		setShowNormalOverlay(btnNormalOverlay.isSelected());
		updateAll();
	}

	/**
	 * @return the wrapper panel of this view
	 */
	public TabLayoutPanel getWrapperPanel() {
		return tabbedPane;
	}

	@Override
	public void updateAll() {
		updateOutput();
		updateProbabilityType();
		updateGUI();
		if (styleBar != null) {
			styleBar.updateGUI();
		}
	}

	private void updateOutput() {
		updateDistribution();
		updatePlotSettings();
		updateIntervalProbability();
		updateDiscreteTable();
		setXAxisPoints();
	}

	private void updateProbabilityType() {
		if (isIniting) {
			return;
		}

		boolean isDiscrete = probmanagerIsDiscrete();

		if (probMode == PROB_INTERVAL) {
			lowPoint.setEuclidianVisible(showProbGeos);
			highPoint.setEuclidianVisible(showProbGeos);
			fldLow.setVisible(true);
			fldHigh.setVisible(true);
			lblBetween.setText(SpreadsheetViewInterface.X_BETWEEN);

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

			if (isDiscrete) {
				setLow((GeoNumberValue) discreteValueList.get(0));
			} else {
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

			if (isDiscrete) {
				setHigh(((GeoNumeric) discreteValueList.get(discreteValueList
						.size() - 1)).getDouble());
			}
			else {
				setHigh(plotSettings.xMax + 1); // move offscreen so the integral
												// looks complete
			}

		}

		// make result field editable for inverse probability calculation
		fldResult.setEditable(probMode != PROB_INTERVAL);

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

	@Override
	protected void changeProbabilityType() {
		if (isCumulative) {
			probMode = PROB_LEFT;
		} else {
			int oldProbMode = probMode;
			if (btnIntervalLeft.getValue()) {
				probMode = ProbabilityCalculatorView.PROB_LEFT;
				if (oldProbMode == PROB_RIGHT) {
					setHigh(getLow());
				}
			} else if (btnIntervalBetween.getValue()) {
				probMode = ProbabilityCalculatorView.PROB_INTERVAL;
			} else if (btnIntervalRight.getValue()) {
				probMode = ProbabilityCalculatorView.PROB_RIGHT;
				if (oldProbMode == PROB_LEFT) {
					setLow(getHigh());
				}
			}
		}
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

		btnNormalOverlay.setVisible(isOverlayDefined());
		lblMeanSigma.setText(getMeanSigma());
		getPlotPanel().repaintView();
	}
	
	private void addRemoveTable(boolean showTable) {
		if (showTable) {
			mainSplitPane
					.add(((ProbabilityTableW) getTable()).getWrappedPanel());
		} else {
			mainSplitPane
					.remove(((ProbabilityTableW) getTable()).getWrappedPanel());
		}
		tabbedPane.onResize();
	}

	@Override
	protected void plotPanelUpdateSettings(PlotSettings settings) {
		getPlotPanel().commonFields
				.updateSettings(getPlotPanel(), plotSettings);
	}

	@Override
	protected void updateDiscreteTable() {
		if (!probmanagerIsDiscrete()) {
			return;
		}
		int[] firstXLastX = generateFirstXLastXCommon();
		getTable().setTable(selectedDist, parameters,
				firstXLastX[0], firstXLastX[1]);
		tabbedPane.onResize();
	}

	@Override
	public PlotPanelEuclidianViewW getPlotPanel() {
		return (PlotPanelEuclidianViewW) super.getPlotPanel();
	}

	@Override
	protected void updateGUI() {
		// set visibility and text of the parameter labels and fields
		for (int i = 0; i < maxParameterCount; ++i) {

			boolean hasParm = i < ProbabilityManager.getParmCount(selectedDist);

			lblParameterArray[i].setVisible(hasParm);
			fldParameterArray[i].setVisible(hasParm);

			if (hasParm) {
				// set label
				lblParameterArray[i].setVisible(true);
				lblParameterArray[i].setText(getParameterLabels()[selectedDist
						.ordinal()][i]);
				// set field
				fldParameterArray[i].setText(format(parameters[i]));
			}
		}

		updateLowHighResult();

		// set distribution combo box
		if (!comboDistribution.getValue(comboDistribution.getSelectedIndex())
				.equals(getDistributionMap().get(selectedDist))) {
			ListBoxApi.select(
					getDistributionMap().get(selectedDist), comboDistribution);
		}

		btnCumulative.setValue(isCumulative);
		btnIntervalLeft.setValue(probMode == PROB_LEFT);
		btnIntervalBetween.setValue(probMode == PROB_INTERVAL);
		btnIntervalRight.setValue(probMode == PROB_RIGHT);

		btnNormalOverlay.setValue(isShowNormalOverlay());
	}

	private void updateLowHighResult() {
		tabbedPane.deferredOnResize();
		updateLowHigh();
		// fldHigh.setCaretPosition(0);
		fldResult.setText(getProbabilityText());
		// fldResult.setCaretPosition(0);
		fldResult
				.setEditable(probMode != ProbabilityCalculatorView.PROB_INTERVAL);
	}

	private void updateLowHigh() {
		// set low/high interval field values
		fldLow.setText(format(low));
		fldHigh.setText(format(high));
	}

	@Override
	public void onChange(ChangeEvent event) {
		if (comboDistribution.getSelectedIndex() > -1) {
			changeDistribution();
		}
	}

	private void changeDistribution() {
		if (!selectedDist
				.equals(this.getReverseDistributionMap().get(comboDistribution
						.getValue(comboDistribution.getSelectedIndex())))) {
			selectedDist = getReverseDistributionMap().get(comboDistribution
					.getValue(comboDistribution.getSelectedIndex()));
			parameters = ProbabilityManager.getDefaultParameters(selectedDist, cons);
			setProbabilityCalculator(selectedDist, parameters,
					isCumulative);
			tabbedPane.onResize();
		}

	}

	private void setDistributionComboBoxMenu() {
		comboDistributionHandler.removeHandler();
		comboDistribution.clear();
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
		
		comboDistribution.addItem(SEPARATOR);
		NodeList<OptionElement> options = SelectElement.as(comboDistribution.getElement())
				.getOptions();
		options.getItem(options.getLength() - 1)
				.setAttribute("disabled", "disabled");
		comboDistribution.addItem(getDistributionMap().get(Dist.BINOMIAL));
		comboDistribution.addItem(getDistributionMap().get(Dist.PASCAL));
		comboDistribution.addItem(getDistributionMap().get(Dist.POISSON));
		comboDistribution.addItem(getDistributionMap().get(Dist.HYPERGEOMETRIC));

		ListBoxApi.select(getDistributionMap().get(selectedDist),
				comboDistribution);
		comboDistribution.addChangeHandler(this);
	}

	/**
	 * @return wheter distribution tab is open
	 */
	@Override
	public boolean isDistributionTabOpen() {
		return tabbedPane.getSelectedIndex() == 0;
	}

	/**
	 * @return ProbabilitiManager
	 */
	@Override
	public ProbabilityManager getProbManager() {
		return probManager;
	}

	/**
	 * @param decimals
	 *			decimals
	 * @param figures
	 *			significant digits
	 */
	public void updatePrintFormat(int decimals, int figures) {
		this.printDecimals = decimals;
		this.printFigures = figures;
		updateGUI();
		updateDiscreteTable();
	}
	
	private class MyTabLayoutPanel extends TabLayoutPanel implements ClickHandler {

		public MyTabLayoutPanel(int splitterSize, Unit px) {
			super(splitterSize, px);
			this.addDomHandler(this, ClickEvent.getType());
		}

		@Override
		public final void onResize() {
			tabResized();
		}

		public void deferredOnResize() {
			Scheduler.get().scheduleDeferred(this::onResize);
		}

		@Override
		public void onClick(ClickEvent event) {
			getApp().setActiveView(App.VIEW_PROBABILITY_CALCULATOR);
		}
	}

	/**
	 * @return plot panel view
	 */
	public EuclidianViewW getPlotPanelEuclidianView() {
		return getPlotPanel();
	}

	/**
	 * Tab resize callback
	 */
	public void tabResized() {
		int tableWidth = probmanagerIsDiscrete() ? ((ProbabilityTableW) getTable()).getStatTable()
				.getTable().getOffsetWidth() + TABLE_PADDING_AND_SCROLLBAR : 0;
		int width = mainSplitPane.getOffsetWidth()
				- tableWidth
				- 5;
		int height = probCalcPanel.getOffsetHeight() - 20;
		if (width > 0) {
			getPlotPanel().setPreferredSize(new Dimension(width,
					Math.min(Math.max(100, height - CONTROL_PANEL_HEIGHT),
							PlotPanelEuclidianViewW.DEFAULT_HEIGHT)));
			getPlotPanel().repaintView();
			getPlotPanel().getEuclidianController().calculateEnvironment();
			plotSplitPane.setWidth(width + "px");
		}

		if (height > 0 && probmanagerIsDiscrete()) {
			((ProbabilityTableW) getTable()).getWrappedPanel()
					.setPixelSize(tableWidth, height);
		}
	}

	@Override
	public void onValueChange(ValueChangeEvent<Boolean> event) {
		
		Object source = event.getSource();
		// Log.debug("valuechangeevent: " + source.toString());
		if (source == btnCumulative) {
			setCumulative(btnCumulative.isSelected());

		} else if (source == btnIntervalLeft || source == btnIntervalBetween
				|| source == btnIntervalRight) {
			simulateRadioButtons((MyToggleButtonW) source);

			if (!isCumulative) {
				changeProbabilityType();
				updateProbabilityType();
				updateGUI();
			}
		}
	}

	private void simulateRadioButtons(MyToggleButtonW source) {
	   if (source.getValue()) {
		   if (source == btnIntervalRight) {
			   btnIntervalLeft.setValue(false);
			   btnIntervalBetween.setValue(false);
		   } else if (source == btnIntervalBetween) {
			   btnIntervalLeft.setValue(false);
			   btnIntervalRight.setValue(false);
		   } else if (source == btnIntervalLeft) {
			   btnIntervalRight.setValue(false);
			   btnIntervalBetween.setValue(false);
		   }
	   }
	}

	/**
	 * @param source
	 *			changed source
	 * @param intervalCheck
	 *			true if triggered by enter/blur
	 */
	void doTextFieldActionPerformed(MathTextFieldW source, boolean intervalCheck) {
		if (isIniting) {
			return;
		}
		String inputText = source.getText().trim();
		boolean update = true;
		if (!"".equals(inputText)) {
			// allow input such as sqrt(2)
			GeoNumberValue nv = kernel.getAlgebraProcessor().evaluateToNumeric(
					inputText, intervalCheck ? source : ErrorHelper.silent());
			GeoNumberValue numericValue = nv != null
					? nv : new GeoNumeric(cons, Double.NaN);
			double value = numericValue.getDouble();
			if (!Double.isNaN(value)) {
				source.resetError();
			}
			if (source == fldLow) {

				checkBounds(numericValue, intervalCheck, false);
			}

			else if (source == fldHigh) {
				checkBounds(numericValue, intervalCheck, true);
			}

			// handle inverse probability
			else if (source == fldResult) {
				update = false;
				if (value < 0 || value > 1) {
					if (!intervalCheck) {
						updateLowHigh();
						return;
					}
					updateGUI();
				} else {
					if (probMode == PROB_LEFT) {
						setHigh(inverseProbability(value));
					}
					if (probMode == PROB_RIGHT) {
						setLow(inverseProbability(1 - value));
					}
					updateLowHigh();
					setXAxisPoints();
				}
			} else {
				// handle parameter entry
				for (int i = 0; i < parameters.length; ++i) {
					if (source == fldParameterArray[i]) {
						if (isValidParameterChange(value, i)) {
							parameters[i] = numericValue;
							if (intervalCheck) {
								updateAll();
							} else {
								updateOutput();
								updateLowHighResult();
							}
						}

					}
				}
			}
			if (intervalCheck) {
				updateIntervalProbability();
				if (update) {
					updateGUI();
				}
			}
		}
	}

	private void addInsertHandler(final MathTextFieldW field) {
		// TODO field.enableGGBKeyboard();
		field.addInputHandler(() -> doTextFieldActionPerformed(field, false));
		field.addChangeHandler(() -> doTextFieldActionPerformed(field, true));
	}

	@Override
	public void setInterval(double low, double high) {
		this.setLow(low);
		this.setHigh(high);
		fldLow.setText("" + low);
		fldHigh.setText("" + high);
		setXAxisPoints();
		updateIntervalProbability();
		updateGUI();
	}
	
	@Override
	public boolean suggestRepaint() {
		return false;
	}

	/**
	 * Resize callback
	 */
	public void onResize() {
		// in most cases it is enough to updatePlotSettings, but when
		// setPersective is called early
		// during Win8 app initialization, we also need to update the tabbed
		// pane and make the whole process deferred
		getApp().invokeLater(() -> {
			tabResized();
			updatePlotSettings();
		});
	}

	private void checkBounds(GeoNumberValue value, boolean intervalCheck, boolean high) {
		boolean valid = high ? isValidInterval(probMode, getLow(), value.getDouble())
				: isValidInterval(probMode, value.getDouble(), getHigh());
		if (valid) {
			if (high) {
				setHigh(value);
			} else {
				setLow(value);
			}
			setXAxisPoints();
		}
		if (intervalCheck) {
			updateGUI();
		} else {
			updateIntervalProbability();
			fldResult.setText(getProbabilityText());
		}
	}

	private void createExportMenu() {

		btnExport = new GPopupMenuW((AppW) app, true) {
			@Override
			public int getPopupLeft() {
				return getPopupMenu().getAbsoluteLeft();
			}
		};
		btnExport.getPopupMenu().addStyleName("btnExport");

		AriaMenuBar menu = new AriaMenuBar();

		if (!getApp().isApplet()) {
			AriaMenuItem miToGraphich = new AriaMenuItem(
					loc.getMenu("CopyToGraphics"), false,
					() -> exportToEVAction.execute());

		menu.addItem(miToGraphich);
		}
		if (((AppW) app).getLAF().copyToClipboardSupported()) {
			AriaMenuItem miAsPicture = new AriaMenuItem(
					loc.getMenu("ExportAsPicture"), false, () -> {
						String url = getPlotPanel()
								.getExportImageDataUrl(3, true, false);
						((AppW) getApp()).getFileManager()
								.showExportAsPictureDialog(url,
										getApp().getExportTitle(),
										"png", "ExportAsPicture", getApp());
					});
			menu.addItem(miAsPicture);
		}

		String image = "<img src=\""
				+ MaterialDesignResources.INSTANCE.prob_calc_export().getSafeUri()
						.asString()
				+ "\" >";
		btnExport.addItem(new AriaMenuItem(image, true, menu));
		btnExport.getPopupMenu().removeStyleName("gwt-MenuBar");
		btnExport.getPopupMenu().addStyleName("gwt-ToggleButton");
		btnExport.getPopupMenu().addStyleName("MyToggleButton");
	}

	@Override
	public StatisticsCalculator getStatCalculator() {
		return statCalculator;
	}

	/**
	 * @return application
	 */
	protected App getApp() {
		return app;
	}

}
