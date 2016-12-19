package org.geogebra.desktop.util;

import java.io.BufferedReader;
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
 * @author Zoltan Kovacs <zoltan@geogebra.org> Implements HTTP requests and
 *         responses for desktop.
 */
public class HttpRequestD extends HttpRequest {
	private String answer;

	@Override
	public void sendRequest(String url) {
		BufferedReader in = null;
		try {
			URL u = new URL(url);
			HttpURLConnection huc = (HttpURLConnection) u.openConnection();
			huc.setConnectTimeout(getTimeout() * 1000);
			huc.setRequestMethod("GET");
			huc.connect();
			in = new BufferedReader(new InputStreamReader(huc.getInputStream(),
					Charsets.UTF_8));
			String s = "";
			answer = in.readLine(); // the last line will never get a "\n" on
									// its end
			while ((s = in.readLine()) != null) {
				if (!("".equals(answer))) // if the answer starts with "\n"s, we
											// ignore them
					answer += "\n";
				answer += s;
			}
		} catch (Exception ex) {
			processed = true;
			Log.error(ex.getMessage());
		} finally {
			try {
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		setResponseText(answer);
		processed = true;
	}

	@Override
	public void sendRequestPost(final String url, final String post,
			final AjaxCallback callback) {
		if (callback == null) {
			sendRequestPostSync(url, post, null);
		}

		else {
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

				@Override
				protected Void doInBackground() throws Exception {
					sendRequestPostSync(url, post, callback);
					return null;
				}
			};
			worker.execute();
		}

	}

	private void sendRequestPostSync(String url, String post,
			AjaxCallback callback) {
		try {
			URL u = new URL(url);
			// Borrowed from http://www.exampledepot.com/egs/java.net/post.html:
			HttpURLConnection huc = (HttpURLConnection) u.openConnection();
			// Borrowed from
			// http://bytes.com/topic/java/answers/720825-how-build-http-post-request-java:
			// uc.setRequestMethod("POST");
			// uc.setRequestProperty("Content-Type",
			// "application/x-www-form-urlencoded");
			// uc.setUseCaches(false);
			// uc.setDoInput(true);
			huc.setDoOutput(true);
			OutputStreamWriter osw = new OutputStreamWriter(
					huc.getOutputStream(), Charsets.UTF_8);
			osw.write(post);
			osw.flush();

			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(
						huc.getInputStream(), Charsets.UTF_8));
				String s = "";
				answer = in.readLine(); // the last line will never get a "\n"
										// on
				// its end
				while ((s = in.readLine()) != null) {
					if (!("".equals(answer))) // if the answer starts with
												// "\n"s, we
						// ignore them
						answer += "\n";
					answer += s;
				}
			} finally {
				if (in != null) {
					in.close();
				}
			}
			osw.close();

			setResponseText(answer);
			processed = true;
			if (callback != null) {
				callback.onSuccess(getResponse());
			}
		} catch (Exception ex) {
			processed = true;
			if (callback != null) {
				callback.onError("Connection error: " + ex.getMessage());
			}
			Log.error(ex.getMessage());
		}
	}

	/**
	 * @param url
	 *            HTTP request URL
	 * @return response This method only works in desktop, since this is
	 *         synchronous.
	 */
	public String sendRequestGetResponseSync(String url) {
		sendRequest(url);
		return getResponse();
	}

	/**
	 * http://stackoverflow.com/questions/1201048/allowing-java-to-use-an-
	 * untrusted-certificate-for-ssl-https-connection
	 */
	public static void ignoreSSL() {
		TrustManager[] trustAllCerts = new TrustManager[] {
				new X509TrustManager() {
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}

					public void checkClientTrusted(
							java.security.cert.X509Certificate[] certs,
							String authType) {
						// accept all
					}

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
