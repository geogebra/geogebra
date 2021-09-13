package org.geogebra.web.full.gui.util;

import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;

public interface AdvancedFocusPanelI extends IsWidget {

	/**
	 * @param handler
	 *            event handler
	 * @param type
	 *            event type
	 * @return registration
	 */
	<H extends EventHandler> HandlerRegistration addDomHandler(
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
