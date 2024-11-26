package org.geogebra.web.html5.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.ImageManager;
import org.geogebra.common.util.MD5EncrypterGWTImpl;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.Util;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.css.GuiResourcesSimpleImpl;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.GgbFile;
import org.geogebra.web.html5.main.MyImageW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.resources.client.ResourcePrototype;

import elemental2.dom.CanvasRenderingContext2D;
import elemental2.dom.DomGlobal;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.HTMLImageElement;
import jsinterop.base.Js;

public class ImageManagerW extends ImageManager {

	private HashMap<String, HTMLImageElement> externalImageTable = new HashMap<>();
	private HashMap<String, ArchiveEntry> externalImageSrcs = new HashMap<>();
	private HashMap<String, HTMLImageElement> internalImageTable = new HashMap<>();

	private boolean preventAuxImage;
	protected int imagesLoaded = 0;

	/**
	 * Clear all lists
	 */
	public void reset() {
		externalImageTable = new HashMap<>();
		externalImageSrcs = new HashMap<>();
		imagesLoaded = 0;
	}

	/**
	 * @param fileName
	 *            original filename
	 * @param src
	 *            file content
	 */
	@Override
	public void addExternalImage(String fileName, String src) {
		if (fileName != null && src != null) {
			addExternalImage(fileName, new ArchiveEntry(fileName, src));
		}
	}

	/**
	 * @param fileName filename
	 * @param image file content (binary or data URL)
	 */
	public void addExternalImage(String fileName, ArchiveEntry image) {
		if (fileName != null && image != null) {
			Log.debug("addExternalImage: " + fileName);
			String fn = StringUtil.removeLeadingSlash(fileName);
			HTMLImageElement img = Dom.createImage();
			externalImageSrcs.put(fn, image);
			externalImageTable.put(fn, img);
		}
	}

	@Override
	public String getExternalImageSrc(String fileName) {
		return getExternalImageData(fileName).createUrl();
	}

	@Override
	public void setImageForFillable(Kernel kernel, GeoText geo, GeoElement fillable) {
		GuiResourcesSimpleImpl res = (GuiResourcesSimpleImpl) GuiResourcesSimple.INSTANCE;
		SVGResource image = (SVGResource) res.getResource(geo.getTextString());
		if (image != null) {
			String fileName = applyImage(image.getName() + ".svg",
					image.getSafeUri().asString(), kernel);
			fillable.setFillType(FillType.IMAGE);
			fillable.setImageFileName(fileName);
			fillable.setAlphaValue(1.0f);
			fillable.updateVisualStyleRepaint(GProperty.HATCHING);
		}
	}

	/**
	 * Apply image uploaded by user.
	 * @param fileName0 - image filename
	 * @param fileData - file content
	 * @return filename
	 */
	public String applyImage(String fileName0, String fileData, Kernel kernel) {
		String fileName = ImageManagerW.getMD5FileName(fileName0, fileData);

		if (!externalImageSrcs.containsKey(fileName)) {
			addExternalImage(fileName, fileData);
			triggerSingleImageLoading(fileName, kernel);
		}

		return fileName;
	}

	private ArchiveEntry getExternalImageData(String fileName) {
		return externalImageSrcs.get(StringUtil.removeLeadingSlash(fileName));
	}

	protected void checkIfAllLoaded(Runnable run, Map<String, ArchiveEntry> toLoad) {
		imagesLoaded++;
		if (imagesLoaded == toLoad.size()) {
			run.run();
			imagesLoaded = 0;
		}
	}

	/**
	 * Image inserted by user as img element.
	 * 
	 * @param fileName
	 *            filename
	 * @param md5fallback
	 *            whether to accept partial match where md5 is OK and rest of
	 *            filename is not
	 * @return image element corresponding to filename
	 */
	public HTMLImageElement getExternalImage(String fileName, boolean md5fallback) {
		HTMLImageElement match = getMatch(fileName);
		if (match == null) {
			match = getMatch(StringUtil.changeFileExtension(fileName,
					FileExtensions.PNG));
		}
		// FIXME this is a bit hacky: if we did not get precise match, assume
		// encoding problem and rely on MD5
		// Only do this for lookup, not on file load: the file may have two
		// different images with same prefix
		if (match == null && md5fallback
				&& fileName.length() > GeoImage.MD5_FOLDER_LENGTH) {
			String md5 = fileName.substring(0, GeoImage.MD5_FOLDER_LENGTH);
			for (Entry<String, HTMLImageElement> entry : externalImageTable
					.entrySet()) {
				String s = entry.getKey();
				if (md5.equals(s.substring(0, GeoImage.MD5_FOLDER_LENGTH))) {
					return entry.getValue();
				}
			}
		}
		return match;
	}

	private HTMLImageElement getMatch(String fileName) {
		return externalImageTable.get(StringUtil.removeLeadingSlash(fileName));
	}

