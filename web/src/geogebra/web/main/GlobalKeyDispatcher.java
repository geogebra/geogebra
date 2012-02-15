package geogebra.web.main;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;

import geogebra.common.kernel.geos.GeoElement;

public class GlobalKeyDispatcher extends
        geogebra.common.main.GlobalKeyDispatcher implements KeyPressHandler {

	private Application app;

	public GlobalKeyDispatcher(Application app) {
		this.app = app;
    }

	@Override
	public void handleFunctionKeyForAlgebraInput(int i, GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void onKeyPress(KeyPressEvent event) {
	    GWT.log(event.getCharCode()+"");
    }

}
