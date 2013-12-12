package geogebra.web.gui.view.probcalculator;

import geogebra.common.euclidian.event.KeyEvent;
import geogebra.common.euclidian.event.KeyHandler;
import geogebra.common.gui.SetLabels;
import geogebra.common.gui.view.data.PlotSettings;
import geogebra.common.gui.view.probcalculator.ProbabilityCalcualtorView;
import geogebra.common.gui.view.probcalculator.ProbabilityManager;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.settings.ProbabilityCalculatorSettings.DIST;
import geogebra.common.util.Unicode;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.html5.main.GlobalKeyDispatcherW;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.util.MyToggleButton2;
import geogebra.web.gui.view.data.PlotPanelEuclidianViewW;
import geogebra.web.main.AppW;

import java.util.HashMap;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
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
public class ProbabilityCalculatorViewW extends ProbabilityCalcualtorView implements ChangeHandler, ClickHandler, KeyHandler, FocusHandler {

	/**
	 * @param app App
	 * creates new probabilitycalculatorView
	 */
	
	private FlowPanel wrappedPanel;
	private Label lblDist;
	private MyToggleButton2 btnCumulative;
	private MyToggleButton2 btnIntervalLeft;
	private MyToggleButton2 btnIntervalBetween;
	private MyToggleButton2 btnIntervalRight;
	private MyToggleButton2 btnExport;
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
	private FlowPanel controlPanel;
	private ScheduledCommand exportAction;
	private ScheduledCommand exportToEVAction;
	private FlowPanel plotPanelPlus;
	private FlowPanel tablePanel;
	private FlowPanel plotSplitPane;
	private FlowPanel mainSplitPane;
	private FlowPanel probCalcPanel;
	private StatisticsCalculatorW statCalculator;
	private TabLayoutPanel tabbedPane;
	private ProbabilityCalculatorStyleBarW styleBar;
	private HandlerRegistration comboProbHandler, comboDistributionHandler;
	
	/**
	 * @param app creates new probabilitycalculatorView
	 */
	public ProbabilityCalculatorViewW(AppW app) {
	   super(app);
	   
	   wrappedPanel = new FlowPanel();
	   wrappedPanel.addStyleName("GGWPropabilityCalculator");
	   
	   createGUIElements();
	   createExportToEvAction();
	   createLayoutPanels();
	   buildProbCalcPanel();
	   
	   statCalculator = new StatisticsCalculatorW(app);
	   
	   tabbedPane = new TabLayoutPanel(30, Unit.PX);
	   tabbedPane.add(probCalcPanel, loc.getMenu("Distribution"));
	   tabbedPane.add(((StatisticsCalculatorW) statCalculator).getWrappedPanel(), loc.getMenu("Statistics"));
	   
	   tabbedPane.addSelectionHandler(new SelectionHandler<Integer>() {

		public void onSelection(SelectionEvent<Integer> event) {
			if (styleBar != null)
				styleBar.updateLayout();
		   }
	   });
	   
	   wrappedPanel.add(tabbedPane);
	   
	   setLabels();
	   
	   attachView();
	   settingsChanged(app.getSettings().getProbCalcSettings());
	   
	   
	   isIniting = false;
	   
    }
	
	public void setLabels() {

		tabbedPane.setTabText(0, loc.getMenu("Distribution"));

		((SetLabels) statCalculator).setLabels();
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
		wrappedPanel.clear();
		plotSplitPane = new FlowPanel();
		plotSplitPane.add(plotPanelPlus);
		
		mainSplitPane = new FlowPanel();
		mainSplitPane.add(plotSplitPane);
		probCalcPanel = new FlowPanel();
		
		probCalcPanel.add(mainSplitPane);
		
		//TODO: do css here!
		
		
		
    }

