package org.geogebra.web.full.gui.util;

import org.gwtproject.event.dom.client.DomEvent;
import org.gwtproject.event.shared.HandlerRegistration;
import org.gwtproject.user.client.ui.IsWidget;

public interface AdvancedFocusPanelI extends IsWidget {

	/**
	 * @param handler
	 *            event handler
	 * @param type
	 *            event type
	 * @return registration
	 */
	<H> HandlerRegistration addDomHandler(
			H handler, DomEvent.Type<H> type);

	int getOffsetHeight();

	int getOffsetWidth();

	boolean isVisible();

	boolean isAttached();

	void setFocus(boolean b);

	void setSelectedContent(String cs);

	void setHeight(String string);

	void setWidth(String string);

}
