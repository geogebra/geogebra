package org.geogebra.common.export;

import java.io.IOException;
import java.util.ArrayList;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;

/**
 * Export GeoGebra worksheet to GeoGebraTube.
 * 
 * @author Florian Sonner
 */
public abstract class GeoGebraTubeExport {

	/**
	 * Application instance.
	 */
	public App app;
	private Localization loc;

	private ArrayList<Macro> macros;

	/**
	 * Constructs a new instance of the GeoGebraTube exporter.
	 * 
	 * @param app
	 *            application
	 */
	public GeoGebraTubeExport(App app) {
		this.app = app;
		this.setLoc(app.getLocalization());
	}

	/**
	 * Upload the current worksheet to GeoGebraTube.
	 */
	// public abstract void uploadWorksheet(ArrayList<Macro> macros);

	protected abstract void statusLabelSetText(String plain);

	protected abstract void pack();

	/**
	 * returns a base64 encoded .ggb file
	 * 
	 * @throws IOException
	 *             when output fails
	 */
	protected String getBase64String() throws IOException {
		return app.getGgbApi().getBase64(true);
	}

	/**
	 * returns a base64 encoded .ggt file
	 * 
	 * @param macroList
	 *            macros to export
	 * @return base64
	 * 
	 * @throws IOException
	 *             when output fails
	 */
	protected abstract String getBase64Tools(ArrayList<Macro> macroList)
			throws IOException;

	/**
	 * Storage container for uploading results.
	 * 
	 * @author Florian Sonner
	 */
	public static class UploadResults {
		private String status;
		private String uid;
		private String errorMessage;

		/**
		 * Parse upload result string.
		 * 
		 * @param string0
		 *            server response
		 */
		public UploadResults(String string0) {
			String string = string0;
			status = uid = errorMessage = "";
			if (string.indexOf("status:") > 0) {
				string = string.substring(string.indexOf("status:"));
			}
			for (String line : string.split(",")) {
				int delimiterPos = line.indexOf(':');
				String key = line.substring(0, delimiterPos).toLowerCase();
				String value = line.substring(delimiterPos + 1).toLowerCase();

				if ("status".equals(key)) {
					status = value;
				} else if ("uid".equals(key)) {
					uid = value;
				} else if ("error".equals(key)) {
					errorMessage = value;
				}
			}
		}

		public boolean hasError() {
			return !"ok".equals(status);
		}

		public String getStatus() {
			return status;
		}

		public String getUID() {
			return uid;
		}

		public String getErrorMessage() {
			return errorMessage;
		}
	}

	protected StringBuilder getPostData() throws IOException {
		Construction cons = app.getKernel().getConstruction();

		boolean isConstruction = (getMacros() == null);

		// build post query
		StringBuilder sb = new StringBuilder();
		sb.append("data=");
		sb.append(encode(
				isConstruction ? getBase64String() : getBase64Tools(getMacros())));

		sb.append("&type=");
		sb.append(isConstruction ? "ggb" : "ggt");

		if (isConstruction) {
			sb.append("&title=");
			sb.append(encode(cons.getTitle()));

			sb.append("&pretext=");
			sb.append(encode(cons.getWorksheetText(0)));

			sb.append("&posttext=");
			sb.append(encode(cons.getWorksheetText(1)));
		}

		sb.append("&version=");
		sb.append(encode(GeoGebraConstants.VERSION_STRING));

		return sb;
	}

	protected abstract String encode(String str);

	protected abstract void setMaximum(int i);

	protected abstract void setMinimum(int i);

	protected abstract void setIndeterminate(boolean b);

	protected abstract void setValue(int end);

	protected abstract void setEnabled(boolean b);

	/**
	 * @param app0
	 *            determines whether we need TUBE_BETA flag
	 * @return base upload URL for GeoGebraTube
	 */
	public String getUploadURL(App app0) {
		if (app0.has(Feature.TUBE_BETA)) {
			return GeoGebraConstants.uploadURLBeta;
		}

		return GeoGebraConstants.uploadURL;
	}

	public Localization getLoc() {
		return loc;
	}

	public void setLoc(Localization loc) {
		this.loc = loc;
	}

	protected ArrayList<Macro> getMacros() {
		return macros;
	}

	protected void setMacros(ArrayList<Macro> macros) {
		this.macros = macros;
	}
}
