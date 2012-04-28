package geogebra.util;
	
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

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
	public void sendRequestPost(String url, String post) {
		try {
			URL u = new URL(url);
			// Borrowed from http://www.exampledepot.com/egs/java.net/post.html:
			HttpURLConnection uc = (HttpURLConnection) u.openConnection();
			// Borrowed from http://bytes.com/topic/java/answers/720825-how-build-http-post-request-java:
			// uc.setRequestMethod("POST");
			// uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			// uc.setUseCaches(false);
			// uc.setDoInput(true);
			uc.setDoOutput(true);
			OutputStreamWriter osw = new OutputStreamWriter(uc.getOutputStream());
		    osw.write(post);
		    osw.flush();
			BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			String s = "";
			answer = in.readLine(); // the last line will never get a "\n" on its end
			while ((s = in.readLine()) != null) {
				if (!("".equals(answer))) // if the answer starts with "\n"s, we ignore them
					answer += "\n";
				answer += s;
			}
		    osw.close();
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
