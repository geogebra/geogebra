package geogebra.web.gui.browser;

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
		if($wnd.android){
			$wnd.android.login();
		}else{
			@geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("External login not possible");
		}
	}-*/;

}
