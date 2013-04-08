package geogebra.touch.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeSet;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import geogebra.common.io.MyXMLio;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Macro;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.plugin.GgbAPI;
import geogebra.touch.TouchApp;
import geogebra.web.kernel.gawt.BufferedImage;

public class GgbAPITouch extends GgbAPI
{
	private HashMap<String, String> archiveContent;

	public GgbAPITouch(TouchApp app)
	{
		this.app = app;
		this.kernel = app.getKernel();
		this.construction = this.kernel.getConstruction();
	}

	public void getGGB(boolean includeThumbnail, Element downloadButton)
	{
		createArchiveContent(includeThumbnail);

		JavaScriptObject callback = getDownloadGGBCallback(downloadButton);
		getGGBZipJs(prepareToEntrySet(this.archiveContent), includeThumbnail, callback);

	}

	private void createArchiveContent(boolean includeThumbnail)
	{
		// includeThumbnail not used yet

		this.archiveContent = new HashMap<String, String>();

		// boolean issaving = getKernel().isSaving();
		// return getNativeBase64(includeThumbnail);
		// getKernel().setSaving(true);

		String constructionXml = getApplication().getXML();
		String macroXml = getApplication().getMacroXMLorEmpty();
		String geogebra_javascript = getKernel().getLibraryJavaScript();
		// String geogebra_python = getKernel().getLibraryPythonScript();

		// writeConstructionImages(getConstruction(), "");

		// write construction thumbnails
		// if (includeThumbnail)
		// {
		// addImageToZip(MyXMLio.XML_FILE_THUMBNAIL, ((EuclidianViewM)
		// super.app.getEuclidianView1()).getCanvasBase64WithTypeString());
		// }

		if (!macroXml.equals(""))
		{
			writeMacroImages();
			this.archiveContent.put(MyXMLio.XML_FILE_MACRO, macroXml);
		}

		this.archiveContent.put(MyXMLio.JAVASCRIPT_FILE, geogebra_javascript);

		// if (!geogebra_python.equals(""))
		// {
		// this.archiveContent.put(MyXMLio.PYTHON_FILE, geogebra_python);
		// }

		this.archiveContent.put(MyXMLio.XML_FILE, constructionXml);
	}

	private native JavaScriptObject getDownloadGGBCallback(Element downloadButton) /*-{
		return function(ggbZip) {
			var URL = $wnd.URL || $wnd.webkitURL;
			var ggburl = URL.createObjectURL(ggbZip);
			downloadButton.setAttribute("href", ggburl);
		}
	}-*/;

