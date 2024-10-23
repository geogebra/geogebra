package org.geogebra.web.html5.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.gui.view.table.InvalidValuesException;
import org.geogebra.common.io.MyXMLio;
import org.geogebra.common.io.file.Base64ZipFile;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.CommandNotLoadedError;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.ExportType;
import org.geogebra.common.main.App.InputPosition;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MaterialVisibility;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.OpenFileListener;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.plugin.GgbAPI;
import org.geogebra.common.plugin.JsObjectWrapper;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.gwtutil.JavaScriptInjector;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.export.ExportLoader;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.zoompanel.ZoomController;
import org.geogebra.web.html5.js.ResourcesInjector;
import org.geogebra.web.html5.multiuser.MultiuserManager;
import org.geogebra.web.html5.util.AnimationExporter;
import org.geogebra.web.html5.util.ArchiveEntry;
import org.geogebra.web.html5.util.ArchiveLoader;
import org.geogebra.web.html5.util.Base64;
import org.geogebra.web.html5.util.FFlate;
import org.geogebra.web.html5.util.FileConsumer;
import org.geogebra.web.html5.util.ImageManagerW;
import org.geogebra.web.html5.util.JsRunnable;
import org.geogebra.web.html5.util.StringConsumer;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.dom.client.Element;

import elemental2.core.Global;
import elemental2.core.JsArray;
import elemental2.core.Uint8Array;
import elemental2.dom.Blob;
import elemental2.promise.Promise.PromiseExecutorCallbackFn.RejectCallbackFn;
import elemental2.promise.Promise.PromiseExecutorCallbackFn.ResolveCallbackFn;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * HTML5 version of API. The methods are exported in ScriptManagerW
 *
 */
public class GgbAPIW extends GgbAPI {
	private MathEditorAPI editor;
	private JsPropertyMap<?> fileLoadingError;

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
		ArchiveLoader view = ((AppW) app).getArchiveLoader();
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

		EuclidianViewWInterface ev = (EuclidianViewWInterface) app
				.getActiveEuclidianView();
		if (app.getGuiManager() != null) {
			app.getGuiManager().getLayout().getDockManager().ensureFocus();

			if (app.getGuiManager().getLayout().getDockManager()
					.getFocusedViewId() == App.VIEW_PROBABILITY_CALCULATOR) {
				ev = (EuclidianViewWInterface) app.getGuiManager()
						.getPlotPanelEuclidanView();
			}
		}

		// get export image
		// DPI ignored
		url = ev.getExportImageDataUrl(exportScale, transparent, greyscale);

