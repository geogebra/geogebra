package geogebra.web.html5;

import geogebra.common.main.App;
import geogebra.web.Web;
import geogebra.web.main.AppW;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

public class View extends Widget {
	
	private HashMap<String, String> archiveContent;
	
	private Element container;
	private AppW app;
	
	public View(Element container, AppW app) {
		this.app = app;
		this.container = container;
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
	
	public boolean getDataParamShowMenuBar() {
		return ((ArticleElement) container).getDataParamShowMenuBar();
    }

	public boolean getDataParamShowAlgebraInput() {
		return ((ArticleElement) container).getDataParamShowAlgebraInput();
    }

	public boolean getDataParamShowToolBar() {
		return ((ArticleElement) container).getDataParamShowToolBar();
    }

	public boolean getDataParamShowToolBarHelp() {
		//return ((ArticleElement) container).getDataParamShowToolBarHelp();
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
	
	private void maybeLoadFile() {
		if (app == null || archiveContent == null) {
			return;
		}

		try {
			app.loadGgbFile(archiveContent);
		} catch (Exception ex) {
			AppW.debug(ex.getMessage());
			return;
		}
		archiveContent = null;

		app.getScriptManager().ggbOnInit();// put this here from Application constructor because we have to delay scripts until the EuclidianView is shown
		
		App.debug("file loaded");
		
	}

	public void maybeLoadFile(HashMap<String, String> archiveCont) {
	    archiveContent = archiveCont;
	    maybeLoadFile();
    }

	public AppW getApplication() {
	    return app;
    }

	public void processBase64String(String dataParamBase64String) {
		archiveContent = new HashMap<String, String>();
		String workerUrls = (!Web.webWorkerSupported ? "false" : GWT.getModuleBaseURL()+"js/zipjs/");
		populateArchiveContent(dataParamBase64String, workerUrls,this);
    }
	
	private int zippedLength = 0;
	
	private void putIntoArciveContent(String key, String value) {
		archiveContent.put(key, value);
		//Application.console(key+" : "+value);
		if (archiveContent.size() == zippedLength) {
			maybeLoadFile();
		}
	}

	private native void populateArchiveContent(String dpb64str, String workerUrls, View view) /*-{
		
		
		
		// Writer for ASCII strings
		function ASCIIWriter() {
			var that = this, data;
		
			function init(callback, onerror) {
				data = "";
				callback();
			}
		
			function writeUint8Array(array, callback, onerror) {
				var i;
				for (i = 0; i < array.length; i++)
					data += $wnd.String.fromCharCode(array[i]);
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
	    
	    $wnd.zip.createReader(new $wnd.zip.Data64URIReader(dpb64str),function(reader) {
	        reader.getEntries(function(entries) {
	        	view.@geogebra.web.html5.View::zippedLength = entries.length;
	            for (var i = 0, l = entries.length; i < l; i++) {
	            	(function(entry){
		            	var filename = entry.filename;
		                if (entry.filename.match(imageRegex)) {
		                        @geogebra.common.main.App::debug(Ljava/lang/String;)(filename+" : image");
		                        entry.getData(new $wnd.zip.Data64URIWriter("image/"+filename.split(".")[1]), function (data) {
		                            view.@geogebra.web.html5.View::putIntoArciveContent(Ljava/lang/String;Ljava/lang/String;)(filename,data);
		                        });
		                    } else {
		                        @geogebra.common.main.App::debug(Ljava/lang/String;)(entry.filename+" : text");
		                        if ($wnd.zip.useWebWorkers === false || (typeof $wnd.zip.forceDataURIWriter !== "undefined" && $wnd.zip.forceDataURIWriter === true)) {
		                        	@geogebra.web.main.AppW::debug(Ljava/lang/String;)("no worker of forced dataURIWriter");
			                        entry.getData(new $wnd.zip.Data64URIWriter("text/plain"), function(data) {
			                			var decoded = $wnd.atob(data.substr(data.indexOf(",")+1));
			                          	view.@geogebra.web.html5.View::putIntoArciveContent(Ljava/lang/String;Ljava/lang/String;)(filename,decodeUTF8(decoded));
			                         });
		                        } else {
		                        	@geogebra.common.main.App::debug(Ljava/lang/String;)("worker");
		                        	entry.getData(new ASCIIWriter(), function(text) {
			                          	view.@geogebra.web.html5.View::putIntoArciveContent(Ljava/lang/String;Ljava/lang/String;)(filename,decodeUTF8(text));
			                         });
		                        }
		                        	
		                	}
	            	})(entries[i]);
	            } 
	            reader.close();
	        });
	    },
	    function (error) {
	    	@geogebra.common.main.App::error(Ljava/lang/String;)(error);
	    });
    }-*/;

	public void processFileName(String url) {
		archiveContent = new HashMap<String, String>();
		String workerUrls = (!Web.webWorkerSupported ? "false" : GWT.getModuleBaseURL()+"js/zipjs/");
	    populateArchiveContentFromFile(url, workerUrls, this);
    }

	private native void populateArchiveContentFromFile(String url, String workerUrls,
            View view) /*-{
		// Writer for ASCII strings
				function ASCIIWriter() {
					var that = this, data;
				
					function init(callback, onerror) {
						data = "";
						callback();
					}
				
					function writeUint8Array(array, callback, onerror) {
						var i;
						for (i = 0; i < array.length; i++)
							data += $wnd.String.fromCharCode(array[i]);
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
			        	view.@geogebra.web.html5.View::zippedLength = entries.length;
			            for (var i = 0, l = entries.length; i < l; i++) {
			            	(function(entry){
				            	var filename = entry.filename;
				                if (entry.filename.match(imageRegex)) {
				                        @geogebra.common.main.App::debug(Ljava/lang/String;)(filename+" : image");
				                        entry.getData(new $wnd.zip.Data64URIWriter("image/"+filename.split(".")[1]), function (data) {
				                            view.@geogebra.web.html5.View::putIntoArciveContent(Ljava/lang/String;Ljava/lang/String;)(filename,data);
				                        });
				                    } else {
				                        @geogebra.common.main.App::debug(Ljava/lang/String;)(entry.filename+" : text");
				                        if ($wnd.zip.useWebWorkers === false || (typeof $wnd.zip.forceDataURIWriter !== "undefined" && $wnd.zip.forceDataURIWriter === true)) {
				                            @geogebra.web.main.AppW::debug(Ljava/lang/String;)("no worker of forced dataURIWriter");
					                        entry.getData(new $wnd.zip.Data64URIWriter("text/plain"), function(data) {
					                			var decoded = $wnd.atob(data.substr(data.indexOf(",")+1));
					                          	view.@geogebra.web.html5.View::putIntoArciveContent(Ljava/lang/String;Ljava/lang/String;)(filename,decodeUTF8(decoded));
					                         });
				                        } else {
				                        	@geogebra.common.main.App::debug(Ljava/lang/String;)("worker");
				                        	entry.getData(new ASCIIWriter(), function(text) {
					                          	view.@geogebra.web.html5.View::putIntoArciveContent(Ljava/lang/String;Ljava/lang/String;)(filename,decodeUTF8(text));
					                         });
				                        }
				                        	
				                	}
			            	})(entries[i]);
			            } 
			            reader.close();
			        });
			    },
			    function (error) {
			    	@geogebra.common.main.App::error(Ljava/lang/String;)(error);
			    });
    }-*/;




}
