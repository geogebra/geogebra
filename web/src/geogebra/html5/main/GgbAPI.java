package geogebra.html5.main;

import geogebra.common.io.MyXMLio;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Macro;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.main.App;
import geogebra.html5.euclidian.EuclidianViewWeb;
import geogebra.html5.gawt.BufferedImage;
import geogebra.html5.util.View;
import geogebra.web.gui.app.GeoGebraAppFrame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeSet;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.RootPanel;

public class GgbAPI  extends geogebra.common.plugin.GgbAPI {

	public GgbAPI(App app) {
		this.app = app;
		this.kernel = app.getKernel();
		this.algebraprocessor=kernel.getAlgebraProcessor();
        this.construction=kernel.getConstruction();
	}

    public byte[] getGGBfile() {
	    // TODO Auto-generated method stub
	    return null;
    }

    public Context2d getContext2D() {
	    return ((AppWeb)app).getCanvas().getContext2d();
    }

	
    public void setBase64(String base64) {
    	if(GeoGebraAppFrame.fileLoader.getView()==null){
    		View view = new View(RootPanel.getBodyElement(), (AppWeb) app);
    		GeoGebraAppFrame.fileLoader.setView(view);
    	}
	    GeoGebraAppFrame.fileLoader.process(base64);
	    
    }

	
    public void setErrorDialogsActive(boolean flag) {
	    // TODO Auto-generated method stub
	    
    }

    public void reset() {
    	app.reset();
    }

    public void refreshViews() {
    	app.refreshViews();
    }


    public String getIPAddress() {
	    // TODO Auto-generated method stub
	    return null;
    }

	
    public String getHostname() {
	    // TODO Auto-generated method stub
	    return null;
    }

	
    public void openFile(String strURL) {
	    // TODO Auto-generated method stub
	    
    }

	
    public String getGraphicsViewCheckSum(String algorithm, String format) {
	    // TODO Auto-generated method stub
	    return null;
    }

	
    public boolean writePNGtoFile(String filename, double exportScale,
            boolean transparent, double DPI) {
	    // TODO Auto-generated method stub
	    return false;
    }

	
    public String getPNGBase64(double exportScale, boolean transparent,
            double DPI) {
	    // TODO Auto-generated method stub
	    return null;
    }

	
    public void drawToImage(String label, double[] x, double[] y) {
	    // TODO Auto-generated method stub
	    
    }
    
    public void tubeSearch(String phrase){
    	((AppWeb)app).tubeSearch(phrase);
    }


    public void clearImage(String label) {
		GeoElement ge = kernel.lookupLabel(label);
		
		if(!ge.isGeoImage()){
			debug("Bad drawToImage arguments");
			return;
		}
		((GeoImage)ge).clearFillImage();
    }
    
    
    HashMap<String, String> archiveContent = null;

    /**
     * This method does something like geogebra.io.MyXMLio.writeGeoGebraFile,
     * but it is a non callback version. Use callbacked version instead.
     * just in base64 in Web.
     */
    @Override
    public String getBase64(boolean includeThumbnail) {
    	createArchiveContent(includeThumbnail);
    	
    	JavaScriptObject callback = getDummyCallback();
    	
    	getNativeBase64ZipJs(prepareToEntrySet(archiveContent), callback,GWT.getModuleName());
    	return "wait for callback";
    }
    
    private native JavaScriptObject getDownloadGGBCallback(Element downloadButton) /*-{
		return function(ggbZip){
			var URL = $wnd.URL || $wnd.webkitURL;
			var ggburl = URL.createObjectURL(ggbZip);
			//downloadButton = document.getElementById('downloadButton')
			downloadButton.setAttribute("href", ggburl);
			//downloadButton.disabled = false;
			}
    }-*/;
    
    public void getGGB(boolean includeThumbnail, Element downloadButton) {
    	createArchiveContent(includeThumbnail);
    	
    	JavaScriptObject callback = getDownloadGGBCallback(downloadButton); 	
    	getGGBZipJs(prepareToEntrySet(archiveContent), callback, GWT.getModuleName());

    }
    
    
    public void getBase64(boolean includeThumbnail, JavaScriptObject callback) {
		createArchiveContent(includeThumbnail);
		
		getNativeBase64ZipJs(prepareToEntrySet(archiveContent), callback, GWT.getModuleName());
    }

    public void getBase64(JavaScriptObject callback) {
    	createArchiveContent(false);

		getNativeBase64ZipJs(prepareToEntrySet(archiveContent), callback, GWT.getModuleName());

    }

