package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.main.App;
import org.geogebra.common.move.ggtapi.models.AjaxCallback;
import org.geogebra.common.move.ggtapi.models.MaterialRestAPI;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.move.ggtapi.models.json.JSONTokener;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.main.AppW;

import elemental2.dom.File;
import elemental2.dom.FileReader;

public class H5PReader implements AjaxCallback {

	private final App app;

	public H5PReader(App app) {
		this.app = app;
	}

	/**
	 * Upload file to backend + embed
	 * @param file file to load
	 */
	public void load(File file) {
		FileReader reader = new FileReader();

		reader.addEventListener("load", (ev) -> {
			if (reader.readyState == FileReader.DONE) {
				String[] splitted = reader.result.asString().split("base64,");
				if (splitted != null && splitted.length == 2) {
					((MaterialRestAPI) app.getLoginOperation().getGeoGebraTubeAPI())
							.uploadAndUnzipH5P(splitted[1], this);
				}

			}
		});

		reader.readAsDataURL(file);
	}

	@Override
	public void onSuccess(String response) {
		JSONTokener tokener = new JSONTokener(response);
		try {
			JSONObject h5p = new JSONObject(tokener);
			String unzippedPath = h5p.getString("url");
			EmbedManager em = app.getEmbedManager();
			if (em != null && unzippedPath != null && !unzippedPath.isEmpty()) {
				em.openH5PTool(unzippedPath);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onError(String error) {
		ToolTipManagerW.sharedInstance().showBottomMessage(app.getLocalization()
				.getMenu("PdfErrorText"), true, (AppW) app);
	}
}