	/**
	 * @param fileName filename
	 * @return corresponding image element
	 */
	public HTMLImageElement getInternalImage(String fileName) {
		return internalImageTable.get(StringUtil.removeLeadingSlash(fileName));
	}

	static void onError(GeoImage gi) {
		if (gi.getStartPoint(0) != null) {
			gi.getStartPoint(0).remove();
			gi.getStartPoint(1).remove();
		}
		gi.remove();
		gi.getKernel().notifyRepaint();
	}

	/**
	 * Load a single image.
	 * @param imageFileName filename
	 * @param geoi image to update
	 */
	public void triggerSingleImageLoading(String imageFileName, GeoImage geoi) {
		HTMLImageElement img = getExternalImage(imageFileName, true);
		img.addEventListener("load", (event) -> geoi.updateRepaint());
		EventListener errorCallback = (event) -> onError(geoi);
		img.addEventListener("error", errorCallback);
		img.addEventListener("abort", errorCallback);
		img.src = externalImageSrcs.get(imageFileName).createUrl();
	}

	/**
	 * Trigger loading of a single image not (yet) connected to a GeoImage
	 * @param imageFileName filename
	 * @param kernel image for construction
	 */
	public void triggerSingleImageLoading(String imageFileName, Kernel kernel) {
		HTMLImageElement img = getExternalImage(imageFileName, true);
		img.addEventListener("load", (event) ->
				updateCascadeImages(kernel.getConstruction()));
		img.src = externalImageSrcs.get(imageFileName).createUrl();
	}

	private void updateCascadeImages(Construction cons) {
		HashMap<String, GeoElement> table = cons.getGeoTable();
		if (table == null || table.isEmpty()) {
			return;
		}
		List<GeoElement> list = table.values().stream()
				.filter(t -> t.isGeoImage() || t.getFillType() == FillType.IMAGE)
				.collect(Collectors.toList());
		GeoElement.updateCascade(list);
	}

	/**
	 * Load all images and tun callback after all are loaded.
	 * 
	 * @param run
	 *            image load callback
	 * @param toLoad
	 *            map of images to be loaded
	 */
	public void triggerImageLoading(final Runnable run, final Map<String, ArchiveEntry> toLoad) {
		this.imagesLoaded = 0;
		for (Entry<String, ArchiveEntry> imgSrc : toLoad.entrySet()) {
			HTMLImageElement el = getExternalImage(imgSrc.getKey(), true);
			el.addEventListener("load", (event) -> checkIfAllLoaded(run, toLoad));
			el.addEventListener("error", (event) -> el.src = getErrorURL());
			el.src = imgSrc.getValue().createUrl();
		}
	}

	/**
	 * @return has images because of async call of geogebra.xml if images
	 *         exists, but not loaded yet.
	 */
	public boolean hasImages() {
		return !externalImageTable.isEmpty();
	}

	/**
	 * @param resource
	 *            resource
	 * @return img element corresponding to the resource
	 */
	public static HTMLImageElement getInternalImage(ResourcePrototype resource) {
		HTMLImageElement img = Dom.createImage();
		img.src = NoDragImage.safeURI(resource);
		return img;
	}

	/**
	 * @param fileName of the image
	 * @param content of the image
	 * @return img element corresponding to the resource
	 */
	public HTMLImageElement addInternalImage(String fileName, String content) {
		String fn = StringUtil.removeLeadingSlash(fileName);
		HTMLImageElement img = Dom.createImage();
		internalImageTable.put(fn, img);
		img.src = content;
		return img;
	}

	private void replace(String fileName, String newName) {
		if (fileName.equals(newName)) {
			return;
		}
		HTMLImageElement el = this.externalImageTable.get(fileName);
		ArchiveEntry image = this.externalImageSrcs.get(fileName);

		this.externalImageTable.put(newName, el);
		this.externalImageSrcs.put(newName, image);
	}

	/**
	 * @return URL of error image
	 */
	public String getErrorURL() {
		return GuiResourcesSimple.INSTANCE.questionMark().getSafeUri().asString();
	}

	/**
	 * @return whether to prevent images from being auxiliary
	 */
	public boolean isPreventAuxImage() {
		if (preventAuxImage) {
			preventAuxImage = false;
			return true;
		}
		return false;
	}

	public void setPreventAuxImage(boolean value) {
		this.preventAuxImage = value;
	}

	/**
	 * Convert all images to saveable format (png or svg).
	 * 
	 * @param cons
	 *            construction
	 */
	public void adjustConstructionImages(Construction cons) {
		// save all GeoImage images
		// TreeSet images =
		// cons.getGeoSetLabelOrder(GeoElement.GEO_CLASS_IMAGE);
		TreeSet<GeoElement> geos = cons.getGeoSetLabelOrder();
		if (geos == null) {
			return;
		}

		for (GeoElement geo : geos) {
			String fileName = geo.getImageFileName();
			// for some reason we sometimes get null and sometimes "" if there
			// is no image used
			if (fileName != null && fileName.length() > 0) {
				geo.getGraphicsAdapter().convertToSaveableFormat();
				String newName = geo.getGraphicsAdapter().getImageFileName();
				replace(fileName, newName);
			}
		}
	}

