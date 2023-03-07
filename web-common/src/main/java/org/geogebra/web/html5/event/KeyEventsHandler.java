package org.geogebra.web.html5.event;

import org.gwtproject.event.dom.client.KeyDownHandler;
import org.gwtproject.event.dom.client.KeyPressHandler;
import org.gwtproject.event.dom.client.KeyUpHandler;

/**
 * Union of GWT key-handling interfaces
 * 
 * @author Zbynek Konecny
 *
 */
public interface KeyEventsHandler extends KeyDownHandler, KeyPressHandler,
        KeyUpHandler {
	// methods declared in parent interfaces
}