	private void createArchiveContent(boolean includeThumbnail) {
	    archiveContent = new HashMap<String, String>();
    	boolean issaving = getKernel().isSaving();
    	//return getNativeBase64(includeThumbnail);
    	getKernel().setSaving(true);
    	adjustConstructionImages(getConstruction(),"");
    	String constructionXml = getApplication().getXML();
    	String macroXml = getApplication().getMacroXMLorEmpty();
    	String geogebra_javascript = getKernel().getLibraryJavaScript();
    	String geogebra_python = getKernel().getLibraryPythonScript();

    	writeConstructionImages(getConstruction(),"");


		// write construction thumbnails
    	if (includeThumbnail)
    		addImageToZip(MyXMLio.XML_FILE_THUMBNAIL,
    			((EuclidianViewWeb)app.getEuclidianView1()).getCanvasBase64WithTypeString());


    	if (!macroXml.equals("")) {
    		writeMacroImages();
    		archiveContent.put(MyXMLio.XML_FILE_MACRO, macroXml);
    	}

    	archiveContent.put(MyXMLio.JAVASCRIPT_FILE, geogebra_javascript);

    	if (!geogebra_python.equals("")) {
    		archiveContent.put(MyXMLio.PYTHON_FILE, geogebra_python);
    	}

    	archiveContent.put(MyXMLio.XML_FILE, constructionXml);
    }

    private native JavaScriptObject getDummyCallback() /*-{
	   return function() {
	   		@geogebra.common.main.App::debug(Ljava/lang/String;)("This is a dummy callback from geogebra.web.main.ggbApi.getBase64(); try the callbacked version instead");
	   };
    }-*/;

	private JavaScriptObject prepareToEntrySet(HashMap<String, String> archive) {
    	JavaScriptObject nativeEntry = JavaScriptObject.createObject();
    	
    	if (archive.entrySet() != null) {
			for (Entry<String, String> entry : archive.entrySet()) {
				pushIntoNativeEntry(entry.getKey(), entry.getValue(),nativeEntry);
			}
		}
    	return nativeEntry;
    }
    
    public native void pushIntoNativeEntry(String key, String value, JavaScriptObject ne) /*-{
    	if (typeof ne["archive"] === "undefined") { //needed because gwt gives an __objectId key :-(
    		ne["archive"] = [];
    	}
    	var obj = {};
    	obj.fileName = key;
    	obj.fileContent = value;
    	ne["archive"].push(obj);
    }-*/;

