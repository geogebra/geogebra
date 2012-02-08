package geogebra.export;

import geogebra.common.GeoGebraConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.main.AbstractApplication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


/**
 * Export GeoGebra worksheet to GeoGebraTube.
 * 
 * @author Florian Sonner
 */
public abstract class GeoGebraTubeExport {
	/**
	 * URL of the webpage to call if a file should be uploaded.
	 */
	private static final String uploadURL = "http://www.geogebratube.org/upload";
	
	/**
	 * Application instance.
	 */
	protected AbstractApplication app;
	
	/**
	 * Constructs a new instance of the GeoGebraTube exporter.
	 * 
	 * @param app
	 */
	public GeoGebraTubeExport(AbstractApplication app) {
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
	

	protected abstract void statusLabelSetText(String plain);

	protected abstract void pack();

	/**
	 * Hides progress dialog.
	 */
	public abstract void hideDialog();
	


	/**
	 * Append a base64 encoded .ggb file to the passed string buffer. 
	 * 
	 * @throws IOException
	 */
	private String getBase64String() throws IOException {
		return app.getGgbApi().getBase64(true);
	}
	
	/**
	 * Shows a small dialog with a progress bar. 
	 */
	protected abstract void showDialog();
	
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
	
	protected abstract void setMaximum(int i);

	protected abstract void setMinimum(int i);

	protected abstract void setIndeterminate(boolean b);

	protected abstract void setValue(int end);

	protected abstract void setEnabled(boolean b);
}