		if (Double.isFinite(dpi) && dpi > 0 && ev instanceof EuclidianViewW) {

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
		String dataUri = getPNG(exportScale, transparent, dpi, greyscale);
		if (copyToClipboard) {
			app.copyImageToClipboard(dataUri);
		}
		return StringUtil.removePngMarker(dataUri);
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
		} else if (geo instanceof  GeoCasCell) {
			str = ((GeoCasCell) geo).getLaTeXInput();
			if (str == null) {
				// regexp should be good enough in most cases, avoids dependency on ReTeX
				str = ((GeoCasCell) geo).getLocalizedInput()
						.replaceAll("([{}$])", "\\\\$1");
			}
		} else {
			str = geo.toString(StringTemplate.latexTemplate);
		}
		DrawEquationW.paintOnCanvasOutput(geo, str, c, app.getFontSize());
		return StringUtil.removePngMarker(c.toDataUrl());
	}

	/**
	 * @param includeThumbnail
	 *            whether to include thumbnail
	 * @param callback
	 *            callback
	 */
	public void getBase64(boolean includeThumbnail, StringConsumer callback) {
		getZippedBase64Async(getFile(includeThumbnail), callback);
	}

	/**
	 * Base64 for ggt file
	 * 
	 * @param includeThumbnail
	 *            whether to add thumbnail
	 * @param callback
	 *            callback
	 */
	public void getAllMacrosBase64(boolean includeThumbnail,
			StringConsumer callback) {
		GgbFile archiveContent = createAllMacrosArchive();
		getZippedBase64Async(archiveContent, callback);
	}

	/**
	 * @param includeThumbnail
	 *            whether to include thumbnail
	 * @return native JS object representing the archive
	 */
	public GgbFile getFile(boolean includeThumbnail) {

		PageListControllerInterface pageController = ((AppW) app)
				.getPageController();
		if (pageController != null) {
			GgbFile jso = new GgbFile();
			HashMap<String, Integer> usage = new HashMap<>();
			GgbFile shared = new GgbFile("");
			for (int i = 0; i < pageController.getSlideCount(); i++) {
				pageController.refreshSlide(i);
				countShared(pageController.getSlide(i), usage, shared);
			}
			for (int i = 0; i < pageController.getSlideCount(); i++) {
				GgbFile f = pageController.getSlide(i);
				mergeFiles(f, jso, GgbFile.SLIDE_PREFIX + i + "/",
						usage);
			}
			jso.put(GgbFile.STRUCTURE_JSON, pageController.getStructureJSON());
			mergeFiles(shared, jso, GgbFile.SHARED_PREFIX, null);
			return jso;
		}
		GgbFile archiveContent = new GgbFile("");
		return createArchiveContent(includeThumbnail, archiveContent);
	}

	public JsPropertyMap<Object> getFileJSON(boolean includeThumbnail) {
		return export(getFile(includeThumbnail));
	}

	private JsPropertyMap<Object> export(GgbFile file) {
		JsPropertyMap<Object> jso = JsPropertyMap.of();
		JsArray<Object> archive = JsArray.of();
		for (Entry<String, ArchiveEntry> entry: file.entrySet()) {
			ArchiveEntry value = entry.getValue();
			if (value.string != null) {
				pushNativeEntryToArchive(entry.getKey(),
						value.string, archive);
			} else {
				pushNativeEntryToArchive(entry.getKey(),
						value.export(), archive);
			}
		}
		jso.set("archive", archive);
		return jso;
	}

	private static void countShared(GgbFile slide,
			HashMap<String, Integer> usage,
			GgbFile shared) {
		for (Entry<String, ArchiveEntry> entry : slide.entrySet()) {
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
		ArchiveLoader view = ((AppW) app).getArchiveLoader();
		view.processJSON(obj);
	}

	/**
	 * @param file GeoGebra file
	 * @return JSON representation
	 */
	public String toJson(GgbFile file) {
		return Global.JSON.stringify(export(file));
	}

	@Override
	public String getBase64(boolean includeThumbnail) {
		GgbFile jso = getFile(includeThumbnail);
		return getZippedBase64Sync(jso);

	}

	/**
	 * @return base64 string for the archived and zipped macros
	 */
	public String getAllMacrosBase64() {
		GgbFile archiveContent = createAllMacrosArchive();
		return getZippedBase64Sync(archiveContent);
	}

	/**
	 * @param macro is the macro that needs to be archived and zipped
	 * @return base64 string for the given macro
	 */
	public String getMacroBase64(Macro macro) {
		GgbFile archiveContent = createMacroArchive(macro);
		return getZippedBase64Sync(archiveContent);
	}

	private String addDPI(String prefixedBase64, double dpi) {
		String base64;
		if (prefixedBase64.startsWith(StringUtil.pngMarker)) {
			base64 = prefixedBase64.substring(StringUtil.pngMarker.length());
		} else {
			base64 = prefixedBase64;
		}

		Uint8Array bytes = Base64.base64ToBytes(base64);

		// change / add pHYs chunk
		// pixels per metre
		double ppm = Math.round(dpi / 2.54 * 100);

		String b64encoded = RewritePhys.rewritePhysChunk(bytes, ppm, ppm);
		return StringUtil.pngMarker + b64encoded;
	}

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
		String allMacrosXml = getApplication().getAllMacrosXMLorEmpty();
		StringBuilder defaults2d = new StringBuilder();
		StringBuilder defaults3d = null;
		if (app.is3D()) {
			defaults3d = new StringBuilder();
		}
		getKernel().getConstruction().getConstructionDefaults()
				.getDefaultsXML(defaults2d, defaults3d);
		String geogebraJavascript = getKernel().getLibraryJavaScript();

		if (!"".equals(allMacrosXml)) {
			writeMacroImages(archiveContent);
			archiveContent.put(MyXMLio.XML_FILE_MACRO, allMacrosXml);
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
			ArchiveEntry thumb = new ArchiveEntry(MyXMLio.XML_FILE_THUMBNAIL,
					((EuclidianViewWInterface) getViewForThumbnail())
					.getCanvasBase64WithTypeString());
			archiveContent.put(MyXMLio.XML_FILE_THUMBNAIL, thumb);
		}

		getKernel().setSaving(isSaving);
		return archiveContent;
	}

	/**
	 * @return base64 encoded thumbnail
	 */
	public String getThumbnailBase64() {
		return StringUtil.removePngMarker(((EuclidianViewWInterface) getViewForThumbnail())
				.getCanvasBase64WithTypeString());
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
	 * Creates an archive with all the macros
	 * @return archive containing all macros and their icons
	 */
	public GgbFile createAllMacrosArchive() {
		GgbFile archiveContent = new GgbFile("");
		writeMacroImages(archiveContent);
		String allMacrosXml = getApplication().getAllMacrosXMLorEmpty();
		if (!"".equals(allMacrosXml)) {
			writeMacroImages(archiveContent);
			archiveContent.put(MyXMLio.XML_FILE_MACRO, allMacrosXml);
		}
		return archiveContent;
	}

	/**
	 * Creates an archive with the given macro
	 * @param macro is the macro that the archive needs to contain
	 * @return archive containing the given macro and its icon
	 */
	public GgbFile createMacroArchive(Macro macro) {
		GgbFile archiveContent = new GgbFile("");
		writeMacroImage(archiveContent, macro);
		String macroXml = getApplication().getMacroXMLorEmpty(macro);
		if (!"".equals(macroXml)) {
			writeMacroImage(archiveContent, macro);
			archiveContent.put(MyXMLio.XML_FILE_MACRO, macroXml);
		}
		return archiveContent;
	}

	private static GgbFile mergeFiles(GgbFile archive,
			GgbFile top, String prefix,
			HashMap<String, Integer> usage) {
		for (Entry<String, ArchiveEntry> entry : archive.entrySet()) {
			if (usage == null || usage.get(entry.getKey()) == null
					|| usage.get(entry.getKey()) < 2) {
				top.put(prefix + entry.getKey(), entry.getValue());
			}
		}
		return top;
	}

	private static void pushNativeEntryToArchive(String key, String value,
			JsArray<Object> archive) {
		JsPropertyMap<Object> obj = JsPropertyMap.of();
		obj.set("fileName", key);
		obj.set("fileContent", value);
		archive.push(obj);
	}

	private JsPropertyMap<Object> prepareFileForFFlate(GgbFile arch) {
		List<String> imgExtensions = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp");

		JsPropertyMap<Object> fflatePrepared = JsPropertyMap.of();

		for (Entry<String, ArchiveEntry> entry: arch.entrySet()) {
			String fileName = entry.getKey();
			ArchiveEntry fileContentObject = entry.getValue();
			JsArray<Object> archiveEntry = new JsArray<>();
			int ind = fileName.lastIndexOf('.');
			String extension = ind > -1 ? fileName.substring(ind + 1).toLowerCase() : "";
			if (fileContentObject.string != null) {
				String fileContent = fileContentObject.string;
				if (imgExtensions.contains(extension)) {
					// base64 needed for thumbnail and for newly inserted images
					String base64 = fileContent.substring(fileContent.indexOf(',') + 1);
					archiveEntry.push(Base64.base64ToBytes(base64));
				} else {
					archiveEntry.push(FFlate.get().strToU8(fileContent));
				}
			} else {
				archiveEntry.push(fileContentObject.data);
			}
			if (imgExtensions.contains(extension) && ! "bmp".equals(extension)) {
				JsPropertyMap<?> options = JsPropertyMap.of("level", 0);
				archiveEntry.push(options);
			}
			fflatePrepared.set(fileName, archiveEntry);
		}

		return fflatePrepared;
	}

	/**
	 * @param includeThumbnail
	 *            whether to include thumbnail
	 * @param clb
	 *            handler for the file
	 */
	public void getZippedGgbAsync(final boolean includeThumbnail, final FileConsumer clb) {
		final GgbFile arch = getFile(includeThumbnail);
		getCompressed(arch, clb);
	}

	public void getZippedMacrosAsync(final FileConsumer clb) {
		getCompressed(createAllMacrosArchive(), clb);
	}

	private void getCompressed(GgbFile arch, FileConsumer clb) {
		JsPropertyMap<Object> fflatePrepared = prepareFileForFFlate(arch);

		FFlate.get().zip(fflatePrepared, (err, data) -> {
			if (Js.isTruthy(err)) {
				Log.error("Async zipping failed, trying synchronous zip");
				Log.error(err);

				Uint8Array syncZipped = FFlate.get().zipSync(fflatePrepared);
				clb.consume(new Blob(
						new JsArray<>(Blob.ConstructorBlobPartsArrayUnionType.of(syncZipped))));
			} else {
				clb.consume(
						new Blob(new JsArray<>(Blob.ConstructorBlobPartsArrayUnionType.of(data))));
			}
		});
	}

	/**
	 * Synchronously zip archive and return the base64 string
	 * @param arch archive
	 * @return zipped archive as a base64 string
	 */
	public String getZippedBase64Sync(GgbFile arch) {
		JsPropertyMap<Object> fflatePrepared = prepareFileForFFlate(arch);
		return Base64.bytesToBase64(FFlate.get().zipSync(fflatePrepared));
	}

	/**
	 * Like getBase64, but only construction, no images
	 * @return compressed XML
	 */
	public String zipXML(String plain) {
		JsArray<Uint8Array> entry = new JsArray<>();
		entry.push(FFlate.get().strToU8(plain));
		return Base64.bytesToBase64(FFlate.get().zipSync(
				JsPropertyMap.of("geogebra.xml", entry)));
	}

	private String unzipXML(String xml) {
		if (StringUtil.empty(xml)) {
			return xml;
		}
		JsPropertyMap<Uint8Array> archive = FFlate.get().unzipSync(Base64.base64ToBytes(xml));
		return FFlate.get().strFromU8(archive.get("geogebra.xml"));
	}

	/**
	 * Asynchronously zip archive and convert it to base64 string
	 * @param arch archive
	 * @param clb callback for handling the resulting base64 string
	 */
	public void getZippedBase64Async(final GgbFile arch, final StringConsumer clb) {
		JsPropertyMap<Object> fflatePrepared = prepareFileForFFlate(arch);

		FFlate.get().zip(fflatePrepared, (err, data) -> {
			if (Js.isTruthy(err)) {
				Log.error("Async zipping failed, trying synchronous zip");
				Log.error(err);

				clb.consume(Base64.bytesToBase64(FFlate.get().zipSync(fflatePrepared)));
			} else {
				clb.consume(Base64.bytesToBase64(data));
			}
		});
	}

	private void writeMacroImages(GgbFile archive) {
		if (kernel.hasMacros()) {
			ArrayList<Macro> macros = kernel.getAllMacros();
			((ImageManagerW) app.getImageManager()).writeMacroImages(macros, archive);
		}
	}

	private void writeMacroImage(GgbFile archive, Macro macro) {
		((ImageManagerW) app.getImageManager())
				.writeMacroImages(Arrays.asList(macro), archive);
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
	 * Add external image to image manager. Allow passing SVGs as text
	 * to be compatible with getFileJSON.
	 * @param filename internal filename
	 * @param urlOrSvgContent data URL or &lt;svg&gt;content&lt;/svg&gt;
	 */
	public void addImage(String filename, String urlOrSvgContent) {
		ImageManagerW imageManager = ((AppW) app).getImageManager();
		String url = urlOrSvgContent;
		if (urlOrSvgContent.charAt(0) == '<') {
			url = Browser.encodeSVG(urlOrSvgContent);
		}
		imageManager.addExternalImage(filename, url);
		imageManager.triggerSingleImageLoading(filename, kernel);
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
	 * it removes the style elements injected by the applet too.
	 */
	public void removeApplet() {
		((AppW) app).getGeoGebraElement().getElement().removeFromParent();
		((AppW) app).getAppletFrame().remove();
		if (GeoGebraFrameW.getInstanceCount() == 0) {
			ResourcesInjector.removeResources();
		}
	}

	@Override
	public void showTooltip(String tooltip) {
		((AppW) app).getToolTipManager().showBottomMessage(tooltip, (AppW) app);
	}

	/**
	 * Add a multiuser interaction
	 * @param clientId id of the client that triggered the selection
	 * @param userName tooltip content
	 * @param label label of an object to use as anchor
	 * @param color color CSS string
	 * @param implicit whether the geo was interacted with (add, update) without explicit selection
	 */
	public void addMultiuserSelection(String clientId, String userName, String color,
			String label, boolean implicit) {
		MultiuserManager.INSTANCE.addSelection(app, clientId, userName, GColor.parseHexColor(color),
				label, implicit);
	}

	/**
	 * Remove a multiuser interaction
	 * @param clientId the id of the client
	 */
	public void removeMultiuserSelections(String clientId) {
		MultiuserManager.INSTANCE.deselect(app, clientId);
	}

	/**
	 * Sets a suffix that is used to label objects within multiuser<br/>
	 * Calling this method with an argument < 0 resets the label prefix
	 * @param labelPrefixIndex Index
	 */
	public void setLabelSuffixForMultiuser(int labelPrefixIndex) {
		String labelPrefix = "";
		int index = labelPrefixIndex;
		while (index > 0) {
			labelPrefix = (char) ('a' + (index - 1) % 26) + labelPrefix;
			index = (index - 1) / 26;
		}
		construction.getLabelManager().setMultiuserSuffix(labelPrefix);
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
	 * If all content is saved, run immediately, otherwise wait until user
	 * saves.
	 * 
	 * @param callback
	 *            callback after file is saved
	 */
	public void checkSaved(final JsRunnable callback) {
		((AppW) app).checkSaved(active -> JsEval.callNativeFunction(callback));
	}

	@Override
	public void setPerspective(String code) {
		if (code.startsWith("save:")) {
			app.getDialogManager().showSaveDialog();
			return;
		}
		super.setPerspective(code);
	}

	/**
	 * @param toolbarString
	 *            custom toolbar definition
	 */
	public void setCustomToolBar(String toolbarString) {
		GuiManagerInterfaceW gm = (GuiManagerInterfaceW) app.getGuiManager();
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
	public void getScreenshotBase64(StringConsumer callback, double scale) {
		((AppW) app).getAppletFrame().getScreenshotBase64(callback, scale);
	}

	@Override
	final public void exportSVG(String filename, Consumer<String> callback) {
		EuclidianView ev = app.getActiveEuclidianView();

		if (ev instanceof EuclidianViewW) {
			EuclidianViewW evw = (EuclidianViewW) ev;

			evw.getExportSVG(true, (svg) -> {
				if (filename != null) {
					// can't use data:image/svg+xml;utf8 in IE11 / Edge
					Browser.exportImage(Browser.encodeSVG(svg), filename);
				}
				if (callback != null) {
					callback.accept(svg);
				}
			});
		}
	}

	/**
	 * Experimental GGB-2150
	 * 
	 */
	@Override
	final public void exportPDF(double scale, String filename,
			Consumer<String> callback, String sliderLabel) {
		ExportLoader.onCanvas2PdfLoaded(() -> {
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
								(GeoNumeric) kernel.lookupLabel(sliderLabel),
								filename, scale, Double.NaN, ExportType.PDF_HTML5);
					}
				} else {
					return;
				}
			}

			if (filename != null) {
				Browser.exportImage(pdf, filename);
			}

			if (callback != null) {
				callback.accept(pdf);
			}
		});
	}

	@Override
	public void exportGIF(String sliderLabel, double scale,
			double timeBetweenFrames, boolean isLoop, String filename,
			double rotate) {

		// each frame as ExportType.PNG
		AnimationExporter.export(kernel.getApplication(), (int) timeBetweenFrames,
				(GeoNumeric) kernel.lookupLabel(sliderLabel), filename,
				scale, rotate, ExportType.PNG);

	}

	@Override
	public void exportWebM(String sliderLabel, double scale,
			double timeBetweenFrames, boolean isLoop, String filename,
			double rotate) {
		// each frame as ExportType.WEBP
		AnimationExporter.export(kernel.getApplication(), (int) timeBetweenFrames,
				(GeoNumeric) kernel.lookupLabel(sliderLabel), filename,
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
	 * @param label - inputbox label
	 * @return content of the inputbox
	 */
	public String getInputBoxState(String label) {
		GeoElement geo = StringUtil.empty(label) ? null
				: kernel.lookupLabel(label);
		if (geo instanceof GeoInputBox) {
			return ((GeoInputBox) geo).getInputBoxState();
		}
		return getEditorState();
	}

	/**
	 * @param state - content of inputbox
	 * @param label - label of inputbox
	 */
	public void setInputBoxState(String state, String label) {
		GeoElement geo = StringUtil.empty(label) ? null
				: kernel.lookupLabel(label);
		if (geo instanceof GeoInputBox) {
			((GeoInputBox) geo).setInputBoxState(state);
		}
	}

	/**
	 * whether an object is interactive or not
	 * @param label of the object
	 * @return true, if object is interactive
	 */
	public boolean isInteractive(String label) {
		GeoElement geo = StringUtil.empty(label) ? null
				: kernel.lookupLabel(label);
		return geo != null && app.getSelectionManager().isSelectableForEV(geo);
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
	public void handlePageAction(String eventType, String pageIdx, Object appState) {
		PageListControllerInterface pageController = ((AppW) app).getPageController();
		if (pageController != null) {
			pageController.handlePageAction(eventType, pageIdx, appState);
		}
	}

	@Override
	public void selectPage(String pageId) {
		if (((AppW) app).getPageController() != null) {
			((AppW) app).getPageController().selectSlide(pageId);
		}
	}

	/**
	 * @return ID of selected page
	 */
	public String getActivePage() {
		return ((AppW) app).getPageController() == null ? ""
				: ((AppW) app).getPageController().getActivePage();
	}

	@Override
	public void previewRefresh() {
		PageListControllerInterface pageController = ((AppW) app).getPageController();
		if (pageController != null) {
			pageController.updatePreviewImage();
		}
	}

	/**
	 * @return array of page IDs in notes
	 */
	public String[] getPages() {
		PageListControllerInterface pageController = ((AppW) app).getPageController();
		return pageController != null ? pageController.getPages() : new String[]{""};
	}

	/**
	 * @return XML of given page
	 */
	public PageContent getPageContent(String pageId) {
		PageListControllerInterface pageController = ((AppW) app).getPageController();
		PageContent ret = pageController != null ? pageController.getPageContent(pageId)
				: PageContent.of(getXML(), getAllObjectNames(), null, null, 0);
		ret.xml = zipXML(ret.xml);
		return ret;
	}

	/**
	 * @param label name of the function
	 */
	public void addGeoToTV(String label) {
		GuiManagerInterfaceW guiManagerW = (GuiManagerInterfaceW) app.getGuiManager();
		GeoElement geo = app.getKernel().lookupLabel(label);
		if (guiManagerW != null && geo != null) {
			guiManagerW.addGeoToTV(geo);
		}
	}

	/**
	 * @param label name of the function
	 */
	public void removeGeoFromTV(String label) {
		GuiManagerInterfaceW guiManagerW = (GuiManagerInterfaceW) app.getGuiManager();
		if (guiManagerW != null) {
			guiManagerW.removeGeoFromTV(label);
		}
	}

	/**
	 * @param values comma separated list min,max,step
	 * @throws InvalidValuesException if min/max/step are not valid numbers
	 */
	public void setValuesOfTV(String values) throws InvalidValuesException {
		GuiManagerInterfaceW guiManagerW = (GuiManagerInterfaceW) app.getGuiManager();
		if (guiManagerW != null && !values.isEmpty()) {
			String[] valueArray = values.split(",");
			if (valueArray.length == 3) {
				guiManagerW.setValues(Double.parseDouble(valueArray[0]),
						Double.parseDouble(valueArray[1]), Double.parseDouble(valueArray[2]));
			}
		}
	}

	/**
	 * @param columnStr column index (as string)
	 * @param showStr "true" or "false"
	 */
	public void showPointsTV(String columnStr, String showStr) {
		GuiManagerInterfaceW guiManagerW = (GuiManagerInterfaceW) app.getGuiManager();
		int column = Integer.parseInt(columnStr);
		boolean show = Boolean.parseBoolean(showStr);
		if (guiManagerW != null) {
			guiManagerW.showPointsTV(column, show);
		}
	}

	/**
	 * Save callback after successful login
	 * @param title material title
	 * @param visibility material visibility
	 * @param callbackAction what should happen after successful login
	 */
	public void startSaveCallback(String title, String visibility, String callbackAction) {
		app.getSaveController().setSaveType(Material.MaterialType.ggs);
		app.getSaveController().ensureTypeOtherThan(Material.MaterialType.ggsTemplate);
		MaterialVisibility matVisibility = MaterialVisibility.value(visibility);
		app.getSaveController().saveAs(title, matVisibility, null);
		app.getSaveController().setRunAfterSave((success) -> {
			if (success) {
				if ("clearAll".equals(callbackAction)) {
					((AppW) app).tryLoadTemplatesOnFileNew();
				}
				if ("openOfflineFile".equals(callbackAction)) {
					// TODO handle open offline file after login
				}
			} else {
				app.showError(MyError.Errors.SaveFileFailed);
			}
		});

	}

	@Override
	public JsObjectWrapper getWrapper(Object options) {
		return new JsObjectWrapperW(options);
	}

	@Override
	protected JsObjectWrapper createWrapper() {
		return new JsObjectWrapperW(JsPropertyMap.of());
	}

	public void switchCalculator(String appCode) {
		((AppW) app).switchToSubapp(appCode);
	}

	/**
	 * @param pageId page ID
	 * @param content page content
	 */
	public void setPageContent(String pageId, PageContent content) {
		PageListControllerInterface pc = ((AppW) app).getPageController();
		content.xml = unzipXML(content.xml);
		if (pc != null) {
			pc.setPageContent(pageId, content);
		} else if (!StringUtil.empty(content.xml)) {
			super.setXML(content.xml);
		}
	}

	public void setFileLoadingError(JsPropertyMap<?> error) {
		this.fileLoadingError = error;
	}

	public Object getFileLoadingError() {
		return this.fileLoadingError;
	}

	/**
	 * If this app is in fullscreen mode (emulated or native), leave that mode
	 */
	public void exitFullScreen() {
		AppW appW = (AppW) app;
		if (isFullScreenActive()) {
			if (ZoomController.useEmulatedFullscreen(appW) && appW.getZoomPanel() != null) {
				appW.getZoomPanel().onExitFullscreen();
			} else {
				Browser.toggleFullscreen(false, null);
			}
		}
	}

	public boolean isFullScreenActive() {
		return ((AppW) app).getFullscreenState().isFullScreenActive();
	}
}