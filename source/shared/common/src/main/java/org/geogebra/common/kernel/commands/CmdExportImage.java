/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.commands;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyStringBuffer;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.ExportType;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.GgbAPI;
import org.geogebra.common.util.StringUtil;

/**
 * CmdExportImage
 *
 * @author Michael Borcherds
 */
public class CmdExportImage extends CmdScripting {
	public static final int PDF_DPI = 288;

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdExportImage(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		app.getSelectionManager().clearSelectedGeos();

		if (MyDouble.isOdd(n)) {
			throw argNumErr(c);
		}
		GeoElement[] arg = resArgs(c);
		Map<String, GeoElement> argMap = new HashMap<>();
		for (int i = 0; i < n; i += 2) {
			GeoElement key = arg[i];
			GeoElement value = arg[i + 1];

			if (!key.isGeoText()) {
				throw argErr(c, key);
			}
			argMap.put(StringUtil.toLowerCaseUS(key
					.toValueString(StringTemplate.maxDecimals)), value);
		}
		final ExportType type = toExportType(getString(argMap, "type", ""));
		final int view = (int) getValue(argMap, "view", 1);
		final int time = (int) getValue(argMap, "time", 200);
		final boolean transparent = getBool(argMap, "transparent", false);
		final boolean clipboard = getBool(argMap, "clipboard", false);
		final boolean grayscaleEN = getBool(argMap, "grayscale", false);
		final boolean grayscale = getBool(argMap, "greyscale", grayscaleEN);
		final boolean loop = getBool(argMap, "loop", true);
		final int width = (int) getValue(argMap, "width", -1);
		final int height = (int) getValue(argMap, "height", -1);
		final double rotate = getValue(argMap, "rotate", 0.0);
		final GeoPoint corner = getCorner(argMap, "corner");
		final GeoPoint corner2 = getCorner(argMap, "corner2");
		GeoElement sliderObject = argMap.remove("slider");
		final String sliderName = sliderObject == null ? null
				: sliderObject instanceof GeoNumeric ? sliderObject.getLabelSimple()
				: sliderObject.toValueString(StringTemplate.maxDecimals);
		final String filename = getString(argMap, "filename", null);
		int dpi = (int) getValue(argMap, "dpi", -1);
		double exportScale = getValue(argMap, "scale", Double.NaN);
		double scaleCM = getValue(argMap, "scalecm", Double.NaN);
		if (scaleCM > 0) {
			exportScale = Double.NaN;
		} else if (Double.isNaN(exportScale)) {
			scaleCM = 1;
		}
		if (!argMap.isEmpty()) {
			String badKey = argMap.keySet().iterator().next();
			throw argErr(c, new MyStringBuffer(kernel, badKey));
		}

		// see CmdSetActiveView
		switch (view) {
		case 2:
			app.setActiveView(App.VIEW_EUCLIDIAN2);
			break;
		case -1:
			app.setActiveView(App.VIEW_EUCLIDIAN3D);
			break;
		case 1:
		default:
			app.setActiveView(App.VIEW_EUCLIDIAN);
			break;
		}

		EuclidianView ev = app.getActiveEuclidianView();
		double viewWidth = ev.getExportWidth();
		double xScale = ev.getXscale();
		double widthRW = viewWidth / xScale;

		if (width > 0) {
			if (scaleCM > 0) {
				// calculate DPI from width

				// dots per cm
				double dpcm = width / (widthRW * scaleCM);

				dpi = (int) (dpcm * 2.54);
			}

			exportScale = width / viewWidth;

		} else if (height > 0) {
			double viewHeight = ev.getExportHeight();
			exportScale = height / viewHeight;
		} else if (scaleCM > 0) {

			// calculate width from dpi

			if (dpi < 0) {
				dpi = 300;
			}

			// dots per cm
			double dpcm = dpi / 2.54;

			double pixelWidth = Math.round(dpcm * widthRW * scaleCM);
			exportScale = pixelWidth / viewWidth;
		}

		if (exportScale <= 0 || !Double.isFinite(exportScale)) {
			exportScale = 1;
		}

		// callbacks need final variables
		GgbAPI api = kernel.getApplication().getGgbApi();
		String label = c.getLabel();
		switch (type) {
		case SVG:
			api.exportSVG(filename, (svg) -> {
				if (label != null) {
					addImageToConstruction(label, svg, corner, corner2, true);
				} else if (filename == null) {
					kernel.getApplication().handleImageExport(svg);
				}
			});
			break;

		case PDF_HTML5:
			api.exportPDF(exportScale, filename, (pdf) -> {
				if (filename == null) {
					kernel.getApplication().handleImageExport(pdf);
				}
			}, sliderName, dpi > 0 ? dpi : PDF_DPI);
			break;

		case ANIMATED_GIF:
			api.exportGIF(sliderName, exportScale, time, loop,
					filename == null ? "anim.gif" : filename, rotate);
			break;
		case WEBM:
			api.exportWebM(sliderName, exportScale, time, loop,
					filename == null ? "anim.webm" : filename, rotate);
			break;
		case PNG:
		default:
			if (filename != null) {
				api.writePNGtoFile(filename, exportScale, transparent, dpi,
						grayscale);
			} else {

				String pngBase64 = api.getPNGBase64(exportScale, transparent, dpi,
						false, grayscale);

				if (pngBase64 == null) {

					int w = (int) Math.floor(ev.getExportWidth() * exportScale);
					int h = (int) Math
							.floor(ev.getExportHeight() * exportScale);

					throw MyError.forCommand(loc, loc.getPlain("ImageErrorAB",
							w + "", h + ""), c.getName(), null);
				}

				if (clipboard) {
					kernel.getApplication().copyImageToClipboard(StringUtil.pngMarker + pngBase64);
					return null;
				}

				if (label != null) {
					addImageToConstruction(label, pngBase64, corner, corner2, false);
				} else {
					kernel.getApplication().handleImageExport(pngBase64);
				}
			}

			break;
		}

		return new GeoElement[0];
	}

