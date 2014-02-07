package geogebra.geogebra3D.web.gui;

import geogebra.geogebra3D.web.gui.layout.panels.EuclidianDockPanelW3D;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.main.AppW;

/**
 * web gui manager for 3D
 * @author mathieu
 *
 */
public class GuiManagerW3D extends GuiManagerW {

	/**
	 * constructor
	 * @param app application
	 */
	public GuiManagerW3D(AppW app) {
	    super(app);
    }

	
	@Override
    protected boolean initLayoutPanels() {

		if (super.initLayoutPanels()){
			layout.registerPanel(new EuclidianDockPanelW3D((AppW) app));
			return true;
		}
		
		return false;
		
	}
}
