package org.geogebra.web.html5.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.io.MyXMLio;
import org.geogebra.common.io.file.Base64ZipFile;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.CommandNotLoadedError;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.ExportType;
import org.geogebra.common.main.App.InputPosition;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.OpenFileListener;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.GgbAPI;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.js.ResourcesInjector;
import org.geogebra.web.html5.multiuser.MultiuserManager;
import org.geogebra.web.html5.util.AnimationExporter;
import org.geogebra.web.html5.util.FileConsumer;
import org.geogebra.web.html5.util.ImageManagerW;
import org.geogebra.web.html5.util.JsRunnable;
import org.geogebra.web.html5.util.StringConsumer;
import org.geogebra.web.html5.util.ViewW;
import org.geogebra.web.resources.JavaScriptInjector;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Element;

import elemental2.core.Global;
import elemental2.promise.Promise.PromiseExecutorCallbackFn.RejectCallbackFn;
import elemental2.promise.Promise.PromiseExecutorCallbackFn.ResolveCallbackFn;
import jsinterop.base.JsPropertyMap;

/**
 * HTML5 version of API. The methods are exported in ScriptManagerW
 *
 */
public class GgbAPIW extends GgbAPI {
	private MathEditorAPI editor;

	/**
	 * @param app
	 *            application
	 */
	public GgbAPIW(App app) {
		this.app = app;
		this.kernel = app.getKernel();
		this.algebraprocessor = kernel.getAlgebraProcessor();
		this.construction = kernel.getConstruction();
	}

	/**
	 * Register equation editor for the get/setEditorState methods.
	 * 
	 * @param editor
	 *            equation editor API
	 */
	public void setEditor(MathEditorAPI editor) {
		this.editor = editor;
	}

	@Override
	public byte[] getGGBfile() {
		throw new IllegalArgumentException(
				"In HTML5 getGGBfile needs at least 1 argument");
	}

	@Override
	public void setBase64(String base64) {
		// resetPerspective();
		// ViewW view = new ViewW((AppW) app);
		// view.processBase64String(base64);

		// needed to reset max used layer etc
		app.clearConstruction();

		app.loadXML(new Base64ZipFile(base64));
	}

	private void resetPerspective() {
		((AppW) app).resetPerspectiveParam();
	}

	/**
	 * @param base64
	 *            base64 encoded file
	 * @param callback
	 *            callback when file loaded
	 */
	public void setBase64(String base64, final JsRunnable callback) {
		if (callback != null) {
			OpenFileListener listener = () -> {
				JsEval.callNativeFunction(callback);
				return true;
			};
			app.registerOpenFileListener(listener);
		}
		setBase64(base64);
	}

	/**
	 * @param filename
	 *            file URL
	 * @param callback
	 *            callback when file loaded
	 */
	public void openFile(String filename, final JsRunnable callback) {
		if (callback != null) {
			OpenFileListener listener = () -> {
				callback.run();
				return true;
			};
			app.registerOpenFileListener(listener);
		}
		openFile(filename);
	}

	@Override
	public void setErrorDialogsActive(boolean flag) {
		app.setErrorDialogsActive(flag);
	}

	@Override
	public void refreshViews() {
		app.refreshViews();
	}

	@Override
	public void openFile(String filename) {
		resetPerspective();
		ViewW view = ((AppW) app).getViewW();
		view.processFileName(filename);
	}

	/**
	 * 
	 * @param exportScale
	 *            scale
	 * @param transparent
	 *            whether to use transparent background
	 * @param dpi
	 *            dots per inch eg. for paste to Word
	 * @return png as String with "data:image/png;base64," header
	 */
	private String getPNG(double exportScale, boolean transparent, double dpi,
			boolean greyscale) {
		String url;

		EuclidianViewWInterface ev = ((EuclidianViewWInterface) app
				.getActiveEuclidianView());

		// get export image
		// DPI ignored
		url = ((EuclidianViewWInterface) app.getActiveEuclidianView())
				.getExportImageDataUrl(exportScale, transparent, greyscale);

		if (MyDouble.isFinite(dpi) && dpi > 0 && ev instanceof EuclidianViewW) {

			JavaScriptInjector
					.inject(GuiResourcesSimple.INSTANCE.rewritePHYS());

			url = addDPI(url, dpi);

		}

		return url;
	}

	@Override
	public boolean writePNGtoFile(String filename, double exportScale,
			boolean transparent, double dpi, boolean greyscale) {
		// make browser save/download PNG file
		Browser.exportImage(getPNG(exportScale, transparent, dpi, greyscale),
				filename);
		return true;
	}

