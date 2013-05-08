package geogebra.web.gui.view.consprotocol;

import geogebra.web.gui.images.AppResources;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class ConstructionProtocolNavigationW extends FlowPanel{

	private AppW app;
	private Label lbSteps;
//	private ConstructionProtocolViewW prot;

	public ConstructionProtocolNavigationW(AppW app){
		this.app = app;
		
		lbSteps = new Label();
		
		//TODO: it must be called later - see desktop version
		initGUI();
	}
	
	public static String getImageIcon(String url) {
		return "<img width=\"16\" height=\"16\" src=\""+url+"\" />";
	}
	
	public void initGUI(){
		Button btFirst = new Button(getImageIcon(AppResources.INSTANCE.nav_skipback().getSafeUri().asString()));
		Button btLast = new Button(getImageIcon(AppResources.INSTANCE.nav_skipforward().getSafeUri().asString()));
		Button btPrev = new Button(getImageIcon(AppResources.INSTANCE.nav_rewind().getSafeUri().asString()));
		Button btNext = new Button(getImageIcon(AppResources.INSTANCE.nav_fastforward().getSafeUri().asString()));	
	
		FlowPanel leftPanel = new FlowPanel();
		leftPanel.add(btFirst);
		leftPanel.add(btPrev);
		leftPanel.add(lbSteps);			
		leftPanel.add(btNext);
		leftPanel.add(btLast);
		
		
		add(leftPanel);
	}
	
	/**
	 * Updates the texts that show the current construction step and
	 * the number of construction steps.	
	 */
//	public void update() {	
//		if (prot != null) {
//			int currentStep = prot.getCurrentStepNumber();
//			int stepNumber  = prot.getLastStepNumber();
//			lbSteps.setText(currentStep + " / " + stepNumber);	
//		}
//	}
	
	/**
	 * Registers this navigation bar at its protocol
	 * to be informed about updates.
	 * @param constructionProtocolView 
	 */
//	public void register(ConstructionProtocolViewW constructionProtocolView) { 
//		if (prot == null) { 
//			initGUI(); 
//		}
//		prot = constructionProtocolView;
//	}
}