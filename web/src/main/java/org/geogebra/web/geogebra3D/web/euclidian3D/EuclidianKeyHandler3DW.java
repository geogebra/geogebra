package org.geogebra.web.geogebra3D.web.euclidian3D;

import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GlobalKeyDispatcherW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;

/**
 * Class to handle tab key properly for {@link EuclidianView3DW}
 *
 * @author laszlo
 *
 */
public class EuclidianKeyHandler3DW implements KeyUpHandler, KeyDownHandler, KeyPressHandler {

	private GlobalKeyDispatcherW gkd;

	/**
	 * Constructor.
	 *
	 * @param app
	 *            {@link AppW}
	 */
	public EuclidianKeyHandler3DW(AppW app) {
		gkd = app.getGlobalKeyDispatcher();
	}

	/**
	 * @param canvas
	 *            to add the key handler to.
	 */
	public void listenTo(Canvas canvas) {
		canvas.addKeyUpHandler(this);
		canvas.addKeyDownHandler(this);
		canvas.addKeyPressHandler(this);
	}

	@Override
	public void onKeyPress(KeyPressEvent event) {
		gkd.onKeyPress(event);
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		gkd.onKeyDown(event);
	}

	@Override
	public void onKeyUp(KeyUpEvent event) {
		gkd.onKeyUp(event);
	}
}