	private void createLayoutPanels() {
		//control panel
	    createControlPanel();
	    plotPanel = new PlotPanelEuclidianViewW(kernel, exportToEVAction);

	    ((PlotPanelEuclidianViewW) plotPanel).setMouseEnabled(true, true);
	    ((PlotPanelEuclidianViewW) plotPanel).setMouseMotionEnabled(true);
	    
	    FlowPanel plotLabelPanel = new FlowPanel();
	    plotLabelPanel.add(lblMeanSigma);
	    plotPanelPlus = new FlowPanel();
	    plotPanelPlus.add(((PlotPanelEuclidianViewW)plotPanel).getComponent());
	    
	    //table panel
	    table  = new ProbabilityTableW(app, this);
	    tablePanel = new FlowPanel();
	    tablePanel.add(((ProbabilityTableW)table).getWrappedPanel());    
    }

	private void createControlPanel() {
	    //distribution combobox panel
		FlowPanel cbPanel = new FlowPanel();
		cbPanel.add(comboDistribution);
		FlowPanel parameterPanel = new FlowPanel();
		
		//parameter panel
		for (int i = 0; i < maxParameterCount; i++) {
			parameterPanel.add(lblParameterArray[i]);
			parameterPanel.add(fldParameterArray[i]);
		}
		
		// interval panel
		// continue here.....
		FlowPanel tb = new FlowPanel();
		tb.add(btnIntervalLeft);
		tb.add(btnIntervalBetween);
		tb.add(btnIntervalRight);
		
		FlowPanel p = new FlowPanel();
		p.add(btnCumulative);
		p.add(lblMeanSigma);
		
		controlPanel = new FlowPanel();
		controlPanel.add(btnCumulative);
		controlPanel.add(cbPanel);
		controlPanel.add(parameterPanel);
		controlPanel.add(tb);
		controlPanel.add(lblProbOf);
		controlPanel.add(fldLow);
		controlPanel.add(lblBetween);
		controlPanel.add(fldHigh);
		controlPanel.add(lblEndProbOf);
		controlPanel.add(fldResult);		
    }

	private void createGUIElements() {
		setLabelArrays();
	    comboDistribution = new ListBox();
	    comboDistribution.addChangeHandler(this);
	    
	    lblDist = new Label();
	    
	    btnCumulative = new MyToggleButton2(AppResources.INSTANCE.cumulative_distribution());
	    
	    btnIntervalLeft = new MyToggleButton2(AppResources.INSTANCE.interval_left());
	    
	    btnIntervalBetween = new MyToggleButton2(AppResources.INSTANCE.interval_between());
	    
	    btnIntervalRight = new MyToggleButton2(AppResources.INSTANCE.interval_right());
	    
	    btnCumulative.addClickHandler(this);
	    btnIntervalLeft.addClickHandler(this);
	    btnIntervalBetween.addClickHandler(this);
	    btnIntervalRight.addClickHandler(this);
	    
	    //buttonGroup
	    FlowPanel gp = new FlowPanel();
	    gp.add(btnIntervalLeft);
	    gp.add(btnIntervalBetween);
	    gp.add(btnIntervalRight);
	    
	    btnExport = new MyToggleButton2(AppResources.INSTANCE.export());
	    btnExport.addClickHandler(this);
	    
	    lblParameterArray = new Label[maxParameterCount];
	    fldParameterArray = new AutoCompleteTextFieldW[maxParameterCount];
	    
	    for (int i = 0; i < maxParameterCount; i++) {
	    	lblParameterArray[i] = new Label();
	    	fldParameterArray[i] = new AutoCompleteTextFieldW(app);
	    	fldParameterArray[i].setColumns(5);
	    	fldParameterArray[i].addKeyHandler(this);
	    	fldParameterArray[i].addFocusHandler(this);
	    }
	    
	    comboProbType = new ListBox();
	    comboProbHandler = comboProbType.addChangeHandler(this);
	    
	    lblProb = new Label();
	    
	    lblProbOf = new Label(); // <= X <=
	    lblBetween = new Label();
	    lblEndProbOf = new Label();
	    
	    fldLow = new AutoCompleteTextFieldW(app);
	    fldLow.setColumns(5);
	    fldLow.addKeyHandler(this);
	    fldLow.addFocusHandler(this);
	    
	    fldHigh = new AutoCompleteTextFieldW(app);
	    fldHigh.setColumns(6);
	    fldHigh.addKeyHandler(this);
	    fldHigh.addFocusHandler(this);
	    
	    fldResult = new AutoCompleteTextFieldW(app);
	    fldResult.setColumns(6);
	    fldResult.addKeyHandler(this);
	    fldResult.addFocusHandler(this);
	    
	    lblMeanSigma = new Label();
	    	  
    }

