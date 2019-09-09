package org.geogebra.web.full.gui.view.algebra.contextmenu;

import org.geogebra.common.gui.view.algebra.Suggestion;
import org.geogebra.common.gui.view.algebra.SuggestionDiscover;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.view.algebra.MenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Discovers geometric theorems.
 */
public class DiscoverAction extends MenuAction<GeoElement> {

    /**
     * New discover action
     */
    public DiscoverAction() {
        super("Discover", MaterialDesignResources.INSTANCE.discover_black());
    }

    @Override
    public void execute(GeoElement geo, AppWFull app) {
        getSuggestion(geo).execute(geo);
    }

    @Override
    public boolean isAvailable(GeoElement geo) {
        return getSuggestion(geo) != null;
    }

    private static Suggestion getSuggestion(GeoElement geo) {
        return SuggestionDiscover.get(geo);
    }

}
