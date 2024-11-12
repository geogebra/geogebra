package org.geogebra.web.html5.main;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.ImageManager;
import org.geogebra.web.html5.gui.laf.VendorSettings;
import org.geogebra.web.html5.safeimage.ConvertToCanvas;
import org.geogebra.web.html5.safeimage.ImagePreprocessor;
import org.geogebra.web.html5.safeimage.SVGPreprocessor;
import org.geogebra.web.html5.safeimage.SafeImage;
import org.geogebra.web.html5.safeimage.SafeImageProvider;
import org.geogebra.web.html5.util.ArchiveEntry;
import org.geogebra.web.html5.util.ImageManagerW;

import elemental2.dom.HTMLImageElement;

/**
 * Factory to create a GeoImage from content and corner points optionally.
 *
 * @author laszlo
 */
public class SafeGeoImageFactory implements SafeImageProvider {
	private final AppW app;
	private final Construction construction;
	private final AlgebraProcessor algebraProcessor;
	private final ImageManagerW imageManager;
	private final VendorSettings vendor;
	private GeoImage geoImage;
	private ArchiveEntry imageFile;
	private HTMLImageElement imageElement;
	private boolean autoCorners;
	private GeoPointND corner1 = null;
	private GeoPointND corner2 = null;
	private GeoPointND corner4 = null;

	/**
	 * Constructor
	 *
	 * @param app the Application
	 */
	public SafeGeoImageFactory(AppW app) {
		this.app = app;
		construction = app.getKernel().getConstruction();
		algebraProcessor = app.getKernel().getAlgebraProcessor();
		imageManager = app.getImageManager();
		autoCorners = true;
		vendor = app.getVendorSettings();
	}

	/**
	 * Constructor
	 *
	 * @param app the application
	 * @param image the base object of the resulting image.
	 */
	public SafeGeoImageFactory(AppW app, GeoImage image) {
		this(app);
		geoImage = image;
	}

	/**
	 * Create the GeoImage setup by the factory.
	 * @param fileName of the image.
	 * @param content of the image
	 * @return the corresponding GeoImage object
	 */
	public GeoImage create(String fileName, String content,
			@Nullable FileExtensions originalExtension) {
		ensureResultImageExists();
		ArchiveEntry imageFile = new ArchiveEntry(fileName, content);
		SafeImage safeImage = new SafeImage(imageFile, this,
				getPreprocessors(), originalExtension);
		safeImage.process();
		return geoImage;
	}

	/**
	 * Create the GeoImage setup by the factory for an internal file.
	 * @param fileName of the image.
	 * @param content of the image
	 * @return the corresponding GeoImage object
	 */
	public GeoImage createInternalFile(String fileName, String content) {
		ensureResultImageExists();
		ArchiveEntry imageFile = new ArchiveEntry(fileName, content);
		onReadyInternal(imageFile, content);
		return geoImage;
	}

	private List<ImagePreprocessor> getPreprocessors() {
		ArrayList<ImagePreprocessor> preprocessors = new ArrayList<>();

		int maxImageSize = app.getAppletParameters().getParamMaxImageSize();
		if (app.getAppletParameters().getDataParamApp() && maxImageSize == 0) {
			maxImageSize = 1024;
		}
		preprocessors.add(new ConvertToCanvas(maxImageSize, vendor.hasBitmapSecurity()));
		preprocessors.add(new SVGPreprocessor());

		return preprocessors;
	}

	private void ensureResultImageExists() {
		if (geoImage == null) {
			geoImage = new GeoImage(construction);
		}
	}

	@Override
	public void onReady(ArchiveEntry imageFile) {
		this.imageFile = imageFile;
		imageManager.addExternalImage(imageFile.getFileName(),
				imageFile);
		imageManager.triggerSingleImageLoading(imageFile.getFileName(),
				geoImage);
		imageElement = imageManager.getExternalImage(imageFile.getFileName(), true);

		imageElement.addEventListener("load", (event) -> onLoad(false));
		imageElement.addEventListener("error",
				(event) -> imageElement.src = imageManager.getErrorURL());
	}

