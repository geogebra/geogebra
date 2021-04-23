package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.kernel.geos.GeoEmbed;
import org.geogebra.common.main.App;
import org.geogebra.common.move.ggtapi.models.AjaxCallback;
import org.geogebra.common.move.ggtapi.models.MaterialRestAPI;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.move.ggtapi.models.json.JSONTokener;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.main.AppW;

import elemental2.dom.File;
import elemental2.dom.FileReader;

public class H5PReader implements AjaxCallback {

	private final App app;
	private GeoEmbed embed;

	public H5PReader(App app) {
		this.app = app;
	}

	/**
	 * Upload file to backend + embed
	 * @param file file to load
	 */
	public void load(File file) {
		FileReader reader = new FileReader();
		EmbedManager em = app.getEmbedManager();
		if (em != null) {
			embed = em.openH5PTool(() -> onError(""));
		}
		reader.addEventListener("load", (ev) -> {
			if (reader.readyState == FileReader.DONE) {
				String dataUrl = reader.result.asString();
				int offset = dataUrl.indexOf(',');
				if (offset > 0) {
					((MaterialRestAPI) app.getLoginOperation().getGeoGebraTubeAPI())
							.uploadAndUnzipH5P(dataUrl.substring(offset + 1), this);
				}

			}
		});

		reader.readAsDataURL(file);
	}

	@Override
	public void onSuccess(String response) {
		if (!embed.isInConstructionList()) {
			return; // deleted by user meanwhile
		}
		JSONTokener tokener = new JSONTokener(response);
		try {
			JSONObject h5p = new JSONObject(tokener);
			String unzippedPath = h5p.getString("url");
			EmbedManager em = app.getEmbedManager();
			if (em != null && unzippedPath != null && !unzippedPath.isEmpty()) {
				embed.setUrl(unzippedPath);
				em.setContentSync(embed.getLabelSimple(), embed.getURL());
			}
		} catch (JSONException e) {
			Log.debug(e);
		}
	}

	@Override
	public void onError(String error) {
		if (embed != null) {
			embed.remove();
		}
		ToolTipManagerW.sharedInstance().showBottomMessage(app.getLocalization()
				.getMenu("PdfErrorText"), true, (AppW) app);
	}
}
