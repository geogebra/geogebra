package org.geogebra.web.shared.ggtapi;

import org.geogebra.common.main.Feature;
import org.geogebra.common.move.ggtapi.models.MarvlService;
import org.geogebra.common.move.ggtapi.models.MaterialRestAPI;
import org.geogebra.common.move.ggtapi.models.MowService;
import org.geogebra.common.move.ggtapi.models.Service;
import org.geogebra.common.move.ggtapi.operations.BackendAPI;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.shared.ggtapi.models.GeoGebraTubeAPIW;

/**
 * Class to provide the right backend API
 * for the given application.
 *
 * @author laszlo
 */
public class BackendAPIFactory {

	private AppW app;
	private AppletParameters articleElement;
	private BackendAPI api = null;

	/**
	 * @param app The application.
	 */
	public BackendAPIFactory(AppW app) {
		this.app = app;
		articleElement = app.getAppletParameters();
	}

	/**
	 * @return the backend API suitable for the applicaion.
	 */
	public BackendAPI get() {
		createApiIfNeeded();
		api.setClient(app.getClientInfo());
		return this.api;
	}

	private void createApiIfNeeded() {
		if (api != null) {
			return;
		}
		api = app.isMebis() ? newMaterialRestAPI() : newTubeAPI();
	}

	/**
	 * Create an appropriate MaterialRestApi
	 * @return a MaterialRestApi based on the backend-url data-param
	 */
	public MaterialRestAPI newMaterialRestAPI() {
		String backendURL = articleElement.getParamBackendURL().isEmpty()
				? MaterialRestAPI.marvlUrl : articleElement.getParamBackendURL();
		Service service = app.isMebis()
				? new MowService() : new MarvlService();

		return new MaterialRestAPI(backendURL, service);
	}

	private GeoGebraTubeAPIW newTubeAPI() {
		return new GeoGebraTubeAPIW(app.getClientInfo(),
				app.has(Feature.TUBE_BETA),
				articleElement);
	}
}
