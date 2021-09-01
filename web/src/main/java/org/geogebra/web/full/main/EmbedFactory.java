package org.geogebra.web.full.main;

import org.geogebra.common.media.MediaURLParser;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.gui.dialog.MediaInputPanel;

public class EmbedFactory {

	/**
	 * Adds the GeoEmbed instance.
	 *
	 * @param input
	 *            embed URL or code
	 */
	/*public void addEmbed(MediaInputPanel mediaInputPanel) {
		mediaInputPanel.resetError();
		String url = extractURL(input);
		if (!input.startsWith("<")) {
			mediaInputPanel.inputField.getTextComponent().setText(url);
		}
		String materialId = getGeoGebraMaterialId(url);
		if (!StringUtil.empty(materialId)) {
			getGeoGebraTubeAPI().getItem(materialId, this);
		} else {
			urlChecker.check(MediaURLParser.toEmbeddableUrl(url), this);
		}
	}*/
}
