package org.geogebra.web.shared;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LogOutEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;

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
	 * switch right buttons with exam timer and info button
	 */
	public void addExamTimer() {
		// remove other buttons
		GlobalHeader.INSTANCE.getButtonPanel().getElement().getStyle()
				.setDisplay(Display.NONE);
		// exam panel with timer and info btn
		examPanel = new FlowPanel();
		examPanel.setStyleName("examPanel");
		timer = new Label("00:00");
		timer.setStyleName("examTimer");
		examInfoBtn = new StandardButton(
				MaterialDesignResources.INSTANCE.info_black(), null, 24, app);
		examInfoBtn.addStyleName("flatButtonHeader");
		examInfoBtn.addStyleName("examInfoBtn");
		examPanel.add(timer);
		examPanel.add(examInfoBtn);
		// add exam panel to
		DivElement exam = DOM.createDiv().cast();
		exam.setId("examId");
		getButtonPanel().getElement().getParentElement().appendChild(exam);
		RootPanel.get("examId").add(examPanel);
	}
}
