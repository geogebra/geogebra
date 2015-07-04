package org.geogebra.web.html5.util;

import java.util.HashMap;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolNavigation;
import org.geogebra.common.main.App;
import org.geogebra.common.move.ggtapi.models.AjaxCallback;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GgbAPIW;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;

public class View {

	private HashMap<String, String> archiveContent;

	private Element container;
	private AppW app;

	public View(Element container, AppW app) {
		this.app = app;
		this.container = container;
	}

	public static String checkLAF() {
		NodeList<Element> nodes = Dom
		        .getElementsByClassName(GeoGebraConstants.GGM_CLASS_NAME);
		for (int i = 0; i < nodes.getLength(); i++) {
			if ("modern"
			        .equals(nodes.getItem(i).getAttribute("data-param-laf"))) {
				return "modern";
			}
			if ("smart".equals(nodes.getItem(i).getAttribute("data-param-laf"))) {
				return "smart";
			}
		}
		return "standard";
	}

	public Element getContainer() {
		return container;
	}

	public void promptUserForGgbFile() {
		App.debug("No data-param-fileName attribute presented");
	}

	public void showError(String errorMessage) {
		App.debug(errorMessage);
	}

	public void hide() {
		App.debug("View.hide called");
	}

	public void showLoadAnimation() {
		app.showLoadingAnimation(true);
	}

	public String getDataParamFileName() {
		return ((ArticleElement) container).getDataParamFileName();
	}

	public String getDataParamJSON() {
		return ((ArticleElement) container).getDataParamJSON();
	}

	public String getDataParamBase64String() {
		return ((ArticleElement) container).getDataParamBase64String();
	}

	public String getDataParamLanguage() {
		return ((ArticleElement) container).getDataParamLanguage();
	}

	public String getDataParamCountry() {
		return ((ArticleElement) container).getDataParamCountry();
	}

	public boolean getDataParamUseBrowserForJS() {
		return ((ArticleElement) container).getDataParamUseBrowserForJS();
	}

	public boolean getDataParamEnableLabelDrags() {
		return ((ArticleElement) container).getDataParamEnableLabelDrags();
	}

	public boolean getDataParamEnableRightClick() {
		return ((ArticleElement) container).getDataParamEnableRightClick();
	}

	public boolean getDataParamShowMenuBar(boolean def) {
		return ((ArticleElement) container).getDataParamShowMenuBar(def);
	}

	public boolean getDataParamShowAlgebraInput(boolean def) {
		return ((ArticleElement) container).getDataParamShowAlgebraInput(def);
	}

	public boolean getDataParamShowToolBar(boolean def) {
		return ((ArticleElement) container).getDataParamShowToolBar(def);
	}

	public boolean getDataParamShowToolBarHelp() {
		// return ((ArticleElement) container).getDataParamShowToolBarHelp();
		return false;
	}

	public boolean getDataParamShiftDragZoomEnabled() {
		return ((ArticleElement) container).getDataParamShiftDragZoomEnabled();
	}

	public boolean getDataParamShowResetIcon() {
		return ((ArticleElement) container).getDataParamShowResetIcon();
	}

	public boolean getDataParamShowAnimationButton() {
		return ((ArticleElement) container).getDataParamShowAnimationButton();
	}

	public int getDataParamCapturingThreshold() {
		return ((ArticleElement) container).getDataParamCapturingThreshold();
	}

	public boolean getDataParamAllowSymbolTable() {
		return ((ArticleElement) container).getDataParamAllowSymbolTable();
	}

	public String getDataParamPerspective() {
		return ((ArticleElement) container).getDataParamPerspective();
	}

	public boolean getDataParamAllowStyleBar() {
		return ((ArticleElement) container).getDataParamAllowStyleBar();
	}

	public ArticleElement getArticleElement() {
		return ((ArticleElement) container);
	}

	private native void log(Object ex)/*-{
		if ($wnd.console) {
			$wnd.console.log(ex);
		}
	}-*/;

	private void maybeLoadFile() {
		if (app == null || archiveContent == null) {
			return;
		}

		try {
			app.loadGgbFile(archiveContent);
			App.debug("loadggb finished" + System.currentTimeMillis());
		} catch (Throwable ex) {
			ex.printStackTrace();
			log(ex);
			return;
		}
		archiveContent = null;

		// app.getScriptManager().ggbOnInit(); //this line is moved from here
		// too,
		// it should load after the images are loaded

		App.debug("file loaded");
		// This is used also by touch where dialog manager is null
		app.notifyFileLoaded();

		// reiniting of navigation bar, to show the correct numbers on the label
		if (app.getGuiManager() != null && app.getUseFullGui()) {
			ConstructionProtocolNavigation cpNav = this.getApplication()
			        .getGuiManager()
			        .getConstructionProtocolNavigationIfExists();
			if (cpNav != null)
				cpNav.update();
		}
		App.debug("end unzipping" + System.currentTimeMillis());
	}

