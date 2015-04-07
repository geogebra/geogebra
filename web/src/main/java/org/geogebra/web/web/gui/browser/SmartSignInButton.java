package org.geogebra.web.web.gui.browser;

import org.geogebra.common.main.App;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window.Location;

public class SmartSignInButton extends SignInButton{
	
	public SmartSignInButton(App app) {
	    super(app, 0);
    }

	public void login(){
		String url =  "https://accounts.geogebra.org/user/signin" 
					+ "/caller/web/"
					+"/expiration/600/"
					+"/clientinfo/smart"
					+"/?lang="+app.getLocalization().getLocaleStr()
					+"&url="+URL.encode(Location.getHref());
		
		gotoURL(url);
	}
	
	private native void gotoURL(String s)/*-{
		$wnd.location.replace(s);
	}-*/;

}