	@Override
	public String getPNGBase64(double exportScale, boolean transparent,
			double dpi, boolean copyToClipboard, boolean greyscale) {
		if (app.getGuiManager() != null) {
			app.getGuiManager().getLayout().getDockManager().ensureFocus();

			if (app.getGuiManager().getLayout().getDockManager()
					.getFocusedViewId() == App.VIEW_PROBABILITY_CALCULATOR) {
				return pngBase64(((EuclidianViewWInterface) app.getGuiManager()
						.getPlotPanelEuclidanView()).getExportImageDataUrl(
								exportScale, transparent, greyscale));
			}
		}
		String ret = pngBase64(getPNG(exportScale, transparent, dpi, greyscale));

		if (copyToClipboard) {
			app.copyImageToClipboard(StringUtil.pngMarker + ret);
		}

		return ret;
	}

	private static String pngBase64(String pngURL) {
		return pngURL.substring(StringUtil.pngMarker.length());
	}

	/**
	 * @param label
	 *            object label
	 * @param value
	 *            whether to use value string
	 * @return base64 encoded PNG of LaTeX formula
	 */
	public String getLaTeXBase64(String label, boolean value) {
		Canvas c = Canvas.createIfSupported();
		GeoElement geo = kernel.lookupLabel(label);
		if (geo == null) {
			return "";
		}
		String str;
		if (value) {
			str = geo.toValueString(StringTemplate.latexTemplate);
		} else {
			str = geo instanceof GeoCasCell
					? ((GeoCasCell) geo)
							.getLaTeXInput(StringTemplate.latexTemplate)
					: geo.toString(StringTemplate.latexTemplate);
		}
		DrawEquationW.paintOnCanvasOutput(geo, str, c, app.getFontSizeWeb());
		return c.toDataUrl().substring(StringUtil.pngMarker.length());
	}

	/**
	 * @param includeThumbnail
	 *            whether to include thumbnail
	 * @param callback
	 *            handler for the file
	 */
	public void getGGBfile(final boolean includeThumbnail,
			final FileConsumer callback) {
		final boolean oldWorkers = setWorkerURL(zipJSworkerURL(), false);
		final JsPropertyMap<Object> arch = getFileJSON(includeThumbnail);
		getGGBZipJs(arch, callback,
				() -> {
					if (oldWorkers && !isUsingWebWorkers()) {
						Log.warn(
								"Saving with workers failed, trying without workers.");
						ResourcesInjector.loadCodecs();
						getGGBZipJs(arch, callback, null);
					}
				});
	}

	/**
	 * @return URL of zip worker directory
	 */
	public static String zipJSworkerURL() {
		// FIXME disabled workers in Touch for now
		if ("tablet".equals(GWT.getModuleName())
				|| "tabletWin".equals(GWT.getModuleName())) {
			return "false";
		}
		return Browser.webWorkerSupported()
				? GWT.getModuleBaseURL() + "js/zipjs/" : "false";
	}

	/**
	 * @param includeThumbnail
	 *            whether to include thumbnail
	 * @param callback
	 *            callback
	 */
	public void getBase64(boolean includeThumbnail, StringConsumer callback) {
		getBase64ZipJs(getFileJSON(includeThumbnail), callback,
				zipJSworkerURL(), false);
	}

	/**
	 * Base64 for ggt file
	 * 
	 * @param includeThumbnail
	 *            whether to add thumbnail
	 * @param callback
	 *            callback
	 */
	public void getMacrosBase64(boolean includeThumbnail,
			StringConsumer callback) {
		GgbFile archiveContent = createMacrosArchive();
		JsPropertyMap<Object> jso = JsPropertyMap.of();
		getBase64ZipJs(prepareToEntrySet(archiveContent, jso, "", null),
				callback, zipJSworkerURL(), false);
	}

	/**
	 * @param includeThumbnail
	 *            whether to include thumbnail
	 * @return native JS object representing the archive
	 */
	public JsPropertyMap<Object> getFileJSON(boolean includeThumbnail) {
		JsPropertyMap<Object> jso = JsPropertyMap.of();
		PageListControllerInterface pageController = ((AppW) app)
				.getPageController();
		if (pageController != null) {
			HashMap<String, Integer> usage = new HashMap<>();
			GgbFile shared = new GgbFile("");
			for (int i = 0; i < pageController.getSlideCount(); i++) {
				pageController.refreshSlide(i);
				countShared(pageController.getSlide(i), usage, shared);
			}
			for (int i = 0; i < pageController.getSlideCount(); i++) {
				GgbFile f = pageController.getSlide(i);
				prepareToEntrySet(f, jso, GgbFile.SLIDE_PREFIX + i + "/",
						usage);
			}
			prepareToEntrySet(shared, jso, GgbFile.SHARED_PREFIX, null);
			pushIntoNativeEntry(GgbFile.STRUCTURE_JSON,
					pageController.getStructureJSON(), jso);
			return jso;
		}
		GgbFile archiveContent = new GgbFile("");
		createArchiveContent(includeThumbnail, archiveContent);
		return prepareToEntrySet(archiveContent, jso, "", null);
	}