	/**
	 * inits the gui of ProbablityCalculatorView
	 */
	public void initGUI() {
		this.wrappedPanel = new FlowPanel();
	}
	
	/**
	 * @return the wrapper panel of this view
	 */
	public FlowPanel getWrapperPanel() {
		return wrappedPanel;
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

		String distStr = distributionMap.get(selectedDist) + "(";
		for (int i = 0; i < parameters.length; i++) {
			distStr += format(parameters[i]);
			if (i < parameters.length - 1) {
				distStr += ",";
			}
		}
		distStr += ")";

		String mean = val[0] == null ? "" : format(val[0]);
		String sigma = val[1] == null ? "" : format(val[1]);

		String meanSigmaStr = Unicode.mu + " = " + mean + "   " + Unicode.sigma
				+ " = " + sigma;

		lblMeanSigma.setText(meanSigmaStr);
	}
	
	private void addRemoveTable(boolean showTable) {
		if (showTable) {
			plotSplitPane.add(tablePanel);
			//plotSplitPane.setDividerSize(defaultDividerSize);
		} else {
			plotSplitPane.remove(tablePanel);
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
						fldParameterArray[i].setCaretPosition(0);
						//fldParameterArray[i].addActionListener(this);
					}
				}

				// set low/high interval field values
				fldLow.setText("" + format(low));
				fldLow.setCaretPosition(0);
				fldHigh.setText("" + format(high));
				fldHigh.setCaretPosition(0);
				fldResult.setText("" + format(probability));
				fldResult.setCaretPosition(0);

				// set distribution combo box
				//comboDistribution.removeActionListener(this);
				if (comboDistribution.getValue(comboDistribution.getSelectedIndex()) != distributionMap
						.get(selectedDist))
					comboDistribution
							.setSelectedIndex(getIndexOf(distributionMap.get(selectedDist), comboDistribution));
				//comboDistribution.addActionListener(this);

				//btnIntervalLeft.removeActionListener(this);
				//btnIntervalBetween.removeActionListener(this);
				//btnIntervalRight.removeActionListener(this);

				btnCumulative.setSelected(isCumulative);
				btnIntervalLeft.setSelected(probMode == PROB_LEFT);
				btnIntervalBetween.setSelected(probMode == PROB_INTERVAL);
				btnIntervalRight.setSelected(probMode == PROB_RIGHT);

				//btnIntervalLeft.addActionListener(this);
				//btnIntervalBetween.addActionListener(this);
				//btnIntervalRight.addActionListener(this);
    }

	public void onChange(ChangeEvent event) {
	    // TODO continue here tomorrow with events!
	    
    }

	public void onClick(ClickEvent event) {
	    // TODO Auto-generated method stub
	    
    }

	public void keyReleased(KeyEvent e) {
	    // TODO Auto-generated method stub
	    
    }

	public void onFocus(FocusEvent event) {
	    // TODO Auto-generated method stub
	    
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


		comboDistribution.addItem(distributionMap.get(DIST.BINOMIAL));
		comboDistribution.addItem(distributionMap.get(DIST.PASCAL));
		comboDistribution.addItem(distributionMap.get(DIST.POISSON));
		comboDistribution.addItem(distributionMap.get(DIST.HYPERGEOMETRIC));

		comboDistribution.setSelectedIndex(getIndexOf(distributionMap.get(selectedDist), comboDistribution));
		comboDistribution.addChangeHandler(this);

	}

	private static int getIndexOf(String value, ListBox lb) {
		int indexToFind = -1;
		for (int i = 0; i < lb.getItemCount(); i++) {
		    if (lb.getValue(i).equals(value)) {
		        indexToFind = i;
		        break;
		    }
		};
		return indexToFind;
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

}
