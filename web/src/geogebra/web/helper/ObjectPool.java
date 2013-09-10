package geogebra.web.helper;

import geogebra.web.gui.app.GGWMenuBar;
import geogebra.web.main.MyGoogleApisFactory;

public class ObjectPool {
	
	private MyGoogleApisFactory googleApisFactory;
	private GGWMenuBar ggwMenubar;
	private MySkyDriveApis mySkyDriveApis;
	
	public ObjectPool() {
		
	}

	public MyGoogleApis getMyGoogleApis() {
	    return googleApisFactory.getAPI();
    }

	public void setMyGoogleApis(MyGoogleApisFactory myGoogleApis) {
	    this.googleApisFactory = myGoogleApis;
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
