package geogebra.geogebra3D.web.euclidian3D;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.gui.util.SelectionTable;
import geogebra.html5.awt.GDimensionW;
import geogebra.web.gui.util.ImageOrText;
import geogebra.web.gui.util.PopupMenuButton;
import geogebra.web.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;

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
	
	/**
	 * set euclidian view from index
	 */
	public void setEVFromIndex(){
		int index = getSelectedIndex();
		ev.setShowAxes(index % 2 == 1, false);
		ev.setShowPlane(index >= 2);
		ev.repaintView();
	}
	
	@Override
    public void update(Object[] geos){
		this.setVisible(geos.length == 0  && !EuclidianView.isPenMode(app.getMode())
				&& app.getMode() != EuclidianConstants.MODE_DELETE);
	}

	@Override
    public boolean prepareToShowPopup(ClickEvent event){
		if(event.getY() < 15){
			this.setSelectedIndex(this.getSelectedIndex() == 0 ? lastNonZeroIndex : 0);
			setEVFromIndex();
			return false;
		}
		return true;
	}
	
	private int lastNonZeroIndex = 1;

	@Override
	public void setIcon(ImageOrText icon) {
	    if(getSelectedIndex() == 0){
	    	super.setIcon(data[lastNonZeroIndex]);
	    	this.removeStyleName("selected");
	    } else {
	    	super.setIcon(icon);
	    	this.addStyleName("selected");
	    	lastNonZeroIndex = getSelectedIndex();
	    }
	}

	@Override
	protected void setDownState(boolean downState) {
	    // ignore
	}
}