	public native void getGGBZipJs(JavaScriptObject arch, JavaScriptObject clb,String module) /*-{

		$wnd.zip.workerScriptsPath = module + "/js/zipjs/";

		function encodeUTF8(string) {
			var n, c1, enc, utftext = [], start = 0, end = 0, stringl = string.length;
			for (n = 0; n < stringl; n++) {
				c1 = string.charCodeAt(n);
				enc = null;
				if (c1 < 128)
					end++;
				else if (c1 > 127 && c1 < 2048)
					enc = String.fromCharCode((c1 >> 6) | 192) + String.fromCharCode((c1 & 63) | 128);
				else
					enc = String.fromCharCode((c1 >> 12) | 224) + String.fromCharCode(((c1 >> 6) & 63) | 128) + String.fromCharCode((c1 & 63) | 128);
				if (enc != null) {
					if (end > start)
						utftext += string.slice(start, end);
					utftext += enc;
					start = end = n + 1;
				}
			}
			if (end > start)
				utftext += string.slice(start, stringl);
			return utftext;
		}
		
		function ASCIIReader(text) {
			var that = this;
		
			function init(callback, onerror) {
				that.size = text.length;
				callback();
			}
		
			function readUint8Array(index, length, callback, onerror) {
				if (text.length <= index) {
					return new $wnd.Uint8Array(0);
				} else if (index < 0) {
					return new $wnd.Uint8Array(0);
				} else if (length <= 0) {
					return new $wnd.Uint8Array(0);
				} else if (text.length < index + length) {
					length = text.length - index;
				}
				var i, data = new $wnd.Uint8Array(length);
				for (i = index; i < index + length; i++)
					data[i - index] = text.charCodeAt(i);
				callback(data);
			}

			that.size = 0;
			that.init = init;
			that.readUint8Array = readUint8Array;
		}
		ASCIIReader.prototype = new $wnd.zip.Reader();
		ASCIIReader.prototype.constructor = ASCIIReader;

		$wnd.zip.createWriter(new $wnd.zip.BlobWriter(), function(zipWriter) {
			
			function addImage(name, data, callback) {
				var data2 = data.substr(data.indexOf(',')+1);
				zipWriter.add(name, new $wnd.zip.Data64URIReader(data2), callback);
			}

			function addText(name, data, callback) {
				@geogebra.common.main.App::debug(Ljava/lang/String;)(name);
				zipWriter.add(name, new ASCIIReader(data), callback);
			}

			function checkIfStillFilesToAdd() {
				var item,
					imgExtensions = ["jpg", "jpeg", "png", "gif", "bmp"];
				if (arch.archive.length > 0) {
					@geogebra.common.main.App::debug(Ljava/lang/String;)("arch.archive.length: "+arch.archive.length);
					item = arch.archive.shift();
					var ind = item.fileName.lastIndexOf('.');
					if (ind > -1 && imgExtensions.indexOf(item.fileName.substr(ind+1).toLowerCase()) > -1) {
					//if (item.fileName.indexOf(".png") > -1) 
							@geogebra.common.main.App::debug(Ljava/lang/String;)("image zipped" + item.fileName);
							addImage(item.fileName,item.fileContent,function(){checkIfStillFilesToAdd();});
					} else {
							@geogebra.common.main.App::debug(Ljava/lang/String;)("text zipped");
							addText(item.fileName,encodeUTF8(item.fileContent),function(){checkIfStillFilesToAdd();});
					}
				} else {
					zipWriter.close(function(dataURI) {
							if (typeof clb === "function") {
								clb(dataURI);
								// that's right, this truncation is necessary
								//clb(dataURI.substr(dataURI.indexOf(',')+1));
							} else {
								@geogebra.common.main.App::debug(Ljava/lang/String;)("not callback was given");
								@geogebra.common.main.App::debug(Ljava/lang/String;)(dataURI);
							}
					});
				}
			}
			
			 checkIfStillFilesToAdd();
			
		}, function(error) {
			@geogebra.common.main.App::debug(Ljava/lang/String;)("error occured while creating ggb zip");
		});              


	 }-*/;


    
	private native void getNativeBase64ZipJs(JavaScriptObject arch, JavaScriptObject clb,String module) /*-{

		$wnd.zip.workerScriptsPath = module + "/js/zipjs/";

		function encodeUTF8(string) {
			var n, c1, enc, utftext = [], start = 0, end = 0, stringl = string.length;
			for (n = 0; n < stringl; n++) {
				c1 = string.charCodeAt(n);
				enc = null;
				if (c1 < 128)
					end++;
				else if (c1 > 127 && c1 < 2048)
					enc = String.fromCharCode((c1 >> 6) | 192) + String.fromCharCode((c1 & 63) | 128);
				else
					enc = String.fromCharCode((c1 >> 12) | 224) + String.fromCharCode(((c1 >> 6) & 63) | 128) + String.fromCharCode((c1 & 63) | 128);
				if (enc != null) {
					if (end > start)
						utftext += string.slice(start, end);
					utftext += enc;
					start = end = n + 1;
				}
			}
			if (end > start)
				utftext += string.slice(start, stringl);
			return utftext;
		}
		
		function ASCIIReader(text) {
			var that = this;
		
			function init(callback, onerror) {
				that.size = text.length;
				callback();
			}
		
			function readUint8Array(index, length, callback, onerror) {
				if (text.length <= index) {
					return new $wnd.Uint8Array(0);
				} else if (index < 0) {
					return new $wnd.Uint8Array(0);
				} else if (length <= 0) {
					return new $wnd.Uint8Array(0);
				} else if (text.length < index + length) {
					length = text.length - index;
				}
				var i, data = new $wnd.Uint8Array(length);
				for (i = index; i < index + length; i++)
					data[i - index] = text.charCodeAt(i);
				callback(data);
			}

			that.size = 0;
			that.init = init;
			that.readUint8Array = readUint8Array;
		}
		ASCIIReader.prototype = new $wnd.zip.Reader();
		ASCIIReader.prototype.constructor = ASCIIReader;
		
		//$wnd.zip.useWebWorkers = false;
		$wnd.zip.createWriter(new $wnd.zip.Data64URIWriter("application/vnd.geogebra.file"), function(zipWriter) {
			function addImage(name, data, callback) {
				var data2 = data.substr(data.indexOf(',')+1);
				zipWriter.add(name, new $wnd.zip.Data64URIReader(data2), callback);
			}

			function addText(name, data, callback) {
				@geogebra.common.main.App::debug(Ljava/lang/String;)(name);
				zipWriter.add(name, new ASCIIReader(data), callback);
			}

			function checkIfStillFilesToAdd() {
				var item,
					imgExtensions = ["jpg", "jpeg", "png", "gif", "bmp"];
				if (arch.archive.length > 0) {
					item = arch.archive.shift();
					var ind = item.fileName.lastIndexOf('.');
					@geogebra.common.main.App::debug(Ljava/lang/String;)(item.fileName);
					if (ind > -1 && imgExtensions.indexOf(item.fileName.substr(ind+1).toLowerCase()) > -1) {
					//if (item.fileName.indexOf(".png") > -1) {
							@geogebra.common.main.App::debug(Ljava/lang/String;)("image zipped" + item.fileName);
							addImage(item.fileName,item.fileContent,function(){checkIfStillFilesToAdd();});
					} else {
							@geogebra.common.main.App::debug(Ljava/lang/String;)("text zipped");
							addText(item.fileName,encodeUTF8(item.fileContent),function(){checkIfStillFilesToAdd();});
					}
				} else {
					zipWriter.close(function(dataURI) {
							if (typeof clb === "function") {
								// that's right, this truncation is necessary
								clb(dataURI.substr(dataURI.indexOf(',')+1));
							} else {
								@geogebra.common.main.App::debug(Ljava/lang/String;)("not callback was given");
								@geogebra.common.main.App::debug(Ljava/lang/String;)(dataURI);
							}
					});
				}
			}
			
			 checkIfStillFilesToAdd();
			
		}, function(error) {
			@geogebra.common.main.App::debug(Ljava/lang/String;)("error occured while creating base64 zip");
		});
    }-*/;