	private void onReadyInternal(ArchiveEntry imageFile, String content) {
		this.imageFile = imageFile;
		imageElement =
				imageManager.addInternalImage(imageFile.getFileName(), content);
		imageElement.addEventListener("load", (event) -> geoImage.updateRepaint());
		imageElement.addEventListener("load", (event) -> onLoad(true));
		imageElement.addEventListener("error",
				(event) -> imageElement.src = imageManager.getErrorURL());
	}

	private void onLoad(boolean isInternalFile) {
		if (isInternalFile) {
			geoImage.setInternalImageFileName(imageFile.getFileName(),
					imageElement.width, imageElement.height);
		} else {
			geoImage.setImageFileName(imageFile.getFileName(),
					imageElement.width, imageElement.height);
		}

		if (autoCorners) {
			app.getGuiManager().setImageCornersFromSelection(geoImage);
		} else {
			setManualCorners();
		}

		if (imageManager.isPreventAuxImage()) {
			geoImage.setAuxiliaryObject(false);
		}
		if (app.isWhiteboardActive()) {
			app.getActiveEuclidianView().getEuclidianController()
					.selectAndShowSelectionUI(geoImage);
		}
		app.setDefaultCursor();
		if (!isInternalFile) {
			addImageSafelyToUndoManager();
		}
		geoImage.setImagePropertiesIfNecessary();
	}

	private void setManualCorners() {
		if (corner1 != null) {
			geoImage.setCorner(corner1, 0);

			if (corner2 == null) {
				corner2 = new GeoPoint(construction, 0, 0, 1);
				geoImage.calculateCornerPoint((GeoPoint) corner2, 2);
			}
			geoImage.setCorner(corner2, 1);

			// make sure 2nd corner is on screen
			ImageManager.ensure2ndCornerOnScreen(
					corner1.getInhomX(), corner2, app);

			if (corner4 != null) {
				geoImage.setCorner(corner4, 2);
			}
			geoImage.setLabel(null);
			GeoImage.updateInstances(app);
		}
	}

	/**
	 * Adds the created GeoImage to the UndoManager while making sure all corner points and the
	 * crop box are correctly initialized and all defined and labeled start points are added
	 */
	private void addImageSafelyToUndoManager() {
		geoImage.matrixTransform(1, 0, 0, 1);
		geoImage.ensureCropBox();
		ArrayList<GeoElement> geosToStore = new ArrayList<>();
		geosToStore.addAll(geoImage.getDefinedAndLabeledStartPoints());
		geosToStore.add(geoImage);
		app.getUndoManager().storeAddGeo(geosToStore);
	}

	/**
	 *
	 * @param autoCorners if factory should create corners auotmatically
	 * @return factory object with autoCorners set.
	 */
	public SafeGeoImageFactory withAutoCorners(boolean autoCorners) {
		this.autoCorners = autoCorners;
		return this;
	}

	/**
	 * Sets corners for the image
	 *
	 * @param cornerLabel1 Corner1
	 * @param cornerLabel2 Corner2
	 * @param cornerLabel4 Corner4
	 * @return factory object with corners set.
	 */
	public SafeGeoImageFactory withCorners(String cornerLabel1, String cornerLabel2,
			String cornerLabel4) {
		this.corner1 = asPoint(cornerLabel1);
		this.corner2 = asPoint(cornerLabel2);
		this.corner4 = asPoint(cornerLabel4);
		return this;
	}

	/**
	 * Sets corners for the image
	 *
	 * @param corner1 Corner1
	 * @param corner2 Corner2
	 * @return factory object with corners set.
	 */
	public SafeGeoImageFactory withCorners(GeoPointND corner1, GeoPointND corner2) {
		this.corner1 = corner1;
		this.corner2 = corner2;
		return this;
	}

	private GeoPointND asPoint(String label) {
		return label == null ? null :  algebraProcessor
				.evaluateToPoint(label, null, true);
	}
}
