package org.geogebra.web.html5.safeimage;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GgbFile;
import org.geogebra.web.html5.util.ImageManagerW;

/**
 * Loader of images in the ggb archive.
 *
 * @author laszlo
 */
public class ImageLoader {
	private final ImageManagerW imageManager;
	private final GgbFile archiveContent;
	private final AppW app;
	private final GgbFile originalArchive;
	private final Runnable afterImages;
	private final HashMap<String, String> images;
	private int loadCount;

	/**
	 *
	 * @param app the application.
	 * @param archive the ggb file to load images from.
	 * @param afterImages callback to run after all images are loaded.
	 */
	public ImageLoader(AppW app, GgbFile archive, GgbFile originalArchive, Runnable afterImages) {
		this.app = app;
		this.imageManager = app.getImageManager();
		this.archiveContent = archive;
		this.originalArchive = originalArchive;
		this.afterImages = afterImages;
		images = new HashMap<>();
	}

	/**
	 * Do the loading itself.
	 */
	public void load() {
		for (Map.Entry<String, String> entry : archiveContent.entrySet()) {
			ImageFile imageFile = new ImageFile(entry.getKey(), entry.getValue());
			if (isValid(imageFile)) {
				addImage(imageFile);
			}
		}

		if (isAllImagesLoaded()) {
			onLoadAll();
		}
	}

	private boolean isValid(ImageFile imageFile) {
		return !(hasImage(imageFile.getFileName()) || imageFile.isThumbnail()
				|| !imageFile.isValid());
	}

	private boolean hasImage(String key) {
		return imageManager.getExternalImage(key, app, false) != null;
	}

	private	 void addImage(ImageFile imageFile) {
		String content = SVGUtil.match(imageFile.getExtension())
				? SVGUtil.fixAndEncode(imageFile.getContent())
				: imageFile.getContent();
		imageManager.addExternalImage(imageFile.getFileName(),
				content);

		images.put(imageFile.getFileName(),
				content);
		loadCount++;
	}

	private boolean isAllImagesLoaded() {
		return images.size() == loadCount;
	}

	private void onLoadAll() {
		if (images.isEmpty()) {
			afterImages.run();
		} else {
			// on images do nothing here: wait for callback when images loaded.
			imageManager.triggerImageLoading(app, afterImages, images);
		}

		app.setCurrentFile(originalArchive);
	}
}
