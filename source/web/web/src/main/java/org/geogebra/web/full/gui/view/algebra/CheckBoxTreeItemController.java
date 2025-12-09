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

package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.event.ZeroOffset;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.dom.client.NativeEvent;
import org.gwtproject.event.dom.client.MouseDownEvent;
import org.gwtproject.event.dom.client.MouseEvent;
import org.gwtproject.event.dom.client.TouchStartEvent;

/**
 * @author Laszlo
 *
 */
public class CheckBoxTreeItemController extends LatexTreeItemController {

	/**
	 * @param item
	 *            AV item
	 */
	public CheckBoxTreeItemController(RadioTreeItem item) {
		super(item);
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		event.stopPropagation();

		if (CancelEventTimer.cancelMouseEvent()
				|| checkMarbleHit(event)) {
			return;
		}

		app.closePopups();
	
		PointerEvent wrappedEvent = PointerEvent.wrapEventAbsolute(event,
				ZeroOffset.INSTANCE);
		onPointerDown(wrappedEvent, event);
		handleAVItem(event);
		if (event.getNativeButton() != NativeEvent.BUTTON_RIGHT) {
			toggleCheckbox();
		}

		Scheduler.get().scheduleDeferred(item::adjustStyleBar);
	}

	@Override
	public void onTouchStart(TouchStartEvent event) {
		event.stopPropagation();
		app.closePopups();

		PointerEvent wrappedEvent = PointerEvent.wrapEvent(event,
				ZeroOffset.INSTANCE);

		onPointerDownMainButton(wrappedEvent);
		handleAVItem(event);
		toggleCheckbox();

		Scheduler.get().scheduleDeferred(item::adjustStyleBar);
	}

	private void toggleCheckbox() {
		GeoBoolean bool = (GeoBoolean) item.geo;
		bool.setValue(!bool.getBoolean());
		item.geo.updateCascade();
		item.kernel.notifyRepaint();
	}

	@Override
	protected boolean canEditStart(MouseEvent<?> event) {
		return false;
	}
}