	private static void countShared(GgbFile slide,
			HashMap<String, Integer> usage,
			GgbFile shared) {
		for (Entry<String, String> entry : slide.entrySet()) {
			String filename = entry.getKey();
			if (filename.contains("/")) {
				Integer currentUsage = usage.get(filename);
				if (currentUsage != null) {
					usage.put(filename, currentUsage + 1);
					shared.put(filename, entry.getValue());
				} else {
					usage.put(filename, 1);
				}
			}
		}
	}

	/**
	 * Load construction and images from JSON
	 * 
	 * @param obj
	 *            JSON archive
	 */
	public void setFileJSON(Object obj) {
		resetPerspective();
		ViewW view = ((AppW) app).getViewW();
		view.processJSON(obj);
	}

	/**
	 * @param file GeoGebra file
	 * @return JSON representation
	 */
	public String toJson(GgbFile file) {
		JsPropertyMap<Object> jso = prepareToEntrySet(file,
				JsPropertyMap.of(), "", null);
		return Global.JSON.stringify(jso);
	}

	private static final class StoreString implements StringConsumer {
		private String result = "";

		protected StoreString() {

		}

		@Override
		public void consume(String s) {
			this.result = s;
		}

		public String getResult() {
			return result;
		}
	}

	@Override
	public String getBase64(boolean includeThumbnail) {
		StoreString storeString = new StoreString();
		JsPropertyMap<Object> jso = getFileJSON(includeThumbnail);
		if (Browser.webWorkerSupported()) {
			ResourcesInjector.loadCodecs();
		}
		getBase64ZipJs(jso, storeString, "false", true);
		return storeString.getResult();

	}

	/**
	 * @return base64 for ggt file
	 */
	public String getMacrosBase64() {
		StoreString storeString = new StoreString();
		GgbFile archiveContent = createMacrosArchive();
		JsPropertyMap<Object> jso = prepareToEntrySet(archiveContent,
				JsPropertyMap.of(), "", null);
		if (Browser.webWorkerSupported()) {
			ResourcesInjector.loadCodecs();
		}
		getBase64ZipJs(jso, storeString, "false", true);
		return storeString.getResult();
	}

	/**
	 * @param includeThumbnail
	 *            whether to include thumbnail
	 * @param callback
	 *            callback
	 */
	public void getBase64(boolean includeThumbnail,
			AsyncOperation<String> callback) {
		getBase64(includeThumbnail, (StringConsumer) callback::callback);
	}

	private native String addDPI(String base64, double dpi) /*-{
		var pngHeader = "data:image/png;base64,";

		if (base64.startsWith(pngHeader)) {
			base64 = base64.substr(pngHeader.length);
		}

		// convert dots per inch into dots per metre
		var pixelsPerM = dpi * 100 / 2.54;

		//console.log("base64 = " + base64);

		// encode PNG as Uint8Array
		var binary_string = $wnd.atob(base64);
		var len = binary_string.length;
		//console.log("len = " + len);
		var bytes = new Uint8Array(len);
		for (var i = 0; i < len; i++) {
			bytes[i] = binary_string.charCodeAt(i);
		}

		// change / add pHYs chunk
		// pixels per metre
		var ppm = Math.round(dpi / 2.54 * 100);

		var b64encoded;

		if (base64.length > 100000) {
			// slower but works with large images
			b64encoded = $wnd.rewrite_pHYs_chunk(bytes, ppm, ppm, true);
		} else {
			// faster but not good for large images eg 4000 x 4000
			bytes = $wnd.rewrite_pHYs_chunk(bytes, ppm, ppm, false);
			b64encoded = btoa(String.fromCharCode.apply(null, bytes));
		}
		return 'data:image/png;base64,' + b64encoded;

	}-*/;

