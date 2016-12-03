package org.geogebra.web.web.main;

import java.io.IOException;
import java.util.ArrayList;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GgbAPIW;
import org.geogebra.web.web.gui.util.PopupBlockAvoider;

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
public class GeoGebraTubeExportW extends
        org.geogebra.common.export.GeoGebraTubeExport {

	public GeoGebraTubeExportW(App app) {
		super(app);
	}

	protected StringBuffer getPostData(String base64) throws IOException {
		Construction cons = app.getKernel().getConstruction();

		boolean isConstruction = (macros == null);

		// build post query
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("data=");
		stringBuffer.append(encode(macros == null ? base64
		        : getBase64Tools(macros)));

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

	protected void doUploadWorksheet(RequestBuilder rb, String postData,
			final PopupBlockAvoider pba) {
		// encode '+'
		// for some reason encode(postData) doesn't work
		postData = postData.replace("+", "%2B");

		try {
			Request response = rb.sendRequest(postData, new RequestCallback() {

				public void onError(Request request, Throwable exception) {
					Log.debug("onError: " + request.toString() + " "
					        + exception.toString());
				}

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
							pba.openURL(
									getUploadURL(app) + "/"
									+ results.getUID());
							hideDialog();
						}
					} else { // not Response.SC_OK
						Log.debug("Upload failed. Response: #"
						        + response.getStatusCode() + " - "
						        + response.getStatusText());

						Log.debug(response.getText());

						statusLabelSetText(loc.getPlain("UploadError",
						        Integer.toString(response.getStatusCode())));
						setEnabled(false);
						pack();

					}

				}
			});
		} catch (RequestException e) {
			statusLabelSetText(loc.getPlain("UploadError",
			        Integer.toString(500)));
			setEnabled(false);
			pack();

			Log.debug(e.getMessage());
		}
	}

	public void uploadWorksheetSimple(String base64, PopupBlockAvoider pba) {
		this.macros = null;

		showDialog();

		Construction cons = app.getKernel().getConstruction();

		try {

			RequestBuilder rb = new RequestBuilder(RequestBuilder.POST,
					getUploadURL(app));
			rb.setHeader("Content-Type",
			        "application/x-www-form-urlencoded; charset=utf-8");
			// rb.setHeader("Accept-Language", "app.getLocaleStr()");

			String postData = getPostData(base64).toString();

			doUploadWorksheet(rb, postData, pba);
		} catch (Exception e) {
			statusLabelSetText(loc.getPlain("UploadError",
			        Integer.toString(400)));
			setEnabled(false);
			pack();

			Log.debug(e.getMessage());

		}
	}

	/**
	 * Upload the current worksheet to GeoGebraTube.
	 */
	public void uploadWorksheet(ArrayList<Macro> macrosIn, PopupBlockAvoider pba) {

		this.macros = macrosIn;

		showDialog();

		Construction cons = app.getKernel().getConstruction();

		try {
			RequestBuilder rb = new RequestBuilder(RequestBuilder.POST,
					getUploadURL(app));
			rb.setHeader("Content-Type",
			        "application/x-www-form-urlencoded; charset=utf-8");
			// rb.setHeader("Accept-Language", "app.getLocaleStr()");

			String postData = getPostData().toString();
			doUploadWorksheet(rb, postData, pba);
		} catch (Exception e) {
			statusLabelSetText(loc.getPlain("UploadError",
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

	/**
	 * Shows a small dialog with a progress bar.
	 */
	@Override
	protected void showDialog() {
		Log.debug("Unimplemented");
	}

	@Override
	protected void statusLabelSetText(String plain) {
		ToolTipManagerW.sharedInstance().showBottomMessage(plain, true,
				(AppW) app);
	}

	@Override
	protected void pack() {
		Log.debug("Unimplemented");
	}

	/**
	 * Hides progress dialog.
	 */
	@Override
	public void hideDialog() {
		Log.debug("Unimplemented");
	}

	@Override
	protected String getBase64Tools(ArrayList<Macro> macros) throws IOException {
		return ((GgbAPIW) app.getGgbApi()).getMacrosBase64();
	}

}
