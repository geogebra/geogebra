package org.geogebra.web.tablet.gui.browser;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.browser.MaterialListElement;
import org.geogebra.web.html5.main.AppW;

public class TabletMaterialElement extends MaterialListElement {

	public TabletMaterialElement(final Material m, final AppW app,
	        final boolean isLocal) {
		super(m, app, isLocal);
	}

	@Override
	public void onView() {
		((GuiManagerW) app.getGuiManager()).getBrowseView()
		        .setMaterialsDefaultStyle();
		if (!isLocal) {
			loadNative(getMaterial().getId(), getMaterial().getTitle(), app
			        .getLoginOperation().getModel().getLoginToken());
		}

	}

	private native void loadNative(int id, String title, String token) /*-{
		if ($wnd.android) {
			$wnd.android.open(id, title, token);
		}
	}-*/;
}
