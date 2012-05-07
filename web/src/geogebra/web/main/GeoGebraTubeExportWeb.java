package geogebra.web.main;

import geogebra.common.GeoGebraConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Macro;
import geogebra.common.main.AbstractApplication;

import java.io.IOException;
import java.util.ArrayList;

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
	@Override
    public void uploadWorksheet(ArrayList<Macro> macrosIn) {
		
		this.macros = macrosIn;
		
		showDialog();

		Construction cons = app.getKernel().getConstruction();

		try {

			RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, uploadURL);
			rb.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			//rb.setHeader("Accept-Language", "app.getLocaleStr()");

			String postData = getPostData().toString();

			// encode '+'
			// for some reason encode(postData) doesn't work
			postData = postData.replace("+", "%2B");
			
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
								AbstractApplication.debug("Opening URL: " + uploadURL + "/" + results.getUID());
								app.showURLinBrowser(uploadURL + "/" + results.getUID());
								hideDialog();
							}
						} else { // not Response.SC_OK
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

	@Override
	protected String encode(String str) {
		if (str != null) {
			return URL.encode(str);
		} 
		
		AbstractApplication.printStacktrace("passed null");
		return "";

	}

	@Override
    protected  void setMaximum(int i){
		AbstractApplication.debug("Unimplemented " + i);
	}

	@Override
    protected  void setMinimum(int i){
		AbstractApplication.debug("Unimplemented " + i);
	}

	@Override
    protected  void setIndeterminate(boolean b){
		AbstractApplication.debug("Unimplemented " + b);
	}

	@Override
    protected  void setValue(int end){
		AbstractApplication.debug("Unimplemented " + end );
	}

	@Override
    protected  void setEnabled(boolean b){
		AbstractApplication.debug("Unimplemented " + b);
	}

	/**
	 * Shows a small dialog with a progress bar. 
	 */
	@Override
    protected void showDialog(){
		AbstractApplication.debug("Unimplemented");
	}

	@Override
    protected void statusLabelSetText(String plain) {
		AbstractApplication.debug("Unimplemented: "+plain);
	}

	@Override
    protected  void pack() {
		AbstractApplication.debug("Unimplemented");
	}

	/**
	 * Hides progress dialog.
	 */
	@Override
    public void hideDialog(){
		AbstractApplication.debug("Unimplemented");
	}

	@Override
    protected String getBase64Tools(ArrayList<Macro> macros) throws IOException {
		AbstractApplication.debug("Unimplemented");
	    return null;
    }


}
