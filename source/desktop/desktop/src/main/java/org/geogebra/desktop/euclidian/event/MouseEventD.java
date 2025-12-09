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

import java.awt.event.MouseEvent;
import java.util.LinkedList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.util.debug.Log;

public class MouseEventD extends AbstractEvent implements MouseEventND {

	private static final LinkedList<MouseEventD> pool = new LinkedList<>();
	private MouseEvent event;

	private MouseEventD(MouseEvent e) {
		Log.debug("possible missing release()");
		this.event = e;
	}

	/**
	 * @param e native event
	 * @return cross-platform (wrapped) evnt
	 */
	public static AbstractEvent wrapEvent(MouseEvent e) {
		if (!pool.isEmpty()) {
			MouseEventD wrap = pool.getLast();
			wrap.event = e;
			pool.removeLast();
			return wrap;
		}
		return new MouseEventD(e);
	}

	@Override
	public GPoint getPoint() {

		return new GPoint(event.getPoint().x, event.getPoint().y);
	}

	@Override
	public boolean isAltDown() {
		return event.isAltDown();
	}

	@Override
	public boolean isShiftDown() {
		return event.isShiftDown();
	}

	@Override
	public void release() {
		MouseEventD.pool.add(this);
	}

	@Override
	public int getX() {
		return event.getX();
	}

	@Override
	public int getY() {
		return event.getY();
	}

	@Override
	public boolean isRightClick() {
		return MouseEventUtil.isRightClick(event);
	}

	@Override
	public boolean isControlDown() {
		return MouseEventUtil.isControlDown(event);
	}

	@Override
	public int getClickCount() {
		return event.getClickCount();
	}

	@Override
	public boolean isMetaDown() {
		return MouseEventUtil.isMetaDown(event);
	}

	@Override
	public boolean isMiddleClick() {
		return (event.getButton() == 2) && (event.getClickCount() == 1);
	}

	@Override
	public boolean isPopupTrigger() {
		return event.isPopupTrigger();
	}

	@Override
	public java.awt.Component getComponent() {
		return event.getComponent();
	}

	@Override
	public PointerEventType getType() {
		return PointerEventType.MOUSE;
	}

}
