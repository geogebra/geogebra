package geogebra.util;
	
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 * Implements HTTP requests and responses for desktop.
 */
public class HttpRequestD extends geogebra.common.util.HttpRequest {
	private String answer;

	@Override
	public void sendRequest(String url) {
		try {
			URL u = new URL(url);
			HttpURLConnection huc = (HttpURLConnection) u.openConnection();
			huc.setConnectTimeout(timeout * 1000);
			huc.setRequestMethod("GET");
			huc.connect();
			BufferedReader in;
			in = new BufferedReader(new InputStreamReader(huc.getInputStream()));
			String s = "";
			answer = in.readLine(); // the last line will never get a "\n" on its end
			while ((s = in.readLine()) != null) {
				if (!("".equals(answer))) // if the answer starts with "\n"s, we ignore them
					answer += "\n";
				answer += s;
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
			HttpURLConnection huc = (HttpURLConnection) u.openConnection();
			// Borrowed from http://bytes.com/topic/java/answers/720825-how-build-http-post-request-java:
			// uc.setRequestMethod("POST");
			// uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			// uc.setUseCaches(false);
			// uc.setDoInput(true);
			huc.setDoOutput(true);
			OutputStreamWriter osw = new OutputStreamWriter(huc.getOutputStream());
		    osw.write(post);
		    osw.flush();
			BufferedReader in = new BufferedReader(new InputStreamReader(huc.getInputStream()));
			String s = "";
			answer = in.readLine(); // the last line will never get a "\n" on its end
			while ((s = in.readLine()) != null) {
				if (!("".equals(answer))) // if the answer starts with "\n"s, we ignore them
					answer += "\n";
				answer += s;
			}
		    osw.close();

		    // Convert the answer string to UTF-8
			responseText = new String(answer.getBytes(), "UTF-8");
			success = true;
			processed = true;
		}
		catch (Exception ex) {
			success = false;
			processed = true;
			System.err.println(ex);
		}
	}

	
	@Override
	public String sendRequestGetResponseSync(String url) {
		sendRequest(url);
		return getResponse();
	}
}
