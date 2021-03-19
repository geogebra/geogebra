package org.geogebra.web.html5.util;

import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolNavigation;
import org.geogebra.common.move.ggtapi.models.AjaxCallback;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GgbAPIW;
import org.geogebra.web.html5.main.GgbFile;
import org.gwtproject.timer.client.Timer;

import com.google.gwt.core.client.JavaScriptObject;

import elemental2.core.Global;
import elemental2.core.JsArray;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * Processes file input
 */
public class ViewW {

	private GgbFile archiveContent;
	private int zippedLength = 0;

	private AppW app;

	/**
	 * @param app
	 *            application
	 */
	public ViewW(AppW app) {
		this.app = app;
	}

	private void maybeLoadFile() {
		if (app == null || archiveContent == null) {
			return;
		}

		try {
			Log.debug("loadggb started" + System.currentTimeMillis());
			app.loadGgbFile(archiveContent, false);
			Log.debug("loadggb finished" + System.currentTimeMillis());
		} catch (Throwable ex) {
			Log.debug(ex);
			return;
		}
		archiveContent = null;

		// app.getScriptManager().ggbOnInit(); //this line is moved from here
		// too,
		// it should load after the images are loaded

		Log.debug("file loaded");

		// reiniting of navigation bar, to show the correct numbers on the label
		if (app.getGuiManager() != null && app.getUseFullGui()) {
			ConstructionProtocolNavigation cpNav = this.getApplication()
					.getGuiManager()
					.getCPNavigationIfExists();
			if (cpNav != null) {
				cpNav.update();
			}
		}
		Log.debug("end unzipping" + System.currentTimeMillis());
	}

	/**
	 * Load file if it's not null.
	 * 
	 * @param archiveCont
	 *            file to load
	 */
	public void maybeLoadFile(GgbFile archiveCont) {
		archiveContent = archiveCont;
		maybeLoadFile();
	}

	/**
	 * @return application
	 */
	protected AppW getApplication() {
		return app;
	}

	/**
	 * @param base64String
	 *            base64 encoded file
	 */
	public void processBase64String(String base64String) {
		populateArchiveContent(base64String.substring(base64String.indexOf(',') + 1));
	}

	private void populateArchiveContent(JavaScriptObject ggbReader) {
		String workerUrls = prepareFileReading();
		GgbAPIW.setWorkerURL(workerUrls, false);
	}

	private void populateArchiveContent(String base64String) {
		archiveContent = new GgbFile();
		FFlate.get().unzip(Base64.base64ToBytes(base64String), (err, data) -> {
			data.forEach(name -> {
				if (name.matches("^.*\\.(png|jpg|jpeg|gif|bmp|tif|tiff)$")) {
					archiveContent.put(name, "data:image/png;base64," + Base64.bytesToBase64(data.get(name)));
				} else {
					archiveContent.put(name, FFlate.get().strFromU8(data.get(name)));
				}
			});

			maybeLoadFile();
		});
	}

	/**
	 * Handle file loading error
	 * 
	 * @param msg
	 *            error message
	 */
	public void onError(String msg) {
		Log.error(msg);
		// eg 403
		if ((msg + "").startsWith("Error 40")) {
			this.app.getScriptManager().ggbOnInit();
			ToolTipManagerW.sharedInstance().showBottomMessage(
					app.getLocalization().getMenu("FileLoadingError"), false,
					app);
		}
	}

	/**
	 * Open file as off / csv / ggb.
	 * 
	 * @param url
	 *            file URL
	 */
	public void processFileName(String url) {
		if (url.endsWith(".off")) {

			HttpRequestW request = new HttpRequestW();
			request.sendRequestPost("GET", url, null, new AjaxCallback() {

				@Override
				public void onSuccess(String response) {
					getApplication().openOFF(response);
				}

				@Override
				public void onError(String error) {
					Log.error("Problem opening file:" + error);
				}
			});
			return;
		}
		if (url.endsWith(".csv")) {

			HttpRequestW request = new HttpRequestW();
			request.sendRequestPost("GET", url, null, new AjaxCallback() {

				@Override
				public void onSuccess(String response) {
					getApplication().openCSV(response);
				}

				@Override
				public void onError(String error) {
					Log.error("Problem opening file:" + error);
				}
			});
			return;
		}

		populateArchiveContent(getHTTPReader(url));
	}

	private native JavaScriptObject getHTTPReader(String url)/*-{
		return new $wnd.zip.HttpReader(url);
	}-*/;

	/**
	 * @param binary
	 *            string (zipped GGB)
	 */
	public void processBinaryString(JavaScriptObject binary) {
		populateArchiveContent(getBinaryReader(binary));
	}

	private native JavaScriptObject getBinaryReader(Object blob) /*-{
		return new $wnd.zip.BlobReader(blob);
	}-*/;

	private native JavaScriptObject getBase64Reader(String base64str)/*-{
		return new $wnd.zip.Data64URIReader(base64str);
	}-*/;

	private String prepareFileReading() {
		archiveContent = new GgbFile();
		String workerUrls = GgbAPIW.zipJSworkerURL();
		Log.debug("start unzipping" + System.currentTimeMillis());
		return workerUrls;
	}

	/**
	 * @param encoded
	 *            JSON encoded ZIP file (zip.js format)
	 */
	public void processJSON(String encoded) {
		processJSON(Global.JSON.parse(encoded));
	}

	public void setFileFromJsonString(String encoded, GgbFile file) {
		setFileFromJson(Js.uncheckedCast(Global.JSON.parse(encoded)), file);
	}

	private void setFileFromJson(JsPropertyMap<Object> json, GgbFile file) {
		if (json.has("archive")) {
			JsArray<JsPropertyMap<String>> content = Js.uncheckedCast(json.get("archive"));
			for (int i = 0; i < content.length; i++) {
				JsPropertyMap<String> entry = content.getAt(i);
				file.put(entry.get("fileName"), entry.get("fileContent"));
			}
		}
	}

	/**
	 * @param zip
	 *            JS object representing the ZIP file, see getFileJSON in GgbAPI
	 */
	public void processJSON(Object zip) {
		new Timer() {
			@Override
			public  void run() {
				archiveContent = new GgbFile();
				setFileFromJson(Js.uncheckedCast(zip), archiveContent);
				maybeLoadFile();
			}
		}.schedule(0);
	}

}
