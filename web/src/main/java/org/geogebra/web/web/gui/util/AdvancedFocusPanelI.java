package org.geogebra.web.web.gui.util;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;

public interface AdvancedFocusPanelI extends IsWidget {

	/**
	 * @param handler
	 * @param type
	 * @return
	 */
	public <H extends EventHandler> HandlerRegistration addDomHandler(
			H handler, DomEvent.Type<H> type);

	public int getOffsetHeight();

	public int getOffsetWidth();

	public boolean isVisible();

	public boolean isAttached();

	public Element getTextarea();

	public void setFocus(boolean b);

	public void setSelectedContent(String cs);

	public void setHeight(String string);

	public void setWidth(String string);

}
