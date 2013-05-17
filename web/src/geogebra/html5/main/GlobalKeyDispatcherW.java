package geogebra.html5.main;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.main.KeyCodes;

import java.util.ArrayList;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;

/**
 * Handles keyboard events.
 */
public class GlobalKeyDispatcherW extends
        geogebra.common.main.GlobalKeyDispatcher implements KeyUpHandler, KeyDownHandler, KeyPressHandler {


	private static boolean controlDown = false;
	private static boolean shiftDown = false;

	public static boolean getControlDown() {
		return controlDown;
	}

	public static boolean getShiftDown() {
		return shiftDown;
	}

	public static void setDownKeys(KeyEvent ev) {
		controlDown = ev.isControlKeyDown();
		shiftDown = ev.isShiftKeyDown();
	}
	
	/**
	 * @param app application
	 */
	public GlobalKeyDispatcherW(App app) {
		super(app);
    }

	@Override
	public void handleFunctionKeyForAlgebraInput(int i, GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void onKeyPress(KeyPressEvent event) {
		setDownKeys(event);
		event.stopPropagation();
		event.preventDefault();
	}

	public void onKeyUp(KeyUpEvent event) {
		setDownKeys(event);
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
		//if (handleSelectedGeosKeys(event, app.getSelectionManager().getSelectedGeos())) {
		//	return true;
		//}

		return false;
    }

	/**
	 * Handles key event by disassembling it into primitive types and handling it using the mothod
	 * from common 
	 * @param event event
	 * @return whether event was consumed
	 */
	public boolean handleGeneralKeys(KeyUpEvent event) {
		
		return handleGeneralKeys(KeyCodes.translateGWTcode(event.getNativeKeyCode()), event.isShiftKeyDown(), event.isControlKeyDown(), event.isAltKeyDown(), false, true);

	}
	
	private boolean handleSelectedGeosKeys(KeyUpEvent event,
			ArrayList<GeoElement> geos) {
		
		return handleSelectedGeosKeys(KeyCodes.translateGWTcode(event.getNativeKeyCode()), geos, event.isShiftKeyDown(), event.isControlKeyDown(), event.isAltKeyDown(), false);
	}

	public boolean handleSelectedGeosKeysNative(NativeEvent event) {
		return handleSelectedGeosKeys(
			geogebra.common.main.KeyCodes.translateGWTcode(event.getKeyCode()),
			selection.getSelectedGeos(),
			event.getShiftKey(),
			event.getCtrlKey(),
			event.getAltKey(), false);
	}

	public void onKeyDown(KeyDownEvent event) {
		setDownKeys(event);
		//AbstractApplication.debug("onkeydown");
	    event.preventDefault();
	    event.stopPropagation();

		// SELECTED GEOS:
		// handle function keys, arrow keys, +/- keys for selected geos, etc.
		handleSelectedGeosKeys(
			KeyCodes.translateGWTcode(
				event.getNativeKeyCode()),
			app.getSelectionManager().getSelectedGeos(),
			event.isShiftKeyDown(),
			event.isControlKeyDown(),
			event.isAltKeyDown(),
			false);
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
    protected void createNewWindow() {
		App.debug("unimplemented");
    }

	@Override
    protected void showPrintPreview(App app2) {
		App.debug("unimplemented");
    }


}