	public void maybeLoadFile(HashMap<String, String> archiveCont) {
		archiveContent = archiveCont;
		maybeLoadFile();
	}

	public AppW getApplication() {
		return app;
	}

	public void processBase64String(String dataParamBase64String) {
		String workerUrls = prepareFileReading();
		populateArchiveContent(dataParamBase64String, workerUrls, this, false);
	}

	private int zippedLength = 0;

	private void putIntoArchiveContent(String key, String value) {
		archiveContent.put(key, value);
		if (archiveContent.size() == zippedLength) {
			maybeLoadFile();
		}
	}

	private native void populateArchiveContent(Object dpb64str,
	        String workerUrls, View view, boolean binary) /*-{
	                                                      
	                                                      
	                                                      
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
			      tmp_arr[ac++] = String.fromCharCode(((c1 & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
			      i += 3;
		      }
	      }
	      
	      return tmp_arr.join('');
      }		
      
      var imageRegex = /\.(png|jpg|jpeg|gif|bmp)$/i;
      if (workerUrls === "false") {
      	$wnd.zip.useWebWorkers = false;
      } else {
      	$wnd.zip.workerScriptsPath = workerUrls;
      }
      
      var readerCallback = function(reader) {
	      reader.getEntries(function(entries) {
		      view.@org.geogebra.web.html5.util.View::zippedLength = entries.length;
		      for (var i = 0, l = entries.length; i < l; i++) {
			      (function(entry){	            		
			      var filename = entry.filename;
			      if (entry.filename.match(imageRegex)) {
				      @org.geogebra.common.main.App::debug(Ljava/lang/String;)(filename+" : image");
				      var filenameParts = filename.split(".");
				      entry.getData(new $wnd.zip.Data64URIWriter("image/"+filenameParts[filenameParts.length - 1]), function (data) {
				      view.@org.geogebra.web.html5.util.View::putIntoArchiveContent(Ljava/lang/String;Ljava/lang/String;)(filename,data);
				      });
			      } else {
				      @org.geogebra.common.main.App::debug(Ljava/lang/String;)(entry.filename+" : text");
				      if ($wnd.zip.useWebWorkers === false || (typeof $wnd.zip.forceDataURIWriter !== "undefined" && $wnd.zip.forceDataURIWriter === true)) {
					      @org.geogebra.common.main.App::debug(Ljava/lang/String;)("no worker of forced dataURIWriter");
					      entry.getData(new $wnd.zip.Data64URIWriter("text/plain"), function(data) {
					      var decoded = $wnd.atob(data.substr(data.indexOf(",")+1));
					      view.@org.geogebra.web.html5.util.View::putIntoArchiveContent(Ljava/lang/String;Ljava/lang/String;)(filename,decodeUTF8(decoded));
					      });
				      } else {
					      @org.geogebra.common.main.App::debug(Ljava/lang/String;)("worker");
					      entry.getData(new ASCIIWriter(), function(text) {
					      view.@org.geogebra.web.html5.util.View::putIntoArchiveContent(Ljava/lang/String;Ljava/lang/String;)(filename,decodeUTF8(text));
					      });
				      }
	      
	      		}
		      })(entries[i]);
		      } 
	      reader.close();
	      });
      };
      
      var errorCallback = function (error) {
      @org.geogebra.common.main.App::error(Ljava/lang/String;)(error);
      };
      
      if (binary) {
      	$wnd.zip.createReader(new $wnd.zip.BlobReader(dpb64str),readerCallback, errorCallback); 
      } else {
      	$wnd.zip.createReader(new $wnd.zip.Data64URIReader(dpb64str),readerCallback, errorCallback); 
      } 
      }-*/;

