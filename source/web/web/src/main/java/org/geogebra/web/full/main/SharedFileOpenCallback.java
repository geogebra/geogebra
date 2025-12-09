/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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