package geogebra.factories;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class UtilFactory extends geogebra.common.factories.UtilFactory {

	String answer;
	
	@Override
	public String newHttpRequestResponse(String url) {
		
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

