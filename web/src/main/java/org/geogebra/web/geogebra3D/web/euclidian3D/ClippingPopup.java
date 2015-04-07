package org.geogebra.web.geogebra3D.web.euclidian3D;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.util.ImageOrText;
import org.geogebra.web.web.gui.util.PopupMenuButton;

public class ClippingPopup extends PopupMenuButton {

	private EuclidianView3D ev;
	private ImageOrText defaultIcon;

	public ClippingPopup(AppW app, ImageOrText[] data, int rows, int columns,
	        SelectionTable mode, EuclidianView3D ev) {
		super(app, data, rows, columns, mode, true,
		        false);
		this.ev = ev;

		defaultIcon = data.length > 2 ? data[2] : null;

		updateGUI();
	}

	/**
	 * update GUI
	 */
	protected void updateGUI() {
		super.setIcon(data[ev.getClippingReduction() + 1]);

		if (ev.useClippingCube() && ev.showClippingCube()) {
			this.addStyleName("selected");
		}
	}

	@Override
	public void update(Object[] geos) {
		this.setVisible(geos.length == 0
		        && !EuclidianView.isPenMode(app.getMode())
		        && app.getMode() != EuclidianConstants.MODE_DELETE);
	}

	@Override
	public void setIcon(ImageOrText icon) {
		super.setIcon(icon);

		if (getSelectedIndex() == 0 && defaultIcon != null) {
			super.setIcon(defaultIcon);
			this.removeStyleName("selected");
		} else {
			super.setIcon(icon);
			this.addStyleName("selected");
		}

	}
}
