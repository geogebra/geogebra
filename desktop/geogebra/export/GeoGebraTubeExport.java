package geogebra.export;

import geogebra.GeoGebra;
import geogebra.kernel.Construction;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 * Export GeoGebra worksheet to GeoGebraTube.
 * 
 * @author Florian Sonner
 */
public class GeoGebraTubeExport {
	/**
	 * URL of the webpage to call if a file should be uploaded.
	 */
	private static final String uploadURL = "http://www.geogebratube.org/upload";
	
	/**
	 * Application instance.
	 */
	private Application app;
	
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
	 * Constructs a new instance of the GeoGebraTube exporter.
	 * 
	 * @param app
	 */
	public GeoGebraTubeExport(Application app) {
		this.app = app;
	}
	
	/**
	 * Upload the current worksheet to GeoGebraTube.
	 */
	public void uploadWorksheet() {
		showDialog();
		
		try {
			URL url;
		    HttpURLConnection urlConn;
		    DataOutputStream printout;
		    BufferedReader input;

		    progressBar.setIndeterminate(true);
		    
			url = new URL(uploadURL);
			
			urlConn = (HttpURLConnection)url.openConnection();
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setUseCaches(false);

			// content type
			urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			urlConn.setRequestProperty("Accept-Language", app.getLocale().toString());
			
			// send output
			try {
				printout = new DataOutputStream(urlConn.getOutputStream());

				Construction cons = app.getKernel().getConstruction();
				
				// build post query
				StringBuffer stringBuffer = new StringBuffer();
				stringBuffer.append("data=");
				stringBuffer.append(URLEncoder.encode(getBase64String(), "UTF-8"));

				stringBuffer.append("&title=");
				stringBuffer.append(URLEncoder.encode(cons.getTitle(), "UTF-8"));
				
				stringBuffer.append("&pretext=");
				stringBuffer.append(URLEncoder.encode(cons.getWorksheetText(0), "UTF-8"));
				
				stringBuffer.append("&posttext=");
				stringBuffer.append(URLEncoder.encode(cons.getWorksheetText(1), "UTF-8"));
				
				stringBuffer.append("&version=");
				stringBuffer.append(URLEncoder.encode(GeoGebra.VERSION_STRING, "UTF-8"));

				int requestLength = stringBuffer.length();
				/*urlConn.disconnect();
				urlConn.setChunkedStreamingMode(1000);
				urlConn.connect();*/
				
				progressBar.setIndeterminate(false);
				progressBar.setMinimum(0);
				progressBar.setMaximum(requestLength);

				// send data in chunks
				int start = 0;
				int end = 0;
				
				// chunking is senseless at the moment as input buffering is activated
				while(end != requestLength) {
					start = end;
					end += 5000;
					
					if(end > requestLength) {
						end = requestLength;
					}
					
					printout.writeBytes(stringBuffer.substring(start, end));
					printout.flush();
					
					// track progress 
					progressBar.setValue(end);
				}
				
				printout.close();
				
				stringBuffer = null;
				
				int responseCode; String responseMessage;
				
				try {
					responseCode = urlConn.getResponseCode();
					responseMessage = urlConn.getResponseMessage();
				} catch (IOException e) {
					// if we can't even get the response code something failed anyway
					responseCode = -1;
					responseMessage = e.getMessage();
				}
				
				// URL ok
				if(responseCode == HttpURLConnection.HTTP_OK) {
					// get response and read it into a string buffer 
					input = new BufferedReader(new InputStreamReader(urlConn
							.getInputStream()));
					
					StringBuffer output = new StringBuffer();
					
					String line;
					while (null != ((line = input.readLine()))) {
						output.append(line);
					}
					
					input.close();

					final UploadResults results = new UploadResults(output.toString());
					
					if(results.HasError()) {
						statusLabel.setText(app.getPlain("UploadError"));
						progressBar.setEnabled(false);
						
						Application.debug("Upload failed. Response: " + output.toString());
					} else {
						app.getGuiManager().showURLinBrowser(uploadURL + "/" + results.getUID());
						hideDialog();
					}
					
					progressDialog.pack();
				} else {
					Application.debug("Upload failed. Response: #" + responseCode + " - " + responseMessage);
					
					BufferedReader errors = new BufferedReader(new InputStreamReader(urlConn.getErrorStream()));
					StringBuffer errorBuffer = new StringBuffer();
					
					String line;
					while (null != ((line = errors.readLine()))) {
						errorBuffer.append(line);
					}
					errors.close();
					
					Application.debug(errorBuffer.toString());
					
					statusLabel.setText(app.getPlain("UploadError", Integer.toString(responseCode)));
					progressBar.setEnabled(false);
					progressDialog.pack();
				}
			} catch (IOException e) {
				statusLabel.setText(app.getPlain("UploadError", Integer.toString(500)));
				progressBar.setEnabled(false);
				progressDialog.pack();
				
				Application.debug(e.getMessage());
			}
		} catch (IOException e) {
			statusLabel.setText(app.getPlain("UploadError", Integer.toString(400)));
			progressBar.setEnabled(false);
			progressDialog.pack();
			
			Application.debug(e.getMessage());
		}
	}
	
	/**
	 * Append a base64 encoded .ggb file to the passed string buffer. 
	 * 
	 * @throws IOException
	 */
	private String getBase64String() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		app.getXMLio().writeGeoGebraFile(baos, true);
		return geogebra.util.Base64.encode(baos.toByteArray(), 0);
	}
	
	/**
	 * Shows a small dialog with a progress bar. 
	 */
	private void showDialog() {
		// initialize components
		progressBar = new JProgressBar();
		statusLabel = new JLabel(app.getPlain("UploadPrepare") + " ...");
		
		// setup buttons
		abortButton = new JButton(app.getMenu("Close"));
		abortButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				hideDialog();
			}
		});
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		buttonPanel.add(abortButton);
		
		// main panel
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		panel.add(statusLabel, BorderLayout.NORTH);
		panel.add(progressBar, BorderLayout.CENTER);
		panel.add(buttonPanel, BorderLayout.SOUTH);
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		// dialog options
		progressDialog = new JDialog();
		progressDialog.setTitle(app.getMenu("UploadGeoGebraTube"));
		progressDialog.setResizable(false);
		progressDialog.add(panel);
		
		progressDialog.pack();
		progressDialog.setVisible(true);
		progressDialog.setLocationRelativeTo(null); // center
	}
	
	/**
	 * Hides progress dialog.
	 */
	public void hideDialog() {
		progressDialog.setVisible(false);
	}
	
	/**
	 * Storage container for uploading results.
	 * 
	 * @author Florian Sonner
	 */
	private class UploadResults {
		private String status;
		private String uid;
		private String errorMessage;
		
		/**
		 * Parse upload result string.
		 *  
		 * @param string
		 */		
		public UploadResults(String string) {
			status = uid = errorMessage = "";
			
			for(String line : string.split(",")) {
				int delimiterPos = line.indexOf(':');
				String key = line.substring(0, delimiterPos).toLowerCase();
				String value = line.substring(delimiterPos+1).toLowerCase();
				
				if(key.equals("status")) {
					status = value;
				} else if(key.equals("uid")) {
					uid = value;
				} else if(key.equals("error")) {
					errorMessage = value;
				}
			}
		}
		
		public boolean HasError() {
			return !status.equals("ok");
		}
		
		public String getStatus() { return status; }
		public String getUID() { return uid; }
		public String getErrorMessage() { return errorMessage; }
	}
}