	private void writeMacroImages() {
		if (kernel.hasMacros()) {
			ArrayList<Macro> macros = kernel.getAllMacros();
			writeMacroImages(macros, "");
		}
	}

	private void writeMacroImages(ArrayList<Macro> macros, String filePath) {
		if (macros == null)
			return;

		for (int i = 0; i < macros.size(); i++) {
			// save all images in macro construction
			Macro macro = macros.get(i);
			writeConstructionImages(macro.getMacroConstruction(), filePath);

			/*
			// save macro icon
			String fileName = macro.getIconFileName();
			BufferedImage img = ((Application)app).getExternalImage(fileName);
			if (img != null)
				// Modified for Intergeo File Format (Yves Kreis) -->
				// writeImageToZip(zip, fileName, img);
				writeImageToZip(zipjs, filePath + fileName, img);
			// <-- Modified for Intergeo File Format (Yves Kreis)
			*/
		}
	}
	
	
	private void adjustConstructionImages(Construction cons, String filePath) {
		// save all GeoImage images
		//TreeSet images = cons.getGeoSetLabelOrder(GeoElement.GEO_CLASS_IMAGE);
		TreeSet<GeoElement> geos = cons.getGeoSetLabelOrder();
		if (geos == null)
			return;

		Iterator<GeoElement> it = geos.iterator();
		while (it.hasNext()) {
			GeoElement geo =  it.next();
			// Michael Borcherds 2007-12-10 this line put back (not needed now
			// MD5 code put in the correct place!)
			String fileName = geo.getImageFileName();
			if (fileName != null) {
					geo.getGraphicsAdapter().convertToSaveableFormat();
			}
		}
	}
	
	
    private void writeConstructionImages(Construction cons, String filePath) {
		// save all GeoImage images
		//TreeSet images = cons.getGeoSetLabelOrder(GeoElement.GEO_CLASS_IMAGE);
		TreeSet<GeoElement> geos = cons.getGeoSetLabelOrder();
		if (geos == null)
			return;

		Iterator<GeoElement> it = geos.iterator();
		while (it.hasNext()) {
			GeoElement geo =  it.next();
			// Michael Borcherds 2007-12-10 this line put back (not needed now
			// MD5 code put in the correct place!)
			String fileName = geo.getImageFileName();
			if (fileName != null) {

				BufferedImage img = geogebra.html5.awt.GBufferedImageW.getGawtImage(geo.getFillImage());
				if (img != null && img.getImageElement() != null) {
					Canvas cv = Canvas.createIfSupported();
					cv.setCoordinateSpaceWidth(img.getWidth());
					cv.setCoordinateSpaceHeight(img.getHeight());
					Context2d c2d = cv.getContext2d();
					c2d.drawImage(img.getImageElement(),0,0);
					String ext = fileName.substring(fileName.lastIndexOf('.')+1).toLowerCase();
					// Opera and Safari cannot toDataUrl jpeg (much less the others)
					//if (ext.equals("jpg") || ext.equals("jpeg"))
					//	addImageToZip(filePath + fileName, cv.toDataUrl("image/jpg"));
					//else
					if (ext.equals("png"))
						addImageToZip(filePath + fileName, cv.toDataUrl("image/png"));
					else
						addImageToZip(filePath + fileName.substring(0,fileName.lastIndexOf('.')) + ".png", cv.toDataUrl("image/png"));

				}
			}
		}
    }

    private void addImageToZip(String filename, String base64img) {
    	archiveContent.put(filename, base64img);
    }
    
    /*
     * waitForResult = false not implemented in web
     * (non-Javadoc)
     * @see geogebra.common.plugin.JavaScriptAPI#evalCommand(java.lang.String, boolean)
     */
	public synchronized boolean evalCommand(final String cmdString, boolean waitForResult) {
			return evalCommand(cmdString);
	}


}