	private ExportType toExportType(String typeStr) {
		return switch (typeStr.toLowerCase(Locale.ROOT)) {
			case "svg" -> ExportType.SVG;
			case "pdf" -> ExportType.PDF_HTML5;
			case "gif" -> ExportType.ANIMATED_GIF;
			case "webm" -> ExportType.WEBM;
			default -> ExportType.PNG;
		};
	}

	private GeoPoint getCorner(Map<String, GeoElement> map, String key) {
		if (map.remove(key) instanceof GeoPoint corner) {
			return corner;
		}
		return null;
	}

	private double getValue(Map<String, GeoElement> map, String key, double fallback) {
		if (map.containsKey(key)) {
			return map.remove(key).evaluateDouble();
		}
		return fallback;
	}

	private String getString(Map<String, GeoElement> map, String key, String fallback) {
		if (map.containsKey(key)) {
			return map.remove(key).toValueString(StringTemplate.maxDecimals);
		}
		return fallback;
	}

	private boolean getBool(Map<String, GeoElement> map, String key, boolean fallback) {
		if (map.containsKey(key)) {
			return "true".equals(map.remove(key).toValueString(StringTemplate.xmlTemplate));
		}
		return fallback;
	}

	private void addImageToConstruction(String label, String imageStr,
			GeoPoint corner1, GeoPoint corner2, boolean svg) {
		String targetLabel = label;
		GeoElementND oldImage = kernel.lookupLabel(label);
		if (!(oldImage instanceof GeoImage) && oldImage != null) {
			oldImage = null;
			targetLabel = null;
		}

		String imageFilename = kernel.getApplication().md5Encrypt(imageStr)
				+ "/image."
				+ (svg ? "svg" : "png");

		GeoPointND c1 = corner1 == null ? new GeoPoint(cons, 0, 0, 1) : corner1;
		GeoPointND c2 = corner2 == null ? new GeoPoint(cons, 1, 0, 1) : corner2;
		final GeoImage geoImage = app.createImageFromString(imageFilename,
				svg ? imageStr : StringUtil.pngMarker + imageStr, (GeoImage) oldImage,
				false, c1, c2);
		boolean oldSuppress = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(false);
		geoImage.setLabel(targetLabel);
		cons.setSuppressLabelCreation(oldSuppress);
		// invokeLater needed in web to make sure image appears
		app.invokeLater(geoImage::updateRepaint);
	}

}
