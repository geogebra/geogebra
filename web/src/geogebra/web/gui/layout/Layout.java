package geogebra.web.gui.layout;

import java.util.ArrayList;

import geogebra.common.io.layout.Perspective;
import geogebra.common.main.settings.AbstractSettings;
import geogebra.common.main.settings.SettingListener;
import geogebra.web.main.Application;

public class Layout extends geogebra.common.gui.Layout implements SettingListener {
	
	private boolean isInitialized = false;
	
	private Application app;
	
	/**
	 * instantiates layot for Web
	 */
	public Layout() {
		this.perspectives = new ArrayList<Perspective>(defaultPerspectives.length);
	}

	public void settingsChanged(AbstractSettings settings) {
		// TODO Auto-generated method stub

	}

	@Override
    protected void applyPerspective(Perspective perspective) {
	    // TODO Auto-generated method stub
	    
    }

}
