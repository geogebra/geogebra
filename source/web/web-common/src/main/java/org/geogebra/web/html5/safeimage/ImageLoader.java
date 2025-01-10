package org.geogebra.web.html5.safeimage;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GgbFile;
import org.geogebra.web.html5.util.ArchiveEntry;
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
	private final HashMap<String, ArchiveEntry> images;
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
		for (Map.Entry<String, ArchiveEntry> entry : archiveContent.entrySet()) {
			if (isValid(entry.getValue())) {
				addImage(entry.getValue());
			}
		}

		if (isAllImagesLoaded()) {
			onLoadAll();
		}
	}

	private boolean isValid(ArchiveEntry imageFile) {
		return !(hasImage(imageFile.getFileName()) || imageFile.isThumbnail()
				|| !imageFile.getExtension().isImage());
	}

	private boolean hasImage(String key) {
		return imageManager.getExternalImage(key, false) != null;
	}

	private void addImage(ArchiveEntry imageFile) {
		ArchiveEntry content = SVGUtil.match(imageFile.getExtension())
				? new ArchiveEntry(imageFile.getFileName(), SVGUtil.fixAndEncode(imageFile.string))
				: imageFile;
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
			imageManager.triggerImageLoading(afterImages, images);
		}

		app.setCurrentFile(originalArchive);
	}
}