	/**
	 * @param includeThumbnail
	 *            whether to include thumbnail
	 * @param archiveContent
	 *            zip archive
	 * @return zip archive (as a map)
	 */
	public GgbFile createArchiveContent(boolean includeThumbnail,
			GgbFile archiveContent) {
		archiveContent.clear();
		final boolean isSaving = getKernel().isSaving();
		// return getNativeBase64(includeThumbnail);
		getKernel().setSaving(true);
		((ImageManagerW) app.getImageManager())
				.adjustConstructionImages(getConstruction());
		String constructionXml = getApplication().getXML();
		String macroXml = getApplication().getMacroXMLorEmpty();
		StringBuilder defaults2d = new StringBuilder();
		StringBuilder defaults3d = null;
		if (app.is3D()) {
			defaults3d = new StringBuilder();
		}
		getKernel().getConstruction().getConstructionDefaults()
				.getDefaultsXML(defaults2d, defaults3d);
		String geogebraJavascript = getKernel().getLibraryJavaScript();

		if (!"".equals(macroXml)) {
			writeMacroImages(archiveContent);
			archiveContent.put(MyXMLio.XML_FILE_MACRO, macroXml);
		}

		if (defaults2d.length() > 0) {
			archiveContent.put(MyXMLio.XML_FILE_DEFAULTS_2D,
					defaults2d.toString());
		}

		if (defaults3d != null && defaults3d.length() > 0) {
			archiveContent.put(MyXMLio.XML_FILE_DEFAULTS_3D,
					defaults3d.toString());
		}

		if (!StringUtil.emptyTrim(geogebraJavascript)) {
			archiveContent.put(MyXMLio.JAVASCRIPT_FILE, geogebraJavascript);
		}

		archiveContent.put(MyXMLio.XML_FILE, constructionXml);

		// GGB-1758 write images at the end
		((ImageManagerW) app.getImageManager())
				.writeConstructionImages(getConstruction(), "", archiveContent);
		EmbedManager embedManager = app.getEmbedManager();
		if (embedManager != null) {
			embedManager.writeEmbeds(getConstruction(),
					archiveContent);
		}
		// write construction thumbnails
		if (includeThumbnail) {
			ImageManagerW
					.addImageToZip(MyXMLio.XML_FILE_THUMBNAIL,
					((EuclidianViewWInterface) getViewForThumbnail())
							.getCanvasBase64WithTypeString(),
					archiveContent);
		}

		getKernel().setSaving(isSaving);
		return archiveContent;
	}

	/**
	 * @return base64 encoded thumbnail
	 */
	public String getThumbnailBase64() {
		return ((EuclidianViewWInterface) getViewForThumbnail())
				.getCanvasBase64WithTypeString()
				.substring(StringUtil.pngMarker.length());
	}

	/**
	 * @return view for thumbnail
	 */
	public EuclidianViewInterfaceCommon getViewForThumbnail() {
		EuclidianViewInterfaceCommon ret = getViewForThumbnail(true);
		if (ret == null) {
			ret = getViewForThumbnail(false);
		}
		if (ret == null) {
			ret = app.getActiveEuclidianView();
		}
		return ret;
	}

	private EuclidianViewInterfaceCommon getViewForThumbnail(
			boolean needsObjects) {
		if (app.isEuclidianView3Dinited() && app.showView(App.VIEW_EUCLIDIAN3D)
				&& (!needsObjects
						|| app.getEuclidianView3D().hasVisibleObjects())) {
			return app.getEuclidianView3D();
		}
		if (app.showView(App.VIEW_EUCLIDIAN) && (!needsObjects
				|| app.getEuclidianView1().hasVisibleObjects())) {
			return app.getEuclidianView1();
		}
		if (app.showView(App.VIEW_EUCLIDIAN2) && (!needsObjects
				|| app.getEuclidianView2(1).hasVisibleObjects())) {
			return app.getEuclidianView2(1);
		}
		if (app.showView(App.VIEW_PROBABILITY_CALCULATOR)) {
			return app.getGuiManager().getPlotPanelEuclidanView();
		}
		return null;
	}

	/**
	 * @return archive with macros + icons
	 */
	public GgbFile createMacrosArchive() {
		GgbFile archiveContent = new GgbFile("");
		writeMacroImages(archiveContent);
		String macroXml = getApplication().getMacroXMLorEmpty();
		if (!"".equals(macroXml)) {
			writeMacroImages(archiveContent);
			archiveContent.put(MyXMLio.XML_FILE_MACRO, macroXml);
		}
		return archiveContent;
	}

	private static JsPropertyMap<Object> prepareToEntrySet(GgbFile archive,
			JsPropertyMap<Object> nativeEntry, String prefix,
			HashMap<String, Integer> usage) {
		for (Entry<String, String> entry : archive.entrySet()) {
			if (usage == null || usage.get(entry.getKey()) == null
					|| usage.get(entry.getKey()) < 2) {
				pushIntoNativeEntry(prefix + entry.getKey(), entry.getValue(),
						nativeEntry);
			}
		}
		return nativeEntry;
	}

	private static native void pushIntoNativeEntry(String key, String value,
			JsPropertyMap<Object> ne) /*-{
		if (typeof ne["archive"] === "undefined") { //needed because gwt gives an __objectId key :-(
			ne["archive"] = [];
		}
		var obj = {};
		obj.fileName = key;
		obj.fileContent = value;
		ne["archive"].push(obj);
	}-*/;

