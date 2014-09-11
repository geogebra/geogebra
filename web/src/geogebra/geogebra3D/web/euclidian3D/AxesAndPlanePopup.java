package geogebra.geogebra3D.web.euclidian3D;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.gui.util.SelectionTable;
import geogebra.html5.awt.GDimensionW;
import geogebra.html5.main.AppW;
import geogebra.web.gui.util.ImageOrText;
import geogebra.web.gui.util.PopupMenuButton;

public class AxesAndPlanePopup extends PopupMenuButton {

	private EuclidianView3D ev;
	private ImageOrText defaultIcon;

	public AxesAndPlanePopup(AppW app, ImageOrText[] data, int rows,
            int columns, SelectionTable mode,
            EuclidianView3D ev) {
	    super(app, data, rows, columns, new GDimensionW(32,32), mode, true, false);
	    this.ev = ev;
	    defaultIcon = data.length > 1 ? data[1] : null;

	    this.setIcon(data[getIndexFromEV()]);	    

	    geogebra.web.gui.util.SelectionTable table = getMyTable();
	    for(int i = 0; i < table.getRowCount(); i++){
	    	for(int j = 0; j < table.getColumnCount(); j++){
	    		table.getWidget(i, j).addStyleName("border");
	    	}
	    }
    }
	
	private int getIndexFromEV(){
		int ret = 0;
		if (ev.getShowXaxis()){
			ret++;
		}
		if (ev.getShowPlane()){
			ret += 2;
		}
		return ret;
	}
	
	public void setIndexFromEV(){
		setSelectedIndex(getIndexFromEV());
	}
	
	/**
	 * set euclidian view from index
	 */
	public void setEVFromIndex(){
		int index = getSelectedIndex();
		ev.getSettings().setShowAxes(index % 2 == 1);
		ev.getSettings().setShowPlate(index >= 2);
	}
	
	@Override
    public void update(Object[] geos){
		this.setVisible(geos.length == 0  && !EuclidianView.isPenMode(app.getMode())
				&& app.getMode() != EuclidianConstants.MODE_DELETE);
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
