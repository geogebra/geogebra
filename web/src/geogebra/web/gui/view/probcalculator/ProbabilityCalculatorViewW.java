package geogebra.web.gui.view.probcalculator;

import geogebra.common.euclidian.event.KeyEvent;
import geogebra.common.euclidian.event.KeyHandler;
import geogebra.common.gui.view.data.PlotSettings;
import geogebra.common.gui.view.probcalculator.ProbabilityCalcualtorView;
import geogebra.common.gui.view.probcalculator.ProbabilityManager;
import geogebra.common.main.settings.ProbabilityCalculatorSettings.DIST;
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
	private AutoCompleteTextFieldW[] fldParamterArray;
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
	   
	   isIniting = false;
	   
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
	    table  = new ProbablitiyTableW(app, this);
	    tablePanel = new FlowPanel();
	    tablePanel.add(((ProbablitiyTableW)table).getWrappedPanel());    
    }

	private void createControlPanel() {
	    //distribution combobox panel
		FlowPanel cbPanel = new FlowPanel();
		cbPanel.add(comboDistribution);
		FlowPanel parameterPanel = new FlowPanel();
		
		//parameter panel
		for (int i = 0; i < maxParameterCount; i++) {
			parameterPanel.add(lblParameterArray[i]);
			parameterPanel.add(fldParamterArray[i]);
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
	    fldParamterArray = new AutoCompleteTextFieldW[maxParameterCount];
	    
	    for (int i = 0; i < maxParameterCount; i++) {
	    	lblParameterArray[i] = new Label();
	    	fldParamterArray[i] = new AutoCompleteTextFieldW(app);
	    	fldParamterArray[i].setColumns(5);
	    	fldParamterArray[i].addKeyHandler(this);
	    	fldParamterArray[i].addFocusHandler(this);
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
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void plotPanelUpdateSettings(PlotSettings settings) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void updateDiscreteTable() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void updateGUI() {
	    // TODO Auto-generated method stub
	    
    }

	public void onChange(ChangeEvent event) {
	    // TODO Auto-generated method stub
	    
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

}
