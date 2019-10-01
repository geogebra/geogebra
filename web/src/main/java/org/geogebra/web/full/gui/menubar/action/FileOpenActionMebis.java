package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.events.StayLoggedOutEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.web.full.gui.view.algebra.MenuAction;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.full.main.activity.GeoGebraActivity;
import org.geogebra.web.html5.main.AppW;

/**
 * Open local file if logged out or online file if logged in
 */
public class FileOpenActionMebis extends MenuAction<Void>
        implements EventRenderable {

    private AppW appW;

    /**
     * @param app      app
     * @param activity activity
     */
    public FileOpenActionMebis(AppW app, GeoGebraActivity activity) {
        super(app.getVendorSettings().getMenuLocalizationKey("Open"),
                activity.getResourceIconProvider().openFileMenu());
        this.appW = app;
    }

    @Override
    public void execute(Void geo, final AppWFull app) {
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
            appW.openSearch(null);
        }
        if (event instanceof LoginEvent
                || event instanceof StayLoggedOutEvent) {
            appW.getLoginOperation().getView().remove(this);
        }
    }

    /**
     * @return true if the whiteboard is active and the user logged in
     */
    private boolean isLoggedOut() {
        return appW.getLoginOperation() != null
                && !appW.getLoginOperation().isLoggedIn();
    }

}
