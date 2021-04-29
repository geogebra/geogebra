package org.geogebra.web.html5.main;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.util.ImageManager;
import org.geogebra.web.html5.gui.laf.VendorSettings;
import org.geogebra.web.html5.safeimage.ConvertToCanvas;
import org.geogebra.web.html5.safeimage.ImageFile;
import org.geogebra.web.html5.safeimage.ImagePreprocessor;
import org.geogebra.web.html5.safeimage.SVGPreprocessor;
import org.geogebra.web.html5.safeimage.SafeImage;
import org.geogebra.web.html5.safeimage.SafeImageProvider;
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
	private ImageFile imageFile;
	private HTMLImageElement imageElement;
	private boolean autoCorners;
	private String cornerLabel1 = null;
	private String cornerLabel2 = null;
	private String cornerLabel4 = null;

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
	public GeoImage create(String fileName, String content) {
		ensureResultImageExists();
		ImageFile imageFile = new ImageFile(fileName, content);
		SafeImage safeImage = new SafeImage(imageFile, this, getPreprocessors());
		safeImage.process();
		return geoImage;
	}

	private List<ImagePreprocessor> getPreprocessors() {
		ArrayList<ImagePreprocessor> preprocessors = new ArrayList<>();
		preprocessors.add(new SVGPreprocessor());

		if (vendor.hasBitmapSecurity()) {
			preprocessors.add(new ConvertToCanvas());
		}

		return preprocessors;
	}

	private void ensureResultImageExists() {
		if (geoImage == null) {
			geoImage = new GeoImage(construction);
		}
	}

	@Override
	public void onReady(ImageFile imageFile) {
		this.imageFile = imageFile;
		imageManager.addExternalImage(imageFile.getFileName(),
				imageFile.getContent());
		imageManager.triggerSingleImageLoading(imageFile.getFileName(),
				geoImage);
		imageElement = imageManager.getExternalImage(imageFile.getFileName(), app, true);

		imageElement.addEventListener("load", (event) -> onLoad());
		imageElement.addEventListener("error",
				(event) -> imageElement.src = imageManager.getErrorURL());
	}

	private void onLoad() {
		geoImage.setImageFileName(imageFile.getFileName(),
				imageElement.width, imageElement.height);

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
		app.storeUndoInfo();
	}

	private void setManualCorners() {
		if (cornerLabel1 != null) {

			GeoPointND corner1 = algebraProcessor
					.evaluateToPoint(cornerLabel1, null, true);
			geoImage.setCorner(corner1, 0);

			GeoPoint corner2;
			if (cornerLabel2 != null) {
				corner2 = (GeoPoint) algebraProcessor
						.evaluateToPoint(cornerLabel2, null, true);
			} else {
				corner2 = new GeoPoint(construction, 0, 0, 1);
				geoImage.calculateCornerPoint(corner2,
						2);
			}
			geoImage.setCorner(corner2, 1);

			// make sure 2nd corner is on screen
			ImageManager.ensure2ndCornerOnScreen(
					corner1.getInhomX(), corner2, app);

			if (cornerLabel4 != null) {
				GeoPointND corner4 = algebraProcessor
						.evaluateToPoint(cornerLabel4, null, true);
				geoImage.setCorner(corner4, 2);
			}
			geoImage.setLabel(null);
			GeoImage.updateInstances(app);
		}
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
		this.cornerLabel1 = cornerLabel1;
		this.cornerLabel2 = cornerLabel2;
		this.cornerLabel4 = cornerLabel4;
		return this;
	}
}
