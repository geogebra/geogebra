package org.geogebra.web.web.euclidian;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.util.ImageOrText;
import org.geogebra.web.web.gui.util.PopupMenuButtonW;

/**
 * Popup for axes style.
 */
public class AxesPopup extends PopupMenuButtonW {

	private ImageOrText defaultIcon;

	public AxesPopup(AppW app, ImageOrText[] data, int rows, int columns,
			SelectionTable mode) {
		super(app, data, rows, columns, mode, true, false, null);
		defaultIcon = data.length > 1 ? data[1] : null;
	}

	@Override
	public void update(Object[] geos) {
		this.setVisible(
				geos.length == 0 && !EuclidianView.isPenMode(app.getMode())
						&& app.getMode() != EuclidianConstants.MODE_DELETE);
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
