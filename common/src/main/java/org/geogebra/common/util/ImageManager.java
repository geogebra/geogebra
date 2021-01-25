package org.geogebra.common.util;

import java.io.StringReader;
import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.io.QDParser;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoPoint;
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
			ensure2ndCornerOnScreen(point1.getInhomX(), point2, app);
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
	public static void ensure2ndCornerOnScreen(double x1, GeoPoint point,
			App app) {
		double x2 = point.inhomX;
		EuclidianView ev = app.getActiveEuclidianView();
		double xmax = ev.toRealWorldCoordX((double) (ev.getWidth()) + 1);
		if (x2 > xmax) {
			point.setCoords((x1 + 9 * xmax) / 10, point.inhomY, 1);
			point.update();
		}
	}

	private void ensure1stCornerOnScreen(GeoPointND point, App app) {
		EuclidianView ev = app.getActiveEuclidianView();
		double xmin = ev.toRealWorldCoordX(0.0);
		double xmax = ev.toRealWorldCoordX((double) (ev.getWidth()) + 1);
		double ymin = ev.toRealWorldCoordY(0.0);
		double ymax = ev.toRealWorldCoordY((double) (ev.getHeight()) + 1);
		point.setCoords(xmin + (xmax - xmin) / 5, ymax - (ymax - ymin) / 5,
				1.0);
		point.update();
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

		GeoPoint point1 = geoImage.getCorner(0);
		GeoPoint point2 = geoImage.getCorner(1);
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
	 * GGB-1419
	 * 
	 * @param fileStr
	 *            SVG to check as string
	 * @return SVG with
	 */
	public static String fixSVG(String fileStr) {
		int svgStart = fileStr.indexOf("<svg");
		int svgEnd = fileStr.indexOf(">", svgStart);
		String svgTag = fileStr.substring(svgStart, svgEnd + 1) + "</svg>";

		// remove eg height="100%"
		svgTag = svgTag.replace("width=\"100%\"", "");
		svgTag = svgTag.replace("height=\"100%\"", "");
		svgTag = svgTag.replace("width='100%'", "");
		svgTag = svgTag.replace("height='100%'", "");

		if (svgTag.contains("width") && svgTag.contains("height")) {
			return fileStr;
		}
		QDParser qd = new QDParser();
		SVGDocHandler handler = new SVGDocHandler();
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
}
