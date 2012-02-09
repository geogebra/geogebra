package geogebra.web.main;

import geogebra.common.GeoGebraConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.main.AbstractApplication;

import java.io.IOException;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;


/**
 * Export GeoGebra worksheet to GeoGebraTube.
 * 
 * @author Florian Sonner
 */
public class GeoGebraTubeExportWeb extends geogebra.common.export.GeoGebraTubeExport {
	
	public GeoGebraTubeExportWeb(AbstractApplication app) {
	    super(app);
    }

	/**
	 * URL of the webpage to call if a file should be uploaded.
	 */
	private static final String uploadURL = "http://www.geogebratube.org/upload";
	
	
	/**
	 * Upload the current worksheet to GeoGebraTube.
	 */
	public void uploadWorksheet() {
		showDialog();
		
		Construction cons = app.getKernel().getConstruction();
		
		try {
			
			RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, uploadURL);
			rb.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			//rb.setHeader("Accept-Language", "app.getLocaleStr()");

			// build post query
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append("data=");
			stringBuffer.append(encode(getBase64String()));

			stringBuffer.append("&title=");
			stringBuffer.append(encode(cons.getTitle()));
			
			stringBuffer.append("&pretext=");
			stringBuffer.append(encode(cons.getWorksheetText(0)));
			
			stringBuffer.append("&posttext=");
			stringBuffer.append(encode(cons.getWorksheetText(1)));
			
			stringBuffer.append("&version=");
			stringBuffer.append(encode(GeoGebraConstants.VERSION_STRING));

			String postData = stringBuffer.toString();
			
			AbstractApplication.debug(postData);
			
			try {
			Request response = rb.sendRequest(postData, new RequestCallback()
			{

			public void onError(Request request, Throwable exception) {
				AbstractApplication.debug("onError: " + request.toString() + " " + exception.toString());
			}
			public void onResponseReceived(Request request, Response response) {

				if (response.getStatusCode() == Response.SC_OK) {

					AbstractApplication.debug("result from server: "+response.getText());

					final UploadResults results = new UploadResults(response.getText());

					if(results.HasError()) {
						statusLabelSetText(app.getPlain("UploadError"));
						setEnabled(false);

						AbstractApplication.debug("Upload failed. Response: " + response.getText());
					} else {
						app.showURLinBrowser(uploadURL + "/" + results.getUID());
						hideDialog();
					}
				} else { // not 
					AbstractApplication.debug("Upload failed. Response: #" + response.getStatusCode() + " - " + response.getStatusText());

					AbstractApplication.debug(response.getText());

					statusLabelSetText(app.getPlain("UploadError", Integer.toString(response.getStatusCode())));
					setEnabled(false);
					pack();

				}

			}});
			}
			catch (RequestException e) {
				statusLabelSetText(app.getPlain("UploadError", Integer.toString(500)));
				setEnabled(false);
				pack();
				
				AbstractApplication.debug(e.getMessage());
			}
		} catch (Exception e) {
			statusLabelSetText(app.getPlain("UploadError", Integer.toString(400)));
			setEnabled(false);
			pack();
			
			AbstractApplication.debug(e.getMessage());

		}
			

	}	

	private String encode(String str) {
		if (str != null) {
			return URL.encode(str);
		} else {
			AbstractApplication.printStacktrace("passed null");
			return "";
		}
    }
	
	protected  void setMaximum(int i){
		AbstractApplication.debug("Unimplemented");
	}

	protected  void setMinimum(int i){
		AbstractApplication.debug("Unimplemented");
	}

	protected  void setIndeterminate(boolean b){
		AbstractApplication.debug("Unimplemented");
	}

	protected  void setValue(int end){
		AbstractApplication.debug("Unimplemented");
	}

	protected  void setEnabled(boolean b){
		AbstractApplication.debug("Unimplemented");
	}
	
	/**
	 * Shows a small dialog with a progress bar. 
	 */
	protected void showDialog(){
		AbstractApplication.debug("Unimplemented");
	}
	
	protected void statusLabelSetText(String plain) {
		AbstractApplication.debug("Unimplemented: "+plain);
	}

	protected  void pack() {
		AbstractApplication.debug("Unimplemented");
	}

	/**
	 * Hides progress dialog.
	 */
	public void hideDialog(){
		AbstractApplication.debug("Unimplemented");
	}
	

}
