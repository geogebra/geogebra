package geogebra.move.ggtapi.views;

import geogebra.common.move.events.BaseEvent;
import geogebra.common.move.views.BaseEventView;

import javax.swing.SwingUtilities;



/**
 * A basic view based on Swing that can handle events 
 * 
 * @author stefan
 *
 */
public class BaseSwingEventView extends BaseEventView {

	@Override
	public void onEvent(final BaseEvent event) {
		
		// call the gui event on the Event dispatch thread.
		SwingUtilities.invokeLater(new Runnable() {
			@SuppressWarnings("synthetic-access")
			public void run() {
				BaseSwingEventView.super.onEvent(event);
			}
		});
	}
}
