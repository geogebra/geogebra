package org.geogebra.common.kernel.commands;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.TextProperties;
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

		if (MyDouble.isOdd(n)) {
			throw argNumErr(c);
		}

		int dpi = -1;
		// pixels
		int width = -1;
		int height = -1;
		double exportScale = Double.NaN;
		// 1 unit = x cm
		double scaleCM = Double.NaN;
		int view = 1;
		boolean transparent = false;
		// boolean copyToClipboard = true;
		String filename = null;
		ExportType type = ExportType.PNG;

		GeoElement[] arg = resArgs(c);

		for (int i = 0; i < n; i += 2) {

			GeoElement key = arg[i];
			GeoElement value = arg[i + 1];

			if (!key.isGeoText()) {
				throw argErr(app, c, key);
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
				}

				break;
			case "dpi":
				dpi = (int) value.evaluateDouble();
				break;
			case "transparent":
				transparent = "true".equals(
						value.toValueString(StringTemplate.defaultTemplate));
				break;
			case "width":
				width = (int) value.evaluateDouble();
				break;
			case "height":
				height = (int) value.evaluateDouble();
				break;
			case "view":
				view = (int) value.evaluateDouble();
				break;
			case "scale":
				exportScale = value.evaluateDouble();
				break;
			case "scalecm":
				scaleCM = value.evaluateDouble();
				break;
			case "filename":
				filename = value.toValueString(StringTemplate.defaultTemplate);
				break;
			default:
				throw argErr(app, c, key);

			}

		}

		GgbAPI api = kernel.getApplication().getGgbApi();

		// Log.debug("dpi = " + dpi);
		// Log.debug("exportScale = " + exportScale);
		// Log.debug("transparent = " + transparent);
		// Log.debug("scaleCM = " + scaleCM);
		// Log.debug("filename = " + filename);
		// Log.debug("type = " + type);

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
			// Log.debug("widthRW= " + widthRW);
			// Log.debug("pixelWidth= " + pixelWidth);
			exportScale = pixelWidth / viewWidth;

		}

		if (exportScale <= 0 || !MyDouble.isFinite(exportScale)) {
			exportScale = 1;
		}

		switch (type) {
		default:
		case PNG:
			if (filename != null) {
				api.writePNGtoFile(filename, exportScale, transparent, dpi);
			} else {
				String png = api.getPNGBase64(exportScale, transparent, dpi,
						false);
				kernel.getApplication().handleImageExport(png);
			}

			break;
		case SVG:

			String svg = api.exportSVG(filename);

			if (filename == null) {
				kernel.getApplication().handleImageExport(svg);
			}

			break;

		case PDF_HTML5:
			String pdf = api.exportPDF(exportScale, filename);
			if (filename == null) {
				kernel.getApplication().handleImageExport(pdf);
			}

			break;
		}

		GeoElement[] ret1 = {};
		return ret1;
	}

}