	/**
	 * @param arch
	 *            archive
	 * @param clb
	 *            callback when zipped
	 * @param errorClb
	 *            callback for errors
	 */
	native void getGGBZipJs(JsPropertyMap<Object> arch, FileConsumer clb,
			JsRunnable errorClb) /*-{

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
								zipWriter.add(name, new ASCIIReader(data),
										callback);
							}

							function checkIfStillFilesToAdd() {
								var item, imgExtensions = [ "jpg", "jpeg",
										"png", "gif", "bmp" ];
								if (arch.archive.length > 0) {
									@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("arch.archive.length: "+arch.archive.length);
									item = arch.archive.shift();
									var ind = item.fileName.lastIndexOf('.');
									if (ind > -1
											&& imgExtensions
													.indexOf(item.fileName
															.substr(ind + 1)
															.toLowerCase()) > -1) {
										//if (item.fileName.indexOf(".png") > -1)
										//@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("image zipped: " + item.fileName);
										addImage(item.fileName,
												item.fileContent, function() {
													checkIfStillFilesToAdd();
												});
									} else {
										//@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("text zipped: " + item.fileName);
										addText(item.fileName,
												encodeUTF8(item.fileContent),
												function() {
													checkIfStillFilesToAdd();
												});
									}
								} else {
									zipWriter
											.close(function(dataURI) {
												if (typeof clb === "function") {
													clb(dataURI);
													// that's right, this truncation is necessary
													//clb(dataURI.substr(dataURI.indexOf(',')+1));
												} else {
													@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("not callback was given");
													@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)(dataURI);
												}
											});
								}
							}

							checkIfStillFilesToAdd();

						},
						function(error) {
							if (typeof errorClb === "function") {
								errorClb(error + "");
							}
							@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("error occured while creating ggb zip");
						});

	}-*/;

	/**
	 * @param arch
	 *            archive
	 * @param clb
	 *            callback for file loaded
	 * @param workerUrls
	 *            URL of webworker directory (or "false" to switch them off)
	 * @param sync
	 *            whether zip should run synchronously
	 */
	void getBase64ZipJs(final JsPropertyMap<Object> arch, final StringConsumer clb,
			String workerUrls, boolean sync) {
		final boolean oldWorkers = setWorkerURL(workerUrls, sync);
		getBase64ZipJs(arch, clb, s -> {
			if (oldWorkers && !isUsingWebWorkers()) {
				Log.warn(
						"Saving with workers failed, trying without workers.");
				ResourcesInjector.loadCodecs();
				getBase64ZipJs(arch, clb, "false", false);
			}

		});
	}

	private native void getBase64ZipJs(Object arch,
			StringConsumer clb, StringConsumer errorClb) /*-{

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

		//$wnd.zip.useWebWorkers = false;
		$wnd.zip
				.createWriter(
						new $wnd.zip.Data64URIWriter(
								"application/vnd.geogebra.file"),
						function(zipWriter) {
							function addImage(name, data, callback) {
								var data2 = data.substr(data.indexOf(',') + 1);
								zipWriter.add(name,
										new $wnd.zip.Data64URIReader(data2),
										callback);
							}

							function addText(name, data, callback) {
								zipWriter.add(name, new ASCIIReader(data),
										callback);
							}

							function checkIfStillFilesToAdd() {
								var item, imgExtensions = [ "jpg", "jpeg",
										"png", "gif", "bmp" ];
								if (arch.archive.length > 0) {
									item = arch.archive.shift();
									var ind = item.fileName.lastIndexOf('.');
									if (ind > -1
											&& imgExtensions
													.indexOf(item.fileName
															.substr(ind + 1)
															.toLowerCase()) > -1) {

										@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("image zipped: " + item.fileName);
										addImage(item.fileName,
												item.fileContent, function() {
													checkIfStillFilesToAdd();
												});
									} else {
										@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("text zipped: " + item.fileName);
										addText(item.fileName,
												encodeUTF8(item.fileContent),
												function() {
													checkIfStillFilesToAdd();
												});
									}
								} else {
									zipWriter
											.close(function(dataURI) {
												if (typeof clb === "function") {
													// that's right, this truncation is necessary
													clb(dataURI.substr(dataURI
															.indexOf(',') + 1));
												} else {
													@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("not callback was given");
													@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)(dataURI);
												}
											});
								}
							}

							checkIfStillFilesToAdd();

						},
						function(error) {
							@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("error occured while creating base64 zip");
							if (typeof errorClb === "function") {
								errorClb(error + "");
							}
						});
	}-*/;

	private void writeMacroImages(GgbFile archive) {
		if (kernel.hasMacros()) {
			ArrayList<Macro> macros = kernel.getAllMacros();
			((ImageManagerW) app.getImageManager()).writeMacroImages(macros,
					archive);
		}
	}

	/**
	 * @param material
	 *            material ID
	 */
	public void openMaterial(final String material) {
		((AppW) app).openMaterial(material,
				err -> Log.debug("Loading failed for id" + material + ": " + err));
	}

	/**
	 * @param width
	 *            setst the applet width
	 */
	public void setWidth(int width) {
		setArticleParam("width", width);
		((AppW) app).getAppletFrame().setWidth(width);
	}

	/**
	 * @param height
	 *            sets the applet height
	 */
	public void setHeight(int height) {
		setArticleParam("height", height);
		((AppW) app).getAppletFrame().setHeight(height);
	}

