package org.geogebra.web.shared;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LogOutEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;

public class GlobalHeader {
	private static ProfilePanel profilePanel;
	public static void addSignIn(final AppW app) {
		final RootPanel signIn = RootPanel.get("signInButton");
		if (signIn == null) {
			return;
		}
		ClickStartHandler.init(signIn, new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				new SignInButton(app, 0, null).login();
			}
		});
		app.getLoginOperation().getView().add(new EventRenderable() {

			public void renderEvent(BaseEvent event) {
				if (event instanceof LoginEvent) {
					signIn.setVisible(false);
					try {
						if (profilePanel == null) {
						profilePanel = new ProfilePanel(app);
					}
						profilePanel.setVisible(true);
					profilePanel.update(((LoginEvent) event).getUser());
					DivElement profile = DOM.createDiv().cast();
					profile.setId("profileId");
					signIn.getElement().getParentElement()
							.appendChild(profile);

					RootPanel.get("profileId").add(profilePanel);
					} catch (Throwable t) {
						Log.debug(t);
					}
				}
				if (event instanceof LogOutEvent) {
					profilePanel.setVisible(false);
					signIn.setVisible(true);
				}
			}
		});
	}
}
