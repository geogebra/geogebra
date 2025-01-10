package org.geogebra.desktop.export;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.PreviewFeature;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.gui.dialog.Dialog;
import org.geogebra.desktop.main.AppD;

/**
 * Export GeoGebra worksheet to GeoGebraTube.
 * 
 * @author Florian Sonner
 */
public class GeoGebraTubeExportD {

	/**
	 * Progress bar dialog.
	 */
	private JDialog progressDialog;

	/**
	 * Progress bar.
	 */
	private JProgressBar progressBar;

	/**
	 * Status label.
	 */
	private JLabel statusLabel;

	/**
	 * Abort button.
	 */
	private JButton abortButton;

	/**
	 * Application instance.
	 */
	public App app;
	private Localization loc;

	/**
	 * Storage container for uploading results.
	 *
	 * @author Florian Sonner
	 */
	public static class UploadResults {
		private String status;
		private String uid;
		private String errorMessage;

		/**
		 * Parse upload result string.
		 *
		 * @param string0
		 *            server response
		 */
		public UploadResults(String string0) {
			String string = string0;
			status = uid = errorMessage = "";
			if (string.indexOf("status:") > 0) {
				string = string.substring(string.indexOf("status:"));
			}
			for (String line : string.split(",")) {
				int delimiterPos = line.indexOf(':');
				String key = line.substring(0, delimiterPos).toLowerCase();
				String value = line.substring(delimiterPos + 1).toLowerCase();

				if ("status".equals(key)) {
					status = value;
				} else if ("uid".equals(key)) {
					uid = value;
				} else if ("error".equals(key)) {
					errorMessage = value;
				}
			}
		}

		public boolean hasError() {
			return !"ok".equals(status);
		}

		public String getStatus() {
			return status;
		}

		public String getUID() {
			return uid;
		}

		public String getErrorMessage() {
			return errorMessage;
		}
	}

	/**
	 * @param app application
	 */
	public GeoGebraTubeExportD(App app) {
		this.app = app;
		this.setLoc(app.getLocalization());
	}

	/**
	 * Upload the current worksheet to geogebra.org.
	 */
	public void uploadWorksheet() {
		showDialog();

		try {
			URL url;
			HttpURLConnection urlConn;
			DataOutputStream printout;
			BufferedReader input;

			progressBar.setIndeterminate(true);

			url = new URL(getUploadURL());

			urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setUseCaches(false);

			// content type
			urlConn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			urlConn.setRequestProperty("Accept-Language",
					app.getLocalization().getLanguageTag());

			// send output
			try {
				printout = new DataOutputStream(urlConn.getOutputStream());

				StringBuilder postData = getPostData();

				int requestLength = postData.length();
				/*
				 * urlConn.disconnect(); urlConn.setChunkedStreamingMode(1000);
				 * urlConn.connect();
				 */

				progressBar.setIndeterminate(false);
				progressBar.setMinimum(0);
				progressBar.setMaximum(requestLength);

				// send data in chunks
				int start = 0;
				int end = 0;

				// chunking is senseless at the moment as input buffering is
				// activated
				while (end != requestLength) {
					start = end;
					end += 5000;

					if (end > requestLength) {
						end = requestLength;
					}

					printout.writeBytes(postData.substring(start, end));
					printout.flush();

					// track progress
					progressBar.setValue(end);
				}

				printout.close();

				postData = null;

				int responseCode;
				String responseMessage;

				try {
					responseCode = urlConn.getResponseCode();
					responseMessage = urlConn.getResponseMessage();
				} catch (IOException e) {
					// if we can't even get the response code something failed
					// anyway
					responseCode = -1;
					responseMessage = e.getMessage();
				}

				// URL ok
				if (responseCode == HttpURLConnection.HTTP_OK) {
					// get response and read it into a string buffer
					input = new BufferedReader(new InputStreamReader(
							urlConn.getInputStream(), StandardCharsets.UTF_8));

					StringBuffer output = new StringBuffer();

					String line;
					while (null != ((line = input.readLine()))) {
						output.append(line);
					}

					input.close();

					final UploadResults results = new UploadResults(
							output.toString());

					if (results.hasError()) {
						statusLabelSetText(getLoc().getPlain("UploadError",
								results.getErrorMessage()));
						progressBar.setEnabled(false);

						Log.debug("Upload failed. Response: "
								+ output.toString());
					} else {

						String createMaterialURL = getUploadURL() + "/"
								+ results.getUID();

						// Add the login token to the URL if a user is logged in
						if (app.getLoginOperation().getModel().isLoggedIn()) {

							String token = app.getLoginOperation().getModel()
									.getLoggedInUser().getLoginToken();
							if (token != null) {
								createMaterialURL += "/lt/" + token;
							}
						}

						// Add the language parameter to show the page in the
						// user language
						createMaterialURL += "/?lang="
								+ ((AppD) app).getLocale().getLanguage();

						app.showURLinBrowser(createMaterialURL);
						hideDialog();
					}

					pack();
				} else {
					Log.debug("Upload failed. Response: #" + responseCode
							+ " - " + responseMessage);

					BufferedReader errors = new BufferedReader(
							new InputStreamReader(urlConn.getErrorStream(),
									StandardCharsets.UTF_8));
					StringBuffer errorBuffer = new StringBuffer();

					String line;
					while (null != ((line = errors.readLine()))) {
						errorBuffer.append(line);
					}
					errors.close();

					Log.debug(errorBuffer.toString());

					statusLabelSetText(getLoc().getPlain("UploadError",
							Integer.toString(responseCode)));
					progressBar.setEnabled(false);
					pack();
				}
			} catch (IOException e) {
				statusLabelSetText(
						getLoc().getPlain("UploadError", Integer.toString(500)));
				progressBar.setEnabled(false);
				pack();

				Log.debug(e.getMessage());
			}
		} catch (IOException e) {
			statusLabelSetText(
					getLoc().getPlain("UploadError", Integer.toString(400)));
			progressBar.setEnabled(false);
			pack();

			Log.debug(e.getMessage());
		}
	}

