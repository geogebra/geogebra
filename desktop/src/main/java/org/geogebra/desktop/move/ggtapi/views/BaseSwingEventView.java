package org.geogebra.desktop.move.ggtapi.views;

import javax.swing.SwingUtilities;

import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.events.GenericEvent;
import org.geogebra.common.move.views.BaseView;
import org.geogebra.common.move.views.EventRenderable;

/**
 * A basic view based on Swing that can handle events
 * 
 * @author stefan
 *
 */
public class BaseSwingEventView extends BaseView<EventRenderable> {

	@Override
	public void onEvent(final GenericEvent<EventRenderable> event) {

		// call the gui event on the Event dispatch thread.
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			@SuppressWarnings("synthetic-access")
			public void run() {
				BaseSwingEventView.super.onEvent(event);
			}
		});
	}
}
