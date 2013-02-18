package geogebra.web.main;

import geogebra.common.GeoGebraConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Macro;
import geogebra.common.main.App;

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

	public GeoGebraTubeExportWeb(App app) {
		super(app);
	}

	/**
	 * URL of the webpage to call if a file should be uploaded.
	 */
	private static final String uploadURL = "http://www.geogebratube.org/upload";

	protected StringBuffer getPostData(String base64) throws IOException {
		Construction cons = app.getKernel().getConstruction();
		
		// build post query
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("data=");
		stringBuffer.append(encode(macros == null ? base64 : getBase64Tools(macros)));

		stringBuffer.append("&title=");
		stringBuffer.append(encode(cons.getTitle()));
		
		stringBuffer.append("&pretext=");
		stringBuffer.append(encode(cons.getWorksheetText(0)));
		
		stringBuffer.append("&posttext=");
		stringBuffer.append(encode(cons.getWorksheetText(1)));
		
		stringBuffer.append("&version=");
		stringBuffer.append(encode(GeoGebraConstants.VERSION_STRING));
		
		return stringBuffer;
	}

	protected void doUploadWorksheet(RequestBuilder rb, String postData) {
		// encode '+'
		// for some reason encode(postData) doesn't work
		postData = postData.replace("+", "%2B");
		
		try {
			Request response = rb.sendRequest(postData, new RequestCallback()
			{

				public void onError(Request request, Throwable exception) {
					App.debug("onError: " + request.toString() + " " + exception.toString());
				}
				public void onResponseReceived(Request request, Response response) {

					if (response.getStatusCode() == Response.SC_OK) {

						App.debug("result from server: "+response.getText());

						final UploadResults results = new UploadResults(response.getText());

						if(results.HasError()) {
							statusLabelSetText(app.getPlain("UploadError"));
							setEnabled(false);

							App.debug("Upload failed. Response: " + response.getText());
						} else {
							App.debug("Opening URL: " + uploadURL + "/" + results.getUID());
							app.showURLinBrowser(uploadURL + "/" + results.getUID());
							hideDialog();
						}
					} else { // not Response.SC_OK
						App.debug("Upload failed. Response: #" + response.getStatusCode() + " - " + response.getStatusText());

						App.debug(response.getText());

						statusLabelSetText(loc.getPlain("UploadError", Integer.toString(response.getStatusCode())));
						setEnabled(false);
						pack();

					}

				}});
		}
		catch (RequestException e) {
			statusLabelSetText(loc.getPlain("UploadError", Integer.toString(500)));
			setEnabled(false);
			pack();

			App.debug(e.getMessage());
		}
	}

    public void uploadWorksheetSimple(String base64) {
		this.macros = null;

		showDialog();

		Construction cons = app.getKernel().getConstruction();

		try {

			RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, uploadURL);
			rb.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			//rb.setHeader("Accept-Language", "app.getLocaleStr()");

			String postData = getPostData(base64).toString();

			doUploadWorksheet(rb, postData);
		} catch (Exception e) {
			statusLabelSetText(loc.getPlain("UploadError", Integer.toString(400)));
			setEnabled(false);
			pack();

			App.debug(e.getMessage());

		}
    }

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
			doUploadWorksheet(rb, postData);
		} catch (Exception e) {
			statusLabelSetText(loc.getPlain("UploadError", Integer.toString(400)));
			setEnabled(false);
			pack();

			App.debug(e.getMessage());

		}
	}	

	@Override
	protected String encode(String str) {
		if (str != null) {
			return URL.encode(str);
		} 
		
		App.printStacktrace("passed null");
		return "";

	}

	@Override
    protected  void setMaximum(int i){
		App.debug("Unimplemented " + i);
	}

	@Override
    protected  void setMinimum(int i){
		App.debug("Unimplemented " + i);
	}

	@Override
    protected  void setIndeterminate(boolean b){
		App.debug("Unimplemented " + b);
	}

	@Override
    protected  void setValue(int end){
		App.debug("Unimplemented " + end );
	}

	@Override
    protected  void setEnabled(boolean b){
		App.debug("Unimplemented " + b);
	}

	/**
	 * Shows a small dialog with a progress bar. 
	 */
	@Override
    protected void showDialog(){
		App.debug("Unimplemented");
	}

	@Override
    protected void statusLabelSetText(String plain) {
		App.debug("Unimplemented: "+plain);
	}

	@Override
    protected  void pack() {
		App.debug("Unimplemented");
	}

	/**
	 * Hides progress dialog.
	 */
	@Override
    public void hideDialog(){
		App.debug("Unimplemented");
	}

	@Override
    protected String getBase64Tools(ArrayList<Macro> macros) throws IOException {
		App.debug("Unimplemented");
	    return null;
    }


}