	private JavaScriptObject prepareToEntrySet(HashMap<String, String> archive)
	{
		JavaScriptObject nativeEntry = JavaScriptObject.createObject();

		if (archive.entrySet() != null)
		{
			for (Entry<String, String> entry : archive.entrySet())
			{
				pushIntoNativeEntry(entry.getKey(), entry.getValue(), nativeEntry);
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

	private void writeMacroImages()
	{
		if (this.kernel.hasMacros())
		{
			ArrayList<Macro> macros = this.kernel.getAllMacros();
			writeMacroImages(macros, "");
		}
	}

	private void writeMacroImages(ArrayList<Macro> macros, String filePath)
	{
		if (macros == null)
			return;

		for (int i = 0; i < macros.size(); i++)
		{
			// save all images in macro construction
			Macro macro = macros.get(i);
			writeConstructionImages(macro.getMacroConstruction(), filePath);

			/*
			 * // save macro icon String fileName = macro.getIconFileName();
			 * BufferedImage img = ((Application)app).getExternalImage(fileName); if
			 * (img != null) // Modified for Intergeo File Format (Yves Kreis) --> //
			 * writeImageToZip(zip, fileName, img); writeImageToZip(zipjs, filePath +
			 * fileName, img); // <-- Modified for Intergeo File Format (Yves Kreis)
			 */
		}
	}

	private void writeConstructionImages(Construction cons, String filePath)
	{
		// save all GeoImage images
		// TreeSet images = cons.getGeoSetLabelOrder(GeoElement.GEO_CLASS_IMAGE);
		TreeSet<GeoElement> geos = cons.getGeoSetLabelOrder();
		if (geos == null)
		{
			return;
		}

		Iterator<GeoElement> it = geos.iterator();
		while (it.hasNext())
		{
			GeoElement geo = it.next();
			// Michael Borcherds 2007-12-10 this line put back (not needed now
			// MD5 code put in the correct place!)
			String fileName = geo.getImageFileName();
			if (fileName != null)
			{
				BufferedImage img = geogebra.web.awt.GBufferedImageW.getGawtImage(geo.getFillImage());
				if (img != null && img.getImageElement() != null)
				{
					Canvas cv = Canvas.createIfSupported();
					cv.setCoordinateSpaceWidth(img.getWidth());
					cv.setCoordinateSpaceHeight(img.getHeight());
					Context2d c2d = cv.getContext2d();
					c2d.drawImage(img.getImageElement(), 0, 0);
					String ext = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
					// Opera and Safari cannot toDataUrl jpeg (much less the others)
					// if (ext.equals("jpg") || ext.equals("jpeg"))
					// addImageToZip(filePath + fileName, cv.toDataUrl("image/jpg"));
					// else
					if (ext.equals("png"))
						addImageToZip(filePath + fileName, cv.toDataUrl("image/png"));
					else
						addImageToZip(filePath + fileName.substring(0, fileName.lastIndexOf('.')) + ".png", cv.toDataUrl("image/png"));

				}
			}
		}
	}

	private void addImageToZip(String filename, String base64img)
	{
		this.archiveContent.put(filename, base64img);
	}

	public native void getGGBZipJs(JavaScriptObject arch, boolean includeThumbnail, JavaScriptObject clb) /*-{

		$wnd.zip.workerScriptsPath = "touch/js/zipjs/";

		function encodeUTF8(string) {
			var n, c1, enc, utftext = [], start = 0, end = 0, stringl = string.length;
			for (n = 0; n < stringl; n++) {
				c1 = string.charCodeAt(n);
				enc = null;
				if (c1 < 128)
					end++;
				else if (c1 > 127 && c1 < 2048)
					enc = String.fromCharCode((c1 >> 6) | 192)
							+ String.fromCharCode((c1 & 63) | 128);
				else
					enc = String.fromCharCode((c1 >> 12) | 224)
							+ String.fromCharCode(((c1 >> 6) & 63) | 128)
							+ String.fromCharCode((c1 & 63) | 128);
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

		$wnd.zip
				.createWriter(
						new $wnd.zip.BlobWriter(),
						function(zipWriter) {

							function addImage(name, data, callback) {
								var data2 = data.substr(data.indexOf(',') + 1);
								zipWriter.add(name,
										new $wnd.zip.Data64URIReader(data2),
										callback);
							}

							function addText(name, data, callback) {
								//								@geogebra.common.main.App::debug(Ljava/lang/String;)(name);
								zipWriter.add(name, new ASCIIReader(data),
										callback);
							}

							function checkIfStillFilesToAdd() {
								var item, imgExtensions = [ "jpg", "jpeg",
										"png", "gif", "bmp" ];

								if (arch.archive.length > 0) {
									//									@geogebra.common.main.App::debug(Ljava/lang/String;)("arch.archive.length: "+arch.archive.length);
									item = arch.archive.shift();
									var ind = item.fileName.lastIndexOf('.');
									if (ind > -1
											&& imgExtensions
													.indexOf(item.fileName
															.substr(ind + 1)
															.toLowerCase()) > -1) {
										//if (item.fileName.indexOf(".png") > -1) 
										//										@geogebra.common.main.App::debug(Ljava/lang/String;)("image zipped" + item.fileName);
										addImage(item.fileName,
												item.fileContent, function() {
													checkIfStillFilesToAdd();
												});

									} else {
										//										@geogebra.common.main.App::debug(Ljava/lang/String;)("text zipped");
										addText(item.fileName,
												encodeUTF8(item.fileContent),
												function() {
													checkIfStillFilesToAdd();
												});
									}
								} else {
									zipWriter.close(function(dataURI) {
										if (typeof clb === "function") {
											clb(dataURI);
											// that's right, this truncation is necessary
											//clb(dataURI.substr(dataURI.indexOf(',')+1));
										} else {
											//													@geogebra.common.main.App::debug(Ljava/lang/String;)("no callback was given");
											//													@geogebra.common.main.App::debug(Ljava/lang/String;)(dataURI);
										}
									});
								}
							}
							checkIfStillFilesToAdd();

						}, function(error) {
							alert("error");
							//							@geogebra.common.main.App::debug(Ljava/lang/String;)("error occured while creating ggb zip");
						});

	}-*/;

	@Override
	public byte[] getGGBfile()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setBase64(String base64)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean evalCommand(String cmdString, boolean waitForResult)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setErrorDialogsActive(boolean flag)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void reset()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void refreshViews()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public String getIPAddress()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getHostname()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void openFile(String strURL)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public String getGraphicsViewCheckSum(String algorithm, String format)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean writePNGtoFile(String filename, double exportScale, boolean transparent, double DPI)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getPNGBase64(double exportScale, boolean transparent, double DPI)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void drawToImage(String label, double[] x, double[] y)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void clearImage(String label)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public String getBase64(boolean includeThumbnail)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
