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
	public Localization loc;

	protected ArrayList<Macro> macros;

	/**
	 * Constructs a new instance of the GeoGebraTube exporter.
	 * 
	 * @param app
	 */
	public GeoGebraTubeExport(App app) {
		this.app = app;
		this.loc = app.getLocalization();
	}

	/**
	 * Upload the current worksheet to GeoGebraTube.
	 */
	// public abstract void uploadWorksheet(ArrayList<Macro> macros);

	protected abstract void statusLabelSetText(String plain);

	protected abstract void pack();

	/**
	 * Hides progress dialog.
	 */
	public abstract void hideDialog();

	/**
	 * returns a base64 encoded .ggb file
	 * 
	 * @throws IOException
	 */
	protected String getBase64String() throws IOException {
		return app.getGgbApi().getBase64(true);
	}

	/**
	 * returns a base64 encoded .ggt file
	 * 
	 * @throws IOException
	 */
	protected abstract String getBase64Tools(ArrayList<Macro> macros)
			throws IOException;

	/**
	 * Shows a small dialog with a progress bar.
	 */
	protected abstract void showDialog();

	/**
	 * Storage container for uploading results.
	 * 
	 * @author Florian Sonner
	 */
	public class UploadResults {
		private String status;
		private String uid;
		private String errorMessage;

		/**
		 * Parse upload result string.
		 * 
		 * @param string
		 */
		public UploadResults(String string) {
			status = uid = errorMessage = "";

			for (String line : string.split(",")) {
				int delimiterPos = line.indexOf(':');
				String key = line.substring(0, delimiterPos).toLowerCase();
				String value = line.substring(delimiterPos + 1).toLowerCase();

				if (key.equals("status")) {
					status = value;
				} else if (key.equals("uid")) {
					uid = value;
				} else if (key.equals("error")) {
					errorMessage = value;
				}
			}
		}

		public boolean HasError() {
			return !status.equals("ok");
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

		boolean isConstruction = (macros == null);

		// build post query
		StringBuilder sb = new StringBuilder();
		sb.append("data=");
		sb.append(encode(isConstruction ? getBase64String()
				: getBase64Tools(macros)));

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
	 * @param app
	 *            determines whether we need TUBE_BETA flag
	 * @return base upload URL for GeoGebraTube
	 */
	protected String getUploadURL(App app) {
		if (app.has(Feature.TUBE_BETA)) {
			return GeoGebraConstants.uploadURLBeta;
		}

		return GeoGebraConstants.uploadURL;
	}
}
