package org.geogebra.web.shared;

import org.geogebra.common.main.App;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.web.html5.gui.laf.SignInControllerI;
import org.geogebra.web.html5.gui.util.Cookies;
import org.geogebra.web.shared.ggtapi.BASEURL;
import org.gwtproject.timer.client.Timer;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

/**
 * Default sign in button: opens GGB signin in a popup.
 *
 */
public class SignInController implements EventRenderable, SignInControllerI {
	/** application */
	protected final App app;
	/**
	 * Interval for checking login cookie (when popup comunication not possible)
	 */
	protected Timer loginChecker;

	private WindowReference signInDialog = null;
	private String callbackURL;
	private int delay;

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
					callbackURL == null ? BASEURL.getCallbackUrl()
							: callbackURL);
		} else {
			signInDialog.close();
			signInDialog = null;
		}
	}

	@Override
	public void renderEvent(BaseEvent event) {
		if (event instanceof LoginEvent && this.loginChecker != null) {
			this.loginChecker.cancel();
		}
	}

	/**
	 * @return sign in button
	 */
	public Button getButton() {
		Button button = new Button(app.getLocalization().getMenu("SignIn"));
		button.addStyleName("signInButton");
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SignInController.this.login();
				initLoginTimer();
			}
		});
		return button;
	}

	/**
	 * Actively listen for cookie change
	 */
	protected void initLoginTimer() {
		if (delay > 0) {
			loginChecker = new Timer() {
				private String oldCookie = null;

				@Override
				public void run() {
					String cookie = Cookies.getCookie("SSID");
					if (cookie != null && !cookie.equals(oldCookie)) {
						app.getLoginOperation().getGeoGebraTubeAPI()
								.performCookieLogin(app.getLoginOperation());
						this.oldCookie = cookie;
					}
				}
			};
			loginChecker.scheduleRepeating(delay);
		}
	}

	@Override
	public void loginFromApp() {
		// needs to open iframe or redirect page: not supported by default
	}
}
