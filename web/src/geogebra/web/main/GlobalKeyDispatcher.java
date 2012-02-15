package geogebra.web.main;

import geogebra.common.kernel.geos.GeoElement;

public class GlobalKeyDispatcher extends
        geogebra.common.main.GlobalKeyDispatcher {

	private Application app;

	public GlobalKeyDispatcher(Application app) {
		this.app = app;
    }

	@Override
	public void handleFunctionKeyForAlgebraInput(int i, GeoElement geo) {
		// TODO Auto-generated method stub

	}

}
