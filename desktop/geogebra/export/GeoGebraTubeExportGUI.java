package geogebra.export;

import geogebra.common.GeoGebraConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.main.AbstractApplication;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
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
public class GeoGebraTubeExportGUI  extends geogebra.common.export.GeoGebraTubeExport {
	
	public GeoGebraTubeExportGUI(AbstractApplication app) {
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
	
	protected void setMaximum(int i) {
		progressBar.setMaximum(i);
	}

	protected void setMinimum(int i) {
		progressBar.setMinimum(i);
		
	}

	protected void setIndeterminate(boolean b) {
		progressBar.setIndeterminate(b);
		
	}

	protected void setValue(int end) {
		progressBar.setValue(end);
		
	}

	protected void setEnabled(boolean b) {
		progressBar.setEnabled(b);
		
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

	    setIndeterminate(true);
	    
		url = new URL(uploadURL);
		
		urlConn = (HttpURLConnection)url.openConnection();
		urlConn.setDoInput(true);
		urlConn.setDoOutput(true);
		urlConn.setUseCaches(false);

		// content type
		urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		urlConn.setRequestProperty("Accept-Language", app.getLocaleStr());
		
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
			stringBuffer.append(URLEncoder.encode(GeoGebraConstants.VERSION_STRING, "UTF-8"));

			int requestLength = stringBuffer.length();
			/*urlConn.disconnect();
			urlConn.setChunkedStreamingMode(1000);
			urlConn.connect();*/
			
			setIndeterminate(false);
			setMinimum(0);
			setMaximum(requestLength);

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
				setValue(end);
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
					statusLabelSetText(app.getPlain("UploadError"));
					setEnabled(false);
					
					AbstractApplication.debug("Upload failed. Response: " + output.toString());
				} else {
					app.showURLinBrowser(uploadURL + "/" + results.getUID());
					hideDialog();
				}
				
				pack();
				} else {
				AbstractApplication.debug("Upload failed. Response: #" + responseCode + " - " + responseMessage);
				
				BufferedReader errors = new BufferedReader(new InputStreamReader(urlConn.getErrorStream()));
				StringBuffer errorBuffer = new StringBuffer();
				
				String line;
				while (null != ((line = errors.readLine()))) {
					errorBuffer.append(line);
				}
				errors.close();
				
				AbstractApplication.debug(errorBuffer.toString());
				
				statusLabelSetText(app.getPlain("UploadError", Integer.toString(responseCode)));
				setEnabled(false);
				pack();
			}
		} catch (IOException e) {
			statusLabelSetText(app.getPlain("UploadError", Integer.toString(500)));
			setEnabled(false);
			pack();
			
			AbstractApplication.debug(e.getMessage());
		}
	} catch (IOException e) {
		statusLabelSetText(app.getPlain("UploadError", Integer.toString(400)));
		setEnabled(false);
		pack();
		
		AbstractApplication.debug(e.getMessage());
	}
}
	
	/**
	 * Shows a small dialog with a progress bar. 
	 */
	protected void showDialog() {
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
	
	protected void pack() {
		progressDialog.pack();
	}

	/**
	 * Hides progress dialog.
	 */
	public void hideDialog() {
		progressDialog.setVisible(false);
	}
	
	protected void statusLabelSetText(String plain) {
		statusLabel.setText(plain);
		}



}
