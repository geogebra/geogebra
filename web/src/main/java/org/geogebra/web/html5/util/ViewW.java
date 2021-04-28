package org.geogebra.web.html5.util;

import java.util.Locale;

import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolNavigation;
import org.geogebra.common.move.ggtapi.models.AjaxCallback;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GgbFile;
import org.gwtproject.timer.client.Timer;

import elemental2.core.ArrayBuffer;
import elemental2.core.Global;
import elemental2.core.JsArray;
import elemental2.core.Uint8Array;
import elemental2.dom.XMLHttpRequest;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * Processes file input
 */
public class ViewW {

	private final AppW app;

	/**
	 * @param app
	 *            application
	 */
	public ViewW(AppW app) {
		this.app = app;
	}

	/**
	 * Load file if it's not null.
	 *
	 * @param archiveContent
	 *            file to load
	 */
	public void maybeLoadFile(GgbFile archiveContent) {
		if (app == null || archiveContent == null) {
			return;
		}

		try {
			long t = System.currentTimeMillis();
			app.loadGgbFile(archiveContent, false);
			Log.debug("GGB file loaded in " + (System.currentTimeMillis() - t) + "ms");
		} catch (Throwable ex) {
			Log.debug(ex);
			return;
		}

		// reiniting of navigation bar, to show the correct numbers on the label
		if (app.getGuiManager() != null && app.getUseFullGui()) {
			ConstructionProtocolNavigation cpNav = this.getApplication()
					.getGuiManager()
					.getCPNavigationIfExists();
			if (cpNav != null) {
				cpNav.update();
			}
		}
	}

	/**
	 * @return application
	 */
	protected AppW getApplication() {
		return app;
	}

	/**
	 * @param base64String
	 *            base64 encoded, zipped GGB file
	 */
	public void processBase64String(String base64String) {
		String suffix = base64String.substring(base64String.indexOf(',') + 1).trim();
		Uint8Array binaryData = Base64.base64ToBytes(suffix);
		populateArchiveContent(binaryData);
	}

	/**
	 * @param binary
	 *            raw zipped GGB file
	 */
	public void processBinaryData(ArrayBuffer binary) {
		populateArchiveContent(new Uint8Array(binary));
	}

	private void populateArchiveContent(Uint8Array binaryData) {
		long t = System.currentTimeMillis();

		FFlate.get().unzip(binaryData, (err, data) -> {
			GgbFile archiveContent = new GgbFile();
			data.forEach(name -> {
				int dotIndex = name.lastIndexOf('.');
				String extension = dotIndex == -1
						? "" : name.substring(dotIndex + 1).toLowerCase(Locale.US);

				if (extension.matches("(png|jpg|jpeg|gif|bmp|tif|tiff)")) {
					String prefix = "data:image/" + extension + ";base64,";
					archiveContent.put(name, prefix + Base64.bytesToBase64(data.get(name)));
				} else {
					archiveContent.put(name, FFlate.get().strFromU8(data.get(name)));
				}
			});

			Log.debug("GGB file uzipped and post-processed in "
					+ (System.currentTimeMillis() - t) + "ms");

			maybeLoadFile(archiveContent);
		});
	}

	/**
	 * Open file as off / csv / ggb.
	 * 
	 * @param url
	 *            file URL
	 */
	public void processFileName(String url) {
		if (url.endsWith(".off") || url.endsWith(".csv")) {
			HttpRequestW request = new HttpRequestW();
			request.sendRequestPost("GET", url, null, new AjaxCallback() {

				@Override
				public void onSuccess(String response) {
					if (url.endsWith(".off")) {
						getApplication().openOFF(response);
					} else if (url.endsWith(".csv")) {
						getApplication().openCSV(response);
					}
				}

				@Override
				public void onError(String error) {
					Log.error("Problem opening file:" + error);
				}
			});
		} else {
			XMLHttpRequest request = new XMLHttpRequest();
			request.open("GET", url);
			request.responseType = "arraybuffer";

			XMLHttpRequest.OnerrorFn onError = (e) -> {
				Log.error(request.statusText + ":" + request.statusText);
				app.afterLoadFileAppOrNot(false);
				ToolTipManagerW.sharedInstance().showBottomMessage(
						app.getLocalization().getMenu("FileLoadingError"),
						false, app);
				return null;
			};

			request.onload = (e) -> {
				if (request.readyState == 4) {
					if (request.status == 200) {
						processBinaryData(Js.uncheckedCast(request.response.asJsObject()));
					} else {
						onError.onInvoke(e);
					}
				}
			};

			request.onerror = onError;
			request.send();
		}
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
				GgbFile archiveContent = new GgbFile();
				setFileFromJson(Js.uncheckedCast(zip), archiveContent);
				maybeLoadFile(archiveContent);
			}
		}.schedule(0);
	}

}
