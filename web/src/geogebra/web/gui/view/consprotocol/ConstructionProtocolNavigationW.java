package geogebra.web.gui.view.consprotocol;

import geogebra.common.gui.view.consprotocol.ConstructionProtocolNavigation;
import geogebra.common.main.App;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class ConstructionProtocolNavigationW extends ConstructionProtocolNavigation implements ClickHandler{

	private AppW app;
	private Label lbSteps;
	private ConstructionProtocolViewW prot;
	private FlowPanel implPanel;
	private Button btFirst;
	private Button btLast;
	private Button btPrev;
	private Button btNext;
	

	public ConstructionProtocolNavigationW(AppW app){
		implPanel = new FlowPanel();
		this.app = app;
		
		lbSteps = new Label();
		
	}
	
	public static String getImageIcon(String url) {
		return "<img width=\"16\" height=\"16\" src=\""+url+"\" />";
	}
	
	public void initGUI(){
		btFirst = new Button(getImageIcon(AppResources.INSTANCE.nav_skipback().getSafeUri().asString()));
		btLast = new Button(getImageIcon(AppResources.INSTANCE.nav_skipforward().getSafeUri().asString()));
		btPrev = new Button(getImageIcon(AppResources.INSTANCE.nav_rewind().getSafeUri().asString()));
		btNext = new Button(getImageIcon(AppResources.INSTANCE.nav_fastforward().getSafeUri().asString()));	
	
		btFirst.addClickHandler(this);
		btLast.addClickHandler(this);
		btPrev.addClickHandler(this);
		btNext.addClickHandler(this);
		
		
		FlowPanel leftPanel = new FlowPanel();
		leftPanel.add(btFirst);
		leftPanel.add(btPrev);
		leftPanel.add(lbSteps);			
		leftPanel.add(btNext);
		leftPanel.add(btLast);
		
		
		implPanel.add(leftPanel);
		update();
	}
	
	/**
	 * Updates the texts that show the current construction step and
	 * the number of construction steps.	
	 */
	public void update() {
		if (prot != null) {
			int currentStep = prot.getCurrentStepNumber();
			int stepNumber  = prot.getLastStepNumber();
			lbSteps.setText(currentStep + " / " + stepNumber);	
		}
	}
	
	/**
	 * Registers this navigation bar at its protocol
	 * to be informed about updates.
	 * @param constructionProtocolView 
	 */
	public void register(ConstructionProtocolViewW constructionProtocolView) { 
		if (prot == null) { 
			initGUI(); 
		}
		prot = constructionProtocolView;
	}

	@Override
    public void setVisible(boolean visible) {
		implPanel.setVisible(visible);
    }

	@Override
    public void setPlayDelay(double delay) {
	    // TODO Auto-generated method stub
		App.debug("ConstructionProtocolNavigationW.setPlayDelay(double) -implementation needed");
	    
    }

	@Override
    public void setPlayButtonVisible(boolean flag) {
	    // TODO Auto-generated method stub
		App.debug("ConstructionProtocolNavigationW.setPlayButtonVisible(boolean) -implementation needed");
	    
    }

	@Override
    public void setConsProtButtonVisible(boolean flag) {
	    // TODO Auto-generated method stub
		App.debug("ConstructionProtocolNavigationW.setconsProtButtonVisible(boolean) -implementation needed");
    }

	@Override
    public void setLabels() {
	    // TODO Auto-generated method stub
		App.debug("ConstructionProtocolNavigationW.setLabels() -implementation needed");
    }
	
	public FlowPanel getImpl(){
		return implPanel;
	}

	public void onClick(ClickEvent event) {
	    // TODO Auto-generated method stub
	    
		Object source = event.getSource();
		
		//TODO : set cursor for wait cursor
		
		if (source == btFirst) {
			prot.firstStep();		
		} 
		else if (source == btLast) {			
			prot.lastStep();
		}
		else if (source == btPrev) {
			prot.previousStep();
		}
		else if (source == btNext) {
			prot.nextStep();
		}
		
    }
}