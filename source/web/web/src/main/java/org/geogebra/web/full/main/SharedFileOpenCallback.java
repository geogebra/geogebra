package org.geogebra.web.full.main;

import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.util.AsyncOperation;

/**
 * Callback for shared files that require login
 */
final class SharedFileOpenCallback
		implements EventRenderable {
	private final AsyncOperation<String> onError;
	private AppWFull app;

	/**
	 * @param app
	 *            application
	 * @param onError
	 *            error handler
	 */
	public SharedFileOpenCallback(AppWFull app,
			AsyncOperation<String> onError) {
		this.onError = onError;
		this.app = app;
	}

	@Override
	public void renderEvent(BaseEvent event) {
		if (event instanceof LoginEvent) {
			LoginEvent loginEvent = (LoginEvent) event;
			if (!loginEvent.isAutomatic()
					|| loginEvent.isSuccessful()) {
				app.checkOpen(onError, this);
			}
		}
	}
}