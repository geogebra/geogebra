package org.geogebra.web.shared;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LogOutEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public class GlobalHeader implements EventRenderable {
	/**
	 * Singleton instance.
	 */
	public static final GlobalHeader INSTANCE = new GlobalHeader();

	private ProfilePanel profilePanel;
	private RootPanel signIn;
	private AppW app;
	private FlowPanel examPanel;
	private Label timer;
	private StandardButton examInfoBtn;

	/**
	 * Activate sign in button in external header
	 * 
	 * @param appW
	 *            application
	 */
	public void addSignIn(final AppW appW) {
		this.app = appW;
		signIn = RootPanel.get("signInButton");
		if (signIn == null) {
			return;
		}
		ClickStartHandler.init(signIn, new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				new SignInButton(appW, 0, null).login();
			}
		});
		app.getLoginOperation().getView().add(this);
	}

	@Override
	public void renderEvent(BaseEvent event) {
		if (event instanceof LoginEvent
				&& ((LoginEvent) event).isSuccessful()) {
			if (profilePanel == null) {
				profilePanel = new ProfilePanel(app);
			}
			signIn.setVisible(false);
			profilePanel.setVisible(true);
			profilePanel.update(((LoginEvent) event).getUser());
			DivElement profile = DOM.createDiv().cast();
			profile.setId("profileId");
			signIn.getElement().getParentElement().appendChild(profile);

			RootPanel.get("profileId").add(profilePanel);
		}
		if (event instanceof LogOutEvent) {
			profilePanel.setVisible(false);
			signIn.setVisible(true);
		}
	}

	/**
	 * @return share button
	 */
	public static RootPanel getShareButton() {
		return RootPanel.get("shareButton");
	}

	/**
	 * @return button
	 */
	public RootPanel getButtonPanel() {
		return RootPanel.get("buttonsID");
	}

	/**
	 * @return application
	 */
	public AppW getApp() {
		return app;
	}

	/**
	 * @return exam timer
	 */
	public Label getTimer() {
		return timer;
	}

	/**
	 * switch right buttons with exam timer and info button
	 */
	public void addExamTimer() {
		// remove other buttons
		GlobalHeader.INSTANCE.getButtonPanel().getElement().getStyle()
				.setDisplay(Display.NONE);
		// exam panel with timer and info btn
		examPanel = new FlowPanel();
		examPanel.setStyleName("examPanel");
		timer = new Label("0:00");
		timer.setStyleName("examTimer");
		examInfoBtn = new StandardButton(
				SharedResources.INSTANCE.info_black(), null, 24, app);
		examInfoBtn.addStyleName("flatButtonHeader");
		examInfoBtn.addStyleName("examInfoBtn");
		examPanel.add(timer);
		examPanel.add(examInfoBtn);
		// add exam panel to
		DivElement exam = DOM.createDiv().cast();
		exam.setId("examId");
		getButtonPanel().getElement().getParentElement().appendChild(exam);
		RootPanel.get("examId").add(examPanel);
		// run timer
		AnimationScheduler.get().requestAnimationFrame(new AnimationCallback() {
			@Override
			public void execute(double timestamp) {
				if (getApp().getExam() != null) {
					String os = Browser.getMobileOperatingSystem();
					getApp().getExam().checkCheating(os);
					if (getApp().getExam().isCheating()
							&& getApp().getGuiManager() instanceof GuiManagerW
							&& ((GuiManagerW) getApp().getGuiManager())
									.getUnbundledToolbar() != null) {
						((GuiManagerW) getApp().getGuiManager())
								.getUnbundledToolbar()
								.setHeaderStyle("examCheat");
					}

					getTimer().setText(getApp().getExam()
							.timeToString(System.currentTimeMillis()));

					AnimationScheduler.get().requestAnimationFrame(this);
				}
			}
		});
		visibilityEventMain(isTablet());
	}

	/**
	 * check and log window resize and focus lost/gained window resize is
	 * checked first - if window is not in full screen mode "cheating" can't be
	 * stopped (only going back to full screen ends "cheating") if window is in
	 * full screen losing focus starts "cheating", gaining focus stops
	 * "cheating"
	 */
	private native void visibilityEventMain(boolean tabletMode) /*-{
		// wrapper to call the appropriate function from visibility.js
		var that = this;

		// fix for firefox and iexplorer (e.g. fullscreen goes to 1079px instead of 1080px)
		//var screenHeight = screen.height - 5;

		//var focus;
		//$wnd.console.log("focus 1: " + focus);
		var fullscreen = true;
		//$wnd.console.log("fullscreen: " + fullscreen);
		if ($wnd.innerHeight < screen.height - 5
				|| $wnd.innerWidth < screen.width - 5) {
			fullscreen = false;
		}
		//var fullHeight = $wnd.innerHeight;
		//var fullWidth = $wnd.innerWidth;

		var startCheating = function() {
			that.@org.geogebra.web.shared.GlobalHeader::startCheating()()
		};
		var stopCheating = function() {
			that.@org.geogebra.web.shared.GlobalHeader::stopCheating()()
		};
		var isTablet = function() {
			return that.@org.geogebra.web.shared.GlobalHeader::isTablet()()
		};

		//	var examActive = function() {
		//	that.@org.geogebra.common.main.App::isExam()()
		//};
		//$wnd.console.log("examActive " + examActive);

		if (tabletMode) {
			$wnd.visibilityEventMain(startCheating, stopCheating);
		} else {

			$wnd.onblur = function(event) {
				// Borrowed from http://www.quirksmode.org/js/events_properties.html
				//$wnd.console.log("4");
				var e = event ? event : $wnd.event;
				var targ;
				if (e.target) {
					targ = e.target;
				} else if (e.srcElement) {
					targ = e.srcElement;
				}
				if (targ.nodeType == 3) { // defeat Safari bug
					targ = targ.parentNode;
				}
				//console.log("Checking cheating: Type = " + e.type
				//		+ ", Target = " + targ + ", " + targ.id
				//		+ " CurrentTarget = " + e.currentTarget + ", "
				//		+ e.currentTarget.id);
				// The focusout event should not be caught:
				if (e.type == "blur") { //&& fullscreen == true
					//$wnd.console.log("5");
					startCheating();
					//focus = false;
					//console.log("focus 2 " + focus);
				}

			};
			$wnd.onfocus = function(event) {
				//$wnd.console.log("6");
				if (fullscreen) {
					stopCheating();
					//	focus = true;
					//	console.log("focus 3 " + focus);
				}
			}
			// window resize has 2 cases: full screen and not full screen
			$wnd
					.addEventListener(
							"resize",
							function() {
								fullscreen = @org.geogebra.web.html5.Browser::isCoveringWholeScreen()();
								if (!fullscreen) {
									startCheating();
								} else {
									stopCheating();
								}
							});
		}
	}-*/ ;

	private void startCheating() {
		if (app.getExam() != null) {
			String os = Browser.getMobileOperatingSystem();
			app.getExam().startCheating(os);
		}
	}

	private void stopCheating() {
		if (app.getExam() != null) {
			app.getExam().stopCheating();
		}
	}

	private boolean isTablet() {
		return app.getLAF().isTablet()
				&& !"TabletWin".equals(app.getLAF().getFrameStyleName());
	}
}
