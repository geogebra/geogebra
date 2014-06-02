package geogebra.web.euclidian;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.gui.util.SelectionTable;
import geogebra.html5.awt.GDimensionW;
import geogebra.web.gui.util.ImageOrText;
import geogebra.web.gui.util.PopupMenuButton;
import geogebra.web.main.AppW;

public class GridPopup extends PopupMenuButton {

	public GridPopup(AppW app, ImageOrText[] data, int rows,
            int columns, SelectionTable mode,
            int initial) {
	    super(app, data, rows, columns, new GDimensionW(32,32), mode, true, false);
	    this.setIcon(data[initial]);
	    // TODO Auto-generated constructor stub
    }
	
	@Override
    public void update(Object[] geos){
		this.setVisible(geos.length == 0  && !EuclidianView.isPenMode(app.getMode())
				&& app.getMode() != EuclidianConstants.MODE_DELETE);
	}

}
