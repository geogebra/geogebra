package org.geogebra.web.html5.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.ImageManager;
import org.geogebra.common.util.MD5EncrypterGWTImpl;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.Util;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GgbFile;
import org.geogebra.web.html5.main.MyImageW;

import com.google.gwt.resources.client.ResourcePrototype;

import elemental2.dom.CanvasRenderingContext2D;
import elemental2.dom.DomGlobal;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.HTMLImageElement;
import jsinterop.base.Js;

public class ImageManagerW extends ImageManager {

	private HashMap<String, HTMLImageElement> externalImageTable = new HashMap<>();
	private HashMap<String, String> externalImageSrcs = new HashMap<>();
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
			Log.debug("addExternalImage: " + fileName);
			String fn = StringUtil.removeLeadingSlash(fileName);
			HTMLImageElement img = Dom.createImage();
			externalImageSrcs.put(fn, src);
			externalImageTable.put(fn, img);
		}
	}

	@Override
	public String getExternalImageSrc(String fileName) {
		return externalImageSrcs.get(StringUtil.removeLeadingSlash(fileName));
	}

	protected void checkIfAllLoaded(AppW app1, Runnable run,
			Map<String, String> toLoad) {
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
	 * @param app1
	 *            application
	 * @param md5fallback
	 *            whether to accept partial match where md5 is OK and rest of
	 *            filename is not
	 * @return image element corresponding to filename
	 */
	public HTMLImageElement getExternalImage(String fileName, AppW app1,
			boolean md5fallback) {
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
				&& fileName.length() > app1.getMD5folderLength(fileName)) {
			int md5length = app1.getMD5folderLength(fileName);
			String md5 = fileName.substring(0, md5length);
			for (Entry<String, HTMLImageElement> entry : externalImageTable
					.entrySet()) {
				String s = entry.getKey();
				if (md5.equals(s.substring(0, md5length))) {
					return entry.getValue();
				}
			}
		}
		return match;
	}

	private HTMLImageElement getMatch(String fileName) {
		return externalImageTable.get(StringUtil.removeLeadingSlash(fileName));
	}

	static void onError(GeoImage gi) {
		gi.getCorner(0).remove();
		gi.getCorner(1).remove();
		gi.remove();
		gi.getKernel().notifyRepaint();
	}

	/**
	 * Load a single image.
	 * 
	 * @param imageFileName
	 *            filename
	 * @param geoi
	 *            image for construction
	 */
	public void triggerSingleImageLoading(String imageFileName, GeoImage geoi) {
		HTMLImageElement img = getExternalImage(imageFileName, (AppW) geoi
				.getKernel().getApplication(), true);
		img.addEventListener("load", (event) -> geoi.updateRepaint());
		EventListener errorCallback = (event) -> onError(geoi);
		img.addEventListener("error", errorCallback);
		img.addEventListener("abort", errorCallback);
		img.src = externalImageSrcs.get(imageFileName);
	}

	/**
	 * Load all images and tun callback after all are loaded.
	 * 
	 * @param app
	 *            app
	 * @param run
	 *            image load callback
	 * @param toLoad
	 *            map of images to be loaded
	 */
	public void triggerImageLoading(final AppW app,
			final Runnable run, final Map<String, String> toLoad) {
		this.imagesLoaded = 0;
		for (Entry<String, String> imgSrc : toLoad.entrySet()) {
			HTMLImageElement el = getExternalImage(imgSrc.getKey(), app, true);
			el.addEventListener("load", (event) -> checkIfAllLoaded(app, run, toLoad));
			el.addEventListener("error", (event) -> el.src = getErrorURL());
			el.src = imgSrc.getValue();
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

	private void replace(String fileName, String newName) {
		if (fileName.equals(newName)) {
			return;
		}
		HTMLImageElement el = this.externalImageTable.get(fileName);
		String src = this.externalImageSrcs.get(fileName);

		this.externalImageTable.put(newName, el);
		this.externalImageSrcs.put(newName, src);
	}

	/**
	 * @return URL of error image
	 */
	public String getErrorURL() {
		return GuiResourcesSimple.INSTANCE.questionMark().getSafeUri()
		        .asString();
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
				String url = getExternalImageSrc(fileName);
				FileExtensions ext = StringUtil.getFileExtension(fileName);

				MyImageW img = (MyImageW) geo.getFillImage();
				addImageToArchive(filePath, fileName, url, ext, img, archive);
			}
		}
	}

	private static void addImageToArchive(String filePath, String fileName,
			String url, FileExtensions ext, MyImageW img,
			Map<String, String> archive) {
		if (ext.equals(FileExtensions.SVG)) {
			addSvgToArchive(fileName, img, archive);
			return;
		}
		String dataURL;
		if ((url == null || url.startsWith("http"))
				&& (img != null && img.getImage() != null)) {
			dataURL = convertImgToPng(img);
		} else {
			dataURL = url;
		}
		if (dataURL != null) {
			if (ext.isAllowedImage()) {
				// png, jpg, jpeg
				// NOT SVG (filtered earlier)
				addImageToZip(filePath + fileName, dataURL, archive);
			} else {
				// not supported, so saved as PNG
				addImageToZip(filePath + StringUtil
						.changeFileExtension(fileName, FileExtensions.PNG),
						dataURL, archive);
			}
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

	private static void addSvgToArchive(String fileName, MyImageW img,
			Map<String, String> archive) {
		HTMLImageElement svg = img.getImage();

		// TODO
		// String svgAsXML =
		// "<svg width=\"100\" height=\"100\"> <circle cx=\"50\" cy=\"50\"
		// r=\"40\" stroke=\"green\" stroke-width=\"4\" fill=\"yellow\"
		// /></svg>";
		String svgAsXML = svg.getAttribute("src");

		// remove eg data:image/svg+xml;base64,
		int index = svgAsXML.indexOf(',');
		svgAsXML = svgAsXML.substring(index + 1);

		svgAsXML = Browser.decodeBase64(svgAsXML);

		Log.debug("svgAsXML (decoded): " + svgAsXML.length() + "bytes");

		archive.put(fileName, svgAsXML);
	}

	/**
	 * @param filename
	 *            image filename
	 * @param base64img
	 *            base64 content
	 * @param archive
	 *            archive
	 */
	public static void addImageToZip(String filename, String base64img,
			Map<String, String> archive) {
		archive.put(filename, base64img);
	}

	/**
	 * @param macros
	 *            macros
	 * @param archive
	 *            archive
	 */
	public void writeMacroImages(ArrayList<Macro> macros, GgbFile archive) {
		if (macros == null) {
			return;
		}

		for (Macro macro : macros) {
			// save all images in macro construction
			// macro may contain images GGB-1865
			writeConstructionImages(macro.getMacroConstruction(), "", archive);
			String fileName = macro.getIconFileName();
			if (fileName != null && !fileName.isEmpty()) {
				String url = getExternalImageSrc(fileName);
				if (url != null) {
					FileExtensions ext = StringUtil.getFileExtension(fileName);

					HTMLImageElement elem = Dom.createImage();
					elem.src = url;

					MyImageW img = new MyImageW(elem, FileExtensions.SVG.equals(ext));

					addImageToArchive("", fileName, url, ext, img, archive);
				}
			}
		}
	}
}
