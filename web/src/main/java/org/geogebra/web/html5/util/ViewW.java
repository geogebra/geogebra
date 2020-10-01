package org.geogebra.web.html5.util;

import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolNavigation;
import org.geogebra.common.move.ggtapi.models.AjaxCallback;
import org.geogebra.common.util.ExternalAccess;
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
	 * @param dataParamBase64String
	 *            base64 encoded file
	 */
	public void processBase64String(String dataParamBase64String) {
		populateArchiveContent(getBase64Reader(dataParamBase64String));
	}

	@ExternalAccess
	protected void putIntoArchiveContent(String key, String value) {
		archiveContent.put(key, value);
		if (archiveContent.size() == zippedLength) {
			maybeLoadFile();
		}
	}

	private void populateArchiveContent(JavaScriptObject ggbReader) {
		String workerUrls = prepareFileReading();
		GgbAPIW.setWorkerURL(workerUrls, false);
		populateArchiveContent(workerUrls, this, ggbReader);
	}

	private native void populateArchiveContent(String workerUrls, ViewW view,
			JavaScriptObject ggbReader) /*-{
      // Writer for ASCII strings
      function ASCIIWriter() {
	      var that = this, data;
	      
	      function init(callback, onerror) {
		      data = "";
		      callback();
	      }
	      
	      function writeUint8Array(array, callback, onerror) {
		      var i;
		      for (i = 0; i < array.length; i++) {
		      	data += $wnd.String.fromCharCode(array[i]);
		      }
		      callback();
	      }
	      
	      function getData(callback) {		
	      	callback(data);
	      }
	      
	      that.init = init;
	      that.writeUint8Array = writeUint8Array;
	      that.getData = getData;
      }
      ASCIIWriter.prototype = new $wnd.zip.Writer();
      ASCIIWriter.prototype.constructor = ASCIIWriter;
      
      function decodeUTF8(str_data) {
	      var tmp_arr = [], i = 0, ac = 0, c1 = 0, c2 = 0, c3 = 0;
	      
	      str_data += '';
	      
	      while (i < str_data.length) {
		      c1 = str_data.charCodeAt(i);
		      if (c1 < 128) {
			      tmp_arr[ac++] = String.fromCharCode(c1);
			      i++;
		      } else if (c1 > 191 && c1 < 224) {
			      c2 = str_data.charCodeAt(i + 1);
			      tmp_arr[ac++] = String.fromCharCode(((c1 & 31) << 6) | (c2 & 63));
			      i += 2;
		      } else {
			      c2 = str_data.charCodeAt(i + 1);
			      c3 = str_data.charCodeAt(i + 2);
			      tmp_arr[ac++] = String.fromCharCode(
			          ((c1 & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
			      i += 3;
		      }
	      }
	      
	      return tmp_arr.join('');
      }		
      
      // see GGB-63
      var imageRegex = /\.(png|jpg|jpeg|gif|bmp|tif|tiff)$/i;    
      
      var readerCallback = function(reader) {
	      reader.getEntries(function(entries) {
		      view.@org.geogebra.web.html5.util.ViewW::zippedLength = entries.length;
		      for (var i = 0, l = entries.length; i < l; i++) {
			      (function(entry){	            		
			      var filename = entry.filename;
			      if (entry.filename.match(imageRegex)) {
				      @org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)(filename+" : image");
				      var filenameParts = filename.split(".");
				      var filenameSimple = filenameParts[filenameParts.length - 1];
				      var dataWriter = new $wnd.zip.Data64URIWriter("image/" + filenameSimple);
				      entry.getData(dataWriter, function (data) {
				      view.@org.geogebra.web.html5.util.ViewW::putIntoArchiveContent(Ljava/lang/String;Ljava/lang/String;)(filename,data);
				      });
			      } else {
				      @org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)(entry.filename+" : text");
				      var forceDataURI = typeof $wnd.zip.forceDataURIWriter !== "undefined" 
				          && $wnd.zip.forceDataURIWriter === true;
				      if ($wnd.zip.useWebWorkers === false || forceDataURI) {
					      @org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("no worker of forced dataURIWriter");
					      entry.getData(new $wnd.zip.Data64URIWriter("text/plain"), function(data) {
					      var decoded = $wnd.atob(data.substr(data.indexOf(",")+1));
					      view.@org.geogebra.web.html5.util.ViewW::putIntoArchiveContent(Ljava/lang/String;Ljava/lang/String;)(filename,decodeUTF8(decoded));
					      });
				      } else {
					      @org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("worker");
					      entry.getData(new ASCIIWriter(), function(text) {
					      view.@org.geogebra.web.html5.util.ViewW::putIntoArchiveContent(Ljava/lang/String;Ljava/lang/String;)(filename,decodeUTF8(text));
					      });
				      }
	      
	      		}
		      })(entries[i]);
		      } 
	     // reader.close();
	      });
      };
      
      var errorCallback = function (error) {
      	view.@org.geogebra.web.html5.util.ViewW::onError(Ljava/lang/String;)(error);
      };
      
      $wnd.zip.createReader(ggbReader,readerCallback, errorCallback);
       
    }-*/;

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
