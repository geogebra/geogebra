package geogebra.server;

import geogebra.web.asyncservices.GeoIPService;
import geogebra.web.gui.app.GeoIPInformation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author Rana
 *
 */
public class GeoIPImpl extends RemoteServiceServlet implements GeoIPService {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;
	private GeoIPInformation geoIPInfo;

	public GeoIPInformation getGeoIPInformation() {
		geoIPInfo = new GeoIPInformation();
		geoIPInfo.setCountry(getCountry());
		geoIPInfo.setLanguage(getLanguage());
		return geoIPInfo;
	}

	private static String getCountry() {

		String country = "";
		try {
			URL url = new URL("http://www.geogebra.org/geoip/");
			//		URL url = new URL("http://www.geogebra.org/geoip/?ip=195.199.144.225");
			URLConnection uc = url.openConnection();
			uc.setReadTimeout(4000);
			BufferedReader in;
			in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			country = in.readLine();
		} catch(Exception e) {

		}
		return country;
	}

	private String getLanguage() {
		//		String acceptCountry = req.getHeader("X-AppEngine-Country");
		//		String acceptCharset = req.getHeader("Accept-Charset");
		String str = getThreadLocalRequest().getHeader("Accept-Language");

		return str.substring(0, str.indexOf(","));

	}

}
