package org.geogebra.web.shared;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LogOutEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.RootPanel;

public class GlobalHeader {
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
				}
				if (event instanceof LogOutEvent) {
					signIn.setVisible(true);
				}
			}
		});
	}
}