	public void processFileName(String url) {
		if (url.endsWith(".off")) {

			HttpRequestW request = new HttpRequestW();
			request.sendRequestPost(url, "", new AjaxCallback() {

				public void onSuccess(String response) {
					app.openOFF(response);

				}

				public void onError(String error) {
					// TODO Auto-generated method stub

				}
			});
			return;

		}
		if (url.endsWith(".csv")) {

			HttpRequestW request = new HttpRequestW();
			request.sendRequestPost(url, "", new AjaxCallback() {

				public void onSuccess(String response) {
					app.openCSV(response);

				}

				public void onError(String error) {
					// TODO Auto-generated method stub

				}
			});
			return;

		}
		String workerUrls = prepareFileReading();
		populateArchiveContentFromFile(url, workerUrls, this);
	}

	private native void populateArchiveContentFromFile(String url,
	        String workerUrls, View view) /*-{
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
	      	tmp_arr[ac++] = String.fromCharCode(((c1 & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
	      	i += 3;
	      }
      }
      
      return tmp_arr.join('');
      }		
      
      var imageRegex = /\.(png|jpg|jpeg|gif|bmp)$/i;
      if (workerUrls === "false") {
      	$wnd.zip.useWebWorkers = false;
      } else {
      	$wnd.zip.workerScriptsPath = workerUrls;
      }
      
      $wnd.zip.createReader(new $wnd.zip.HttpReader(url),function(reader) {
      reader.getEntries(function(entries) {
      view.@org.geogebra.web.html5.util.View::zippedLength = entries.length;
       for (var i = 0, l = entries.length; i < l; i++) {
       	(function(entry){
           	var filename = entry.filename;
               if (entry.filename.match(imageRegex)) {
                       @org.geogebra.common.main.App::debug(Ljava/lang/String;)(filename+" : image");
                       var filenameParts = filename.split(".");
                       entry.getData(new $wnd.zip.Data64URIWriter("image/"+filenameParts[filenameParts.length -1]), function (data) {
                           view.@org.geogebra.web.html5.util.View::putIntoArchiveContent(Ljava/lang/String;Ljava/lang/String;)(filename,data);
                       });
                   } else {
                       @org.geogebra.common.main.App::debug(Ljava/lang/String;)(entry.filename+" : text");
                       if ($wnd.zip.useWebWorkers === false || (typeof $wnd.zip.forceDataURIWriter !== "undefined" && $wnd.zip.forceDataURIWriter === true)) {
                           @org.geogebra.common.main.App::debug(Ljava/lang/String;)("no worker of forced dataURIWriter");
                           entry.getData(new $wnd.zip.Data64URIWriter("text/plain"), function(data) {
                   			var decoded = $wnd.atob(data.substr(data.indexOf(",")+1));
                             	view.@org.geogebra.web.html5.util.View::putIntoArchiveContent(Ljava/lang/String;Ljava/lang/String;)(filename,decodeUTF8(decoded));
                            });
                       } else {
                       	@org.geogebra.common.main.App::debug(Ljava/lang/String;)("worker");
                       	entry.getData(new ASCIIWriter(), function(text) {
                             	view.@org.geogebra.web.html5.util.View::putIntoArchiveContent(Ljava/lang/String;Ljava/lang/String;)(filename,decodeUTF8(text));
                            });
                       }
                       	
               	}
       	})(entries[i]);
       } 
       reader.close();
      });
      },
      function (error) {
      @org.geogebra.common.main.App::error(Ljava/lang/String;)(error);
      });
      }-*/;

	/**
	 * @param binary
	 *            string (zipped GGB)
	 */
	public void processBinaryString(JavaScriptObject binary) {
		String workerUrls = prepareFileReading();
		populateArchiveContent(binary, workerUrls, this, true);

	}

	private String prepareFileReading() {
		archiveContent = new HashMap<String, String>();
		String workerUrls = GgbAPIW.zipJSworkerURL();
		App.debug("start unzipping" + System.currentTimeMillis());
		return workerUrls;
	}

	private void prepare(int t) {
		archiveContent = new HashMap<String, String>();
		this.zippedLength = t;
	}

	public boolean getDataParamApp() {
		return ((ArticleElement) container).getDataParamApp();
	}

	public native void processJSON(String encoded) /*-{

		var content = JSON.parse(encoded).archive;
		this.@org.geogebra.web.html5.util.View::prepare(I)(content.length);
		for (var k = 0; k < content.length; k++) {
			this.@org.geogebra.web.html5.util.View::putIntoArchiveContent(Ljava/lang/String;Ljava/lang/String;)(content[k].fileName,content[k].fileContent);
		}

	}-*/;

	public void adjustScale() {
		((ArticleElement) this.container).adjustScale();
	}

	public String getDataParamTubeID() {
		return ((ArticleElement) this.container).getDataParamTubeID();
	}
}
