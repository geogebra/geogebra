package org.geogebra.web.geogebra3D.web.euclidian3D;

import org.geogebra.common.gui.AccessibilityManagerInterface;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GlobalKeyDispatcherW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;

public class EuclidianKeyHandler3DW implements KeyUpHandler, KeyDownHandler, KeyPressHandler {
	private AppW app;
	private AccessibilityManagerInterface am;
	private GlobalKeyDispatcherW gkd;

	public EuclidianKeyHandler3DW(AppW app, Canvas canvas) {
		this.app = app;
		am = app.getAccessibilityManager();
		gkd = app.getGlobalKeyDispatcher();
		canvas.addKeyUpHandler(this);
		canvas.addKeyDownHandler(this);
		canvas.addKeyPressHandler(this);
	}
	
	private boolean isTabOverGui(KeyEvent<?> event) {
		return false;//!am.isTabOverGeos() && event.getNativeEvent().getKeyCode() == KeyCodes.KEY_TAB;
	}

	@Override
	public void onKeyPress(KeyPressEvent event) {
		if (isTabOverGui(event)) {
			Log.debug("TAB in KeyPressEvent");
		} else {
			Log.debug("GKD way keyPress");
			gkd.onKeyPress(event);
		}
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		if (isTabOverGui(event)) {
			Log.debug("TAB in KeyDownEvent");
		} else {
			Log.debug("GKD way keyDown");
			gkd.onKeyDown(event);
		}
	}

	@Override
	public void onKeyUp(KeyUpEvent event) {
		if (isTabOverGui(event)) {
			Log.debug("TAB in KeyUpEvent");
		} else {
			Log.debug("GKD way keyUp");
			gkd.onKeyUp(event);
		}
	}
}
