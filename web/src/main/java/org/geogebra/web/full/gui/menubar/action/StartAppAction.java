package org.geogebra.web.full.gui.menubar.action;

import com.google.gwt.user.client.Window;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Starts a GeoGebra app.
 */
public final class StartAppAction extends DefaultMenuAction<Void> {

	private static final String PRODUCTION_ROOT_URL = "https://www.geogebra.org/";
	private static final String BETA_ROOT_URL = "https://beta.geogebra.org/";

	private String url;

	/**
	 * Factory method
	 *
	 * @param app   app
	 * @param appId app id
	 * @return a new StartAppAction instance
	 */
	public static StartAppAction create(App app, String appId) {
		String rootUrl = app.has(Feature.TUBE_BETA) ? BETA_ROOT_URL : PRODUCTION_ROOT_URL;
		String fullUrl = rootUrl + appId;
		return new StartAppAction(fullUrl);
	}

	private StartAppAction(String url) {
		this.url = url;
	}

	@Override
	public void execute(Void item, AppWFull app) {
		Window.Location.assign(url);
	}
}