	/**
	 * @param width
	 *            height
	 * @param height
	 *            width
	 * 
	 *            Sets the size of the applet
	 */
	public void setSize(int width, int height) {
		setArticleParam("width", width);
		setArticleParam("height", height);
		((AppW) app).getAppletFrame().setSize(width, height);
	}

	private void setArticleParam(String name, int value) {
		((AppW) app).getAppletParameters().setAttribute(name, value + "");

	}

	/**
	 * @param show
	 * 
	 *            wheter show the toolbar in geogebra-web applets or not
	 */
	public void showToolBar(boolean show) {
		if (app.getGuiManager() != null) {
			((GuiManagerInterfaceW) app.getGuiManager()).showToolBar(show);
		}
	}

	/**
	 * @param show
	 * 
	 *            wheter show the menubar in geogebra-web applets or not
	 */
	public void showMenuBar(boolean show) {
		if (app.getGuiManager() != null) {
			((GuiManagerInterfaceW) app.getGuiManager()).showMenuBar(show);
		}
	}

	/**
	 * @param show
	 * 
	 *            whether show the algebrainput in geogebra-web applets or not
	 */
	public void showAlgebraInput(boolean show) {

		final AppW appW = (AppW) this.app;

		// from ViewMenuW
		appW.persistWidthAndHeight();

		appW.setShowAlgebraInput(show, false);
		appW.setInputPosition(
				appW.getInputPosition() == InputPosition.algebraView
						? InputPosition.bottom : InputPosition.algebraView,
				true);
		appW.updateSplitPanelHeight();

		appW.updateCenterPanelAndViews();
		if (appW.getGuiManager() != null
				&& appW.getGuiManager().getLayout() != null) {
			appW.getGuiManager().getLayout().getDockManager().resizePanels();
		}

	}

	/**
	 * @param show
	 * 
	 *            wheter show the reseticon in geogebra-web applets or not
	 */
	public void showResetIcon(boolean show) {
		((AppW) app).getAppletFrame().showResetIcon(show);
	}

	/**
	 * @param url
	 *            image URL
	 * @param corner1
	 *            bottom left corner
	 * @param corner2
	 *            bottom right corner
	 * @param corner4
	 *            top left corner
	 * @return image label
	 */
	public String insertImage(String url, String corner1, String corner2,
			String corner4) {
		
		GeoImage geoImage = ((AppW) app).urlDropHappened(url,
				checkCorner(corner1), checkCorner(corner2),
				checkCorner(corner4));

		return geoImage.getLabelSimple();
	}

	/**
	 * Add external image to image manager
	 * @param filename internal filename
	 * @param url data URL
	 */
	public void addImage(String filename, String url) {
		ImageManagerW imageManager = ((AppW) app).getImageManager();
		imageManager.addExternalImage(filename, url);
		imageManager.triggerSingleImageLoading(filename, new GeoImage(construction));
	}

	private static String checkCorner(String cornerExp) {
		return StringUtil.isNaN(cornerExp) || StringUtil.empty(cornerExp) ? null
				: cornerExp;
	}

	/**
	 * recalculates euclidianviews environments
	 */
	public void recalculateEnvironments() {
		((AppW) app).recalculateEnvironments();
	}

	/**
	 * remove applet from the page, and free memory. If applet is the last one,
	 * it remove the style elements injected by the applet too.
	 */
	public void removeApplet() {
		((AppW) app).getAppletFrame().remove();
	}

	@Override
	public void showTooltip(String tooltip) {
		ToolTipManagerW.sharedInstance().showBottomMessage(tooltip, false,
				(AppW) app);
	}

	/**
	 * Add a multiuser interaction
	 * @param user tooltip content
	 * @param label label of an object to use as anchor
	 * @param color color CSS string
	 */
	public void addMultiuserSelection(String user, String color, String label) {
		MultiuserManager.INSTANCE.addSelection(app, user, GColor.parseHexColor(color), label);
	}

	public void removeMultiuserSelections(String user) {
		MultiuserManager.INSTANCE.deselect(user);
	}

	public void asyncEvalCommand(String command, ResolveCallbackFn<String> onSuccess,
			RejectCallbackFn onFailure) {
		((AppW) app).getAsyncManager().asyncEvalCommand(command, onSuccess, onFailure);
	}

	public void asyncEvalCommandGetLabels(String command, ResolveCallbackFn<String> onSuccess,
			RejectCallbackFn onFailure) {
		((AppW) app).getAsyncManager().asyncEvalCommandGetLabels(command, onSuccess, onFailure);
	}

	/**
	 * Try to evaluate command only once, might fail if the command is not loaded
	 * @param cmdString command to evaluate
	 * @return whether the evaluation succeeded
	 */
	public synchronized boolean evalCommandNoException(String cmdString) {
		try {
			return super.evalCommand(cmdString);
		} catch (CommandNotLoadedError e) {
			Log.debug("Command not loaded yet. "
					+ "Please try asyncEvalCommand(cmdString)");
			throw e;
		}
	}

