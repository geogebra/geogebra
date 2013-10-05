package geogebra.html5.move.ggtapi.operations;

import geogebra.common.GeoGebraConstants;
import geogebra.common.move.ggtapi.models.GeoGebraTubeAPI;
import geogebra.common.move.ggtapi.operations.LogInOperation;
import geogebra.common.move.views.BaseEventView;
import geogebra.html5.move.ggtapi.models.AuthenticationModelW;
import geogebra.html5.move.ggtapi.models.GeoGebraTubeAPIW;
import geogebra.web.util.URLEncoder;


/**
 * The web version of the login operation.
 * uses an own AuthenticationModel and an own implementation of the API
 * 
 * @author stefan
 */
public class LoginOperationW extends LogInOperation {
	
	private static final class BASEURL {
		public static String opener = "web/html/opener.html";
		public static String callbackHTML = "web/html/ggtcallback.html";
		public static String WEB_GUI = "http://geogebra.org/web/test50/";
		public static String LOCALHOST = "http://127.0.0.1:8888/";
		public static String APPSPOT = "http://geogebraweb.appspot.com/";
	}

	/**
	 * Initializes the SignInOperation for Web by creating the corresponding model and view classes
	 */
	public LoginOperationW() {
		super();
		
		setView(new BaseEventView());
		setModel(new AuthenticationModelW());
		
		iniNativeEvents();
	}
	
	private native void iniNativeEvents() /*-{
		var t = this;
	    $wnd.addEventListener("message",function(event) {
	    	var data;
	    	//later if event.origin....
	    	if (event.data) {
	    		data = $wnd.JSON.parse(event.data);
	    		if (data.action === "logintoken") {
	    			t.@geogebra.html5.move.ggtapi.operations.LoginOperationW::processToken(Ljava/lang/String;)(data.msg);
	    		}
	    	}
	    }, false);
    }-*/;

	@Override
	public GeoGebraTubeAPI getGeoGebraTubeAPI() {
		return GeoGebraTubeAPIW.getInstance(GeoGebraTubeAPI.url);

	}
	
	@Override
	protected String getURLLoginCaller() {
		return "web";
	}

	@Override
	protected String getURLClientInfo() {
		URLEncoder enc = new URLEncoder();
		return enc.encode("GeoGebra Web Application V" + GeoGebraConstants.VERSION_STRING);
	}
	
	/**
	 * @return change this concerning what environment the project runs.
	 */
	public String getCallbackUrl() {
		
		return getActiveUrl() + BASEURL.callbackHTML;
	}
	
	/**
	 * @return the url that will redirect the window to GGT login
	 */
	public String getOpenerUrl() {
		return getActiveUrl() + BASEURL.opener;
	}
	
	
	//AG: JUST FOR TESTING!
	/*@Override
    public String getLoginURL(String languageCode) {
		return "http://test.geogebratube.org:8080/user/login" 
				+ "/caller/"+getURLLoginCaller()
				+"/expiration/"+getURLTokenExpirationMinutes()
				+"/clientinfo/"+getURLClientInfo()
				+"/?lang="+languageCode;
	}*/
	
	private void processToken(String token) {
		performTokenLogin(token);
	}
	
	private static String getActiveUrl() {
		return BASEURL.WEB_GUI;
	}
}