	/**
	 * Shows a small dialog with a progress bar.
	 */
	protected void showDialog() {
		// initialize components
		progressBar = new JProgressBar();
		statusLabel = new JLabel(getLoc().getMenu("UploadPrepare") + " ...");

		// setup buttons
		abortButton = new JButton(getLoc().getMenu("Close"));
		abortButton.addActionListener(arg0 -> hideDialog());

		JPanel buttonPanel = new JPanel(
				new FlowLayout(FlowLayout.RIGHT, 10, 0));
		buttonPanel.add(abortButton);

		// main panel
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		panel.add(statusLabel, BorderLayout.NORTH);
		panel.add(progressBar, BorderLayout.CENTER);
		panel.add(buttonPanel, BorderLayout.SOUTH);
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		// dialog options
		progressDialog = new Dialog();
		progressDialog
				.setTitle(app.getLocalization().getMenu("UploadGeoGebraTube"));
		progressDialog.setResizable(false);
		progressDialog.add(panel);

		progressDialog.pack();
		progressDialog.setVisible(true);
		progressDialog.setLocationRelativeTo(null); // center
	}

	protected void pack() {
		progressDialog.pack();
	}

	/**
	 * Hides progress dialog.
	 */
	public void hideDialog() {
		progressDialog.setVisible(false);
	}

	protected void statusLabelSetText(String plainText) {
		statusLabel.setText(plainText);
	}

	protected String encode(String str) {
		return URLEncoder.encode(str, StandardCharsets.UTF_8);
	}

	/**
	 * returns a base64 encoded .ggb file
	 */
	protected String getBase64String() {
		return app.getGgbApi().getBase64(true);
	}

	protected StringBuilder getPostData() throws IOException {
		Construction cons = app.getKernel().getConstruction();

		// build post query
		StringBuilder sb = new StringBuilder();
		sb.append("data=");
		sb.append(encode(
				getBase64String()));

		sb.append("&type=");
		sb.append("ggb");

		sb.append("&title=");
		sb.append(encode(cons.getTitle()));

		sb.append("&pretext=");
		sb.append(encode(cons.getWorksheetText(0)));

		sb.append("&posttext=");
		sb.append(encode(cons.getWorksheetText(1)));

		sb.append("&version=");
		sb.append(encode(GeoGebraConstants.VERSION_STRING));

		return sb;
	}

	/**
	 * @return base upload URL for GeoGebraTube
	 */
	public String getUploadURL() {
		if (PreviewFeature.isAvailable(PreviewFeature.RESOURCES_API_BETA)) {
			return GeoGebraConstants.uploadURLBeta;
		}

		return GeoGebraConstants.uploadURL;
	}

	public Localization getLoc() {
		return loc;
	}

	public void setLoc(Localization loc) {
		this.loc = loc;
	}

}
