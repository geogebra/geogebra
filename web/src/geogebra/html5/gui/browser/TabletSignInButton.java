package geogebra.html5.gui.browser;

import geogebra.common.main.App;

public class TabletSignInButton extends SignInButton{
	
	public TabletSignInButton(App app) {
	    super(app, 0);
    }

	@Override
    public void login(){
		loginNative();
	}
	
	private native void loginNative()/*-{
		$wnd.android.login();
	}-*/;

}
