package org.geogebra.common.move.ggtapi.operations;

import org.geogebra.common.main.App;
import org.geogebra.common.move.operations.BaseOperation;
import org.geogebra.common.move.views.BaseEventView;
import org.geogebra.common.move.views.EventRenderable;

/**
 * @author gabor
 * 
 * Common things for Opening from GGT
 *
 */
public class OpenFromGGTOperation extends BaseOperation<EventRenderable>{

	private static String GGT_URL = "http://tube.geogebra.org/widgetprovider/index/widgettype/";
	/**
	 * @author gabor
	 * App type concerning from where this class called
	 */
	public static final class APP_TYPE {
		/**
		 * Web
		 */
		public final static String WEB = "web";
		/**
		 *	Desktop 
		 */
		public final static String DESKTOP = "desktop";
	}
	/**
	 * Application App
	 */
	protected App app;
	
	/**
	 * @param app
	 * Creates a new Opening From GGT operation
	 */
	public OpenFromGGTOperation(App app) {
		this.app = app;
		setView(new BaseEventView());
	}
	
	/**
	 * @return generates url for opening from GGT
	 */
	public String generateOpenFromGGTURL(String type) {
		String url = OpenFromGGTOperation.GGT_URL + type;
        
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
