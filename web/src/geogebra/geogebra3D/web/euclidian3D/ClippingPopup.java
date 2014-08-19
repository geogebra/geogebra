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

public class ClippingPopup extends PopupMenuButton {

	private EuclidianView3D ev;

	public ClippingPopup(AppW app, ImageOrText[] data, int rows,
            int columns, SelectionTable mode,
            EuclidianView3D ev) {
	    super(app, data, rows, columns, new GDimensionW(32,32), mode, true, false);
	    this.ev = ev;
	   
	    updateGUI();

	    geogebra.web.gui.util.SelectionTable table = getMyTable();
	    for(int i = 0; i < table.getRowCount(); i++){
	    	for(int j = 0; j < table.getColumnCount(); j++){
	    		table.getWidget(i, j).addStyleName("border");
	    	}
	    }
    }
	
	/**
	 * update GUI
	 */
	protected void updateGUI(){
		super.setIcon(data[ev.getClippingReduction()]);	    

		if (ev.useClippingCube() && ev.showClippingCube()){
			this.addStyleName("selected");
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
			//if (getSelectedIndex() == EuclidianStyleBar3DW.clippingIndex(ev)){
				// toggle use of clipping box
				if(ev.useClippingCube() && ev.showClippingCube()){
					ev.setUseClippingCube(false);
					ev.setShowClippingCube(false);
					this.removeStyleName("selected");
				}else{
					ev.setUseClippingCube(true);
					ev.setShowClippingCube(true);
					this.addStyleName("selected");
				}
				
				ev.repaintView();
			//}
				/*
			this.setSelectedIndex(this.getSelectedIndex() == 0 ? 1 : 0);
			EuclidianStyleBar3DW.setClippingIndex(this.ev, getSelectedIndex());
			*/
				
			return false;
			
		}
		return true;
	}

	@Override
	public void setIcon(ImageOrText icon) {
		super.setIcon(icon);
		this.addStyleName("selected");
		ev.setUseClippingCube(true);
		ev.setShowClippingCube(true);

//	    if(getSelectedIndex() == 0 && defaultIcon != null){
//	    	super.setIcon(defaultIcon);
//	    	this.removeStyleName("selected");
//	    } else {
//	    	super.setIcon(icon);
//	    	this.addStyleName("selected");
//	    }
	}

	@Override
	protected void setDownState(boolean downState) {
	    // ignore
	}
}
