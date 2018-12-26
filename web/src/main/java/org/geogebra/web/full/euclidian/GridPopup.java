package org.geogebra.web.full.euclidian;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.main.AppW;

/**
 * Grid style popup
 *
 */
public class GridPopup extends PopupMenuButtonWithDefault {

	/**
	 * @param app
	 *            app
	 * @param data
	 *            icons
	 * @param ev
	 *            view
	 */
	public GridPopup(AppW app, ImageOrText[] data, EuclidianView ev) {
		super(app, data);
		this.setIcon(data[EuclidianStyleBarW.gridIndex(ev)]);
	}

	@Override
	public void update(Object[] geos) {
		this.setVisible(
				geos.length == 0 && !EuclidianView.isPenMode(app.getMode())
						&& app.getMode() != EuclidianConstants.MODE_DELETE);
	}

}
