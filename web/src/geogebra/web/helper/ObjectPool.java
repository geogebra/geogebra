package geogebra.web.helper;

import geogebra.web.gui.app.GGWMenuBar;

public class ObjectPool {
	
	private MyGoogleApis myGoogleApis;
	private GGWMenuBar ggwMenubar;
	
	public ObjectPool() {
		
	}

	public MyGoogleApis getMyGoogleApis() {
	    return myGoogleApis;
    }

	public void setMyGoogleApis(MyGoogleApis myGoogleApis) {
	    this.myGoogleApis = myGoogleApis;
    }

	public GGWMenuBar getGgwMenubar() {
	    return ggwMenubar;
    }

	public void setGgwMenubar(GGWMenuBar ggwMenubar) {
	    this.ggwMenubar = ggwMenubar;
    }
	
	

}
