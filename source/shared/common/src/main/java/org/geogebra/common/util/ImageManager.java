package org.geogebra.common.util;

import java.io.StringReader;
import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.EdgeInsets;
import org.geogebra.common.io.QDParser;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;

abstract public class ImageManager {

	/**
	 * Set image corners; use selected points if any.
	 * 
	 * @param geoImage
	 *            image
	 * @param app
	 *            application
	 */
	public void setCornersFromSelection(GeoImage geoImage, App app) {
		boolean label = !app.isWhiteboardActive();
		ArrayList<GeoPointND> corners = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			GeoPointND p = getImageCornerFromSelection(i, app);
			if (p != null) {
				corners.add(p);
			}
		}

		GeoPointND point1 = null;

		if (corners.size() == 0) {
			point1 = new GeoPoint(app.getKernel().getConstruction());
			point1.setCoords(0, 0, 1.0);
			if (label) {
				point1.setLabel(null);
			}
			ensure1stCornerOnScreen(point1, app);
			corners.add(point1);
		} else if (corners.size() == 1) {
			point1 = corners.get(0);
		}

		for (int i = 0; i < corners.size(); i++) {
			geoImage.setCorner(corners.get(i), i);
		}

		if (corners.size() < 2) {
			GeoPoint point2 = new GeoPoint(app.getKernel().getConstruction());
			geoImage.calculateCornerPoint(point2, 2);
			geoImage.setCorner(point2, 1);
			if (label) {
				point2.setLabel(null);
			}

			// make sure 2nd corner is on screen
			if (!geoImage.isMeasurementTool()) {
				ensure2ndCornerOnScreen(point1.getInhomX(), point2, app);
				ensureImageHeightFitsInScreen(point1.getInhomX(), point2, app, geoImage);
			}
		}
		if (app.isWhiteboardActive()) {
			centerOnScreen(geoImage, app);
		}
		geoImage.setLabel(null);
		GeoImage.updateInstances(app);
	}

	private GeoPointND getImageCornerFromSelection(int index, App app) {
		ArrayList<GeoElement> sel = app.getSelectionManager().getSelectedGeos();
		if (sel.size() > index) {
			GeoElement geo0 = sel.get(index);
			if (geo0.isGeoPoint()) {
				return (GeoPointND) geo0;
			}
		}
		return null;
	}

	/**
	 * @param x1
	 *            1st corner x-coord
	 * @param point
	 *            initial position
	 * @param app
	 *            app
	 */
	public static void ensure2ndCornerOnScreen(double x1, GeoPointND point,
			App app) {
		double x2 = point.getInhomX();
		EuclidianView ev = app.getActiveEuclidianView();
		EdgeInsets safeArea = ev.getSafeAreaInsets();
		double xmax = ev.toRealWorldCoordX(ev.getWidth() - safeArea.getRight());
		if (x2 > xmax) {
			point.setCoords((x1 + 9 * xmax) / 10, point.getInhomY(), 1);
			point.update();
		}
	}

	private void ensure1stCornerOnScreen(GeoPointND point, App app) {
		EuclidianView ev = app.getActiveEuclidianView();
		EdgeInsets safeArea = ev.getSafeAreaInsets();
		double xMin = ev.toRealWorldCoordX(safeArea.getLeft());
		double xMax = ev.toRealWorldCoordX(ev.getWidth() - safeArea.getRight());
		double yMin = ev.toRealWorldCoordY(safeArea.getTop());
		double yMax = ev.toRealWorldCoordY(ev.getHeight() - safeArea.getBottom());
		point.setCoords(xMin + (xMax - xMin) / 5, yMax - (yMax - yMin) / 5,
				1.0);
		point.update();
	}

	private void ensureImageHeightFitsInScreen(double x1, GeoPointND point,
			App app, GeoImage image) {
		EuclidianView ev = app.getActiveEuclidianView();
		EdgeInsets safeArea = ev.getSafeAreaInsets();

		double xScale = ev.getKernel().getXscale();
		double yScale = ev.getKernel().getYscale();
		double imageHeight = image.getTotalHeight(ev) / yScale;
		double imageWidth = image.getTotalWidth(ev) / xScale;
		double factor = imageHeight / imageWidth;
		double realWorldWidth = image.getRealWorldX(1) - image.getRealWorldX(0);
		double realWorldHeight = realWorldWidth * factor;
		double yMax = ev.toRealWorldCoordY(safeArea.getTop());
		if (point.getInhomY() + realWorldHeight > yMax) {
			double expectedHeight = (yMax - point.getInhomY()) * 0.9;
			double expectedWidth = expectedHeight / factor;
			point.setCoords(x1 + expectedWidth, point.getInhomY(), 1);
			point.update();
		}
	}

	/**
	 * centers an image on screen
	 * 
	 * @param geoImage
	 *            image to be centered
	 * @param app
	 *            application
	 */
	private static void centerOnScreen(GeoImage geoImage, App app) {
		EuclidianView ev = app.getActiveEuclidianView();
		double screenWidth = ev.toRealWorldCoordX((double) (ev.getWidth()) + 1)
				- ev.toRealWorldCoordX(0.0);
		double screenHeight = ev.toRealWorldCoordY(
				(double) (ev.getHeight()) + 1) - ev.toRealWorldCoordY(0.0);

		GeoPoint point1 = geoImage.getStartPoint(0);
		GeoPoint point2 = geoImage.getStartPoint(1);
		GeoPoint point3 = new GeoPoint(app.getKernel().getConstruction());
		geoImage.calculateCornerPoint(point3, 3);

		double imageWidth = point2.inhomX - point1.inhomX;
		double imageHeight = point3.inhomY - point2.inhomY;

		point1.setCoords(
				ev.toRealWorldCoordX(0.0) + (screenWidth - imageWidth) / 2,
				ev.toRealWorldCoordY(0.0) + (screenHeight - imageHeight) / 2,
				1.0);
		point1.update();
		point2.setCoords(
				ev.toRealWorldCoordX(0.0) + (screenWidth + imageWidth) / 2,
				ev.toRealWorldCoordY(0.0) + (screenHeight - imageHeight) / 2,
				1.0);
		point2.update();
		geoImage.setCorner(point1, 0);
		geoImage.setCorner(point2, 1);
	}

	/**
	 * Update width/height based on viewBox to ensure correct rendering (GGB-1419)
	 * 
	 * @param fileStr
	 *            SVG to check as string
	 * @return SVG with width and height
	 */
	public static String fixSVG(String fileStr) {
		return fixSVG(fileStr, false);
	}

	/**
	 * Like {@link #fixSVG(String)}, but also removes aspect ratio
	 *
	 * @param fileStr
	 *            SVG to check as string
	 * @return SVG with width and height
	 */
	public static String fixAndRemoveAspectRatio(String fileStr) {
		return fixSVG(fileStr, true);
	}

	private static String fixSVG(String fileStr, boolean removeAspectRatio) {
		int svgStart = fileStr.indexOf("<svg");
		int svgEnd = fileStr.indexOf(">", svgStart);
		String svgTag = fileStr.substring(svgStart, svgEnd + 1) + "</svg>";

		// remove eg height="100%"
		svgTag = svgTag.replace("width=\"100%\"", "");
		svgTag = svgTag.replace("height=\"100%\"", "");
		svgTag = svgTag.replace("width='100%'", "");
		svgTag = svgTag.replace("height='100%'", "");

		if (!removeAspectRatio && svgTag.contains("width") && svgTag.contains("height")) {
			return fileStr;
		}
		QDParser qd = new QDParser();
		SVGDocHandler handler = new SVGDocHandler();
		if (removeAspectRatio) {
			handler.removeAspectRatio();
		}
		try {
			qd.parse(handler, new StringReader(svgTag));
			return fileStr.substring(0, svgStart) + handler.getSVGTag()
					+ fileStr.substring(svgEnd + 1);
		} catch (Exception e) {
			Log.debug(e);
		}

		return fileStr;
	}

	/**
	 * 
	 * @param filename0
	 *            filename eg "79054025255fb1a26e4bc422aef54eb4/image.png"
	 * @param urlBase64
	 *            ie something starting "data:image/png;base64,iVBOR..."
	 */
	public abstract void addExternalImage(String filename0, String urlBase64);

	public String getExternalImageSrc(String name) {
		return name;
	}

	/**
	 * @param kernel - kernel
	 * @param geo - text defining the image
	 * @param fillable - set image filling for this geo
	 */
	public void setImageForFillable(Kernel kernel, GeoText geo, GeoElement fillable) {
		// only works on platforms with SVG support (web, desktop)
	}
}
