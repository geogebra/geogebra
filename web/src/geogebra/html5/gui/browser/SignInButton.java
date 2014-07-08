package geogebra.html5.gui.browser;

import geogebra.common.main.App;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;

public class SignInButton extends Button{
	protected final App app;

	public SignInButton(final App app, final int delay){
		super(app.getLocalization().getMenu("SignIn"));
		this.app = app;
		this.addStyleName("signInButton");
		this.addClickHandler(new ClickHandler(){
			@Override
            public void onClick(ClickEvent event) {
				SignInButton.this.login();
				if(delay > 0){
				Timer loginChecker = new Timer(){
					private String oldCookie = null;
					@Override
		            public void run() {
						String cookie = Cookies.getCookie("SSID");
						if(cookie != null && !cookie.equals(oldCookie)){
				    		if(app.getLoginOperation().performCookieLogin(cookie)){
				    			cancel();
				    		}
				    		this.oldCookie = cookie;
						}
		            }};
		        loginChecker.scheduleRepeating(delay);
				}
            }});
	}

	protected void login() {
		app.getDialogManager().showLogInDialog();
	    
    }
}
