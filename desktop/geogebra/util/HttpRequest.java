package geogebra.util;
	
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 * Implements HTTP requests and responses for desktop.
 */
public class HttpRequest extends geogebra.common.util.HttpRequest {
	private String answer;

	@Override
	public void sendRequest(String url) {
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
			success = false;
			processed = true;
			System.err.println(ex);
		}
		responseText = answer;
		success = true;
		processed = true;
	}

	@Override
	public String sendRequestGetResponseSync(String url) {
		sendRequest(url);
		return getResponse();
	}
}
