package org.geogebra.web.full.euclidian;

import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.util.PopupMenuButtonW;
import org.geogebra.web.html5.gui.util.ImageOrText;

/**
 * Popup button with default value
 */
public class PopupMenuButtonWithDefault extends PopupMenuButtonW {

	private ImageOrText defaultIcon;

	/**
	 * @param app
	 *            app
	 * @param data
	 *            icons
	 */
	public PopupMenuButtonWithDefault(App app, ImageOrText[] data) {
		super(app, data, -1, data.length, SelectionTable.MODE_ICON, true, false,
				null, false);
		defaultIcon = data.length > 1 ? data[1] : null;
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
