package geogebra.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import geogebra.common.util.AbstractHttpRequest;

public class HttpRequest extends AbstractHttpRequest {
	String answer;
	
    public String getResponse(String url) {
    	try {
    		URL u = new URL(url);
        	BufferedReader in = new BufferedReader(new InputStreamReader(u.openStream()));
        	String s = "";
        	answer = in.readLine(); // the last line will never get a "\n" on its end
            while ((s = in.readLine()) != null) {
                answer += "\n" + s;
            }
    	}
    	catch (Exception ex) {
    		System.err.println(ex);
    	}
		
	    return answer;
    }

}
