package org.geogebra.desktop.export;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.geogebra.common.export.GeoGebraTubeExport;
import org.geogebra.common.jre.util.Base64;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.main.App;
import org.geogebra.common.util.Charsets;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.gui.dialog.Dialog;
import org.geogebra.desktop.main.AppD;

/**
 * Export GeoGebra worksheet to GeoGebraTube.
 * 
 * @author Florian Sonner
 */
public class GeoGebraTubeExportD extends GeoGebraTubeExport {

	public GeoGebraTubeExportD(App app) {
		super(app);
	}

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

	@Override
	protected void setMaximum(int i) {
		progressBar.setMaximum(i);
	}

	@Override
	protected void setMinimum(int i) {
		progressBar.setMinimum(i);

	}

	@Override
	protected void setIndeterminate(boolean b) {
		progressBar.setIndeterminate(b);

	}

	@Override
	protected void setValue(int end) {
		progressBar.setValue(end);

	}

	@Override
	protected void setEnabled(boolean b) {
		progressBar.setEnabled(b);

	}

	/**
	 * Upload the current worksheet to GeoGebraTube.
	 * 
	 * @param macrosIn
	 *            null to upload current construction, otherwise upload just
	 *            tools
	 */
	public void uploadWorksheet(ArrayList<Macro> macrosIn) {

		this.setMacros(macrosIn);

		showDialog();

		try {
			URL url;
			HttpURLConnection urlConn;
			DataOutputStream printout;
			BufferedReader input;

			setIndeterminate(true);

			url = new URL(getUploadURL(app));

			urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setUseCaches(false);

			// content type
			urlConn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			urlConn.setRequestProperty("Accept-Language",
					app.getLocalization().getLocaleStr());

			// send output
			try {
				printout = new DataOutputStream(urlConn.getOutputStream());

				StringBuilder postData = getPostData();

				int requestLength = postData.length();
				/*
				 * urlConn.disconnect(); urlConn.setChunkedStreamingMode(1000);
				 * urlConn.connect();
				 */

				setIndeterminate(false);
				setMinimum(0);
				setMaximum(requestLength);

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
					setValue(end);
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
							urlConn.getInputStream(), Charsets.getUtf8()));

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
						setEnabled(false);

						Log.debug("Upload failed. Response: "
								+ output.toString());
					} else {

						String createMaterialURL = getUploadURL(app) + "/"
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
									Charsets.getUtf8()));
					StringBuffer errorBuffer = new StringBuffer();

					String line;
					while (null != ((line = errors.readLine()))) {
						errorBuffer.append(line);
					}
					errors.close();

					Log.debug(errorBuffer.toString());

					statusLabelSetText(getLoc().getPlain("UploadError",
							Integer.toString(responseCode)));
					setEnabled(false);
					pack();
				}
			} catch (IOException e) {
				statusLabelSetText(
						getLoc().getPlain("UploadError", Integer.toString(500)));
				setEnabled(false);
				pack();

				Log.debug(e.getMessage());
			}
		} catch (IOException e) {
			statusLabelSetText(
					getLoc().getPlain("UploadError", Integer.toString(400)));
			setEnabled(false);
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
		abortButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				hideDialog();
			}
		});

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

	@Override
	protected void pack() {
		progressDialog.pack();
	}

	/**
	 * Hides progress dialog.
	 */
	public void hideDialog() {
		progressDialog.setVisible(false);
	}

	@Override
	protected void statusLabelSetText(String plain) {
		statusLabel.setText(plain);
	}

	@Override
	protected String encode(String str) {
		try {
			return URLEncoder.encode(str, Charsets.UTF_8);
		} catch (UnsupportedEncodingException e) {
			Log.debug("error from GeoGebraTubeExport.encode()");
			return str;
		}
	}

	@Override
	protected String getBase64Tools(ArrayList<Macro> macros)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		((AppD) app).getXMLio().writeMacroStream(baos, macros);
		return Base64.encodeToString(baos.toByteArray(), false);
	}

}
