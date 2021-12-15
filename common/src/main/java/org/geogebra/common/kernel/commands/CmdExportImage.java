package org.geogebra.common.kernel.commands;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.TextProperties;
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

		String label = c.getLabel();

		// time between frames (ms) for animated GIF
		int time = 200;
		// slider name for animated GIF
		String sliderName = null;
		// for animated GIF
		boolean loop = true;

		GeoPoint corner = null;
		GeoPoint corner2 = null;
		int dpi = -1;
		// pixels
		int width = -1;
		int height = -1;
		double exportScale = Double.NaN;
		// 1 unit = x cm
		double scaleCM = 1;
		int view = 1;
		boolean transparent = false;
		boolean clipboard = false;
		boolean grayscale = false;
		// boolean copyToClipboard = true;
		String filename = null;
		ExportType type = ExportType.PNG;
		// angle (radians) to turn 3D View during animated GIF export
		// 0 = no rotation
		double rotate = 0;
		
		GeoElement[] arg = resArgs(c);

		for (int i = 0; i < n; i += 2) {

			GeoElement key = arg[i];
			GeoElement value = arg[i + 1];

			if (!key.isGeoText()) {
				throw argErr(c, key);
			}

			switch (StringUtil.toLowerCaseUS(((TextProperties) key)
					.toValueString(StringTemplate.maxDecimals))) {

			case "type":
				String typeStr = StringUtil.toLowerCaseUS(
						value.toValueString(StringTemplate.defaultTemplate));
				switch (typeStr) {
				default:
				case "png":
					type = ExportType.PNG;
					break;
				case "svg":
					type = ExportType.SVG;
					break;
				case "pdf":
					type = ExportType.PDF_HTML5;
					break;
				case "gif":
					type = ExportType.ANIMATED_GIF;
					break;
				case "webm":
					type = ExportType.WEBM;
					break;
				}

				break;
			case "dpi":
				dpi = (int) value.evaluateDouble();
				break;
			case "time":
				time = (int) value.evaluateDouble();
				break;
			case "transparent":
				transparent = "true".equals(
						value.toValueString(StringTemplate.defaultTemplate));
				break;
			case "clipboard":
				clipboard = "true".equals(value.toValueString(StringTemplate.defaultTemplate));
				break;
			case "greyscale":
			case "grayscale":
				grayscale = "true".equals(
						value.toValueString(StringTemplate.defaultTemplate));
				break;
			case "loop":
				loop = "true".equals(
						value.toValueString(StringTemplate.defaultTemplate));
				break;
			case "width":
				width = (int) value.evaluateDouble();
				break;
			case "rotate":
				rotate = value.evaluateDouble();
				break;
			case "height":
				height = (int) value.evaluateDouble();
				break;
			case "corner":
				if (value instanceof GeoPoint) {
					corner = (GeoPoint) value;
				}
				break;
			case "corner2":
				if (value instanceof GeoPoint) {
					corner2 = (GeoPoint) value;
				}
				break;
			case "view":
				view = (int) value.evaluateDouble();
				break;
			case "scale":
				exportScale = value.evaluateDouble();
				scaleCM = Double.NaN;
				break;
			case "scalecm":
				scaleCM = value.evaluateDouble();
				exportScale = Double.NaN;
				break;
			case "slider":
				if (value instanceof GeoNumeric) {
					sliderName = ((GeoNumeric) value).getLabelSimple();
				} else {
					sliderName = value
							.toValueString(StringTemplate.defaultTemplate);
				}
				break;
			case "filename":
				filename = value.toValueString(StringTemplate.defaultTemplate);
				break;
			default:
				throw argErr(c, key);

			}

		}

		GgbAPI api = kernel.getApplication().getGgbApi();

		// see CmdSetActiveView
		switch (view) {
		default:
		case 1:
			app.setActiveView(App.VIEW_EUCLIDIAN);
			break;
		case 2:
			app.setActiveView(App.VIEW_EUCLIDIAN2);
			break;
		case -1:
			app.setActiveView(App.VIEW_EUCLIDIAN3D);
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

		if (exportScale <= 0 || !MyDouble.isFinite(exportScale)) {
			exportScale = 1;
		}

		// callbacks need final variables
		final String ffilename = filename;
		final GeoPoint fcorner = corner;
		final GeoPoint fcorner2 = corner2;

		switch (type) {
		default:
		case PNG:
			if (filename != null) {
				api.writePNGtoFile(filename, exportScale, transparent, dpi,
						grayscale);
			} else {

				String png = api.getPNGBase64(exportScale, transparent, dpi,
						false, grayscale);

				if (png == null) {

					int w = (int) Math.floor(ev.getExportWidth() * exportScale);
					int h = (int) Math
							.floor(ev.getExportHeight() * exportScale);

					throw MyError.forCommand(loc, loc.getPlain("ImageErrorAB",
							w + "", h + ""), c.getName(), null);
				}

				if (clipboard) {
					kernel.getApplication().copyImageToClipboard(StringUtil.pngMarker + png);
					return null;
				}

				if (label != null) {
					addImageToConstruction(label, png, corner, corner2, false);
				} else {
					kernel.getApplication().handleImageExport(png);
				}
			}

			break;

		case SVG:
			api.exportSVG(filename, (svg) -> {
				if (label != null) {
					addImageToConstruction(label, svg, fcorner, fcorner2, true);
				} else if (ffilename == null) {
					kernel.getApplication().handleImageExport(svg);
				}
			});
			break;

		case PDF_HTML5:
			api.exportPDF(exportScale, filename, (pdf) -> {
				if (ffilename == null) {
					kernel.getApplication().handleImageExport(pdf);
				}
			}, sliderName);
			break;

		case ANIMATED_GIF:
			api.exportGIF(sliderName, exportScale, time, loop,
					filename == null ? "anim.gif" : filename, rotate);
			break;
		case WEBM:
			api.exportWebM(sliderName, exportScale, time, loop,
					filename == null ? "anim.webm" : filename, rotate);
			break;
		}

		return new GeoElement[0];
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
