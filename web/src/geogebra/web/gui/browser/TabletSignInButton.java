package geogebra.web.gui.browser;

import geogebra.common.main.App;
import geogebra.html5.main.AppW;

public class TabletSignInButton extends SignInButton{
	
	public TabletSignInButton(App app) {
	    super(app, 0);
    }

	@Override
    public void login(){
		loginNative(((AppW) app).getLocalization().getLocaleStr());
	}
	
	private native void loginNative(String locale)/*-{
		if($wnd.android){
			$wnd.android.login(locale);
		}else{
			@geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("External login not possible");
		}
	}-*/;

}
