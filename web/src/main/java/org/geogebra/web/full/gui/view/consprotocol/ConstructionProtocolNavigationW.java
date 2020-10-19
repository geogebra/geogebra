package org.geogebra.web.full.gui.view.consprotocol;

import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolNavigation;
import org.geogebra.common.kernel.ConstructionStepper;
import org.geogebra.common.main.App;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.gui.util.MyCJButton;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.util.GPushButton;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.javax.swing.GSpinnerW;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.timer.client.Timer;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class ConstructionProtocolNavigationW
		extends ConstructionProtocolNavigation implements ClickHandler {

	private Label lbSteps;
	private FlowPanel implPanel;
	private GPushButton btFirst;
	private GPushButton btLast;
	private GPushButton btPrev;
	private GPushButton btNext;
	GPushButton btPlay;
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

	/**
	 * @param app
	 *            application
	 * @param viewID
	 *            parent view ID
	 */
	public ConstructionProtocolNavigationW(AppW app, int viewID) {
		super(app, viewID);
		implPanel = new FlowPanel();
		
		spDelay = new GSpinnerW();
		
		lbSteps = new Label();
	}
	
	public static Image getImageForIcon(SafeUri src) {
		return new Image(src);
	}

	@Override
	protected void initGUI() {
		btFirst = new GPushButton(
				new Image(GuiResources.INSTANCE.icons_play_skipback()));
		btFirst.getUpHoveringFace().setImage(
				new Image(
				GuiResources.INSTANCE.icons_play_skipback_hover()));

		btLast = new GPushButton(new Image(
				GuiResources.INSTANCE.icons_play_skipforward()));
		btLast.getUpHoveringFace()
				.setImage(
						new Image(
				GuiResources.INSTANCE.icons_play_skipforward_hover()));

		btPrev = new GPushButton(new Image(
				GuiResources.INSTANCE.icons_play_rewind()));
		btPrev.getUpHoveringFace().setImage(
				new Image(
				GuiResources.INSTANCE.icons_play_rewind_hover()));

		btNext = new GPushButton(
				new Image(
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
		btPlay = new GPushButton(); // will be initialized in setLabels()
		btPlay.addClickHandler(this);
	
		spDelay.addChangeHandler(new ChangeHandler() {

			@Override
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

			@Override
			public void onClick(ClickEvent event) {
				toggleConstructionProtocol();

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
	 * Show or hide construction protocol
	 */
	protected void toggleConstructionProtocol() {
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

	/**
	 * Updates the texts that show the current construction step and the number
	 * of construction steps.
	 */
	@Override
    public void update() {
		int currentStep = getProt().getCurrentStepNumber();
		int stepNumber = getProt().getLastStepNumber();
			lbSteps.setText(currentStep + " / " + stepNumber);	
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
		if (btPlay != null) {
			// String btPlayText =
			// "<div class=\"gwt-Label\">"+app.getPlain((isPlaying)?"Pause":"Play")+"</div>";
			// Image playImage = getImageForIcon(
			// ((isPlaying) ? AppResources.INSTANCE.nav_pause() :
			// AppResources.INSTANCE.nav_play())
			// .getSafeUri());
			// btPlay.setHTML(playImage.toString()+btPlayText);

			if (isPlaying()) {
				btPlay.getUpFace().setImage(playIcon);
				btPlay.getUpHoveringFace().setImage(playIconHover);
			} else {
				btPlay.getUpFace().setImage(pauseIcon);
				btPlay.getUpHoveringFace().setImage(pauseIconHover);
			}
		}
		if (btOpenWindow != null) {
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

	public FlowPanel getImpl() {
		return implPanel;
	}

	@Override
	public void onClick(ClickEvent event) {
		Object source = event.getSource();
		
		ConstructionStepper stepper = getProt();

		if (source == btFirst) {
			stepper.firstStep();
		} 
		else if (source == btLast) {			
			stepper.lastStep();
		}
		else if (source == btPrev) {
			stepper.previousStep();
		}
		else if (source == btNext) {
			stepper.nextStep();
			return;
		}
		else if (source == btPlay) {
			if (isPlaying()) {
				player.stopAnimation();
			} else {
				player = new AutomaticPlayer();
				player.startAnimation();
			}
		}

		if (prot != null) {
			prot.scrollToConstructionStep();
		}
	}
	
	/**
	 * Make all components enabled / disabled
	 * @param flag whether components should be enabled
	 */
	void setComponentsEnabled(boolean flag) {
		NodeList<Element> buttons = implPanel.getElement().getElementsByTagName("button");
		for (int i = 0; i < buttons.getLength(); i++) {
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

	private class AutomaticPlayer {
		Timer timer;
		
	      /**
         * Creates a new player to step through the construction
         * automatically.
         */
		public AutomaticPlayer() {
			timer = new Timer() {
				
				@Override
                public void run() {
					getProt().nextStep();
					if (getProt().getCurrentStepNumber() == getProt()
							.getLastStepNumber()) {
		        		stopAnimation();
		        	}
					if (isPlaying()) {
		        		timer.schedule((int) (playDelay * 1000));
		        	}	                
                }
				
			};
		}

		public synchronized void startAnimation() {
//			app.startDispatchingEventsTo(btPlay);
			//TODO set cursor:wait
			
			setPlaying(true);
			app.setNavBarButtonPause();
			setComponentsEnabled(false);

			if (getProt().getCurrentStepNumber() == getProt()
					.getLastStepNumber()) {
				getProt().setConstructionStep(-1);
			}

			timer.run();
		}
		
        public synchronized void stopAnimation() {
        	//TODO remove cursor:wait
        	timer.cancel();
            
            // unblock application events
//			app.stopDispatchingEvents();
			setPlaying(false);
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