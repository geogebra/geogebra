package geogebra.web.main;

import geogebra.common.main.App;
import geogebra.web.helper.MyGoogleApis;

public class MyGoogleApisFactory {

	private App app;

	public MyGoogleApisFactory(App app) {
	    this.app = app;
    }
	
	public MyGoogleApis getAPI(){
		return new MyGoogleApis(app);
	}

}