	/**
	 * Try to evaluate command only once, might fail if the command is not loaded
	 * @param cmdString command to evaluate
	 * @return comma separated list of labels of the resulting Geos
	 */
	public synchronized String evalCommandGetLabelsNoException(String cmdString) {
		try {
			return super.evalCommandGetLabels(cmdString);
		} catch (CommandNotLoadedError e) {
			Log.debug("Command not loaded yet. "
					+ "Please try asyncEvalCommandGetLabels(cmdString, callback)");
			throw e;
		}
	}

	/**
	 * Remember where file was stored in WinStore app
	 * 
	 * @param s
	 *            external saving path
	 */
	public void setExternalPath(String s) {
		((AppW) app).setExternalPath(s);
	}

	/**
	 * If all content is saved, run immediately, otherwise wait until user
	 * saves.
	 * 
	 * @param callback
	 *            callback after file is saved
	 */
	public void checkSaved(final JsRunnable callback) {
		((AppW) app).checkSaved(active -> JsEval.callNativeFunction(callback));
	}

	/**
	 * @param toolbarString
	 *            custom toolbar definition
	 */
	public void setCustomToolBar(String toolbarString) {
		GuiManagerInterfaceW gm = ((GuiManagerInterfaceW) app.getGuiManager());
		gm.setToolBarDefinition(toolbarString);
		gm.setGeneralToolBarDefinition(toolbarString);
		gm.updateToolbar();
	}

	/**
	 * Make screenshot of the whole app as PNG.
	 * 
	 * @param callback
	 *            callback
	 */
	public void getScreenshotBase64(StringConsumer callback) {
		((AppW) app).getAppletFrame().getScreenshotBase64(callback);
	}

	/**
	 * @param workerUrls
	 *            worker folder URL
	 * @param sync
	 *            whether to use zipjs synchronously
	 * @return whether webworkers can be used
	 */
	public static native boolean setWorkerURL(String workerUrls,
			boolean sync) /*-{
		if (workerUrls === "false" || !workerUrls || sync) {
			$wnd.zip.useWebWorkers = false;
			$wnd.zip.synchronous = sync;
		} else {
			$wnd.zip.synchronous = false;
			$wnd.zip.useWebWorkers = true;

			$wnd.zip.workerScripts = {
				deflater : [ workerUrls + "z-worker.js",
						workerUrls + "pako1.0.6_min.js",
						workerUrls + "codecs.js" ],
				inflater : [ workerUrls + "z-worker.js",
						workerUrls + "pako1.0.6_min.js",
						workerUrls + "codecs.js" ]
			};

		}
		return $wnd.zip.useWebWorkers;
	}-*/;

	/**
	 * @return whether webworkers are used in zipjs
	 */
	native boolean isUsingWebWorkers()/*-{
		return $wnd.zip.useWebWorkers;
	}-*/;

	/**
	 * GGB-1780
	 * 
	 * @return current construction as SVG
	 */
	@Override
	final public String exportSVG(String filename) {
		EuclidianView ev = app.getActiveEuclidianView();

		if (ev instanceof EuclidianViewW) {
			EuclidianViewW evw = (EuclidianViewW) ev;

			String svg = evw.getExportSVG(1, true);

			if (filename != null) {
				// can't use data:image/svg+xml;utf8 in IE11 / Edge
				Browser.exportImage(Browser.encodeSVG(svg), filename);
			}

			return svg;
		}

		return null;
	}

	/**
	 * Experimental GGB-2150
	 * 
	 */
	@Override
	final public String exportPDF(double scale, String filename,
			String sliderLabel) {

		String pdf;

		if (app.isWhiteboardActive()) {

			// export each slide as separate page
			pdf = ((AppW) app).getPageController().exportPDF();

		} else {

			EuclidianView ev = app.getActiveEuclidianView();

			if (ev instanceof EuclidianViewW) {

				EuclidianViewW evw = (EuclidianViewW) ev;

				if (sliderLabel == null) {
					pdf = evw.getExportPDF(scale);
				} else {
					pdf = AnimationExporter.export(kernel.getApplication(), 0,
							(GeoNumeric) kernel.lookupLabel(sliderLabel), false,
							filename, scale, Double.NaN, ExportType.PDF_HTML5);
				}

			} else {
				return null;
			}
		}

		if (filename != null) {
			Browser.exportImage(pdf, filename);
		}
		return pdf;
	}

	@Override
	public void exportGIF(String sliderLabel, double scale,
			double timeBetweenFrames, boolean isLoop, String filename,
			double rotate) {

		// each frame as ExportType.PNG
		AnimationExporter.export(kernel.getApplication(), (int) timeBetweenFrames,
				(GeoNumeric) kernel.lookupLabel(sliderLabel), isLoop, filename,
				scale, rotate, ExportType.PNG);

	}

