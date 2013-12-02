package geogebra.web.gui.view.probcalculator;

import geogebra.common.euclidian.event.KeyEvent;
import geogebra.common.euclidian.event.KeyHandler;
import geogebra.common.gui.view.data.PlotSettings;
import geogebra.common.gui.view.probcalculator.ProbabilityCalcualtorView;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.util.MyToggleButton2;
import geogebra.web.main.AppW;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

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
	private ListBox comboDistributon;
	private Label lblDist;
	private MyToggleButton2 btnCumulative;
	private MyToggleButton2 btnIntervalLeft;
	private MyToggleButton2 btnIntervalBetween;
	private MyToggleButton2 btnIntervalRight;
	private MyToggleButton2 btnExport;
	private Label[] lblParameterArray;
	private AutoCompleteTextFieldW[] fldParamterArray;
	private ListBox comboProbType;
	private Label lblProb;
	private Label lblProbOf;
	private Label lblBetween;
	private Label lblEndProbOf;
	private AutoCompleteTextFieldW fldLow;
	private AutoCompleteTextFieldW fldHigh;
	private AutoCompleteTextFieldW fldResult;
	private Label lblMeanSigma;
	
	/**
	 * @param app creates new probabilitycalculatorView
	 */
	public ProbabilityCalculatorViewW(AppW app) {
	   super(app);
	   
	   wrappedPanel = new FlowPanel();
	   wrappedPanel.addStyleName("GGWPropabilityCalculator");
	   
	   createGUIElements();
	   createLayoutPanels();
	   buildProbCalcPanel();
	   isIniting = false;
	   
    }
	
	private void buildProbCalcPanel() {
	    // TODO Auto-generated method stub
	    
    }

	private void createLayoutPanels() {
		//control panel
	    createControlPanel();
	    
    }

	private void createControlPanel() {
	    //distribution combobox panel
		FlowPanel cbPanel = new FlowPanel();
		cbPanel.add(comboDistributon);
		FlowPanel parameterPanel = new FlowPanel();
		
		//parameter panel
		for (int i = 0; i < maxParameterCount; i++) {
			parameterPanel.add(lblParameterArray[i]);
			parameterPanel.add(fldParamterArray[i]);
		}
		
		// interval panel
		// continue here.....
    }

	private void createGUIElements() {
		setLabelArrays();;
	    comboDistributon = new ListBox();
	    comboDistributon.addChangeHandler(this);
	    
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
	    comboProbType.addChangeHandler(this);
	    
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

}
