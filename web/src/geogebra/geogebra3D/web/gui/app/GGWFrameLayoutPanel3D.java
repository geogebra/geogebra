package geogebra.geogebra3D.web.gui.app;

import geogebra.web.gui.app.GGWFrameLayoutPanel;
import geogebra.web.gui.app.GGWToolBar;

/**
 * 
 * @author mathieu
 *
 */
public class GGWFrameLayoutPanel3D extends GGWFrameLayoutPanel {

	@Override
    protected GGWToolBar newGGWToolBar(){
		return new GGWToolBar3D();
	}
}
