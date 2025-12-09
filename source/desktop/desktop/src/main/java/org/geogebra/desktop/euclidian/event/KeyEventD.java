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

package org.geogebra.desktop.euclidian.event;

import java.awt.event.KeyEvent;
import java.util.LinkedList;

import org.geogebra.desktop.main.AppD;

public class KeyEventD extends org.geogebra.common.euclidian.event.KeyEvent {

	private static final LinkedList<KeyEventD> pool = new LinkedList<>();
	private KeyEvent event;

	public KeyEventD(KeyEvent e) {
		this.event = e;
	}

	/**
	 * @param e native event
	 * @return wrapped event
	 */
	public static KeyEventD wrapEvent(KeyEvent e) {
		if (!pool.isEmpty()) {
			KeyEventD wrap = pool.getLast();
			wrap.event = e;
			pool.removeLast();
			return wrap;
		}
		return new KeyEventD(e);
	}

	/**
	 * Return event back to pool.
	 */
	public void release() {
		KeyEventD.pool.add(this);
	}

	@Override
	public boolean isEnterKey() {
		return event.getKeyChar() == '\n';
	}

	@Override
	public boolean isCtrlDown() {
		return AppD.isControlDown(event);
	}

	@Override
	public boolean isAltDown() {
		return AppD.isAltDown(event);
	}

	@Override
	public char getCharCode() {
		return event.getKeyChar();
	}

	@Override
	public void preventDefault() {
		event.consume();
	}

}
