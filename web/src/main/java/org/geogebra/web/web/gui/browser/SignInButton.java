package org.geogebra.web.web.gui.browser;

import org.geogebra.common.main.App;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.web.web.gui.util.WindowReference;
import org.geogebra.web.web.move.ggtapi.operations.BASEURL;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;

public class SignInButton extends Button implements EventRenderable{
	protected final App app;
	protected Timer loginChecker;

	public SignInButton(final App app, final int delay, String callbackURL) {
		super(app.getLocalization().getMenu("SignIn"));
		this.callbackURL = callbackURL;
		this.app = app;
		this.addStyleName("signInButton");
		app.getLoginOperation().getView().add(this);
		
		this.addClickHandler(new ClickHandler(){
			@Override
            public void onClick(ClickEvent event) {
				SignInButton.this.login();
				if(delay > 0){
				loginChecker = new Timer(){
					private String oldCookie = null;
					@Override
		            public void run() {
						String cookie = Cookies.getCookie("SSID");
						if(cookie != null && !cookie.equals(oldCookie)){
				    		app.getLoginOperation().performCookieLogin(cookie);
				    		this.oldCookie = cookie;
						}
		            }};
		        loginChecker.scheduleRepeating(delay);
				}
            }});
	}

	private WindowReference signInDialog = null;
	private String callbackURL;

	/**
	 * Show login dialog
	 */
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
	    if(event instanceof LoginEvent && this.loginChecker != null){
	    	this.loginChecker.cancel();
	    }
	    
    }
}
