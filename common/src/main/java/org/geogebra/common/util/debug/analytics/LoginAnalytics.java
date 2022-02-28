package org.geogebra.common.util.debug.analytics;

import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.util.debug.Analytics;

public class LoginAnalytics implements EventRenderable {

	@Override
	public void renderEvent(BaseEvent event) {
		if (event instanceof LoginEvent) {
			handleLoginEvent((LoginEvent) event);
		}
	}

	private void handleLoginEvent(LoginEvent loginEvent) {
		if (!loginEvent.isAutomatic() && loginEvent.isSuccessful()) {
			Analytics.logEvent(Analytics.Event.LOGIN);
		}
	}
}