	/**
	 * Prefix filename with a hash.
	 * 
	 * @param imgFileName
	 *            original filename
	 * @param fileStr
	 *            file content (base64)
	 * @return filename with MD5 hash as directory
	 */
	public static String getMD5FileName(String imgFileName, String fileStr) {
		String zipDirectory = MD5EncrypterGWTImpl.encrypt(fileStr);

		String fn = imgFileName;
		int index = imgFileName.lastIndexOf('/');
		if (index != -1) {
			// filename without path
			fn = fn.substring(index + 1);
		}

		fn = Util.checkImageExtension(Util.processFilename(fn));

		// filename will be of form
		// "a04c62e6a065b47476607ac815d022cc/liar.gif"
		return zipDirectory + '/' + fn;
	}

	/**
	 * @param cons
	 *            construction
	 * @param filePath
	 *            path
	 * @param archive
	 *            file
	 */
	public void writeConstructionImages(Construction cons, String filePath,
			GgbFile archive) {
		TreeSet<GeoElement> geos = cons.getGeoSetLabelOrder();
		if (geos == null) {
			return;
		}

		for (GeoElement geo : geos) {
			String fileName = geo.getImageFileName();
			if (!"".equals(fileName)) {
				ArchiveEntry url = getExternalImageData(fileName);
				FileExtensions ext = StringUtil.getFileExtension(fileName);

				MyImageW img = (MyImageW) geo.getFillImage();
				addImageToArchive(filePath, fileName, url, ext, img, archive);
			}
		}
	}

	private static void addImageToArchive(String filePath, String fileName,
			ArchiveEntry data, FileExtensions ext, MyImageW img,
			GgbFile archive) {
		if (data == null) {
			return;
		}

		String url = data.string;
		ArchiveEntry dataURL;
		String fullPath;
		if (ext.isAllowedImage()) {
			// png, jpg, jpeg
			fullPath = filePath + fileName;
		} else {
			// not supported, so saved as PNG
			fullPath = filePath + StringUtil
					.changeFileExtension(fileName, FileExtensions.PNG);
		}
		if ((url == null || url.startsWith("http"))
				&& data.data == null && (img != null && img.getImage() != null)) {
			dataURL = new ArchiveEntry(fullPath, convertImgToPng(img));
		} else if (url != null && ext == FileExtensions.SVG) {
			dataURL = new ArchiveEntry(fullPath, convertSvgDataUrl(url));
		} else {
			dataURL = data;
		}

		if (!dataURL.isEmpty()) {
			archive.put(fullPath, dataURL);
		}
	}

	private static String convertImgToPng(MyImageW img) {
		String url;
		HTMLCanvasElement cv = (HTMLCanvasElement) DomGlobal.document.createElement("canvas");
		cv.width = img.getWidth();
		cv.height = img.getHeight();
		CanvasRenderingContext2D c2d = Js.uncheckedCast(cv.getContext("2d"));
		c2d.drawImage(img.getImage(), 0, 0);
		url = cv.toDataURL("image/png");
		// Opera and Safari cannot toDataUrl jpeg (much less the others)
		// if ("jpg".equals(ext) || "jpeg".equals(ext))
		// addImageToZip(filePath + fileName, cv.toDataUrl("image/jpg"));
		// else
		return url;
	}

	private static String convertSvgDataUrl(String dataUrl) {
		// remove eg data:image/svg+xml;base64,
		int index = dataUrl.indexOf(',');
		String svgAsXML = dataUrl.substring(index + 1);
		svgAsXML = Browser.decodeBase64(svgAsXML);
		return svgAsXML;
	}

	/**
	 * @param macros
	 *            macros
	 * @param archive
	 *            archive
	 */
	public void writeMacroImages(List<Macro> macros, GgbFile archive) {
		if (macros == null) {
			return;
		}

		for (Macro macro : macros) {
			// save all images in macro construction
			// macro may contain images GGB-1865
			writeConstructionImages(macro.getMacroConstruction(), "", archive);
			String fileName = macro.getIconFileName();
			if (fileName != null && !fileName.isEmpty()) {
				ArchiveEntry url = getExternalImageData(fileName);
				if (url != null) {
					FileExtensions ext = StringUtil.getFileExtension(fileName);
					MyImageW img = null;
					if (url.string != null) {
						HTMLImageElement elem = Dom.createImage();
						elem.src = url.string;
						img = new MyImageW(elem, FileExtensions.SVG.equals(ext));
					}
					addImageToArchive("", fileName, url, ext, img, archive);
				}
			}
		}
	}
}
