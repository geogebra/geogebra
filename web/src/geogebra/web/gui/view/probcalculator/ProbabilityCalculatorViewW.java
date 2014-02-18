package geogebra.web.gui.view.probcalculator;

import geogebra.common.gui.view.data.PlotSettings;
import geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import geogebra.common.gui.view.probcalculator.ProbabilityManager;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.App;
import geogebra.common.main.settings.ProbabilityCalculatorSettings.DIST;
import geogebra.common.util.Unicode;
import geogebra.html5.awt.GDimensionW;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.html5.gui.util.ListBoxApi;
import geogebra.html5.main.GlobalKeyDispatcherW;
import geogebra.web.euclidian.EuclidianViewW;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.util.MyToggleButton2;
import geogebra.web.gui.view.data.PlotPanelEuclidianViewW;
import geogebra.web.main.AppW;

import java.util.HashMap;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * @author gabor
 * 
 * ProbablityCalculatorView for web
 *
 */
public class ProbabilityCalculatorViewW extends ProbabilityCalculatorView implements ChangeHandler, FocusHandler, ValueChangeHandler<Boolean>, BlurHandler, KeyUpHandler {

	/**
	 * separator for list boxes
	 */
	public static final String SEPARATOR = "--------------------";
	/**
	 * @param app App
	 * creates new probabilitycalculatorView
	 */
	
	private Label lblDist;
	private MyToggleButton2 btnCumulative;
	private MyToggleButton2 btnIntervalLeft;
	private MyToggleButton2 btnIntervalBetween;
	private MyToggleButton2 btnIntervalRight;
	private Label[] lblParameterArray;
	private AutoCompleteTextFieldW[] fldParameterArray;
	private ListBox comboProbType, comboDistribution;
	private Label lblProb;
	private Label lblProbOf;
	private Label lblBetween;
	private Label lblEndProbOf;
	private AutoCompleteTextFieldW fldLow;
	private AutoCompleteTextFieldW fldHigh;
	private AutoCompleteTextFieldW fldResult;
	private Label lblMeanSigma;
	FlowPanel controlPanel;
	private ScheduledCommand exportToEVAction;
	private FlowPanel plotPanelPlus;
	private FlowPanel plotSplitPane;
	private FlowPanel mainSplitPane;
	private FlowPanel probCalcPanel;
	private StatisticsCalculatorW statCalculator;
	private MyTabLayoutPanel tabbedPane;
	private ProbabilityCalculatorStyleBarW styleBar;
	private HandlerRegistration comboProbHandler, comboDistributionHandler;
	private boolean valueChanged;
	
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
	   
	   statCalculator = new StatisticsCalculatorW(app);
	   
	   tabbedPane = new MyTabLayoutPanel(30, Unit.PX);
	   tabbedPane.add(probCalcPanel, loc.getMenu("Distribution"));
	   tabbedPane.add(statCalculator.getWrappedPanel(), loc.getMenu("Statistics"));
	   
	   tabbedPane.addSelectionHandler(new SelectionHandler<Integer>() {

		public void onSelection(SelectionEvent<Integer> event) {
			if (styleBar != null)
				styleBar.updateLayout();
		   }
	   });
	   	   
	   setLabels();
	   
