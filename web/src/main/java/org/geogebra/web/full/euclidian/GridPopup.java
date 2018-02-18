package org.geogebra.web.full.euclidian;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.main.Feature;
import org.geogebra.web.full.gui.util.PopupMenuButtonW;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.main.AppW;

/**
 * Grid style popup
 *
 */
public class GridPopup extends PopupMenuButtonW {

	private ImageOrText defaultIcon;

	/**
	 * @param app
	 *            app
	 * @param data
	 *            icons
	 * @param mode
	 *            selecion mode
	 * @param ev
	 *            view
	 */
	public GridPopup(AppW app, ImageOrText[] data, 
	        SelectionTable mode, EuclidianView ev) {
		super(app, data, -1, data.length, mode, true, false, null, false);
		defaultIcon = data.length > 1 ? data[1] : null;
		this.setIcon(data[EuclidianStyleBarW.gridIndex(ev)]);
	}

	@Override
	public void update(Object[] geos) {
		if (app.has(Feature.MOW_CLEAR_VIEW_STYLEBAR)) {
			this.setVisible(geos.length == 0);
		} else {
			this.setVisible(
					geos.length == 0 && !EuclidianView.isPenMode(app.getMode())
							&& app.getMode() != EuclidianConstants.MODE_DELETE);
		}
	}

	@Override
	public void setIcon(ImageOrText icon) {
		if (getSelectedIndex() == 0 && defaultIcon != null) {
			super.setIcon(defaultIcon);
			this.removeStyleName("selected");
		} else {
			super.setIcon(icon);
			this.addStyleName("selected");
		}
	}
}
