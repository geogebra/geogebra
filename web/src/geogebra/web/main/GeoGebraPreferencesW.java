package geogebra.web.main;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestBuilder.Method;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

import geogebra.common.main.App;
import geogebra.common.main.GeoGebraPreferences;
import geogebra.web.css.GuiResources;

public class GeoGebraPreferencesW implements GeoGebraPreferences{

	private static GeoGebraPreferencesW singleton;
	
	public static GeoGebraPreferencesW getPref() {
	    if (singleton == null) {
	    	singleton = new GeoGebraPreferencesW();
	    }
	    return singleton;
    }

	public void clearPreferences() {
	    // TODO Auto-generated method stub
		App.debug("unimplemented method");
	    
    }

	public void loadXMLPreferences(final App app) {

//		try {
//        new RequestBuilder(RequestBuilder.GET, "").sendRequest("", new RequestCallback() {
//
//			public void onResponseReceived(Request request,
//                    Response response) {
//				app.setXML(response.getText(), false);
//                
//            }
//
//			public void onError(Request request, Throwable exception) {
//                // TODO Auto-generated method stub
//                
//            }
//
//        	});
//    } catch (RequestException e) {
//        // TODO Auto-generated catch block
//        e.printStackTrace();
//    }
		
		String xml = GuiResources.INSTANCE.default_preferences().getText();
		app.setXML(xml, false);
	}
}
