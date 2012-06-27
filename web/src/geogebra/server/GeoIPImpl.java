package geogebra.server;

import geogebra.web.asyncservices.GeoIPService;
import geogebra.web.main.Application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class GeoIPImpl extends RemoteServiceServlet implements GeoIPService {
	
	HttpServletRequest aReq;
	HttpServletResponse aRes;
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		aReq = req;
		aRes = resp;
		resp.setContentType("text/plain");
	}
	
	public String getCountry() {
		
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
	
	public String getLanguage() {
//		String acceptCountry = req.getHeader("X-AppEngine-Country");
//		String acceptCharset = req.getHeader("Accept-Charset");
		String str = aReq.getHeader("Accept-Language");
		return str.substring(0, str.indexOf(","));
		
	}

}
