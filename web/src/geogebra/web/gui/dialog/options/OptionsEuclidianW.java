package geogebra.web.gui.dialog.options;

import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.gui.dialog.options.OptionsEuclidian;
import geogebra.html5.euclidian.EuclidianViewWeb;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.ui.Widget;

public class OptionsEuclidianW extends OptionsEuclidian implements OptionPanelW {

	private AppW app;

	public OptionsEuclidianW(AppW app,
            EuclidianViewInterfaceCommon activeEuclidianView) {
		this.app = app;
		initGUI();
    }

	private void initGUI() {
	    app.setDefaultCursor();
    }

	public void setLabels() {
	    // TODO Auto-generated method stub
	    
    }

	public void setView(EuclidianViewWeb euclidianView1) {
	    // TODO Auto-generated method stub
	    
    }

	public void showCbView(boolean b) {
	    // TODO Auto-generated method stub
	    
    }

	public void updateGUI() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void updateBounds() {
	    // TODO Auto-generated method stub
	    
    }

	public Widget getWrappedPanel() {
	    // TODO Auto-generated method stub
	    return null;
    }

}
