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
		//no it is private, but can be public, also it is void, but can return boolean as in desktop, if needed
	    dispatchEvent(event);
    }

	private void dispatchEvent(KeyPressEvent event) {
	    //we ust find out somethinkg here to identify the component that fired this, like class names for example,
		//id-s or data-param-attributes
		
		//we have keypress here only
		handleKeyPressed(event);
	    
    }

	private void handleKeyPressed(KeyPressEvent event) {
	    
    }

}
