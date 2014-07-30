package geogebra.web.euclidian;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.gui.util.SelectionTable;
import geogebra.html5.awt.GDimensionW;
import geogebra.web.gui.util.ImageOrText;
import geogebra.web.gui.util.PopupMenuButton;
import geogebra.web.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;

public class GridPopup extends PopupMenuButton {

	private EuclidianView ev;
	private ImageOrText defaultIcon;

	public GridPopup(AppW app, ImageOrText[] data, int rows,
            int columns, SelectionTable mode,
            EuclidianView ev) {
	    super(app, data, rows, columns, new GDimensionW(32,32), mode, true, false);
	    this.ev = ev;
	    defaultIcon = data.length > 1 ? data[1] : null;
	    this.setIcon(data[EuclidianStyleBarW.gridIndex(ev)]);	    

	    geogebra.web.gui.util.SelectionTable table = getMyTable();
	    for(int i = 0; i < table.getRowCount(); i++){
	    	for(int j = 0; j < table.getColumnCount(); j++){
	    		table.getWidget(i, j).addStyleName("border");
	    	}
	    }
    }
	
	@Override
    public void update(Object[] geos){
		this.setVisible(geos.length == 0  && !EuclidianView.isPenMode(app.getMode())
				&& app.getMode() != EuclidianConstants.MODE_DELETE);
	}

	@Override
    public boolean prepareToShowPopup(ClickEvent event){
		if(event.getY() < 15){
			this.setSelectedIndex(this.getSelectedIndex() == 0 ? 1 : 0);
			EuclidianStyleBarW.setGridType(this.ev, getSelectedIndex());
			return false;
		}
		return true;
	}

	@Override
	public void setIcon(ImageOrText icon) {
	    if(getSelectedIndex() == 0 && defaultIcon != null){
	    	super.setIcon(defaultIcon);
	    	this.removeStyleName("selected");
	    } else {
	    	super.setIcon(icon);
	    	this.addStyleName("selected");
	    }
	}

	@Override
	protected void setDownState(boolean downState) {
	    // ignore
	}
}
