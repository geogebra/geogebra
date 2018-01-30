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
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.plugin.GgbAPI;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

/**
 * CmdGetTime
 *
 * @author Michael Borcherds
 * @author Himanshu Gupta
 */
public class CmdExportImage extends CommandProcessor {

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
	@SuppressWarnings("deprecation")
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		
		if (n > 1) {
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
		//boolean copyToClipboard = true;
		String filename = null;
		ExportType type = ExportType.PNG;

		
		if (n==1) {
			GeoElement[] arg = resArgs(c);
			if (arg[0].isGeoText()) {
				
				// eg "{clipboard: false, dpi:96, scale:1, transparent: true, clipboard: false, view: 1 ,format:"PNG"}";
				String json = ((TextProperties)arg[0]).toValueString(StringTemplate.maxDecimals);
				// for testing
				//json = "{clipboard: false, dpi:96, scale:1.1, transparent: true, clipboard: false, view: 1 ,format:'PNG'}";
				try {
					JSONObject options = new JSONObject(json);
					if (options.has("dpi")) {
						dpi = (Integer)options.get("dpi");
					}
					if (options.has("view")) {
						view = (Integer)options.get("view");
					}
					if (options.has("type")) {
						String typeStr = StringUtil.toLowerCaseUS(options.get("type").toString());
						switch (typeStr) {
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
					}
					if (options.has("transparent")) {
						transparent = (Boolean)options.get("transparent");
					}
					
					if (options.has("width")) {
						width = (Integer)options.get("width");
					}
					if (options.has("height")) {
						height = (Integer)options.get("height");
					}
					if (options.has("scaleCM")) {
						scaleCM = getDouble(options,"scaleCM");
					}
					if (options.has("scale")) {
						exportScale = getDouble(options,"scale");
					}
					if (options.has("filename")) {
						filename = options.getString("filename").toString();
					}


				} catch (JSONException e) {
					throw argErr(app, c, arg[0]);
				}
			} else {
				throw argErr(app, c, arg[0]);
			}
		}
		
		GgbAPI api = kernel.getApplication().getGgbApi();
		
		Log.debug("dpi = " + dpi);
		Log.debug("exportScale = " + exportScale);
		Log.debug("transparent = " + transparent);
		//Log.debug("clipboard = " + copyToClipboard);
		Log.debug("scaleCM = " + scaleCM);
		Log.debug("filename = " + filename);
		Log.debug("type = " + type);
		
		
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
		if (width > 0) {
			double viewWidth = ev.getExportWidth();
			exportScale = width / viewWidth;
		} else if (height > 0) {
			double viewHeight = ev.getExportHeight();
			exportScale = height / viewHeight;
		} else if (scaleCM >= 0) {
			
			double viewWidth = ev.getExportWidth();
			double xScale = ev.getXscale();	
			double widthRW = viewWidth / xScale;

			// dots per cm
			double dpcm;
			
			
			if (width > 0) {
				// calculate DPI from width
				dpcm = width / (widthRW * scaleCM);
				dpi = (int) (dpcm * 2.54);
				exportScale = width / viewWidth;
			} else {
				// calculate width from dpi

				if (dpi < 0) {
					dpi = 300;
				}

				dpcm = dpi / 2.54;

				double pixelWidth = Math.round(dpcm * widthRW * scaleCM);
				// Log.debug("widthRW= " + widthRW);
				// Log.debug("pixelWidth= " + pixelWidth);
				exportScale = pixelWidth / viewWidth;
			}
				
				
			
		}
		
		Log.debug("exportScale = " + exportScale);

		if (exportScale <=0 || !MyDouble.isFinite(exportScale)) {
			exportScale = 1;
		}
		
		
		
		switch (type) {
		default:
		case PNG:
			if (filename != null) {
				api.writePNGtoFile(filename, exportScale, transparent, dpi);
			} else {
				String png = api.getPNGBase64(exportScale, transparent, 72, /*copyToClipboard*/false);
				kernel.getApplication().handleImageExport(png);
			}
			
			break;
		case SVG:
			
			api.exportSVG(filename);
			
			break;
			
		case PDF_HTML5:
			api.exportPDF(exportScale, filename);

			break;
		}


		GeoElement[] ret1 = { };
		return ret1;
	}

	private double getDouble(JSONObject options, String string) {
		Object obj;
		try {
			obj = options.get(string);
			if (obj instanceof Integer) {
				return ((Integer)obj).doubleValue();
			}
			if (obj instanceof Double) {
				return ((Double)obj).doubleValue();
			}
		} catch (JSONException e) {
			// fall through -> NaN
		}
		
		return Double.NaN;
	}



	

}
