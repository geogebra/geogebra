package org.geogebra.web.full.main;

import java.util.ArrayList;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GgbAPIW;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;

import elemental2.dom.DomGlobal;

/**
 * Export GeoGebra worksheet to GeoGebraTube.
 * 
 * @author Florian Sonner
 */
public class GeoGebraTubeExportW extends
        org.geogebra.common.export.GeoGebraTubeExport {

	/**
	 * @param app
	 *            application
	 */
	public GeoGebraTubeExportW(App app) {
		super(app);
	}

	/**
	 * @param base64
	 *            material base64
	 * @return urlencoded POST fields
	 */
	protected StringBuffer getPostData(String base64) {
		Construction cons = app.getKernel().getConstruction();

		boolean isConstruction = getMacros() == null;

		// build post query
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("data=");
		stringBuffer.append(encode(getMacros() == null ? base64
		        : getBase64Tools(getMacros())));

		stringBuffer.append("&type=");
		stringBuffer.append(isConstruction ? "ggb" : "ggt");

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

	/**
	 * @param rb
	 *            request builder
	 * @param postData0
	 *            worksheet metadata
	 * @param pba
	 *            helper to keep popup alive
	 */
	protected void doUploadWorksheet(RequestBuilder rb, String postData0) {
		// encode '+'
		// for some reason encode(postData) doesn't work
		String postData = postData0.replace("+", "%2B");

		try {
			rb.sendRequest(postData, new RequestCallback() {

				@Override
				public void onError(Request request, Throwable exception) {
					Log.debug("onError: " + request.toString() + " "
					        + exception.toString());
				}

				@Override
				public void onResponseReceived(Request request,
				        Response response) {

					if (response.getStatusCode() == Response.SC_OK) {

						Log.debug("result from server: " + response.getText());

						final UploadResults results = new UploadResults(
						        response.getText());

						if (results.hasError()) {
							statusLabelSetText(app.getLocalization().getPlain(
							        "UploadError", results.getStatus()));
							setEnabled(false);

							Log.debug("Upload failed. Response: "
							        + response.getText());
						} else {
							Log.debug("Opening URL: " + getUploadURL(app) + "/"
							        + results.getUID());
							DomGlobal.window.open(
									getUploadURL(app) + "/"
									+ results.getUID(), "_blank");
						}
					} else { // not Response.SC_OK
						Log.debug("Upload failed. Response: #"
						        + response.getStatusCode() + " - "
						        + response.getStatusText());

						Log.debug(response.getText());

						statusLabelSetText(getLoc().getPlain("UploadError",
						        Integer.toString(response.getStatusCode())));
						setEnabled(false);
						pack();

					}

				}
			});
		} catch (RequestException e) {
			statusLabelSetText(getLoc().getPlain("UploadError",
			        Integer.toString(500)));
			setEnabled(false);
			pack();

			Log.debug(e.getMessage());
		}
	}

	/**
	 * Upload the current worksheet to GeoGebraTube.
	 * 
	 * @param macrosIn
	 *            macros
	 */
	public void uploadWorksheet(ArrayList<Macro> macrosIn) {

		this.setMacros(macrosIn);

		try {
			RequestBuilder rb = new RequestBuilder(RequestBuilder.POST,
					getUploadURL(app));
			rb.setHeader("Content-Type",
			        "application/x-www-form-urlencoded; charset=utf-8");
			// rb.setHeader("Accept-Language", "app.getLocaleStr()");

			String postData = getPostData().toString();
			doUploadWorksheet(rb, postData);
		} catch (Exception e) {
			statusLabelSetText(getLoc().getPlain("UploadError",
			        Integer.toString(400)));
			setEnabled(false);
			pack();

			Log.debug(e.getMessage());

		}
	}

	@Override
	protected String encode(String str) {
		if (str != null) {
			return URL.encode(str);
		}

		Log.error("passed null");
		return "";

	}

	@Override
	protected void setMaximum(int i) {
		Log.debug("Unimplemented " + i);
	}

	@Override
	protected void setMinimum(int i) {
		Log.debug("Unimplemented " + i);
	}

	@Override
	protected void setIndeterminate(boolean b) {
		Log.debug("Unimplemented " + b);
	}

	@Override
	protected void setValue(int end) {
		Log.debug("Unimplemented " + end);
	}

	@Override
	protected void setEnabled(boolean b) {
		Log.debug("Unimplemented " + b);
	}

	@Override
	protected void statusLabelSetText(String plain) {
		ToolTipManagerW.sharedInstance().showBottomMessage(plain, (AppW) app);
	}

	@Override
	protected void pack() {
		Log.debug("Unimplemented");
	}

	@Override
	protected String getBase64Tools(ArrayList<Macro> macros1) {
		return ((GgbAPIW) app.getGgbApi()).getMacrosBase64();
	}

}
