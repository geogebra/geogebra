package org.geogebra.web.web.gui.view.consprotocol;

import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolNavigation;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.javax.swing.GPanelW;
import org.geogebra.web.html5.javax.swing.GSpinnerW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.images.AppResources;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class ConstructionProtocolNavigationW extends ConstructionProtocolNavigation implements ClickHandler{

	AppW app;
	private Label lbSteps;
	ConstructionProtocolViewW prot;
	private FlowPanel implPanel;
	private Button btFirst;
	private Button btLast;
	private Button btPrev;
	private Button btNext;
	Button btPlay;
	GSpinnerW spDelay;
	private AutomaticPlayer player;
	private Button btOpenWindow;
	

	public ConstructionProtocolNavigationW(AppW app){
		implPanel = new FlowPanel();
		this.app = app;
		
		spDelay = new GSpinnerW();
		
		lbSteps = new Label();
		
	}
	
	public static Image getImageForIcon(SafeUri src) {
		return new Image(src);
	}
	
	public void initGUI(){
		btFirst = new Button(getImageForIcon(AppResources.INSTANCE.nav_skipback().getSafeUri()).toString());
		btLast = new Button(getImageForIcon(AppResources.INSTANCE.nav_skipforward().getSafeUri()).toString());
		btPrev = new Button(getImageForIcon(AppResources.INSTANCE.nav_rewind().getSafeUri()).toString());
		btNext = new Button(getImageForIcon(AppResources.INSTANCE.nav_fastforward().getSafeUri()).toString());	
	
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
		
		playPanel = new GPanelW();
		playPanel.setVisible(showPlayButton);
		btPlay = new Button();	//will be initialized in setLabels()
		btPlay.addClickHandler(this);
	
		spDelay.addChangeHandler(new ChangeHandler(){

			public void onChange(ChangeEvent event) {
				try {
					playDelay = Double.parseDouble(spDelay.getValue().toString());
				} catch (Exception ex) {
					playDelay = 2;
				} 
            }
			
		});
		
		((GPanelW)playPanel).getImpl().add(btPlay);
		((GPanelW)playPanel).getImpl().add(spDelay);
		((GPanelW)playPanel).getImpl().add(new Label("s"));
		
		leftPanel.addStyleName("navbar_leftPanel");
		((GPanelW)playPanel).getImpl().addStyleName("navbar_playPanel");
		
		implPanel.add(leftPanel);
		implPanel.add(((GPanelW)playPanel).getImpl());
		
		if (!app.isApplet()) {
			btOpenWindow = new Button(getImageForIcon(AppResources.INSTANCE.table().getSafeUri()).toString());		
			btOpenWindow.addClickHandler(new ClickHandler(){
	
				public void onClick(ClickEvent event) {
					if (!app.getGuiManager().showView(App.VIEW_CONSTRUCTION_PROTOCOL)) {
						app.getGuiManager().setShowView(true,
						        App.VIEW_CONSTRUCTION_PROTOCOL);
						btOpenWindow.addStyleName("consProtIsOpen");
					} else {
						app.getGuiManager().setShowView(false,
						        App.VIEW_CONSTRUCTION_PROTOCOL);
						btOpenWindow.removeStyleName("consProtIsOpen");
					}
					
	            }
			});
			btOpenWindow.setVisible(showConsProtButton);
			addPaddingPlayPanel(showConsProtButton);
			btOpenWindow.addStyleName("navbar_btOpenWindow");
			implPanel.add(btOpenWindow);
		}

		setLabels();
		update();
	}
	
	/**
	 * Updates the texts that show the current construction step and
	 * the number of construction steps.	
	 */
	@Override
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
	 * @param constructionProtocolView {@link ConstructionProtocolViewW}
	 */
	public void register(ConstructionProtocolViewW constructionProtocolView) { 
		if (prot == null) { 
			initGUI(); 
		}
		prot = constructionProtocolView;
		prot.registerNavigationBar(this);
	}

	@Override
    public void setVisible(boolean visible) {
		implPanel.setVisible(visible);
    }

	@Override
    public void setPlayDelay(double delay) {
		playDelay = delay;
		
		try {
			spDelay.setValue(new Double(playDelay) + "");
		} catch (Exception e) {
			spDelay.setValue(new Integer((int) Math.round(playDelay))+"");
			
		}
    }

	@Override
    public void setConsProtButtonVisible(boolean flag) {
		showConsProtButton = flag;	
		if (btOpenWindow != null) {
			btOpenWindow.setVisible(flag);
			addPaddingPlayPanel(flag);
		}
    }

	@Override
    public void setLabels() {
		if (btPlay != null){
			String btPlayText = "<div class=\"gwt-Label\">"+app.getPlain((isPlaying)?"Pause":"Play")+"</div>";
			Image playImage = getImageForIcon(
					((isPlaying) ? AppResources.INSTANCE.nav_pause() : AppResources.INSTANCE.nav_play())
					.getSafeUri());
			btPlay.setHTML(playImage.toString()+btPlayText);
		}
		if (btOpenWindow != null){
			btOpenWindow.setTitle(app.getLocalization().getPlainTooltip("ConstructionProtocol"));
		}
	}
	
	private void addPaddingPlayPanel(boolean addPadding) {
		if (addPadding) {
			((GPanelW)playPanel).getImpl().addStyleName("navbar_playPanel_padding");
		} else {
			((GPanelW)playPanel).getImpl().removeStyleName("navbar_playPanel_padding");
		}
	}
	
	public FlowPanel getImpl(){
		return implPanel;
	}

	public void onClick(ClickEvent event) {
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
		else if (source == btPlay){
			if (isPlaying){
				player.stopAnimation();
			} else {
				player = new AutomaticPlayer(playDelay);
				player.startAnimation();
			}
		}
		prot.scrollToConstructionStep();
    }
	
	/**
	 * Make all components enabled / disabled
	 * @param flag whether components should be enabled
	 */
	void setComponentsEnabled(boolean flag) {
		NodeList<Element> buttons = implPanel.getElement().getElementsByTagName("button");
		for (int i=0; i < buttons.getLength(); i++) {
			buttons.getItem(i).setPropertyBoolean("disabled", !flag);
		}
		btPlay.setEnabled(true);	
		//? lbSteps.setEnabled(true);
	}		
	
	private class AutomaticPlayer{
		Timer timer;
		
	      /**
         * Creates a new player to step through the construction
         * automatically.
         * @param delay in seconds between steps
         */
		public AutomaticPlayer(double delay){
			timer = new Timer(){
				
				@Override
                public void run() {
					prot.nextStep();        	
		        	if (prot.getCurrentStepNumber() == prot.getLastStepNumber()) {
		        		stopAnimation();
		        	}
		        	if (isPlaying){
		        		timer.schedule((int) (playDelay * 1000));
		        	}	                
                }
				
			};
		}

		public synchronized void startAnimation() {
//			app.startDispatchingEventsTo(btPlay);
			//TODO set cursor:wait
			
			isPlaying = true;
			Image playImage = getImageForIcon(AppResources.INSTANCE.nav_pause().getSafeUri());
			btPlay.setHTML(playImage.toString()+"<div class=\"gwt-Label\">"+app.getPlain("Pause")+"</div>");
			setComponentsEnabled(false);

			if (prot.getCurrentStepNumber() == prot.getLastStepNumber()) {
				prot.setConstructionStep(-1);
			}

			timer.run();
		}
		
        public synchronized void stopAnimation() {
        	//TODO remove cursor:wait
        	timer.cancel();
            
            // unblock application events
//			app.stopDispatchingEvents();
			isPlaying = false;
			Image playImage = getImageForIcon(AppResources.INSTANCE.nav_play().getSafeUri());
			btPlay.setHTML(playImage.toString()+"<div class=\"gwt-Label\">"+app.getPlain("Play")+"</div>");
			setComponentsEnabled(true);
        }
	}
}