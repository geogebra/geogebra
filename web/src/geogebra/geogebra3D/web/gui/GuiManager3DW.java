package geogebra.geogebra3D.web.gui;

import geogebra.geogebra3D.web.gui.layout.panels.EuclidianDockPanel3DW;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.layout.DockPanelW;
import geogebra.web.main.AppW;

/**
 * web gui manager for 3D
 * @author mathieu
 *
 */
public class GuiManager3DW extends GuiManagerW {

	private DockPanelW euclidian3Dpanel;
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
			this.euclidian3Dpanel = new EuclidianDockPanel3DW((AppW) app);
			layout.registerPanel(this.euclidian3Dpanel);
			return true;
		}
		
		return false;
		
	}
	
	public DockPanelW getEuclidian3DPanel(){
		return this.euclidian3Dpanel;
	}
}