	@Override
	public void exportWebM(String sliderLabel, double scale,
			double timeBetweenFrames, boolean isLoop, String filename,
			double rotate) {
		// each frame as ExportType.WEBP
		AnimationExporter.export(kernel.getApplication(), (int) timeBetweenFrames,
				(GeoNumeric) kernel.lookupLabel(sliderLabel), isLoop, filename,
				scale, rotate, ExportType.WEBP);
	}

	/**
	 * @param callback
	 *            native callback
	 */
	public void exportPSTricks(StringConsumer callback) {
		this.exportPSTricks(asyncOperation(callback));
	}

	/**
	 * @param callback
	 *            native callback
	 */
	public void exportPGF(StringConsumer callback) {
		this.exportPGF(asyncOperation(callback));
	}

	/**
	 * @param callback
	 *            native callback
	 */
	public void exportAsymptote(StringConsumer callback) {
		this.exportAsymptote(asyncOperation(callback));
	}

	/**
	 * @param key
	 *            menu key
	 * @param callback
	 *            callback to run when properties loaded
	 * @return return value
	 */
	final public String translate(final String key,
			final StringConsumer callback) {
		final Localization loc = app.getLocalization();
		if (callback != null) {
			((AppW) app).afterLocalizationLoaded(
					() -> callback.consume(loc.getMenu(key)));
		}
		return loc.getMenu(key);
	}

	private static AsyncOperation<String> asyncOperation(
			final StringConsumer callback) {
		return callback::consume;
	}

	/**
	 * @param columnNamesJS
	 *            JS string array
	 * @return exported construction
	 */
	public String exportConstruction(JsArrayString columnNamesJS) {
		String[] columnNames = new String[columnNamesJS.length()];
		for (int i = 0; i < columnNames.length; i++) {
			columnNames[i] = columnNamesJS.get(i);
		}
		return this.exportConstruction(columnNames);
	}

	/**
	 * @param token
	 *            token
	 * @param showUI
	 *            whether to show UI when token is invalid
	 */
	public void login(String token, boolean showUI) {
		String normalizedToken = StringUtil.isNaN(token) ? "" : token;
		if (showUI && StringUtil.empty(normalizedToken)) {
			app.getLoginOperation().showLoginDialog();
		} else {
			login(normalizedToken);
		}
	}

	/**
	 * @param text
	 *            JSON describing editor state
	 * @param label
	 *            label for geo element, empty string or null for new input
	 */
	public void setEditorState(String text, String label) {
		if (editor != null) {
			GeoElement geo = StringUtil.empty(label) ? null
					: kernel.lookupLabel(label);
			editor.setState(text, geo);
		}
	}

	/**
	 * @return JSON describing editor state
	 */
	public String getEditorState() {
		return editor == null ? "" : editor.getState();
	}

	/**
	 *
	 * @return then embedded calculator apis.
	 */
	public JsPropertyMap<Object> getEmbeddedCalculators(boolean includeGraspableMath) {
		return ((AppW) app).getEmbeddedCalculators(includeGraspableMath);
	}

	/**
	 * @return frame DOM element
	 */
	public Element getFrame() {
		return ((AppW) app).getFrameElement();
	}

	@Override
	public void newConstruction() {
		((AppW) app).tryLoadTemplatesOnFileNew();
	}

	/**
	 * reset after login and after save callbacks
	 */
	public void resetAfterSaveLoginCallbacks() {
		((AppW) app).getGuiManager().setRunAfterLogin(null);
		app.getSaveController().setRunAfterSave(null);
	}

	@Override
	public void handleSlideAction(String eventType, String pageIdx, String appState) {
		EventType event = null;
		String[] args = new String[] {};
		switch (eventType) {
			case "addSlide":
				event = EventType.ADD_SLIDE;
				break;

			case "removeSlide":
				event = EventType.REMOVE_SLIDE;
				args = !"undefined".equals(pageIdx) ? new String[] { pageIdx }
						: new String[] {};
				break;

			case "moveSlide":
				event = EventType.MOVE_SLIDE;
				args = pageIdx.split(",");
				break;

			case "pasteSlide":
				event = EventType.PASTE_SLIDE;
				args = new String[] { pageIdx, null, appState };
				break;

			case "clearSlide":
				event = EventType.CLEAR_SLIDE;
				args = new String[] { pageIdx };
				break;

			default:
				Log.error("No event type sent");
				break;
		}
		if (event != null) {
			((AppW) app).getPageController().executeAction(event, args);
		}
	}

	@Override
	public void selectSlide(String pageIdx) {
		int page = "undefined".equals(pageIdx) ? -1 : Integer.parseInt(pageIdx);
		if (page > -1) {
			((AppW) app).getPageController().selectSlide(page);
		}
	}

	@Override
	public void previewRefresh() {
		PageListControllerInterface pageController = ((AppW) app).getPageController();
		if (pageController != null) {
			pageController.updatePreviewImage();
		}
	}
}