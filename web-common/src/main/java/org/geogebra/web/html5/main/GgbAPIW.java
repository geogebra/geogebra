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
import org.geogebra.common.kernel.arithmetic.MyDouble;
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
import org.geogebra.common.plugin.EventType;
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
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.multiuser.MultiuserManager;
import org.geogebra.web.html5.util.AnimationExporter;
import org.geogebra.web.html5.util.ArchiveEntry;
import org.geogebra.web.html5.util.Base64;
import org.geogebra.web.html5.util.FFlate;
import org.geogebra.web.html5.util.FileConsumer;
import org.geogebra.web.html5.util.ImageManagerW;
import org.geogebra.web.html5.util.JsRunnable;
import org.geogebra.web.html5.util.StringConsumer;
import org.geogebra.web.html5.util.ViewW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Element;

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
		} else {
			str = geo instanceof GeoCasCell
					? ((GeoCasCell) geo)
							.getLaTeXInput(StringTemplate.latexTemplate)
					: geo.toString(StringTemplate.latexTemplate);
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
	public void getMacrosBase64(boolean includeThumbnail,
			StringConsumer callback) {
		GgbFile archiveContent = createMacrosArchive();
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
		ViewW view = ((AppW) app).getViewW();
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
	 * @return base64 for ggt file
	 */
	public String getMacrosBase64() {
		GgbFile archiveContent = createMacrosArchive();
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
		getCompressed(createMacrosArchive(), clb);
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
	 * @param urlOrSvgContent data URL or &lt;svg>content&lt;/svg>
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
	 * it remove the style elements injected by the applet too.
	 */
	public void removeApplet() {
		((AppW) app).getAppletFrame().remove();
	}

	@Override
	public void showTooltip(String tooltip) {
		ToolTipManagerW.sharedInstance().showBottomMessage(tooltip, (AppW) app);
	}

	/**
	 * Add a multiuser interaction
	 * @param clientId id of the client that triggered the selection
	 * @param userName tooltip content
	 * @param label label of an object to use as anchor
	 * @param color color CSS string
	 * @param newGeo if the geo was added
	 */
	public void addMultiuserSelection(String clientId, String userName, String color,
			String label, boolean newGeo) {
		MultiuserManager.INSTANCE.addSelection(app, clientId, userName, GColor.parseHexColor(color),
				label, newGeo);
	}

	/**
	 * Remove a multiuser interaction
	 * @param clientId the id of the client
	 */
	public void removeMultiuserSelections(String clientId) {
		MultiuserManager.INSTANCE.deselect(app, clientId);
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

			evw.getExportSVG(1, true, (svg) -> {
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
}