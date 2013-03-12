package geogebra.web.helper;

import geogebra.web.gui.app.GGWMenuBar;

public class ObjectPool {
	
	private MyGoogleApis myGoogleApis;
	private GGWMenuBar ggwMenubar;
	private MySkyDriveApis mySkyDriveApis;
	
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

	public void setMySkyDriveApis(MySkyDriveApis mySkyDriveApis) {
	    this.mySkyDriveApis = mySkyDriveApis;
    }
	
	public MySkyDriveApis getMySkyDriveApis() {
		return this.mySkyDriveApis;
	}
	
	

}
