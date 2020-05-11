package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.events.StayLoggedOutEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Opens file in Mebis Tafel.
 */
public class OpenFileActionMebis extends DefaultMenuAction<Void> implements EventRenderable {

	private AppWFull app;

	@Override
	public void execute(Void item, final AppWFull app) {
		this.app = app;
		if (isLoggedOut()) {
			app.getLoginOperation().showLoginDialog();
			app.getLoginOperation().getView().add(this);
		} else {
			app.openSearch(null);
		}
	}

	@Override
	public void renderEvent(BaseEvent event) {
		if (event instanceof LoginEvent
				&& ((LoginEvent) event).isSuccessful()) {
			app.openSearch(null);
		}
		if (event instanceof LoginEvent
				|| event instanceof StayLoggedOutEvent) {
			app.getLoginOperation().getView().remove(this);
		}
	}

	/**
	 * @return true if the whiteboard is active and the user logged in
	 */
	private boolean isLoggedOut() {
		return app.getLoginOperation() != null
				&& !app.getLoginOperation().isLoggedIn();
	}
}
