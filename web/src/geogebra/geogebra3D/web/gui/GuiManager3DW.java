package geogebra.geogebra3D.web.gui;

import geogebra.geogebra3D.web.gui.layout.panels.EuclidianDockPanel3DW;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.main.AppW;

/**
 * web gui manager for 3D
 * @author mathieu
 *
 */
public class GuiManager3DW extends GuiManagerW {

	/**
	 * constructor
	 * @param app application
	 */
	public GuiManager3DW(AppW app) {
	    super(app);
    }

	
	@Override
    protected boolean initLayoutPanels() {

		if (super.initLayoutPanels()){
			layout.registerPanel(new EuclidianDockPanel3DW((AppW) app));
			return true;
		}
		
		return false;
		
	}
}
