package org.geogebra.web.full.gui.openfileview;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.web.full.gui.images.AppResources;
import org.gwtproject.user.client.ui.FlowPanel;

class MaterialImagePanel extends FlowPanel {

	public MaterialImagePanel(Material material) {
		setStyleName("cardImgPanel");
		setBackground(material);
	}

	/**
	 * Update thumbnail from material
	 * @param m material
	 */
	public void setBackground(Material m) {
		final String thumb = m.getThumbnail();
		if (thumb != null && thumb.length() > 0) {
			setBackgroundImageUrl(thumb);
		} else {
			setBackgroundImageUrl(AppResources.INSTANCE.geogebra64().getSafeUri().asString());
		}
	}

	private void setBackgroundImageUrl(String url) {
		getElement().getStyle().setBackgroundImage(
				"url(" + url + ")");
	}
}
