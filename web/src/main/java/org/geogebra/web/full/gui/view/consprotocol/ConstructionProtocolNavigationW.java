package org.geogebra.web.full.gui.view.consprotocol;

import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolNavigation;
import org.geogebra.common.kernel.ConstructionStepper;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.gui.util.MyCJButton;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.util.GPushButton;
import org.geogebra.web.html5.gui.util.GToggleButton;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.javax.swing.GSpinnerW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.timer.client.Timer;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class ConstructionProtocolNavigationW
		extends ConstructionProtocolNavigation implements ClickHandler {

	private final Label lbSteps;
	private final FlowPanel implPanel;
	private GPushButton btFirst;
	private GPushButton btLast;
	private GPushButton btPrev;
	private GPushButton btNext;
	private GToggleButton btPlay;
	private final GSpinnerW spDelay;
	private AutomaticPlayer player;
	private MyCJButton btOpenWindow;
	private FlowPanel playPanel;

	private static final String hoverColor = GeoGebraColorConstants.GEOGEBRA_ACCENT.toString();

	private final Image playIcon
			= getIcon(GuiResourcesSimple.INSTANCE.play_circle());
	private final Image playIconHover
			= getFilledIcon(GuiResourcesSimple.INSTANCE.play_circle());
	private final Image pauseIcon
			= getIcon(GuiResourcesSimple.INSTANCE.pause_circle());
	private final Image pauseIconHover
			= getFilledIcon(GuiResourcesSimple.INSTANCE.pause_circle());
	private final Image skipBackIcon
			= getIcon(GuiResourcesSimple.INSTANCE.skip_previous());
	private final Image skipBackIconHover
			= getFilledIcon(GuiResourcesSimple.INSTANCE.skip_previous());
	private final Image skipForwardIcon
			= getIcon(GuiResourcesSimple.INSTANCE.skip_next());
	private final Image skipForwardIconHover
			= getFilledIcon(GuiResourcesSimple.INSTANCE.skip_next());
	private final Image rewindIcon
			= getIcon(GuiResourcesSimple.INSTANCE.fast_rewind());
	private final Image rewindIconHover
			= getFilledIcon(GuiResourcesSimple.INSTANCE.fast_rewind());
	private final Image fastForwardIcon
			= getIcon(GuiResourcesSimple.INSTANCE.fast_forward());
	private final Image fastForwardIconHover
			= getFilledIcon(GuiResourcesSimple.INSTANCE.fast_forward());

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
	
	private static Image getIcon(SVGResource resource) {
		return new Image(resource.getSafeUri(), 0, 0, 24, 24);
	}

	private static Image getFilledIcon(SVGResource resource) {
		return new Image(resource.withFill(hoverColor).getSafeUri(), 0, 0, 24, 24);
	}

	@Override
	protected void initGUI() {
		btFirst = new GPushButton(skipBackIcon);
		btFirst.getUpHoveringFace().setImage(skipBackIconHover);

		btLast = new GPushButton(skipForwardIcon);
		btLast.getUpHoveringFace().setImage(skipForwardIconHover);

		btPrev = new GPushButton(rewindIcon);
		btPrev.getUpHoveringFace().setImage(rewindIconHover);

		btNext = new GPushButton(fastForwardIcon);
		btNext.getUpHoveringFace().setImage(fastForwardIconHover);
	
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

		btPlay = new GToggleButton();
		btPlay.getUpFace().setImage(playIcon);
		btPlay.getUpHoveringFace().setImage(playIconHover);
		btPlay.getDownFace().setImage(pauseIcon);
		btPlay.getDownHoveringFace().setImage(pauseIconHover);

		btPlay.addClickHandler(this);
	
		spDelay.addChangeHandler(event -> {
			try {
				playDelay = Double.parseDouble(spDelay.getValue());
			} catch (Exception ex) {
				playDelay = 2;
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

		btOpenWindow.addClickHandler(event -> toggleConstructionProtocol());
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
			btPlay.setDown(!isPlaying());
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
	}
	
	@Override
	public void setButtonPlay() {
		btPlay.setDown(false);
	}

	@Override
	public void setButtonPause() {
		btPlay.setDown(true);
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