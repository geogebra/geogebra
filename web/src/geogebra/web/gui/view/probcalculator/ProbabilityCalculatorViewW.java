package geogebra.web.gui.view.probcalculator;

import geogebra.common.gui.view.data.PlotSettings;
import geogebra.common.gui.view.probcalculator.ProbabilityCalcualtorView;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.util.MyToggleButton2;
import geogebra.web.main.AppW;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

/**
 * @author gabor
 * 
 * ProbablityCalculatorView for web
 *
 */
public class ProbabilityCalculatorViewW extends ProbabilityCalcualtorView implements ChangeHandler {

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
	    // TODO Auto-generated method stub
	    
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
	    
	    //Continue here after lunch :-)
	  
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

}
