/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.geogebra3D.web.euclidian3D;

import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GlobalKeyDispatcherW;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.event.dom.client.KeyDownEvent;
import org.gwtproject.event.dom.client.KeyDownHandler;
import org.gwtproject.event.dom.client.KeyPressEvent;
import org.gwtproject.event.dom.client.KeyPressHandler;
import org.gwtproject.event.dom.client.KeyUpEvent;
import org.gwtproject.event.dom.client.KeyUpHandler;

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
