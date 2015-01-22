package geogebra.web.euclidian;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.gui.util.SelectionTable;
import geogebra.html5.main.AppW;
import geogebra.web.gui.util.ImageOrText;
import geogebra.web.gui.util.PopupMenuButton;

public class GridPopup extends PopupMenuButton {

	private EuclidianView ev;
	private ImageOrText defaultIcon;

	public GridPopup(AppW app, ImageOrText[] data, int rows, int columns,
	        SelectionTable mode, EuclidianView ev) {
		super(app, data, rows, columns, mode, true, false);
		this.ev = ev;
		defaultIcon = data.length > 1 ? data[1] : null;
		this.setIcon(data[EuclidianStyleBarW.gridIndex(ev)]);
	}

	@Override
	public void update(Object[] geos) {
		this.setVisible(geos.length == 0
		        && !EuclidianView.isPenMode(app.getMode())
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
