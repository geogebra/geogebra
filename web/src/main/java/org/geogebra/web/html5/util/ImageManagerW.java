package org.geogebra.web.html5.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.ImageManager;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gawt.GBufferedImageW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.resources.client.ImageResource;

public class ImageManagerW extends ImageManager {

	private HashMap<String, ImageElement> externalImageTable = new HashMap<>();
	private HashMap<String, String> externalImageSrcs = new HashMap<>();
	private boolean preventAuxImage;
	protected int imagesLoaded = 0;

	public void reset() {
		externalImageTable = new HashMap<>();
		externalImageSrcs = new HashMap<>();
		imagesLoaded = 0;
	}

	public void addExternalImage(String fileName, String src) {
		if (fileName != null && src != null) {
			Log.debug("addExternalImage: " + fileName);
			String fn = StringUtil.removeLeadingSlash(fileName);
			ImageElement img = Document.get().createImageElement();
			externalImageSrcs.put(fn, src);
			externalImageTable.put(fn, img);
		}
	}

	public String getExternalImageSrc(String fileName) {
		return externalImageSrcs.get(StringUtil.removeLeadingSlash(fileName));
	}

	protected void checkIfAllLoaded(AppW app1, Runnable run) {
		imagesLoaded++;
		if (imagesLoaded == externalImageSrcs.size()) {
			run.run();
			imagesLoaded = 0;
		}
	}

	public ImageElement getExternalImage(String fileName, AppW app1) {
		ImageElement match = getMatch(fileName);
		if(match == null){
			match = getMatch(StringUtil.changeFileExtension(fileName,
					FileExtensions.PNG));
		}
		// FIXME this is a bit hacky: if we did not get precise match, assume
		// encoding problem and rely on MD5
		if (match == null
				&& fileName.length() > app1.getMD5folderLength(fileName)) {
			int md5length = app1.getMD5folderLength(fileName);
			String md5 = fileName.substring(0, md5length);
			for (Entry<String, ImageElement> entry : externalImageTable
					.entrySet()) {
				String s = entry.getKey();
				if (md5.equals(s.substring(0, md5length))) {
					return entry.getValue();
				}
			}
		}
		return match;
	}

	private ImageElement getMatch(String fileName) {
		return externalImageTable.get(StringUtil.removeLeadingSlash(fileName));
	}

	public static GBufferedImage toBufferedImage(ImageElement im) {
		return new GBufferedImageW(im);
	}

	static class ImageLoadCallback2 implements ImageLoadCallback {
		public GeoImage gi;

		public ImageLoadCallback2(GeoImage gi2) {
			this.gi = gi2;
		}

		@Override
		public void onLoad() {
			gi.updateRepaint();
		}
	}

	static class ImageErrorCallback2 implements ImageLoadCallback {
		public GeoImage gi;
		private AppW app;

		public ImageErrorCallback2(GeoImage gi2, AppW app) {
			this.gi = gi2;
			this.app = app;
		}

		@Override
		public void onLoad() {
			// Image onerror and onabort actually
			gi.getCorner(0).remove();
			gi.getCorner(1).remove();
			gi.remove();
			app.getKernel().notifyRepaint();
		}
	}

	public void triggerSingleImageLoading(String imageFileName, GeoImage geoi) {
		ImageElement img = getExternalImage(imageFileName, (AppW) geoi
				.getKernel().getApplication());
		ImageWrapper.nativeon(img, "load", new ImageLoadCallback2(geoi));
		ImageErrorCallback2 i2 = new ImageErrorCallback2(geoi, (AppW) geoi
				.getKernel().getApplication());
		ImageWrapper.nativeon(img, "error", i2);
		ImageWrapper.nativeon(img, "abort", i2);
		img.setSrc(externalImageSrcs.get(imageFileName));
	}

	public void triggerImageLoading(final AppW app,
			final Runnable run) {

		if (externalImageSrcs.entrySet() != null) {
			for (Entry<String, String> imgSrc : externalImageSrcs.entrySet()) {
				ImageWrapper img = new ImageWrapper(
						getExternalImage(imgSrc.getKey(), app));
				img.attachNativeLoadHandler(this, new ImageLoadCallback() {

					@Override
					public void onLoad() {
						checkIfAllLoaded(app, run);
					}
				});
				img.getElement().setSrc(imgSrc.getValue());
			}
		}
	}

	/**
	 * @return has images because of async call of geogebra.xml if images
	 *         exists, but not loaded yet.
	 */
	public boolean hasImages() {
		return !externalImageTable.isEmpty();
	}

	public static ImageElement getInternalImage(ImageResource resource) {
		ImageElement img = Document.get().createImageElement();
		img.setSrc(resource.getSafeUri().asString());
		return img;
	}

	private void replace(String fileName, String newName) {
		if (fileName.equals(newName)) {
			return;
		}
		ImageElement el = this.externalImageTable.get(fileName);
		String src = this.externalImageSrcs.get(fileName);

		this.externalImageTable.put(newName, el);
		this.externalImageSrcs.put(newName, src);
	}

	public String getErrorURL() {
		return GuiResourcesSimple.INSTANCE.questionMark().getSafeUri()
		        .asString();
	}

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

	public void adjustConstructionImages(Construction cons) {
		// save all GeoImage images
		// TreeSet images =
		// cons.getGeoSetLabelOrder(GeoElement.GEO_CLASS_IMAGE);
		TreeSet<GeoElement> geos = cons.getGeoSetLabelOrder();
		if (geos == null) {
			return;
		}

		Iterator<GeoElement> it = geos.iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
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
}
