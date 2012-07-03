package geogebra.web.main;

import geogebra.common.awt.GColor;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.kernel.ConstructionDefaults;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.PointProperties;
import geogebra.common.main.App;
import geogebra.common.main.GWTKeycodes;
import geogebra.common.main.KeyCodes;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.common.util.CopyPaste;
import geogebra.web.euclidian.EuclidianViewW;
import geogebra.web.gui.applet.GeoGebraFrame;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JTable;

import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;

public class GlobalKeyDispatcherW extends
        geogebra.common.main.GlobalKeyDispatcher implements KeyUpHandler, KeyDownHandler {

	public GlobalKeyDispatcherW(App app) {
		this.app = app;
    }

	@Override
	public void handleFunctionKeyForAlgebraInput(int i, GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void onKeyUp(KeyUpEvent event) {
		//AbstractApplication.debug("onkeyup");
		event.preventDefault();
		event.stopPropagation();
		//no it is private, but can be public, also it is void, but can return boolean as in desktop, if needed
		dispatchEvent(event);
    }

	private void dispatchEvent(KeyUpEvent event) {
	    //we Must find out something here to identify the component that fired this, like class names for example,
		//id-s or data-param-attributes
		
		//we have keypress here only
		handleKeyPressed(event);
	    
    }

	private boolean handleKeyPressed(KeyUpEvent event) {
		// GENERAL KEYS:
		// handle ESC, function keys, zooming with Ctrl +, Ctrl -, etc.
		if (handleGeneralKeys(event)) {
			return true;
		}
		
		// SELECTED GEOS:
		// handle function keys, arrow keys, +/- keys for selected geos, etc.
		if (handleSelectedGeosKeys(event, app.getSelectedGeos())) {
			return true;
		}
		
		return false;
    }

	public boolean handleGeneralKeys(KeyUpEvent event) {
		
		return handleGeneralKeys(KeyCodes.translateGWTcode(event.getNativeKeyCode()), event.isShiftKeyDown(), event.isControlKeyDown(), event.isAltKeyDown(), false, true);

	}
	
	private boolean handleSelectedGeosKeys(KeyUpEvent event,
			ArrayList<GeoElement> geos) {
		
		return handleSelectedGeosKeys(KeyCodes.translateGWTcode(event.getNativeKeyCode()), geos, event.isShiftKeyDown(), event.isControlKeyDown(), event.isAltKeyDown(), false);
	}

	public void onKeyDown(KeyDownEvent event) {
		//AbstractApplication.debug("onkeydown");
	    event.preventDefault();
	    event.stopPropagation();
    }

	@Override
    protected boolean handleCtrlShiftN(boolean isAltDown) {
	    App.debug("unimplemented");
	    return false;
    }

	@Override
    protected boolean handleEnter() {
	    App.debug("unimplemented");
	    return false;
    }

	@Override
    protected void copyDefinitionsToInputBarAsList(ArrayList<GeoElement> geos) {
	    App.debug("unimplemented");
	    
    }

	@Override
    protected void createNewWindow(Object object) {
		App.debug("unimplemented");
    }

	@Override
    protected void showPrintPreview(App app2) {
		App.debug("unimplemented");
    }


}