	   attachView();
	   settingsChanged(app.getSettings().getProbCalcSettings());
	   
	   
	   isIniting = false;
	   
    }
	
	public void setLabels() {

		tabbedPane.setTabText(0, loc.getMenu("Distribution"));

		statCalculator.setLabels();
		tabbedPane.setTabText(1, loc.getMenu("Statistics"));

		setLabelArrays();

		lblDist.setText(loc.getMenu("Distribution") + ": ");
		lblProb.setText(loc.getMenu("Probability") + ": ");

		setProbabilityComboBoxMenu();

		lblBetween.setText(loc.getMenu("XBetween")); // <= X <=
		lblEndProbOf.setText(loc.getMenu("EndProbabilityOf") + " = ");
		lblProbOf.setText(loc.getMenu("ProbabilityOf"));

		setDistributionComboBoxMenu();

		if (table != null)
			((ProbabilityTableW) table).setLabels();
		if (styleBar != null)
			styleBar.setLabels();

		btnCumulative.setToolTipText(loc.getMenu("Cumulative"));

		btnIntervalLeft.setToolTipText(loc.getMenu("LeftProb"));
		btnIntervalRight.setToolTipText(loc.getMenu("RightProb"));
		btnIntervalBetween.setToolTipText(loc.getMenu("IntervalProb"));

	}
	
	/**
	 * @return The style bar for this view.
	 */
	public ProbabilityCalculatorStyleBarW getStyleBar() {
		if (styleBar == null) {
			styleBar = new ProbabilityCalculatorStyleBarW( app, this);
		}

		return styleBar;
	}
	
	private void createExportToEvAction() {
		/**
		 * Action to export all GeoElements that are currently displayed in this
		 * panel to a EuclidianView. The viewID for the target EuclidianView is
		 * stored as a property with key "euclidianViewID".
		 * 
		 * This action is passed as a parameter to plotPanel where it is used in the
		 * plotPanel context menu and the EuclidianView transfer handler when the
		 * plot panel is dragged into an EV.
		 */
		exportToEVAction = new ScheduledCommand() {
			
			private HashMap<String, Object> value = new HashMap<String, Object>();
			
			public Object getValue(String key) {
				return value.get(key);
			}
			
			public void putValue(String key, Object value) {
				this.value.put(key, value);
			}
			
			public void execute() {
				Integer euclidianViewID = (Integer) this
						.getValue("euclidianViewID");

			
				// if null ID then use EV1 unless shift is down, then use EV2
				if (euclidianViewID == null) {
					euclidianViewID = GlobalKeyDispatcherW.getShiftDown() ? app.getEuclidianView2()
							.getViewID() : app.getEuclidianView1().getViewID();
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
		
		mainSplitPane = new FlowPanel();
		mainSplitPane.addStyleName("mainSplitPanel");
		mainSplitPane.add(plotSplitPane);
		mainSplitPane.add(controlPanel);
		probCalcPanel = new FlowPanel();
		probCalcPanel.addStyleName("ProbCalcPanel");
		probCalcPanel.getElement().setId("ProbCalcPanel");
		
		probCalcPanel.add(mainSplitPane);
		
		//TODO: do css here!
		
		
		
    }

	private void createLayoutPanels() {
		//control panel
	    createControlPanel();
	    plotPanel = new PlotPanelEuclidianViewW(kernel, exportToEVAction);

	    

	    
	    plotPanelPlus = new FlowPanel();
	    plotPanelPlus.addStyleName("PlotPanelPlus");
	    plotPanelPlus.add(((PlotPanelEuclidianViewW)plotPanel).getComponent());
	    plotPanelPlus.add(lblMeanSigma);
	    
	    //table panel
	    table  = new ProbabilityTableW(app, this);
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
		
		//parameter panel
		for (int i = 0; i < maxParameterCount; i++) {
			parameterPanel.add(lblParameterArray[i]);
			parameterPanel.add(fldParameterArray[i]);
		}
		
		// interval panel
		// continue here.....
		FlowPanel tb = new FlowPanel();
		tb.addStyleName("intervalPanel");
		tb.add(btnIntervalLeft);
		tb.add(btnIntervalBetween);
		tb.add(btnIntervalRight);
		
		//FlowPanel p = new FlowPanel();
		//p.add(btnCumulative);
		//p.add(lblMeanSigma);
		
		FlowPanel resultPanel = new FlowPanel();
		resultPanel.addStyleName("resultPanel");
		resultPanel.add(lblProbOf);
		resultPanel.add(fldLow);
		resultPanel.add(lblBetween);
		resultPanel.add(fldHigh);
		resultPanel.add(lblEndProbOf);
		resultPanel.add(fldResult);	
		
		controlPanel = new FlowPanel();
		controlPanel.addStyleName("controlPanel");
		//controlPanel.add(btnCumulative);
		controlPanel.add(cbPanel);
		controlPanel.add(parameterPanel);
		controlPanel.add(tb);
		controlPanel.add(resultPanel);
			
    }

	private void createGUIElements() {
		setLabelArrays();
	    comboDistribution = new ListBox();
	    comboDistribution.addStyleName("comboDistribution");
	    comboDistributionHandler = comboDistribution.addChangeHandler(this);
	    
	    lblDist = new Label();
	    
	    btnCumulative = new MyToggleButton2(AppResources.INSTANCE.cumulative_distribution());
	    
	    btnIntervalLeft = new MyToggleButton2(AppResources.INSTANCE.interval_left());
	    
	    btnIntervalBetween = new MyToggleButton2(AppResources.INSTANCE.interval_between());
	    
	    btnIntervalRight = new MyToggleButton2(AppResources.INSTANCE.interval_right());
	    
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
	    fldParameterArray = new AutoCompleteTextFieldW[maxParameterCount];
	    
	    for (int i = 0; i < maxParameterCount; i++) {
	    	lblParameterArray[i] = new Label();
	    	fldParameterArray[i] = new AutoCompleteTextFieldW(app);
	    	fldParameterArray[i].setColumns(5);
	    	fldParameterArray[i].addKeyUpHandler(this);
	    	fldParameterArray[i].addFocusHandler(this);
	    	fldParameterArray[i].addBlurHandler(this);
	    	fldParameterArray[i].getTextBox().setTabIndex(i);
	    }
	    
	    comboProbType = new ListBox();
	    comboProbHandler = comboProbType.addChangeHandler(this);
	    
	    lblProb = new Label();
	    
	    lblProbOf = new Label(); // <= X <=
	    lblBetween = new Label();
	    lblEndProbOf = new Label();
	    
	    fldLow = new AutoCompleteTextFieldW(app);
	    fldLow.setColumns(5);
	    fldLow.addKeyUpHandler(this);
	    fldLow.addFocusHandler(this);
	    fldLow.addBlurHandler(this);
	    fldLow.getTextBox().setTabIndex(maxParameterCount);
	    
	    fldHigh = new AutoCompleteTextFieldW(app);
	    fldHigh.setColumns(6);
	    fldHigh.addKeyUpHandler(this);
	    fldHigh.addFocusHandler(this);
	    fldHigh.addBlurHandler(this);
	    fldHigh.getTextBox().setTabIndex(maxParameterCount + 1);
	    
	    fldResult = new AutoCompleteTextFieldW(app);
	    fldResult.setColumns(6);
	    fldResult.addKeyUpHandler(this);
	    fldResult.addFocusHandler(this);
	    fldResult.addBlurHandler(this);
	    fldResult.getTextBox().setTabIndex(maxParameterCount + 2);
	    
	    lblMeanSigma = new Label();
	    lblMeanSigma.addStyleName("lblMeanSigma");
	    	  
    }
	
	/**
	 * @return the wrapper panel of this view
	 */
	public TabLayoutPanel getWrapperPanel() {
		return tabbedPane;
	}

	@Override
    public void updateAll() {
		//updateFonts();
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
	
	private void updateProbabilityType() {

		if (isIniting)
			return;

		boolean isDiscrete = probmanagerIsDiscrete();
		int oldProbMode = probMode;

		if (isCumulative) {
			probMode = PROB_LEFT;
		} else {
			if (btnIntervalLeft.getValue()) {
				probMode = ProbabilityCalculatorView.PROB_LEFT;
			} else if (btnIntervalBetween.getValue()) {
				probMode = ProbabilityCalculatorView.PROB_INTERVAL;
			} else if (btnIntervalRight.getValue()) {
				probMode = ProbabilityCalculatorView.PROB_RIGHT;
			}
		}
		this.getPlotDimensions();

		if (probMode == PROB_INTERVAL) {
			lowPoint.setEuclidianVisible(showProbGeos);
			highPoint.setEuclidianVisible(showProbGeos);
			fldLow.setVisible(true);
			fldHigh.setVisible(true);
			lblBetween.setText(loc.getMenu("XBetween"));

			low = plotSettings.xMin + 0.4
					* (plotSettings.xMax - plotSettings.xMin);
			high = plotSettings.xMin + 0.6
					* (plotSettings.xMax - plotSettings.xMin);

		}

		else if (probMode == PROB_LEFT) {
			lowPoint.setEuclidianVisible(false);
			highPoint.setEuclidianVisible(showProbGeos);
			fldLow.setVisible(false);
			fldHigh.setVisible(true);
			lblBetween.setText(loc.getMenu("XLessThanOrEqual"));

			if (oldProbMode == PROB_RIGHT) {
				high = low;
			}

			if (isDiscrete)
				low = ((GeoNumeric) discreteValueList.get(0)).getDouble();
			else
				low = plotSettings.xMin - 1; // move offscreen so the integral
												// looks complete

		}

		else if (probMode == PROB_RIGHT) {
			lowPoint.setEuclidianVisible(showProbGeos);
			highPoint.setEuclidianVisible(false);
			fldLow.setVisible(true);
			fldHigh.setVisible(false);
			lblBetween.setText(loc.getMenu("LessThanOrEqualToX"));

			if (oldProbMode == PROB_LEFT) {
				low = high;
			}

			if (isDiscrete)
				high = ((GeoNumeric) discreteValueList.get(discreteValueList
						.size() - 1)).getDouble();
			else
				high = plotSettings.xMax + 1; // move offscreen so the integral
												// looks complete

		}

		// make result field editable for inverse probability calculation
		if (probMode != PROB_INTERVAL) {
			//fldResult.setBackground(fldLow.getBackground());
			//fldResult.setBorder(fldLow.getBorder());
			fldResult.setEditable(true);
			fldResult.setFocusable(true);

		} else {

			//fldResult.setBackground(wrapperPanel.getBackground());
			//fldResult.setBorder(BorderFactory.createEmptyBorder());
			fldResult.setEditable(false);
			fldResult.setFocusable(false);

		}

		if (isDiscrete) {
			high = Math.round(high);
			low = Math.round(low);

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
			if (hasIntegral)
				integral.update();
		}

		setMeanSigma();
		plotPanel.repaintView();
		//wrapperPanel.repaint();

	}
	
	public void setMeanSigma() {
		Double[] val = probManager.getDistributionMeasures(selectedDist,
				parameters);

		// mean/sigma are undefined for the Cauchy distribution
		if (selectedDist == DIST.CAUCHY) {
			lblMeanSigma.setText("");
			return;
		}

		for (int i = 0; i < parameters.length; i++) {
			format(parameters[i]);
			if (i < parameters.length - 1) {
			}
		}
		String mean = val[0] == null ? "" : format(val[0]);
		String sigma = val[1] == null ? "" : format(val[1]);

		String meanSigmaStr = Unicode.mu + " = " + mean + "   " + Unicode.sigma
				+ " = " + sigma;

		lblMeanSigma.setText(meanSigmaStr);
	}
	
	private void addRemoveTable(boolean showTable) {
		if (showTable) {
			plotSplitPane.add(((ProbabilityTableW)table).getWrappedPanel());
			tabbedPane.onResize();
			//plotSplitPane.setDividerSize(defaultDividerSize);
		} else {
			plotSplitPane.remove(((ProbabilityTableW)table).getWrappedPanel());
			tabbedPane.onResize();
			//plotSplitPane.setDividerSize(0);
		}
	}

	@Override
    protected void plotPanelUpdateSettings(PlotSettings settings) {
		((PlotPanelEuclidianViewW) plotPanel).commonFields.updateSettings(((PlotPanelEuclidianViewW) plotPanel), plotSettings);
	}

	@Override
    protected void updateDiscreteTable() {
		if (!probmanagerIsDiscrete())
			return;
		int[] firstXLastX = generateFirstXLastXCommon();
		((ProbabilityTableW) table).setTable(selectedDist, parameters, firstXLastX[0], firstXLastX[1]);
		tabbedPane.onResize();
		
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
						lblParameterArray[i].setText(parameterLabels[selectedDist
								.ordinal()][i]);
						// set field
						//fldParameterArray[i].removeActionListener(this);
						fldParameterArray[i].setText("" + format(parameters[i]));
						//fldParameterArray[i].setCaretPosition(0); //calls onblur every time it set
						//fldParameterArray[i].addActionListener(this);
					}
				}

				// set low/high interval field values
				fldLow.setText("" + format(low));
				//fldLow.setCaretPosition(0);
				fldHigh.setText("" + format(high));
				//fldHigh.setCaretPosition(0);
				fldResult.setText("" + format(probability));
				//fldResult.setCaretPosition(0);

				// set distribution combo box
				//comboDistribution.removeActionListener(this);
				if (comboDistribution.getValue(comboDistribution.getSelectedIndex()) != distributionMap
						.get(selectedDist))
					comboDistribution
							.setSelectedIndex(ListBoxApi.getIndexOf(distributionMap.get(selectedDist), comboDistribution));
				//comboDistribution.addActionListener(this);

				//btnIntervalLeft.removeActionListener(this);
				//btnIntervalBetween.removeActionListener(this);
				//btnIntervalRight.removeActionListener(this);

				
				btnCumulative.setValue(isCumulative);
				btnIntervalLeft.setValue(probMode == PROB_LEFT);
				btnIntervalBetween.setValue(probMode == PROB_INTERVAL);
				btnIntervalRight.setValue(probMode == PROB_RIGHT);				
				//btnIntervalLeft.addActionListener(this);
				//btnIntervalBetween.addActionListener(this);
				//btnIntervalRight.addActionListener(this);
    }

	public void onChange(ChangeEvent event) {
		Object source = event.getSource();
		if (source == comboDistribution) {

			if (comboDistribution.getSelectedIndex() > -1 && !comboDistribution.getValue(comboDistribution.getSelectedIndex()).equals(SEPARATOR))
				
				if (!selectedDist.equals(this.reverseDistributionMap
						.get(comboDistribution.getValue(comboDistribution.getSelectedIndex())))) {

					selectedDist = reverseDistributionMap.get(comboDistribution.getValue(comboDistribution
							.getSelectedIndex()));
					parameters = ProbabilityManager
							.getDefaultParameters(selectedDist);
					this.setProbabilityCalculator(selectedDist, parameters,
							isCumulative);
				}
		} else if (source == comboProbType) {
			updateProbabilityType();
		}
    }

	public void onFocus(FocusEvent event) {
		Object source = event.getSource();
	    if (source instanceof TextBox) {	    	
	    	((TextBox)event.getSource()).selectAll();
	    }
    }
	
	private void setProbabilityComboBoxMenu() {

		comboProbType.clear();
		comboProbHandler.removeHandler();
		if (isCumulative)
			comboProbType.addItem(loc.getMenu("LeftProb"));
		else {
			comboProbType.addItem(loc.getMenu("IntervalProb"));
			comboProbType.addItem(loc.getMenu("LeftProb"));
			comboProbType.addItem(loc.getMenu("RightProb"));
		}
		comboProbHandler = comboProbType.addChangeHandler(this);

	}

	private void setDistributionComboBoxMenu() {

		comboDistributionHandler.removeHandler();
		comboDistribution.clear();
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
		
		comboDistribution.addItem(SEPARATOR);


		comboDistribution.addItem(distributionMap.get(DIST.BINOMIAL));
		comboDistribution.addItem(distributionMap.get(DIST.PASCAL));
		comboDistribution.addItem(distributionMap.get(DIST.POISSON));
		comboDistribution.addItem(distributionMap.get(DIST.HYPERGEOMETRIC));

		comboDistribution.setSelectedIndex(ListBoxApi.getIndexOf(distributionMap.get(selectedDist), comboDistribution));
		comboDistribution.addChangeHandler(this);

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

	/**
	 * @return wheter distribution tab is open
	 */
	public boolean isDistributionTabOpen() {
		return tabbedPane.getSelectedIndex() == 0;
    }

	/**
	 * @return ProbabilitiManager
	 */
	public ProbabilityManager getProbManager() {
	   return probManager;
    }

	public void updatePrintFormat(int printDecimals, int printFigures) {
		this.printDecimals = printDecimals;
		this.printFigures = printFigures;
		updateGUI();
		updateDiscreteTable();
    }
	
	private class MyTabLayoutPanel extends TabLayoutPanel {

		public MyTabLayoutPanel(int splitterSize, Unit px) {
	        super(splitterSize, px);
        }
		
		@Override
        public void onResize() { 
			int width = probCalcPanel.getOffsetWidth() - ((ProbabilityTableW) table).getStatTable().getTable().getOffsetWidth() - 30;
			if (width > 0) { 
				plotPanel.setPreferredSize(new GDimensionW(width, PlotPanelEuclidianViewW.DEFAULT_HEIGHT));
				plotPanel.repaintView();
				plotPanel.getEuclidianController().calculateEnvironment();
				controlPanel.setWidth(width + "px");
			}
		}
		
	}

	public EuclidianViewW getPlotPanelEuclidianView() {
	    return (EuclidianViewW) plotPanel;
    }

	public void onValueChange(ValueChangeEvent<Boolean> event) {
		
		Object source = event.getSource();
		//App.debug("valuechangeevent: " + source.toString());
		if (source == btnCumulative) {
			setCumulative(btnCumulative.isSelected());

		} else if (source == btnIntervalLeft || source == btnIntervalBetween
				|| source == btnIntervalRight) {
			simulateRadioButtons((MyToggleButton2) source);

			if (!isCumulative) {
				updateProbabilityType();
			}
		}
		

    }

	private void simulateRadioButtons(MyToggleButton2 source) {
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
	
	private void doTextFieldActionPerformed(TextBox source) {
		if (isIniting)
			return;
		try {
			String inputText = source.getText().trim();
			if (!inputText.equals("") && !(inputText.charAt(inputText.length() -1) == '.') && !inputText.equals("-")) {
			// Double value = Double.parseDouble(source.getText());

				// allow input such as sqrt(2)
				NumberValue nv;
				nv = kernel.getAlgebraProcessor().evaluateToNumeric(inputText,
						false);
				double value = nv.getDouble();
				App.debug(value + "");
	
				if (source == fldLow.getTextBox()) {
					if (isValidInterval(probMode, value, high)) {
						low = value;
						setXAxisPoints();
					} else {
						updateGUI();
					}
	
				}
	
				else if (source == fldHigh.getTextBox()) {
					if (isValidInterval(probMode, low, value)) {
						high = value;
						setXAxisPoints();
					} else {
						updateGUI();
					}
				}
	
				// handle inverse probability
				else if (source == fldResult.getTextBox()) {
					if (value < 0 || value > 1) {
						updateGUI();
					} else {
						if (probMode == PROB_LEFT) {
							high = inverseProbability(value);
						}
						if (probMode == PROB_RIGHT) {
							low = inverseProbability(1 - value);
						}
						setXAxisPoints();
					}
				}
	
				else
					// handle parameter entry
					for (int i = 0; i < parameters.length; ++i)
						if (source == fldParameterArray[i].getTextBox()) {
	
							if (isValidParameter(value, i)) {
								parameters[i] = value;
								updateAll();
							}
	
						}
	
				updateIntervalProbability();
				updateGUI();
			}

		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

	}

	@Override
    public void onBlur(BlurEvent event) {
		TextBox source = (TextBox) event.getSource();
	    doTextFieldActionPerformed(source);
	    updateGUI();
    }

	@Override
    public void onKeyUp(KeyUpEvent event) {
	    TextBox source = (TextBox) event.getSource();
	    if (event.getNativeKeyCode() != KeyCodes.KEY_LEFT && event.getNativeKeyCode() != KeyCodes.KEY_RIGHT) {
	    	doTextFieldActionPerformed(source);
	    }
    }

	public void setInterval(double low, double high) {
		this.low = low;
		this.high = high;
		fldLow.setText("" + low);
		fldHigh.setText("" + high);
		setXAxisPoints();
		updateIntervalProbability();
		updateGUI();
	}
}
