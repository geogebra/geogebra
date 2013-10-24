package geogebra.common.move.ggtapi.operations;

import geogebra.common.main.App;

/**
 * @author gabor
 * 
 * Common things for Opening from GGT
 *
 */
public class OpenFromGGTOperation {

	public static String GGT_URL = "http://www.geogebratube.org/widgetprovider/index/widgettype/desktop";
	private App app;
	
	/**
	 * @param app
	 * Creates a new Opening From GGT operation
	 */
	public OpenFromGGTOperation(App app) {
		this.app = app;
	}
	
	/**
	 * @return generates url for opening from GGT
	 */
	public String generateOpenFromGGTURL() {
		String url = OpenFromGGTOperation.GGT_URL;
        
		// Add the login token to the URL if a user is logged in
		if (app.getLoginOperation().isLoggedIn()) {
			
			String token = app.getLoginOperation().getModel().getLoggedInUser().getLoginToken();
			if (token != null) {
				url += "/lt/" + token;
			}
		} else {
			url += "/lt/nouser";
		}
		
		// Add the application language parameter to the URL
		url += "/?lang=" + app.getLocalization().getLanguage();
		return url;
	}

}
