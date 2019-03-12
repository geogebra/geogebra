package org.geogebra.desktop.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.SwingWorker;

import org.geogebra.common.move.ggtapi.models.AjaxCallback;
import org.geogebra.common.util.Charsets;
import org.geogebra.common.util.HttpRequest;
import org.geogebra.common.util.debug.Log;

/**
 * Implements HTTP requests and responses for desktop.
 *  
 * @author Zoltan Kovacs 
 */
public class HttpRequestD extends HttpRequest {
	private String answer;

	@Override
	public void sendRequestPost(final String method, final String url,
			final String post, final AjaxCallback callback) {
		if (callback == null) {
			sendRequestPostSync(method, url, post, null);
		}

		else {
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

				@Override
				protected Void doInBackground() throws Exception {
					sendRequestPostSync(method, url, post, callback);
					return null;
				}
			};
			worker.execute();
		}

	}

	/**
	 * @param url
	 *            url
	 * @param post
	 *            post data
	 * @param callback
	 *            callback
	 */
	void sendRequestPostSync(String method, String url, String post,
			AjaxCallback callback) {
		HttpURLConnection huc = null;
		try {
			URL u = new URL(url);
			// Borrowed from http://www.exampledepot.com/egs/java.net/post.html:
			huc = (HttpURLConnection) u.openConnection();
			// Borrowed from
			// http://bytes.com/topic/java/answers/720825-how-build-http-post-request-java:
			huc.setRequestMethod(method);
			if (getAuth() != null) {
				huc.setRequestProperty("Authorization", "Basic " + getAuth());
			}
			if (!"text/plain".equals(this.getType())) {
				huc.setRequestProperty("Content-Type", getType());
			}

			// uc.setUseCaches(false);
			// uc.setDoInput(true);
			if (post != null) {
				huc.setDoOutput(true);
				OutputStreamWriter osw = new OutputStreamWriter(
						huc.getOutputStream(), Charsets.getUtf8());

				osw.write(post);
				osw.flush();
				answer = readOutput(huc.getInputStream());
				osw.close();
			} else {
				answer = readOutput(huc.getInputStream());
			}


			setResponseText(answer);
			processed = true;
			if (callback != null) {
				callback.onSuccess(getResponse());
			}
		} catch (Exception ex) {
			processed = true;
			String err = "(No error)";
			try {
				if (huc != null && huc.getErrorStream() != null) {
					err = readOutput(huc.getErrorStream());
				}
			} catch (IOException e2) {
				Log.warn("invalid HTTP stream");
			}
			if (callback != null) {
				Log.error(err);
				callback.onError(
						"Connection error: " + ex.getMessage());

			}
			ex.printStackTrace();
			Log.error(ex.getMessage());
		}
	}

	public static String readOutput(InputStream inputStream)
			throws IOException {
		BufferedReader in = null;
		StringBuilder ans;
		try {
			in = new BufferedReader(
					new InputStreamReader(inputStream,
							Charsets.getUtf8()));
			String s = in.readLine();
			if (s == null) {
				return null;
			}
			ans = new StringBuilder(s); // the last line will
															// never
													// get a "\n"
									// on
			// its end
			while ((s = in.readLine()) != null) {
				if (!("".equals(ans.toString()))) {
					// "\n"s, we
					// ignore them
					ans.append("\n");
				}
				ans.append(s);
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}
		return ans.toString();
	}

	/**
	 * @param url
	 *            HTTP request URL
	 * @return response This method only works in desktop, since this is
	 *         synchronous.
	 */
	public String sendRequestGetResponseSync(String url) {
		sendRequestPost("GET", url, null, null);
		return getResponse();
	}

	/**
	 * http://stackoverflow.com/questions/1201048/allowing-java-to-use-an-
	 * untrusted-certificate-for-ssl-https-connection
	 */
	public static void ignoreSSL() {
		Log.error("****************************** ignoring SSL certificate");
		TrustManager[] trustAllCerts = new TrustManager[] {
				new X509TrustManager() {
					@Override
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}

					@Override
					public void checkClientTrusted(
							java.security.cert.X509Certificate[] certs,
							String authType) {
						// accept all
					}

					@Override
					public void checkServerTrusted(
							java.security.cert.X509Certificate[] certs,
							String authType) {
						// truest everyyone
					}
				} };

		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			// and ignore exceptions
		}

	}
}
