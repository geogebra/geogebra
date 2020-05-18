package org.geogebra.web.html5.safeimage;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GgbFile;
import org.geogebra.web.html5.util.ImageManagerW;

/**
 * Loader of images in the ggb archive.
 * It ensures that no suspicious content will be in the
 * loaded images.
 *
 * @author laszlo
 */
public class SafeImageLoader implements SafeImageProvider {
	private final ImageManagerW imageManager;
	private final GgbFile archiveContent;
	private AppW app;
	private Runnable afterImages;
	private final HashMap<String, String> images;
	private int imagesRequested;

	/**
	 *
	 * @param app the application.
	 * @param archive the ggb file to load images from.
	 * @param afterImages callback to run after all images are loaded.
	 */
	public SafeImageLoader(AppW app, GgbFile archive, Runnable afterImages) {
		this.app = app;
		this.imageManager = app.getImageManager();
		this.archiveContent = archive;
		this.afterImages = afterImages;
		images = new HashMap<>();
	}

	/**
	 * Do the loading itself.
	 */
	public void load() {
		imagesRequested = 0;

		for (Map.Entry<String, String> entry : archiveContent.entrySet()) {
			ImageFile imageFile = new ImageFile(entry.getKey(), entry.getValue());
			if (isValid(imageFile)) {
				requestSafeImage(imageFile);
			}
		}

		if (imagesRequested == 0) {
			onLoadAll();
		}
	}

	private void requestSafeImage(ImageFile imageFile) {
		imagesRequested++;
		SafeImage safeImage = new SafeImage(imageFile, this);
		safeImage.process();
	}

	private boolean isValid(ImageFile imageFile) {
		return !(hasImage(imageFile.getFileName()) || imageFile.isThumbnail()
				|| !imageFile.isValid());
	}

	private boolean hasImage(String key) {
		return imageManager.getExternalImage(key, app, false) != null;
	}

	@Override
	public void onReady(ImageFile processedImage) {
		imageManager.addExternalImage(processedImage.getFileName(),
				processedImage.getContent());

		images.put(processedImage.getFileName(),
				processedImage.getContent());

		if (isAllImagesLoaded()) {
			onLoadAll();
		}
	}

	private boolean isAllImagesLoaded() {
		return images.size() == imagesRequested;
	}

	private void onLoadAll() {
		if (images.isEmpty()) {
			afterImages.run();
		} else {
			// on images do nothing here: wait for callback when images loaded.
			imageManager.triggerImageLoading(app, afterImages, images);
		}

		app.setCurrentFile(archiveContent);
	}
}
