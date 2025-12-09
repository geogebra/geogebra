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

package org.geogebra.web.shared;

import org.geogebra.common.main.App;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.gwtutil.Cookies;
import org.geogebra.web.html5.gui.laf.SignInControllerI;
import org.geogebra.web.shared.ggtapi.StaticFileUrls;
import org.gwtproject.timer.client.Timer;

/**
 * Default sign in controller: opens GGB sign in page in a popup, processes the login token after.
 */
public class SignInController implements EventRenderable, SignInControllerI {
	/** application */
	protected final App app;
	/**
	 * Interval for checking login cookie (when popup communication not possible)
	 */
	protected Timer loginChecker;

	private WindowReference signInDialog = null;
	private final String callbackURL;
	private final int delay;

	/**
	 * @param app
	 *            application
	 * @param delay
	 *            regular check delay
	 * @param callbackURL
	 *            callback URL
	 */
	public SignInController(final App app, final int delay, String callbackURL) {
		this.callbackURL = callbackURL;
		this.app = app;
		this.delay = delay;
		app.getLoginOperation().getView().add(this);
	}

	@Override
	public void login() {
		if (signInDialog == null || signInDialog.closed()) {
			signInDialog = WindowReference.createSignInWindow(app,
					callbackURL == null ? StaticFileUrls.getCallbackUrl()
							: callbackURL);
		} else {
			signInDialog.close();
			signInDialog = null;
		}
	}

	@Override
	public void renderEvent(BaseEvent event) {
		if (event instanceof LoginEvent && loginChecker != null) {
			loginChecker.cancel();
		}
	}

	@Override
	public void initLoginTimer() {
		if (delay > 0) {
			loginChecker = new Timer() {
				private String oldCookie = null;

				@Override
				public void run() {
					String cookie = Cookies.getCookie("SSID");
					if (cookie != null && !cookie.equals(oldCookie)) {
						app.getLoginOperation().passiveLogin();
						oldCookie = cookie;
					}
				}
			};
			loginChecker.scheduleRepeating(delay);
		}
	}
}
