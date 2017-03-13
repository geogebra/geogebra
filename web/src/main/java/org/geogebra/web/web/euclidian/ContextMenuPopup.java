package org.geogebra.web.web.euclidian;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.images.ImgResourceHelper;
import org.geogebra.web.web.gui.util.ImageOrText;
import org.geogebra.web.web.gui.util.PopupMenuButtonW;

public class ContextMenuPopup extends PopupMenuButtonW {

	private EuclidianController ec;
	private boolean menuShown = false;
	public ContextMenuPopup(AppW app) {
		super(app, null, -1, -1, null, false, false, null, null);
		ImgResourceHelper.setIcon(AppResources.INSTANCE.dots(), this);

		ec = app.getActiveEuclidianView().getEuclidianController();
	}

	@Override
	public void update(Object[] geos) {
		setVisible(true);
	}

	@Override
	public ImageOrText getButtonIcon() {
		return this.getIcon();
	}

	@Override
	protected void onClickAction() {
		if (menuShown) {
			menuShown = false;
			ImgResourceHelper.setIcon(AppResources.INSTANCE.dots(), this);
			app.closePopups();

		} else {
			ec.showObjectContextMenu(0, 0);
			ImgResourceHelper.setIcon(AppResources.INSTANCE.dots_active(),
					this);
			menuShown = true;
		}
	}

}
