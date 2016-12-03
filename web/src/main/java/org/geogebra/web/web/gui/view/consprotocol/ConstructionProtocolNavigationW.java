package org.geogebra.web.web.gui.view.consprotocol;

import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolNavigation;
import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.javax.swing.GSpinnerW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.util.ImageOrText;
import org.geogebra.web.web.gui.util.MyCJButton;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;

public class ConstructionProtocolNavigationW extends ConstructionProtocolNavigation implements ClickHandler{

	AppW app;
	private Label lbSteps;
	ConstructionProtocolViewW prot;
	private FlowPanel implPanel;
	private PushButton btFirst;
	private PushButton btLast;
	private PushButton btPrev;
	private PushButton btNext;
	PushButton btPlay;
	GSpinnerW spDelay;
	private AutomaticPlayer player;
	private MyCJButton btOpenWindow;
	private FlowPanel playPanel;
	
	final private Image playIcon = new Image(
			GuiResourcesSimple.INSTANCE.icons_play_circle());
	final private Image playIconHover = new Image(
			GuiResourcesSimple.INSTANCE.icons_play_circle_hover());
	final private Image pauseIcon = new Image(
			GuiResourcesSimple.INSTANCE.icons_play_pause_circle());
	final private Image pauseIconHover = new Image(
			GuiResourcesSimple.INSTANCE.icons_play_pause_circle_hover());

	public ConstructionProtocolNavigationW(AppW app, int viewID) {
		implPanel = new FlowPanel();
		this.app = app;
		
		spDelay = new GSpinnerW();
		
		lbSteps = new Label();
		this.viewID = viewID;
		
	}
	
	public static Image getImageForIcon(SafeUri src) {
		return new Image(src);
	}
	
	public void initGUI(){
		btFirst = new PushButton(new Image(
				GuiResources.INSTANCE.icons_play_skipback()));
		btFirst.getUpHoveringFace().setImage(
				new Image(
				GuiResources.INSTANCE.icons_play_skipback_hover()));

		btLast = new PushButton(new Image(
				GuiResources.INSTANCE.icons_play_skipforward()));
		btLast.getUpHoveringFace()
				.setImage(
						new Image(
				GuiResources.INSTANCE.icons_play_skipforward_hover()));

		btPrev = new PushButton(new Image(
				GuiResources.INSTANCE.icons_play_rewind()));
		btPrev.getUpHoveringFace().setImage(
				new Image(
				GuiResources.INSTANCE.icons_play_rewind_hover()));

		btNext = new PushButton(new Image(
				GuiResources.INSTANCE.icons_play_fastforward()));
		btNext.getUpHoveringFace()
				.setImage(
						new Image(
				GuiResources.INSTANCE.icons_play_fastforward_hover()));
	
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
		
		playPanel = new FlowPanel();
		playPanel.setVisible(showPlayButton);
		btPlay = new PushButton(); // will be initialized in setLabels()
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
		
		playPanel.add(btPlay);
		playPanel.add(spDelay);
		playPanel.add(new Label("s"));
		
		leftPanel.addStyleName("navbar_leftPanel");
		playPanel.addStyleName("navbar_playPanel");
		
		implPanel.add(leftPanel);
		implPanel.add(playPanel);
		
		btOpenWindow = new MyCJButton();
		btOpenWindow.setIcon(new ImageOrText(GuiResources.INSTANCE
				.icons_view_construction_protocol_p24()));
		// getImageForIcon(
		// AppResources.INSTANCE.table().getSafeUri()).toString());
		btOpenWindow.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				if (!app.getGuiManager().showView(
						App.VIEW_CONSTRUCTION_PROTOCOL)) {
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
		btOpenWindow.setVisible(isConsProtButtonVisible());
		addPaddingPlayPanel(showConsProtButton);
		btOpenWindow.addStyleName("navbar_btOpenWindow");
		implPanel.add(btOpenWindow);

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
	
	@Override
	public void register(ConstructionProtocolView constructionProtocolView) {
		if (prot == null) { 
			initGUI(); 
		}

		if (constructionProtocolView instanceof ConstructionProtocolViewW) {
			prot = (ConstructionProtocolViewW) constructionProtocolView;
			prot.registerNavigationBar(this);
		}
	}

	@Override
    public void setVisible(boolean visible) {
		implPanel.setVisible(visible);
    }

	@Override
    public void setPlayDelay(double delay) {
		playDelay = delay;
		
		try {
			spDelay.setValue(playDelay + "");
		} catch (Exception e) {
			spDelay.setValue(Math.round(playDelay) + "");
			
		}
    }

	@Override
    public void setConsProtButtonVisible(boolean flag) {
		showConsProtButton = flag;	
		if (btOpenWindow != null) {
			btOpenWindow.setVisible(isConsProtButtonVisible());
			addPaddingPlayPanel(isConsProtButtonVisible());
		}
    }

	@Override
    public void setLabels() {
		if (btPlay != null){
			// String btPlayText =
			// "<div class=\"gwt-Label\">"+app.getPlain((isPlaying)?"Pause":"Play")+"</div>";
			// Image playImage = getImageForIcon(
			// ((isPlaying) ? AppResources.INSTANCE.nav_pause() :
			// AppResources.INSTANCE.nav_play())
			// .getSafeUri());
			// btPlay.setHTML(playImage.toString()+btPlayText);

			if (isPlaying) {
				btPlay.getUpFace().setImage(playIcon);
				btPlay.getUpHoveringFace().setImage(playIconHover);
			} else {
				btPlay.getUpFace().setImage(pauseIcon);
				btPlay.getUpHoveringFace().setImage(pauseIconHover);
			}
		}
		if (btOpenWindow != null){
			btOpenWindow.setTitle(app.getLocalization().getPlainTooltip("ConstructionProtocol"));
		}
	}
	
	private void addPaddingPlayPanel(boolean addPadding) {
		if (addPadding) {
			playPanel.addStyleName("navbar_playPanel_padding");
		} else {
			playPanel.removeStyleName("navbar_playPanel_padding");
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
			return;
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
	
	@Override
	public void setButtonPlay() {
		// Image playImage = getImageForIcon(AppResources.INSTANCE.nav_play()
		// .getSafeUri());
		// btPlay.setHTML(playImage.toString() + "<div class=\"gwt-Label\">"
		// + app.getPlain("Play") + "</div>");
		btPlay.getUpFace().setImage(playIcon);
		btPlay.getUpHoveringFace().setImage(playIconHover);
	}

	@Override
	public void setButtonPause() {
		// Image playImage = getImageForIcon(AppResources.INSTANCE.nav_pause()
		// .getSafeUri());
		// btPlay.setHTML(playImage.toString() + "<div class=\"gwt-Label\">"
		// + app.getPlain("Pause") + "</div>");
		btPlay.getUpFace().setImage(pauseIcon);
		btPlay.getUpHoveringFace().setImage(pauseIconHover);
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
			app.setNavBarButtonPause();
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
			app.setNavBarButtonPlay();
			setComponentsEnabled(true);
        }
	}

	@Override
	protected void setPlayPanelVisible(boolean flag) {
		if (playPanel != null) {
			playPanel.setVisible(flag);
		}

	}
}